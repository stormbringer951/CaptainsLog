package CaptainsLog.campaign.intel;

import CaptainsLog.Constants;
import CaptainsLog.ui.ButtonRow;
import CaptainsLog.ui.TextArea;
import CaptainsLog.ui.button.ClearAll;
import CaptainsLog.ui.button.CreateLogEntry;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TextFieldAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.text.MessageFormat;
import java.util.Set;

public class FleetLogIntel extends BaseIntel {

    public static FleetLogIntel getInstance() {
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        IntelInfoPlugin intel = intelManager.getFirstIntel(FleetLogIntel.class);
        if (!(intel instanceof FleetLogIntel)) {
            intel = new FleetLogIntel();
            intelManager.addIntel(intel, true);
        }
        return (FleetLogIntel) intel;
    }

    private FleetLogIntel() {}

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        int numberOfEntries = Global.getSector().getIntelManager().getIntel(CustomMessageIntel.class).size();
        info.addTitle("Fleet Logbook", getTitleColor(mode));
        info.addPara(
            MessageFormat.format(
                "There {0, choice, 0#are 0 entries|1#is 1 entry|1<are {0, number, integer} entries} in the logbook",
                numberOfEntries
            ),
            4,
            getBulletColorForMode(mode),
            Misc.getHighlightColor(),
            String.valueOf(numberOfEntries)
        );
    }

    @Override
    public void createLargeDescription(CustomPanelAPI panel, float width, float height) {
        width -= 10; // give 10px padding on the right
        float pad = 5;
        float titleHeight = 28;
        float buttonRowHeight = 30;
        float bodyHeight = height - titleHeight - pad - buttonRowHeight - pad;
//        TextArea title = new TextArea(width, titleHeight, pad);
//        TextArea body = new TextArea(width, bodyHeight, pad, new FleetLogPanelPlugin());
//        title.setFont(Fonts.ORBITRON_16);
//        TextFieldAPI titleField = title.render(panel, 0, 0);
//        TextFieldAPI bodyField = body.render(panel, 0, titleHeight + pad);
//        ButtonRow buttonRow = new ButtonRow(
//            width,
//            buttonRowHeight,
//            new CreateLogEntry(titleField, bodyField),
//            new ClearAll(titleField, bodyField)
//        );
//        buttonRow.render(panel, 0, height - buttonRow.getHeight() - 4 * pad);
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "fleet_log");
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
    public boolean hasLargeDescription() {
        return true;
    }

    @Override
    public boolean hasSmallDescription() {
        return false;
    }

    @Override
    public boolean isImportant() {
        // for now, so it's always accessible
        return true;
    }

    @Override
    public SectorEntityToken getEntity() {
        return null;
    }
}
