package CaptainsLog.campaign.intel;

import CaptainsLog.Constants;
import CaptainsLog.SettingsUtils;
import CaptainsLog.campaign.intel.button.LayInCourse;
import CaptainsLog.scripts.Utils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.loading.Description.Type;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.Set;

public class UnremovableIntel extends BaseIntel {

    private final SectorEntityToken cryosleeper;

    public UnremovableIntel(SectorEntityToken cryosleeper) {
        this.cryosleeper = cryosleeper;
        getMapLocation(null).getMemoryWithoutUpdate().set(CAPTAINS_LOG_MEMORY_KEY, true);
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);
        info.addPara(getName(), c, 0f);

        float initPad;
        if (mode == ListInfoMode.IN_DESC) {
            initPad = 10f;
        } else {
            initPad = 3f;
        }

        bullet(info);
        info.addPara(
            cryosleeper.getStarSystem().getName(),
            initPad,
            getBulletColorForMode(mode),
            Misc.getHighlightColor(),
            Utils.getSystemNameOrHyperspaceBase(cryosleeper)
        );
        unindent(info);
    }

    public String getSmallDescriptionTitle() {
        return "Colony " + Constants.MEGASTRUCTURE_STELNET_INTEL_TYPE_SUBSTRING;
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;

        Description desc = Global.getSettings().getDescription("derelict_cryosleeper", Type.CUSTOM);

        TooltipMakerAPI text = info.beginImageWithText(cryosleeper.getCustomEntitySpec().getSpriteName(), 64);
        text.addPara(desc.getText1FirstPara(), Misc.getGrayColor(), opad);
        info.addImageWithText(opad);

        info.addPara(
            "Located in the " + cryosleeper.getStarSystem().getNameWithLowercaseType() + ".",
            opad,
            Misc.getHighlightColor(),
            cryosleeper.getStarSystem().getBaseName()
        );

        addGenericButton(info, width, new LayInCourse(cryosleeper));
    }

    @Override
    public String getIcon() {
        return cryosleeper.getCustomEntitySpec().getIconName();
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Constants.MEGASTRUCTURE_INTEL_TAG);
        return tags;
    }

    @Override
    public FactionAPI getFactionForUIColors() {
        return super.getFactionForUIColors();
    }

    @Override
    protected String getName() {
        return "Derelict Cryosleeper Location";
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return getEntity();
    }

    @Override
    public boolean shouldRemoveIntel() {
        boolean shouldRemove = cryosleeper == null || !cryosleeper.isAlive() || SettingsUtils.excludeMegastructures();
        if (shouldRemove) {
            setHidden(true);
            getMapLocation(null).getMemoryWithoutUpdate().unset(CAPTAINS_LOG_MEMORY_KEY);
        }
        return shouldRemove;
    }

    @Override
    public String getCommMessageSound() {
        return "ui_discovered_entity";
    }

    @Override
    public SectorEntityToken getEntity() {
        return cryosleeper;
    }

    @Override
    public IntelSortTier getSortTier() {
        return IntelSortTier.TIER_6;
    }
}
