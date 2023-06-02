package CaptainsLog.campaign.intel.automated;

import CaptainsLog.Constants;
import CaptainsLog.scripts.Utils;
import CaptainsLog.ui.button.LayInCourse;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.loading.Description.Type;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.Set;

public class MegastructureIntel extends AutomatedIntel {

    public MegastructureIntel(SectorEntityToken token) {
        super(token);
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, IntelInfoPlugin.ListInfoMode mode) {
        Color c = getTitleColor(mode);
        info.addPara(getName(), c, 0f);

        float initPad;
        if (mode == IntelInfoPlugin.ListInfoMode.IN_DESC) {
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

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        String descriptionId = token.getCustomEntitySpec().getCustomDescriptionId();
        Description desc = Global.getSettings().getDescription(descriptionId, Type.CUSTOM);
        info.addImage(token.getCustomEntitySpec().getInteractionImage(), width, opad);

        // TooltipMakerAPI text = info.beginImageWithText(token.getCustomEntitySpec().getSpriteName(), 64);
        info.addPara(desc.getText1(), Misc.getTextColor(), opad);
        // info.addImageWithText(opad);

        info.addPara(
            "Located in the " + token.getStarSystem().getNameWithLowercaseType() + ".",
            opad,
            Misc.getHighlightColor(),
            token.getStarSystem().getBaseName()
        );
        addGenericButton(info, width, new LayInCourse(token));
    }

    public String getSmallDescriptionTitle() {
        return "Colony " + Constants.MEGASTRUCTURE_STELNET_INTEL_TYPE_SUBSTRING;
    }

    @Override
    public boolean shouldRemoveIntel() {
        return IntelValidityUtils.isMegastructureIntelInvalid(token);
    }

    @Override
    public String getIcon() {
        return token.getCustomEntitySpec().getIconName();
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Constants.MEGASTRUCTURE_INTEL_TAG);
        return tags;
    }

    @Override
    public IntelInfoPlugin.IntelSortTier getSortTier() {
        return IntelInfoPlugin.IntelSortTier.TIER_6;
    }

    @Override
    public String getSortString() {
        // sort by name first and then distance
        return getName() + super.getSortString();
    }
}
