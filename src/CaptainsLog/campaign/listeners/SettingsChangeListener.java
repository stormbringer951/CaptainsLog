package CaptainsLog.campaign.listeners;

import CaptainsLog.scripts.Utils;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettingsListener;

public class SettingsChangeListener implements LunaSettingsListener {

    @Override
    public void settingsChanged(String modId) {
        if (!modId.equals("CaptainsLog")) {
            return;
        }
        if (Global.getCurrentState() != GameState.CAMPAIGN) {
            return; // only need to refresh things in campaign context
        }
        // TODO: add check instead of assuming they have toggled some options
        Utils.tryCreateIntels();
    }
}
