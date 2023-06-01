package CaptainsLog.campaign.intel.automated;

import CaptainsLog.Constants;
import CaptainsLog.SettingsUtils;
import CaptainsLog.scripts.Utils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.econ.CommRelayCondition;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.*;
import java.util.Set;

public class CommRelayIntel extends AutomatedIntel {

    public CommRelayIntel(SectorEntityToken token) {
        super(token);
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
            token.getStarSystem().getName(),
            initPad,
            getBulletColorForMode(mode),
            Misc.getHighlightColor(),
            Utils.getSystemNameOrHyperspaceBase(token)
        );
        unindent(info);
    }

    public String getSmallDescriptionTitle() {
        return Constants.STELNET_INTEL_TYPE_SUBSTRING_COMM_RELAY;
    }

    private float getBonus() {
        if (token.hasTag(Tags.MAKESHIFT)) {
            return CommRelayCondition.MAKESHIFT_COMM_RELAY_BONUS;
        } else {
            return CommRelayCondition.COMM_RELAY_BONUS;
        }
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;

        Description desc = Global.getSettings().getDescription(token.getCustomDescriptionId(), Description.Type.CUSTOM);
        for (String para : desc.getText1Paras()) {
            info.addPara(para, opad);
        }

        bullet(info);
        String modifier = "+" + Math.round(getBonus());
        info.addPara(
            modifier + " stability for same-faction colonies in system",
            opad,
            Misc.getHighlightColor(),
            modifier
        );
        unindent(info);

        info.addPara(
            "This comm relay is located in the " + token.getStarSystem().getNameWithLowercaseType() + ".",
            opad,
            Misc.getHighlightColor(),
            token.getStarSystem().getBaseName()
        );
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
            tags.add(Constants.COMM_RELAY_INTEL_TAG);
        }
        return tags;
    }

    @Override
    public boolean shouldRemoveIntel() {
        return IntelValidityUtils.isCommRelayIntelInvalid(token);
    }

    @Override
    public IntelSortTier getSortTier() {
        return IntelSortTier.TIER_6;
    }

    @Override
    public String getSortString() {
        return "zzz" + getName() + token.getStarSystem().getBaseName(); // Put all of these together towards the back of the tier
    }
}
