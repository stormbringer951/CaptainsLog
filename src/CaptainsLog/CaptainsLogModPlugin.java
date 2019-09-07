package CaptainsLog;

import CaptainsLog.campaign.intel.CustomMessageIntel;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import CaptainsLog.campaign.listeners.RemovableSalvageListener;
import CaptainsLog.campaign.listeners.RuinsListener;
import CaptainsLog.scripts.CaptainsLogEveryFrame;

public class CaptainsLogModPlugin extends BaseModPlugin {
    @Override
    public void onEnabled(boolean wasEnabledBefore) {
        if (!wasEnabledBefore) {
            Global.getSector().getIntelManager().addIntel(new CustomMessageIntel("To create new captain's logs, " +
                    "please open the console from the Console Commands mod, and type CaptainsLog [your message] to" +
                    " create new log entries."));
        }
    }

    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().addTransientScript(new CaptainsLogEveryFrame());

        // Histidine: make sure you don't add a new listener on each game load unless it's transient though
        Global.getSector().getListenerManager().addListener(new RemovableSalvageListener(), true);
        Global.getSector().getListenerManager().addListener(new RuinsListener(), true);
    }
}