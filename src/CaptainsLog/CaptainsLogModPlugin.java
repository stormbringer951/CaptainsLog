package CaptainsLog;

import CaptainsLog.campaign.listeners.LocationChangeListener;
import CaptainsLog.campaign.listeners.RemovableSalvageListener;
import CaptainsLog.scripts.CaptainsLogEveryFrame;
import CaptainsLog.scripts.RuinObserver;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

public class CaptainsLogModPlugin extends BaseModPlugin {

    @Override
    public void onGameLoad(boolean newGame) {
        if (!Global.getSettings().getBoolean("captains_log_enable_automatic_logging")) {
            return;
        }
        Global.getSector().addTransientScript(new CaptainsLogEveryFrame());
        Global.getSector().addTransientScript(new RuinObserver());

        // Histidine: make sure you don't add a new listener on each game load unless it's transient though
        Global.getSector().getListenerManager().addListener(new RemovableSalvageListener(), true);
        Global.getSector().getListenerManager().addListener(new LocationChangeListener(), true);
    }
}
