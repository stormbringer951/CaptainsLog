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
            // this code is very awkward; reportCurrentLocationChanged() runs before CoreScript::markSystemAsEntered(). I don't think there are side effects but a regression is possible
            ((StarSystemAPI) curr).setEnteredByPlayer(true);
            // find unclaimed Domain or third-party faction comm relays in unexplored systems
            Utils.tryCreateCommRelayReports(curr.getEntitiesWithTag(Tags.COMM_RELAY), log, true);
        }
        if (prev instanceof StarSystemAPI) {
            // leaving systems should add player-built comm relays or comm relays created by other means
            Utils.tryCreateCommRelayReports(prev.getEntitiesWithTag(Tags.COMM_RELAY), log, true);
        }
        Utils.tryCreateCryosleeperReports(curr.getEntitiesWithTag(Tags.CRYOSLEEPER), log, true);
        Utils.tryCreateUnsearchedRuinsReports(curr.getEntitiesWithTag(Tags.PLANET), log, true);
        Utils.tryCreateSalvageableReports(curr.getEntitiesWithTag(Tags.SALVAGEABLE), log, false);
    }
}
