package CaptainsLog;

import CaptainsLog.campaign.listeners.LocationChangeListener;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import CaptainsLog.campaign.listeners.RemovableSalvageListener;
import CaptainsLog.campaign.listeners.RuinsListener;
import CaptainsLog.scripts.CaptainsLogEveryFrame;

@SuppressWarnings("unused")
public class CaptainsLogModPlugin extends BaseModPlugin {
    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().addTransientScript(new CaptainsLogEveryFrame());

        // Histidine: make sure you don't add a new listener on each game load unless it's transient though
        Global.getSector().getListenerManager().addListener(new RemovableSalvageListener(), true);
        Global.getSector().getListenerManager().addListener(new RuinsListener(), true);
        Global.getSector().getListenerManager().addListener(new LocationChangeListener(), true);
    }
}