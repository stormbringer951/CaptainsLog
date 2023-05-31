package CaptainsLog.campaign.listeners;

import CaptainsLog.scripts.Utils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.listeners.CurrentLocationChangedListener;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import org.apache.log4j.Logger;

public class LocationChangeListener implements CurrentLocationChangedListener {

    private static final Logger log = Global.getLogger(LocationChangeListener.class);

    public void reportCurrentLocationChanged(LocationAPI prev, LocationAPI curr) {
        if (curr instanceof StarSystemAPI) {
            ((StarSystemAPI) curr).setEnteredByPlayer(true);
            Utils.tryCreateCommRelayReports(curr.getEntitiesWithTag(Tags.COMM_RELAY), log, true);
        }
        Utils.tryCreateCryosleeperReports(curr.getEntitiesWithTag(Tags.CRYOSLEEPER), log, true);
        Utils.tryCreateUnsearchedRuinsReports(curr.getEntitiesWithTag(Tags.PLANET), log, true);
        Utils.tryCreateSalvageableReports(curr.getEntitiesWithTag(Tags.SALVAGEABLE), log, false);
    }
}
