package CaptainsLog.campaign.listeners;

import CaptainsLog.scripts.Utils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.listeners.DiscoverEntityListener;
import org.apache.log4j.Logger;

public class RemovableSalvageListener implements DiscoverEntityListener {

    private static final Logger log = Global.getLogger(RemovableSalvageListener.class);

    @Override
    public void reportEntityDiscovered(SectorEntityToken entity) {
        Utils.tryCreateMegastructureReport(entity, log, false);
        Utils.tryCreateSalvageableReport(entity, log, false);
    }
}
