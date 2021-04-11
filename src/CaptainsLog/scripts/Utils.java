package CaptainsLog.scripts;

import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;

public final class Utils {
    // This fixes a possible regression where a SectorEntityToken is not hidden but we don't have access to the map yet
    public static boolean isInUnexploredSystem(SectorEntityToken token) {
        LocationAPI loc = token.getContainingLocation();
        if (loc instanceof StarSystemAPI) {
            StarSystemAPI system = (StarSystemAPI) loc;
            return !system.isEnteredByPlayer();
        } else {
            return false;
        }
    }
}
