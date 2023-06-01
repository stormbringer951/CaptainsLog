package CaptainsLog.campaign.intel.automated;

import CaptainsLog.Constants;
import CaptainsLog.SettingsUtils;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Tags;

public class IntelValidityUtils {

    public static boolean doesIntelAlreadyExist(SectorEntityToken token) {
        return token.getMemoryWithoutUpdate().getBoolean(Constants.CAPTAINS_LOG_MEMORY_KEY);
    }

    private static boolean objectDoesntExist(SectorEntityToken token) {
        return token == null || !token.isAlive();
    }

    private static boolean objectNotFoundYet(SectorEntityToken token) {
        return token.hasSensorProfile() || token.isDiscoverable();
    }

    public static boolean isMegastructureInvalid(SectorEntityToken token) {
        if (SettingsUtils.excludeMegastructures() || objectDoesntExist(token) || objectNotFoundYet(token)) {
            return true;
        }
        return !token.hasTag(Tags.CRYOSLEEPER) && !token.hasTag(Tags.CORONAL_TAP);
    }
}
