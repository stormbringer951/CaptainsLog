package CaptainsLog.campaign.listeners;

import CaptainsLog.scripts.Utils;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;
import org.apache.log4j.Logger;

import java.util.Objects;

public class SettingsChangeListener implements LunaSettingsListener {

    private static final Logger log = Global.getLogger(SettingsChangeListener.class);
    @Override
    public void settingsChanged(String modId) {
        if (!modId.equals("CaptainsLog")) {
            return;
        }
        if (Global.getCurrentState() != GameState.CAMPAIGN) {
            return; // only need to refresh things in campaign context
        }

        // TODO: add check instead of assuming they have toggled some options
        Utils.tryCreateIntels(Global.getSector(), log);
    }
}
