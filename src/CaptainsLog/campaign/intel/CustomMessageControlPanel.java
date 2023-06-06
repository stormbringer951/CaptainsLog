package CaptainsLog.campaign.intel;

import CaptainsLog.Constants;
import CaptainsLog.ui.button.OpenLogCreationDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import java.text.MessageFormat;
import java.util.Set;

public class CustomMessageControlPanel extends BaseIntel {

    public static CustomMessageControlPanel getInstance() {
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        IntelInfoPlugin intel = intelManager.getFirstIntel(CustomMessageControlPanel.class);
        if (!(intel instanceof CustomMessageControlPanel)) {
            intel = new CustomMessageControlPanel();
            intelManager.addIntel(intel, true);
        }
        return (CustomMessageControlPanel) intel;
    }

    private CustomMessageControlPanel() {}

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        int numberOfEntries = Global.getSector().getIntelManager().getIntel(CustomMessageIntel.class).size();
        info.addTitle("Captain's Logbook", getTitleColor(mode));
        info.addPara(
            MessageFormat.format(
                "There {0, choice, 0#are 0 entries|1#is 1 entry|1<are {0, number, integer} entries} in your logbook.",
                numberOfEntries
            ),
            4,
            getBulletColorForMode(mode),
            Misc.getHighlightColor(),
            String.valueOf(numberOfEntries)
        );
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        int numberOfEntries = Global.getSector().getIntelManager().getIntel(CustomMessageIntel.class).size();
        info.addPara(
                MessageFormat.format(
                        "There {0, choice, 0#are 0 entries|1#is 1 entry|1<are {0, number, integer} entries} in your logbook.",
                        numberOfEntries
                ),
                4,
                Misc.getTextColor(),
                Misc.getHighlightColor(),
                String.valueOf(numberOfEntries)
        );

        addGenericButton(info, width, new OpenLogCreationDialog(this));
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "captains_log");
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Constants.CUSTOM_MESSAGE_INTEL_TAG);
        return tags;
    }

    @Override
    public IntelSortTier getSortTier() {
        return IntelSortTier.TIER_0;
    }

    @Override
    public SectorEntityToken getEntity() {
        return null;
    }

    @Override
    public String getSmallDescriptionTitle() {
        return "Captain's Logbook";

    }
}
