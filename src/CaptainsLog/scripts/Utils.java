package CaptainsLog.scripts;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Misc;

import org.apache.log4j.Logger;

import CaptainsLog.campaign.intel.RuinsIntelv2;
import CaptainsLog.campaign.intel.SalvageableIntel;
import CaptainsLog.campaign.intel.UnremovableIntel;

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

    public static int tryCreateCryosleeperReports(List<SectorEntityToken> tokens, Logger log, boolean showMessage) {
        int count = 0;
        for (SectorEntityToken token : tokens) {
            if (tryCreateCryosleeperReport(token, log, showMessage)) {
                count++;
            }
        }
        return count;
    }

    public static boolean tryCreateCryosleeperReport(SectorEntityToken cryosleeper, Logger log, boolean showMessage) {
        if (!cryosleeper.hasTag(Tags.CRYOSLEEPER) || cryosleeper.hasSensorProfile() || cryosleeper.isDiscoverable()) {
            return false; // not a discovered cryosleeper
        }

        // TODO: refactor to memory key
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        for (IntelInfoPlugin intel : intelManager.getIntel(UnremovableIntel.class)) {
            UnremovableIntel cs = (UnremovableIntel) intel;
            if (cs.getEntity() == cryosleeper) {
                return false; // report exists
            }
        }


        UnremovableIntel report = new UnremovableIntel(cryosleeper);
        report.setNew(showMessage);
        intelManager.addIntel(report, !showMessage);
        log.info("Created intel report for cryosleeper in " + cryosleeper.getStarSystem());

        return true;
    }

    public static int tryCreateSalvageableReports(List<SectorEntityToken> tokens, Logger log, boolean showMessage) {
        int count = 0;

        for (SectorEntityToken salvageObject : tokens) {
            if (Utils.tryCreateSalvageableReport(salvageObject, log, showMessage)) {
                ++count;
            }
        }

        return count;
    }

    public static boolean tryCreateSalvageableReport(SectorEntityToken token, Logger log, boolean showMessage) {
        if (SalvageableIntel.shouldRemoveIntelEntry(token)) {
            return false;
        }

        // TODO: refactor to memory key
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        for (IntelInfoPlugin i : intelManager.getIntel(SalvageableIntel.class)) {
            SalvageableIntel rs = (SalvageableIntel) i;
            if (rs.getEntity() == token) {
                return false;
            }
        }

        SalvageableIntel report = new SalvageableIntel(token);
        report.setNew(showMessage);
        intelManager.addIntel(report, !showMessage);
        log.info("Created report for " + token.getFullName() + " in " + getSystemNameOrHyperspace(token));
        return true;
    }

    public static int tryCreateUnsearchedRuinsReports(List<SectorEntityToken> entities, Logger log, boolean showMessage) {
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
        if (RuinsIntelv2.doesNotHaveUnexploredRuins(entity)) {
            return false; // not eligible
        }

        // TODO: refactor to memory key
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        for (IntelInfoPlugin intel : intelManager.getIntel(RuinsIntelv2.class)) {
            RuinsIntelv2 r = (RuinsIntelv2) intel;
            if (r.getEntity() == entity) {
                return false; // report exists
            }
        }

        RuinsIntelv2 report = new RuinsIntelv2(entity);
        report.setNew(showMessage);
        intelManager.addIntel(new RuinsIntelv2(entity), !showMessage);
        log.info("Created intel report for " + entity.getName() + " in " + getSystemNameOrHyperspaceBase(entity));
        return true;
    }
}
