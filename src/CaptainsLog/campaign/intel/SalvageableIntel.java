package CaptainsLog.campaign.intel;

import CaptainsLog.campaign.intel.button.IgnoreSalvage;
import CaptainsLog.campaign.intel.button.LayInCourse;
import CaptainsLog.scripts.Utils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

public class SalvageableIntel extends BaseIntel {

    // TODO: split into derelict_ship and other?
    public static final String IGNORE_SALVAGEABLE_MEM_FLAG = "$captainsLog_ignoreSalvageable";
    private static final Logger log = Global.getLogger(SalvageableIntel.class);
    private final SectorEntityToken salvageObject;
    private final ShipVariantAPI variant;
    private final int rating;

    public SalvageableIntel(SectorEntityToken salvageObject) {
        this.salvageObject = salvageObject;

        // entity.getCustomPlugin() instanceof DerelictShipEntityPlugin ||
        // Entities.WRECK.equals(type)

        if (salvageObject.getCustomPlugin() instanceof DerelictShipEntityPlugin) {
            DerelictShipEntityPlugin p = (DerelictShipEntityPlugin) salvageObject.getCustomPlugin();
            variant = Global.getSettings().getVariant(p.getData().ship.variantId);
        } else {
            variant = null;
        }

        float salvageValue = estimateSalvageValue();
        rating = getValueRating(salvageValue);

        getMapLocation(null).getMemory().set(CAPTAINS_LOG_MEMORY_KEY, true);

        log.info("Adding intel for new " + getName() + ". Sort value: " + salvageValue + " (" + rating + ")");
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);

        String title = salvageObject.getFullName();
        if (isEnding()) {
            title += " - Deleted";
        }

        info.addPara(title, c, 0f);

        float initPad;
        if (mode == ListInfoMode.IN_DESC) {
            initPad = 10f;
        } else {
            initPad = 3f;
        }

        bullet(info);
        if (isShip()) {
            info.addPara(variant.getHullSpec().getHullName(), Misc.getRelColor(rating / 4f), initPad);
        }

        info.addPara(
            Utils.getSystemNameOrHyperspace(salvageObject),
            initPad,
            getBulletColorForMode(mode),
            Misc.getPositiveHighlightColor(),
            Utils.getSystemNameOrHyperspaceBase(salvageObject)
        );

        unindent(info);
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;

        Description desc = getDesc();

        if (hasImage()) {
            TooltipMakerAPI text = info.beginImageWithText(getImage(), 48);
            text.addPara(desc.getText1FirstPara(), Misc.getGrayColor(), opad);
            info.addImageWithText(opad);
        } else {
            info.addPara(desc.getText1FirstPara(), Misc.getGrayColor(), opad);
        }

        info.addPara(
            "Location: " + Utils.getSystemNameOrHyperspace(salvageObject) + ".",
            opad,
            Misc.getPositiveHighlightColor(),
            Utils.getSystemNameOrHyperspaceBase(salvageObject)
        );

        if (!isEnding()) {
            addGenericButton(info, width, new LayInCourse(salvageObject));
            addGenericButton(info, width, new IgnoreSalvage(this));
        }
    }

    @Override
    public SectorEntityToken getEntity() {
        return salvageObject;
    }

    @Override
    protected String getName() {
        String name = "Salvageable ";
        if (isShip()) {
            name += variant.getHullSpec().getHullName();
        } else {
            name += salvageObject.getFullName();
        }
        return name;
    }

    private boolean isShip() {
        return variant != null;
    }

    @Override
    public String getIcon() {
        return salvageObject.getCustomEntitySpec().getIconName();
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Tags.INTEL_EXPLORATION);
        return tags;
    }

    private float estimateSalvageValue() {
        float value = 0;

        SalvageEntityGenDataSpec salvageSpec = (SalvageEntityGenDataSpec) Global
            .getSettings()
            .getSpec(SalvageEntityGenDataSpec.class, salvageObject.getCustomEntityType(), true);

        if (salvageSpec != null) {
            value += salvageToValue(salvageSpec.getDropValue(), salvageSpec.getDropRandom());
        }
        value += salvageToValue(salvageObject.getDropValue(), salvageObject.getDropRandom());
        if (isShip()) {
            value += variant.getHullSpec().getBaseValue();
        }
        return value;
    }
    
    private float salvageToValue(List<SalvageEntityGenDataSpec.DropData> dropValue, List<SalvageEntityGenDataSpec.DropData> dropRandom) {
        float value = 0;
        for (SalvageEntityGenDataSpec.DropData data : dropValue) {
            value += data.value;
        }
        for (SalvageEntityGenDataSpec.DropData data : dropRandom) {
            if (data.value > 0) {
                value += data.value;
            } else {
                value += 500; // close enough - Alex
            }
        }
        return value;
    }

    private boolean hasImage() {
        return getImage() != null && !getImage().equals("");
    }

    private String getImage() {
        if (isShip()) {
            return variant.getHullSpec().getSpriteName();
        } else {
            return salvageObject.getCustomEntitySpec().getSpriteName();
        }
    }

    private Description getDesc() {
        if (isShip()) {
            return Global.getSettings().getDescription(variant.getHullSpec().getDescriptionId(), Description.Type.SHIP);
        } else {
            return Global.getSettings().getDescription(salvageObject.getCustomDescriptionId(), Description.Type.CUSTOM);
        }
    }

    @Override
    public String getSmallDescriptionTitle() {
        if (isShip()) {
            return "Salvageable Ship";
        } else {
            return "Salvageable " + salvageObject.getFullName();
        }
    }

    @Override
    public FactionAPI getFactionForUIColors() {
        return super.getFactionForUIColors();
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return getEntity();
    }

    @Override
    public boolean shouldRemoveIntel() {
        return shouldRemoveIntelEntry(salvageObject);
    }

    public static boolean shouldRemoveIntelEntry(SectorEntityToken token) {
        return (
            token == null ||
            !token.hasTag(Tags.SALVAGEABLE) ||
            token.hasSensorProfile() ||
            token.isDiscoverable() ||
            token.hasTag("nex_museum_ship") ||
            token.hasTag(Tags.CRYOSLEEPER) ||
            !token.isAlive() ||
            token.getMemoryWithoutUpdate().getBoolean(IGNORE_SALVAGEABLE_MEM_FLAG) ||
            token.getFullName().equals("Technology Cache") ||
            Utils.isInUnexploredSystem(token)
        );
    }

    @Override
    public String getCommMessageSound() {
        return "ui_discovered_entity";
    }

    @Override
    public IntelSortTier getSortTier() {
        return IntelSortTier.TIER_5;
    }

    /**
     * @param salvageValue estimated value of the salvage (ignored for ships)
     * @return number between 0 and 4
     */
    private int getValueRating(float salvageValue) {
        if (isShip()) {
            switch (variant.getHullSize()) {
                case FRIGATE:
                    return 1;
                case DESTROYER:
                    return 2;
                case CRUISER:
                    return 3;
                case CAPITAL_SHIP:
                    return 4;
                default:
                    return 0;
            }
        } else {
            if (salvageValue < 5000f) {
                return 1;
            } else if (salvageValue < 10000f) {
                return 2;
            } else if (salvageValue < 15000f) {
                return 3;
            } else if (salvageValue < 20000f) {
                return 4;
            } else {
                return 0;
            }
        }
    }
}
