package CaptainsLog.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import org.lwjgl.input.Keyboard;

public class CaptainsLogKeydownDetector implements EveryFrameScript {

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        final SectorAPI sector = Global.getSector();

        if (!sector.getCampaignUI().isShowingDialog()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
                // TODO: find/set unused key
                Global
                    .getSector()
                    .getCampaignUI()
                    .showInteractionDialog(new LogCreatorInteractionDialog(), Global.getSector().getPlayerFleet());
            }
        }
    }
}
