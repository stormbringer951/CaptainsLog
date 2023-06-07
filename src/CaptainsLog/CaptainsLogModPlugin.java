package CaptainsLog;

import CaptainsLog.campaign.intel.CustomMessageControlPanel;
import CaptainsLog.campaign.listeners.LocationChangeListener;
import CaptainsLog.campaign.listeners.RemovableSalvageListener;
import CaptainsLog.campaign.listeners.SettingsChangeListener;
import CaptainsLog.scripts.CaptainsLogEveryFrame;
import CaptainsLog.scripts.CaptainsLogKeydownDetector;
import CaptainsLog.scripts.RuinObserver;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;

public class CaptainsLogModPlugin extends BaseModPlugin {

    @Override
    public void onGameLoad(boolean newGame) {
        CustomMessageControlPanel.getInstance();

        Global.getSector().addTransientScript(new CaptainsLogEveryFrame());
        Global.getSector().addTransientScript(new RuinObserver());
        Global.getSector().addTransientScript(new CaptainsLogKeydownDetector());

        // Histidine: make sure you don't add a new listener on each game load unless it's transient though
        Global.getSector().getListenerManager().addListener(new RemovableSalvageListener(), true);
        Global.getSector().getListenerManager().addListener(new LocationChangeListener(), true);
    }

    @Override
    public void onApplicationLoad() throws Exception {
        LunaSettings.addSettingsListener(new SettingsChangeListener());
    }
}
