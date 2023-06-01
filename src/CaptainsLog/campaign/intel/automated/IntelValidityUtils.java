package CaptainsLog.campaign.intel.automated;

import CaptainsLog.Constants;
import CaptainsLog.SettingsUtils;
import CaptainsLog.scripts.Utils;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Misc;

public class IntelValidityUtils {

    public static boolean doesIntelAlreadyExist(SectorEntityToken token) {
        return token.getMemoryWithoutUpdate().getBoolean(Constants.CAPTAINS_LOG_MEMORY_KEY);
    }

    private static boolean objectDoesntExist(SectorEntityToken token) {
        return token == null || !token.isAlive();
    }

    private static boolean shouldIgnoreRuins(SectorEntityToken token) {
        return token.getMemoryWithoutUpdate().getBoolean(Constants.IGNORE_RUINS_MEM_FLAG);
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

    public static boolean isRuinsIntelInvalid(SectorEntityToken token) {
        if (SettingsUtils.excludeRuinsReports() || shouldIgnoreRuins(token) || Utils.isInUnexploredSystem(token)) {
            return true;
        }
        MarketAPI market = token.getMarket();
        if (!Misc.hasUnexploredRuins(market)) {
            return true;
        }
        return market.getName().equals("Praetorium"); // Sylphon world; custom interaction dialogue prevents exploring
    }

    public static boolean areRuinsDiscovered(SectorEntityToken token) {
        MarketAPI market = token.getMarket();
        if (market == null) {
            return false;
        }
        boolean playerActuallySurveyed = market.getSurveyLevel() == MarketAPI.SurveyLevel.FULL;
        boolean observedFromOrbitalWreckage = token.hasTag(Constants.PROXIMITY_SURVEYED_RUINS); // tagged from Observer

        return playerActuallySurveyed || observedFromOrbitalWreckage;
    }
}
