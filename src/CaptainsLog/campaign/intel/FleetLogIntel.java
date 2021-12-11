package CaptainsLog.campaign.intel;

import CaptainsLog.ui.TextArea;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

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
        info.addTitle("Fleet Logbook", getTitleColor(mode));
        info.addPara("There are no entries in the logbook", getBulletColorForMode(mode), 4);
    }

    @Override
    public void createLargeDescription(CustomPanelAPI panel, float width, float height) {
        width -= 10; // give 10px padding on the right
        float titleHeight = 28;
        float pad = 2;
        TextArea title = new TextArea(width, titleHeight, pad);
        title.setFont(Fonts.ORBITRON_16);
        TextArea body = new TextArea(width, height - titleHeight - 2 * pad, pad);
        body.setPlugin(new FleetLogPanelPlugin());
        title.render(panel, 0, 0);
        body.render(panel, 0, titleHeight + pad);
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
}
