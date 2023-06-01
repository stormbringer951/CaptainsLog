package CaptainsLog.campaign.intel.automated;

import CaptainsLog.Constants;
import CaptainsLog.SettingsUtils;
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

    private static boolean objectNotFoundYet(SectorEntityToken token) {
        return token.hasSensorProfile() || token.isDiscoverable();
    }

    private static boolean isCoreWorld(SectorEntityToken token) {
        return !token.getStarSystem().isInConstellation(); // assume all systems without constellation are inhabited core worlds;
    }

    public static boolean isCommRelayIntelInvalid(SectorEntityToken token) {
        return (
            SettingsUtils.excludeCommRelays() ||
            objectDoesntExist(token) ||
            isInUnexploredSystem(token) ||
            isCoreWorld(token)
        );
    }

    public static boolean isMegastructureIntelInvalid(SectorEntityToken token) {
        return (
            SettingsUtils.excludeMegastructures() ||
            objectDoesntExist(token) ||
            objectNotFoundYet(token) ||
            (!token.hasTag(Tags.CRYOSLEEPER) && !token.hasTag(Tags.CORONAL_TAP))
        );
    }

    public static boolean isRuinsIntelInvalid(SectorEntityToken token) {
        return (
            SettingsUtils.excludeRuinsReports() ||
            token.getMemoryWithoutUpdate().getBoolean(Constants.IGNORE_RUINS_MEM_FLAG) ||
            isInUnexploredSystem(token) ||
            !Misc.hasUnexploredRuins(token.getMarket()) ||
            token.getMarket().getName().equals("Praetorium") // Sylphon; custom interaction dialogue prevents exploring
        );
    }

    public static boolean isSalvageableIntelInvalid(SectorEntityToken token) {
        return (
            SettingsUtils.excludeSalvageableReports() ||
            objectDoesntExist(token) ||
            objectNotFoundYet(token) ||
            isInUnexploredSystem(token) ||
            !token.hasTag(Tags.SALVAGEABLE) ||
            token.getMemoryWithoutUpdate().getBoolean(Constants.IGNORE_SALVAGEABLE_MEM_FLAG) ||
            token.hasTag("nex_museum_ship") ||
            token.getFullName().equals("Technology Cache")
        );
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

    // This fixes a possible regression where a SectorEntityToken is not hidden but we don't have access to the map yet
    private static boolean isInUnexploredSystem(SectorEntityToken token) {
        return token.getStarSystem() != null && !token.getStarSystem().isEnteredByPlayer();
    }
}
