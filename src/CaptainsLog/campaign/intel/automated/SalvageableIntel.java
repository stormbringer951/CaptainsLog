package CaptainsLog.campaign.intel.automated;

import CaptainsLog.Constants;
import CaptainsLog.SettingsUtils;
import CaptainsLog.scripts.Utils;
import CaptainsLog.ui.button.IgnoreSalvage;
import CaptainsLog.ui.button.LayInCourse;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

public class SalvageableIntel extends AutomatedIntel {

    private static final Logger log = Global.getLogger(SalvageableIntel.class);
    private final ShipVariantAPI variant;

    public SalvageableIntel(SectorEntityToken token) {
        super(token);
        // entity.getCustomPlugin() instanceof DerelictShipEntityPlugin ||
        // Entities.WRECK.equals(type)

        if (token.getCustomPlugin() instanceof DerelictShipEntityPlugin) {
            DerelictShipEntityPlugin p = (DerelictShipEntityPlugin) token.getCustomPlugin();
            variant = Global.getSettings().getVariant(p.getData().ship.variantId);
        } else {
            variant = null;
        }

        float salvageValue = estimateSalvageValue();
        int rating = getValueRating(salvageValue);

        log.info("Adding intel for new " + getName() + ". Sort value: " + salvageValue + " (" + rating + ")");
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);

        String title = getName();
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
            String hullSizeString = Misc.getHullSizeStr(variant.getHullSpec().getHullSize());
            info.addPara(
                hullSizeString + "-sized vessel",
                initPad,
                getBulletColorForMode(mode),
                Misc.getHighlightColor(),
                hullSizeString
            );
        }

        info.addPara(
            Utils.getSystemNameOrHyperspace(token),
            initPad,
            getBulletColorForMode(mode),
            Misc.getHighlightColor(),
            Utils.getSystemNameOrHyperspaceBase(token)
        );

        int distanceLY = Math.round(Misc.getDistanceToPlayerLY(token));
        info.addPara(
            distanceLY + " light years away",
            initPad,
            getBulletColorForMode(mode),
            Misc.getHighlightColor(),
            Integer.toString(distanceLY)
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
            "Location: " + Utils.getSystemNameOrHyperspace(token) + ".",
            opad,
            Misc.getHighlightColor(),
            Utils.getSystemNameOrHyperspaceBase(token)
        );

        if (!isEnding()) {
            addGenericButton(info, width, new LayInCourse(token));
            addGenericButton(info, width, new IgnoreSalvage(this));
        }
    }

    @Override
    protected String getName() {
        String name = Constants.SALVAGE_STELNET_INTEL_TYPE_SUBSTRING + " ";
        if (isShip()) {
            // Misc.getHullSizeStr(variant.getHullSpec().getHullSize())
            name += variant.getHullSpec().getHullName() + " " + variant.getHullSpec().getDesignation();
        } else {
            name += token.getFullName();
        }
        return name;
    }

    private boolean isShip() {
        return variant != null;
    }

    @Override
    public String getIcon() {
        return token.getCustomEntitySpec().getIconName();
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        if (SettingsUtils.isStelnetEnabled()) {
            tags.add(Constants.STELNET_FILTERED_INTEL_TAG);
        } else {
            tags.add(Constants.SALVAGEABLE_INTEL_TAG);
        }
        return tags;
    }

    private float estimateSalvageValue() {
        float value = 0;

        SalvageEntityGenDataSpec salvageSpec = (SalvageEntityGenDataSpec) Global
            .getSettings()
            .getSpec(SalvageEntityGenDataSpec.class, token.getCustomEntityType(), true);

        if (salvageSpec != null) {
            value += salvageToValue(salvageSpec.getDropValue(), salvageSpec.getDropRandom());
        }
        value += salvageToValue(token.getDropValue(), token.getDropRandom());
        if (isShip()) {
            value += variant.getHullSpec().getBaseValue();
        }
        return value;
    }

    private float salvageToValue(
        List<SalvageEntityGenDataSpec.DropData> dropValue,
        List<SalvageEntityGenDataSpec.DropData> dropRandom
    ) {
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
            return token.getCustomEntitySpec().getSpriteName();
        }
    }

    private Description getDesc() {
        if (isShip()) {
            return Global.getSettings().getDescription(variant.getHullSpec().getDescriptionId(), Description.Type.SHIP);
        } else {
            return Global.getSettings().getDescription(token.getCustomDescriptionId(), Description.Type.CUSTOM);
        }
    }

    @Override
    public String getSmallDescriptionTitle() {
        if (isShip()) {
            return Constants.SALVAGE_STELNET_INTEL_TYPE_SUBSTRING + " Ship";
        } else {
            return Constants.SALVAGE_STELNET_INTEL_TYPE_SUBSTRING + " " + token.getFullName();
        }
    }

    @Override
    public boolean shouldRemoveIntel() {
        return IntelValidityUtils.isSalvageableIntelInvalid(token);
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

    @Override
    public void endAfterDelay(float days) {
        getMapLocation(null).getMemoryWithoutUpdate().unset(Constants.CAPTAINS_LOG_MEMORY_KEY);
        super.endAfterDelay(days);
    }
}