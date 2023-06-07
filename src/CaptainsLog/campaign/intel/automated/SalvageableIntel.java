package CaptainsLog.campaign.intel.automated;

import CaptainsLog.Constants;
import CaptainsLog.SettingsUtils;
import CaptainsLog.scripts.Utils;
import CaptainsLog.ui.button.IgnoreSalvage;
import CaptainsLog.ui.button.LayInCourse;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.*;
import java.util.Set;

public abstract class SalvageableIntel extends AutomatedIntel {

    public SalvageableIntel(SectorEntityToken token) {
        super(token);
    }

    protected abstract void addSalvageableSpecificInfo(TooltipMakerAPI info, float pad, ListInfoMode mode);

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
        addSalvageableSpecificInfo(info, initPad, mode);

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
            // TooltipMakerAPI text = info.beginImageWithText(getImage(), 48, 48, true);
            TooltipMakerAPI text = info.beginImageWithText(getImage(), 48, width, true);
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
    protected abstract String getName();

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

    protected abstract boolean hasImage();

    protected abstract String getImage();

    protected abstract Description getDesc();

    @Override
    public abstract String getSmallDescriptionTitle();

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

    @Override
    public void endAfterDelay(float days) {
        getMapLocation(null).getMemoryWithoutUpdate().unset(Constants.CAPTAINS_LOG_MEMORY_KEY);
        super.endAfterDelay(days);
    }
}
