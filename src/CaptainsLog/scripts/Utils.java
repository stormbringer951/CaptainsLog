package CaptainsLog.scripts;

import CaptainsLog.Constants;
import CaptainsLog.SettingsUtils;
import CaptainsLog.campaign.intel.automated.*;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.misc.BreadcrumbIntel;
import com.fs.starfarer.api.util.Misc;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public final class Utils {
    private static final Logger log = Global.getLogger(Utils.class);

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

    public static int tryCreateMegastructureReports(List<SectorEntityToken> tokens, boolean showMessage) {
        if (SettingsUtils.excludeMegastructures()) {
            return 0;
        }
        int count = 0;
        for (SectorEntityToken token : tokens) {
            if (tryCreateMegastructureReport(token, showMessage)) {
                count++;
            }
        }
        return count;
    }

    public static boolean tryCreateMegastructureReport(SectorEntityToken token, boolean showMessage) {
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

    public static int tryCreateCommRelayReports(List<SectorEntityToken> tokens, boolean showMessage) {
        if (SettingsUtils.excludeCommRelays()) {
            return 0;
        }
        int count = 0;

        for (SectorEntityToken token : tokens) {
            if (tryCreateCommRelayReport(token, showMessage)) {
                ++count;
            }
        }

        return count;
    }

    public static boolean tryCreateCommRelayReport(SectorEntityToken token, boolean showMessage) {
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

    public static int tryCreateSalvageableReports(List<SectorEntityToken> tokens, boolean showMessage) {
        if (SettingsUtils.excludeSalvageableReports()) {
            // early exit when called on startup or when settings change
            return 0;
        }
        int count = 0;

        for (SectorEntityToken salvageObject : tokens) {
            if (Utils.tryCreateSalvageableReport(salvageObject, showMessage)) {
                ++count;
            }
        }

        return count;
    }

    public static boolean tryCreateSalvageableReport(SectorEntityToken token, boolean showMessage) {
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

    public static int tryCreateUnexploredRuinsReports(List<SectorEntityToken> entities, boolean showMessage) {
        if (SettingsUtils.excludeRuinsReports()) {
            return 0;
        }
        int count = 0;

        for (SectorEntityToken entity : entities) {
            if (
                IntelValidityUtils.areRuinsDiscovered(entity) &&
                tryCreateUnexploredRuinsReport(entity, showMessage)
            ) {
                ++count;
            }
        }

        return count;
    }

    public static boolean tryCreateUnexploredRuinsReport(SectorEntityToken token, boolean showMessage) {
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

    public static void tryCreateIntels() {
        SectorAPI sector = Global.getSector();
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

        int count = Utils.tryCreateUnexploredRuinsReports(sector.getEntitiesWithTag(Tags.PLANET), false);
        count += Utils.tryCreateSalvageableReports(sector.getEntitiesWithTag(Tags.SALVAGEABLE), false);
        count += Utils.tryCreateMegastructureReports(sector.getEntitiesWithTag(Tags.CRYOSLEEPER), false);
        count += Utils.tryCreateCommRelayReports(sector.getCustomEntitiesWithTag(Tags.COMM_RELAY), false);

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

    private static boolean isSurveyedInterestingCondition(BreadcrumbIntel intel) {
        // see impl in SurveyDataSpecial::initInterestingProperty()
        if (intel.isImportant()) {
            return false; // don't remove if important
        }
        if (intel.getRemoveTrigger() == null) {
            return false; // this intel has a remove trigger
        }
        SectorEntityToken entity = intel.getRemoveTrigger();
        if (entity.getMarket() == null) {
            return false; // needs a market
        }
        if (!(entity instanceof PlanetAPI)) {
            return false; // must be a planet
        }
        if (!intel.getTitle().endsWith(" Location") && !intel.getTitle().equals("Habitable World")) {
            return false; // subject detection
        }
        if (!intel.getText().contains(", your crews found partially accessible memory banks that")) {
            return false; // text1ForIntel detection
        }
        return entity.getMarket().getSurveyLevel() == MarketAPI.SurveyLevel.FULL;
    }

    public static void removeFleetLogIntel(boolean summarizeUpdates) {
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        ArrayList<IntelInfoPlugin> toRemove = new ArrayList<>();
        int removeCount = 0;

        // Alex now handles most cases in FleetLogIntel.shouldRemoveIntel()
        // Debris fields are handled

        for (IntelInfoPlugin i : intelManager.getIntel(BreadcrumbIntel.class)) {
            BreadcrumbIntel intel = (BreadcrumbIntel) i;

            if (intel.isNew() || intel.shouldRemoveIntel()) {
                continue; // no need to handle intel that hasn't been seen, or which hasn't been handled.
            }

            if (isSurveyedInterestingCondition(intel)) {
                log.info("Removing " + intel.getTitle() + ", completed.");

                intel.setTitle(intel.getTitle() + " - Surveyed");
                intel.endAfterDelay();

                if (summarizeUpdates) {
                    ++removeCount;
                } else {
                    Global.getSector().getCampaignUI().addMessage(intel);
                }
            }
        }

        if (summarizeUpdates && removeCount > 0) {
            Global.getSector().getCampaignUI().addMessage(
                    "Captain's Log removed " + removeCount + " completed vanilla " + "fleet log entries",
                    Misc.getTextColor(),
                    Integer.toString(toRemove.size()),
                    "",
                    Misc.getHighlightColor(),
                    Misc.getHighlightColor()
            );
        }
    }
}
