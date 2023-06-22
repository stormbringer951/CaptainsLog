package CaptainsLog.campaign.listeners;

import CaptainsLog.scripts.Utils;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.listeners.CurrentLocationChangedListener;
import com.fs.starfarer.api.impl.campaign.ids.Tags;

public class LocationChangeListener implements CurrentLocationChangedListener {

    public void reportCurrentLocationChanged(LocationAPI prev, LocationAPI curr) {
        if (curr instanceof StarSystemAPI) {
            // this code is very awkward; reportCurrentLocationChanged() runs before CoreScript::markSystemAsEntered(). I don't think there are side effects but a regression is possible
            ((StarSystemAPI) curr).setEnteredByPlayer(true);
            // find unclaimed Domain or third-party faction comm relays in unexplored systems
            Utils.tryCreateCommRelayReports(curr.getEntitiesWithTag(Tags.COMM_RELAY), true);
        }
        if (prev instanceof StarSystemAPI) {
            // leaving systems should add player-built comm relays or comm relays created by other means
            Utils.tryCreateCommRelayReports(prev.getEntitiesWithTag(Tags.COMM_RELAY), true);
        }
        Utils.tryCreateMegastructureReports(curr.getEntitiesWithTag(Tags.CRYOSLEEPER), true);
        Utils.tryCreateUnexploredRuinsReports(curr.getEntitiesWithTag(Tags.PLANET), true);
        Utils.tryCreateSalvageableReports(curr.getEntitiesWithTag(Tags.SALVAGEABLE), false);
        Utils.removeFleetLogIntel(false);
    }
}
