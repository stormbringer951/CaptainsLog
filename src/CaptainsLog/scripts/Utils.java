package CaptainsLog.scripts;

import CaptainsLog.SettingsUtils;
import CaptainsLog.campaign.intel.*;
import CaptainsLog.campaign.intel.automated.IntelValidityUtils;
import CaptainsLog.campaign.intel.automated.MegastructureIntel;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import org.apache.log4j.Logger;

public final class Utils {

    // This fixes a possible regression where a SectorEntityToken is not hidden but we don't have access to the map yet
    public static boolean isInUnexploredSystem(SectorEntityToken token) {
        if (token.getStarSystem() != null) {
            return !token.getStarSystem().isEnteredByPlayer();
        } else {
            return false;
        }
    }

    public static String getSystemNameOrHyperspaceBase(SectorEntityToken token) {
        if (token.getStarSystem() != null) {
            return token.getStarSystem().getBaseName();
        } else {
            return "Hyperspace";
        }
    }

    public static String getSystemNameOrHyperspace(SectorEntityToken token) {
        if (token.getStarSystem() != null) {
            return token.getStarSystem().getNameWithLowercaseType();
        }

        float maxRangeLY = 2f;

        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            float dist = Misc.getDistanceLY(token.getLocationInHyperspace(), system.getLocation());
            if (dist <= maxRangeLY) {
                return "Hyperspace near " + system.getName();
            }
        }

        return "Hyperspace";
    }

    public static int tryCreateMegastructureReports(List<SectorEntityToken> tokens, Logger log, boolean showMessage) {
        if (SettingsUtils.excludeMegastructures()) {
            return 0;
        }
        int count = 0;
        for (SectorEntityToken token : tokens) {
            if (tryCreateMegastructureReport(token, log, showMessage)) {
                count++;
            }
        }
        return count;
    }

    public static boolean tryCreateMegastructureReport(SectorEntityToken token, Logger log, boolean showMessage) {
        if (
            SettingsUtils.excludeMegastructures() ||
            IntelValidityUtils.isMegastructureInvalid(token) ||
            IntelValidityUtils.doesIntelAlreadyExist(token)
        ) {
            return false;
        }

        MegastructureIntel report = new MegastructureIntel(token);
        report.setNew(showMessage);
        Global.getSector().getIntelManager().addIntel(report, !showMessage);
        log.info("Created intel report for " + token.getName() + " in " + token.getStarSystem());

        return true;
    }

    public static int tryCreateCommRelayReports(List<SectorEntityToken> tokens, Logger log, boolean showMessage) {
        if (SettingsUtils.excludeCommRelays()) {
            return 0;
        }
        int count = 0;

        for (SectorEntityToken token : tokens) {
            if (tryCreateCommRelayReport(token, log, showMessage)) {
                ++count;
            }
        }

        return count;
    }

    public static boolean tryCreateCommRelayReport(SectorEntityToken token, Logger log, boolean showMessage) {
        if (SettingsUtils.excludeCommRelays()) {
            return false;
        }
        if (CommRelayIntel.intelShouldNotExist(token)) {
            return false;
        }
        if (IntelValidityUtils.doesIntelAlreadyExist(token)) {
            return false;
        }
        CommRelayIntel report = new CommRelayIntel(token);
        report.setNew(showMessage);
        Global.getSector().getIntelManager().addIntel(report, !showMessage);
        log.info("Created report for " + token.getFullName() + " in " + getSystemNameOrHyperspace(token));
        return true;
    }

    public static int tryCreateSalvageableReports(List<SectorEntityToken> tokens, Logger log, boolean showMessage) {
        if (SettingsUtils.excludeSalvageableReports()) {
            // early exit when called on startup or when settings change
            return 0;
        }
        int count = 0;

        for (SectorEntityToken salvageObject : tokens) {
            if (Utils.tryCreateSalvageableReport(salvageObject, log, showMessage)) {
                ++count;
            }
        }

        return count;
    }

    public static boolean tryCreateSalvageableReport(SectorEntityToken token, Logger log, boolean showMessage) {
        if (SettingsUtils.excludeSalvageableReports()) {
            // called in loop or singly on discovery
            return false;
        }
        if (SalvageableIntel.shouldRemoveIntelEntry(token)) {
            return false;
        }
        if (IntelValidityUtils.doesIntelAlreadyExist(token)) {
            return false;
        }
        SalvageableIntel report = new SalvageableIntel(token);
        report.setNew(showMessage);
        Global.getSector().getIntelManager().addIntel(report, !showMessage);
        log.info("Created report for " + token.getFullName() + " in " + getSystemNameOrHyperspace(token));
        return true;
    }

    public static int tryCreateUnsearchedRuinsReports(
        List<SectorEntityToken> entities,
        Logger log,
        boolean showMessage
    ) {
        if (SettingsUtils.excludeRuinsReports()) {
            return 0;
        }
        int count = 0;

        for (SectorEntityToken entity : entities) {
            MarketAPI market = entity.getMarket();
            if (market == null || market.getSurveyLevel() != MarketAPI.SurveyLevel.FULL) {
                continue;
            }
            if (tryCreateUnsearchedRuinsReport(entity, log, showMessage)) {
                ++count;
            }
        }

        return count;
    }

    public static boolean tryCreateUnsearchedRuinsReport(SectorEntityToken entity, Logger log, boolean showMessage) {
        if (SettingsUtils.excludeRuinsReports()) {
            return false;
        }
        if (RuinsIntel.shouldRemove(entity)) {
            return false; // not eligible
        }

        // TODO: refactor to memory key
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        for (IntelInfoPlugin intel : intelManager.getIntel(RuinsIntel.class)) {
            RuinsIntel r = (RuinsIntel) intel;
            if (r.getEntity() == entity) {
                return false; // report exists
            }
        }

        RuinsIntel report = new RuinsIntel(entity);
        report.setNew(showMessage);
        intelManager.addIntel(new RuinsIntel(entity), !showMessage);
        log.info("Created intel report for " + entity.getName() + " in " + getSystemNameOrHyperspaceBase(entity));
        return true;
    }

    public static void tryCreateIntels(SectorAPI sector, Logger log) {
        int count = Utils.tryCreateUnsearchedRuinsReports(sector.getEntitiesWithTag(Tags.PLANET), log, false);
        count += Utils.tryCreateSalvageableReports(sector.getEntitiesWithTag(Tags.SALVAGEABLE), log, false);
        count += Utils.tryCreateMegastructureReports(sector.getEntitiesWithTag(Tags.CRYOSLEEPER), log, false);
        count += Utils.tryCreateCommRelayReports(sector.getCustomEntitiesWithTag(Tags.COMM_RELAY), log, false);

        if (count > 0) {
            sector
                .getCampaignUI()
                .addMessage(
                    "Captain's Log added " + count + " new intel entries",
                    Misc.getTextColor(),
                    Integer.toString(count),
                    "",
                    Misc.getHighlightColor(),
                    Misc.getHighlightColor()
                );
        }
    }
}
