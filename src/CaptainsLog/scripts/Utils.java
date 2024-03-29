package CaptainsLog.scripts;

import CaptainsLog.Constants;
import CaptainsLog.SettingsUtils;
import CaptainsLog.campaign.intel.automated.*;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Misc;
import java.awt.*;
import java.util.List;
import org.apache.log4j.Logger;

public final class Utils {

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
            IntelValidityUtils.isMegastructureIntelInvalid(token) ||
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
        if (
            SettingsUtils.excludeCommRelays() ||
            IntelValidityUtils.isCommRelayIntelInvalid(token) ||
            IntelValidityUtils.doesIntelAlreadyExist(token)
        ) {
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
        if (
            SettingsUtils.excludeSalvageableReports() ||
            IntelValidityUtils.isSalvageableIntelInvalid(token) ||
            IntelValidityUtils.doesIntelAlreadyExist(token)
        ) {
            return false;
        }
        SalvageableIntel report;
        if (IntelValidityUtils.isDerelictShip(token)) {
            ShipVariantAPI variant = getVariant((DerelictShipEntityPlugin) token.getCustomPlugin());
            if (variant != null) {
                report = new SalvageableShipIntel(token, variant);
            } else {
                log.error("Cannot detect derelict ship type; defaulting to salvageable report.");
                Global
                    .getSector()
                    .getCampaignUI()
                    .addMessage(
                        "Captain's Log: cannot detect derelict ship type; defaulting to salvageable report. Please make a copy of this save and report this bug on Discord or forums.",
                        Misc.getNegativeHighlightColor()
                    );
                report = new SalvageableMiscIntel(token); // Graceful recovery
                // Once bug has been fixed, user can disable salvageable intel then re-enable to get the correct intel
            }
        } else {
            report = new SalvageableMiscIntel(token);
        }
        report.setNew(showMessage);
        Global.getSector().getIntelManager().addIntel(report, !showMessage);
        log.info("Created report for " + token.getFullName() + " in " + getSystemNameOrHyperspace(token));
        return true;
    }

    public static int tryCreateUnexploredRuinsReports(
        List<SectorEntityToken> entities,
        Logger log,
        boolean showMessage
    ) {
        if (SettingsUtils.excludeRuinsReports()) {
            return 0;
        }
        int count = 0;

        for (SectorEntityToken entity : entities) {
            if (
                IntelValidityUtils.areRuinsDiscovered(entity) &&
                tryCreateUnexploredRuinsReport(entity, log, showMessage)
            ) {
                ++count;
            }
        }

        return count;
    }

    public static boolean tryCreateUnexploredRuinsReport(SectorEntityToken token, Logger log, boolean showMessage) {
        if (
            SettingsUtils.excludeRuinsReports() ||
            IntelValidityUtils.isRuinsIntelInvalid(token) ||
            IntelValidityUtils.doesIntelAlreadyExist(token)
        ) {
            return false;
        }
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        RuinsIntel report = new RuinsIntel(token);
        report.setNew(showMessage);
        intelManager.addIntel(new RuinsIntel(token), !showMessage);
        log.info("Created intel report for " + token.getName() + " in " + getSystemNameOrHyperspaceBase(token));
        return true;
    }

    public static void tryCreateIntels(SectorAPI sector, Logger log) {
        for (SectorEntityToken token : sector.getEntitiesWithTag(Constants.PROXIMITY_SURVEYED_RUINS)) {
            if (!Misc.hasUnexploredRuins(token.getMarket())) {
                // not necessary but neat periodic cleanup check for proximity surveyed ruins that have been surveyed
                token.removeTag(Constants.PROXIMITY_SURVEYED_RUINS);
                log.info(
                    "Removed " +
                    Constants.PROXIMITY_SURVEYED_RUINS +
                    " tag from " +
                    token.getName() +
                    " because ruins have been marked explored."
                );
            }
        }

        int count = Utils.tryCreateUnexploredRuinsReports(sector.getEntitiesWithTag(Tags.PLANET), log, false);
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

    public static ShipVariantAPI getVariant(DerelictShipEntityPlugin plugin) {
        if (plugin.getData().ship.variantId != null) {
            return Global.getSettings().getVariant(plugin.getData().ship.variantId);
        } else if (plugin.getData().ship.variant != null) {
            return plugin.getData().ship.variant;
        } else {
            return null;
        }
    }
}
