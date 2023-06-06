package CaptainsLog.scripts;

import CaptainsLog.Constants;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import lunalib.lunaSettings.LunaSettings;
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

        Integer hotkey = LunaSettings.getInt(Constants.MOD_ID, Constants.CUSTOM_LOG_CREATE_HOTKEY);
        if (!sector.getCampaignUI().isShowingDialog() && hotkey != null && hotkey != 0 && Keyboard.isKeyDown(hotkey)) {
            // Make sure to set a condition for your input checks to not accept numbers of 0, as lwjgl sets both unbound & unknown characters to that value.
            sector
                .getCampaignUI()
                .showInteractionDialog(new LogCreatorInteractionDialog(null), sector.getPlayerFleet());
        }
    }
}
