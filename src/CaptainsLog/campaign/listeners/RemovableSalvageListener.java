package CaptainsLog.campaign.listeners;

import CaptainsLog.scripts.Utils;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.listeners.DiscoverEntityListener;

public class RemovableSalvageListener implements DiscoverEntityListener {

    @Override
    public void reportEntityDiscovered(SectorEntityToken entity) {
        Utils.tryCreateMegastructureReport(entity, false);
        Utils.tryCreateSalvageableReport(entity, false);
    }
}
