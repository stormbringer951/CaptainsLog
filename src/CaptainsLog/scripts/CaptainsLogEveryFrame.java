package CaptainsLog.scripts;

import CaptainsLog.campaign.intel.CustomMessageIntel;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.misc.BreadcrumbIntel;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;
import CaptainsLog.CaptainsLogModPlugin;
import CaptainsLog.campaign.intel.RuinsIntel;
import CaptainsLog.campaign.intel.RuinsIntelv2;
import CaptainsLog.campaign.intel.SalvageableIntel;
import CaptainsLog.campaign.intel.UnremovableIntel;

import java.awt.*;
import java.util.ArrayList;

public class CaptainsLogEveryFrame implements EveryFrameScript {
    private static final Logger log = Global.getLogger(CaptainsLogModPlugin.class);
    private final IntervalUtil interval = new IntervalUtil(0.1f, 0.1f);
    private boolean notRunYet = true;

    private static int updateCryosleepers(SectorAPI sector) {
        IntelManagerAPI intelManager = sector.getIntelManager();
        int count = 0;

        for (SectorEntityToken sleeper : sector.getEntitiesWithTag(Tags.CRYOSLEEPER)) {
            if (shouldCreateCryosleeperReport(sleeper, intelManager)) {
                UnremovableIntel report = new UnremovableIntel(sleeper);
                report.setNew(false);
                intelManager.addIntel(report, true);
                ++count;
                log.info("Created intel report for cryosleeper in " + sleeper.getStarSystem());
            }
        }

        return count;
    }

    private static int updateUnsearchedRuins(SectorAPI sector) {
        IntelManagerAPI intelManager = sector.getIntelManager();
        int count = 0;

        for (IntelInfoPlugin intel : intelManager.getIntel(RuinsIntel.class)) {
            // cleanly remove old instead of removing compat entirely
            intelManager.removeIntel(intel);
        }

        for (SectorEntityToken item : sector.getEntitiesWithTag(Tags.PLANET)) {
            MarketAPI market = item.getMarket();

            if (item.getMarket() != null && shouldCreateUnsearchedRuinsReport(item, intelManager)) {
                RuinsIntelv2 report = new RuinsIntelv2(item);
                report.setNew(false);
                intelManager.addIntel(report, true);
                log.info("Created intel report for " + market.getName() + " in "
                        + market.getStarSystem().getNameWithLowercaseType());
                ++count;
            }
        }

        return count;
    }

    private static int updateSalvageables(SectorAPI sector) {
        IntelManagerAPI intelManager = sector.getIntelManager();
        int count = 0;

        for (SectorEntityToken salvageObject : sector.getEntitiesWithTag(Tags.SALVAGEABLE)) {
            if (shouldCreateSalvageableReport(salvageObject, intelManager)) {
                SalvageableIntel report = new SalvageableIntel(salvageObject);
                report.setNew(false);
                intelManager.addIntel(report, true);
                log.info("Created report for " + salvageObject.getFullName() + " in "
                        + (salvageObject.isInHyperspace() ? "hyperspace" : salvageObject.getStarSystem().getBaseName()));
                ++count;
            }
        }

        return count;
    }

    public static boolean shouldCreateSalvageableReport(SectorEntityToken salvageObject, IntelManagerAPI intelManager) {
        if (SalvageableIntel.shouldRemoveIntelEntry(salvageObject)) {
            return false;
        }

        for (IntelInfoPlugin i : intelManager.getIntel(SalvageableIntel.class)) {
            SalvageableIntel rs = (SalvageableIntel) i;
            if (rs.getEntity() == salvageObject) {
                return false;
            }
        }

        return true;
    }

    public static boolean shouldCreateCryosleeperReport(SectorEntityToken cryosleeper, IntelManagerAPI intelManager) {
        if (cryosleeper.hasSensorProfile() || cryosleeper.isDiscoverable()) {
            return false; // is not discovered
        }

        for (IntelInfoPlugin intel : intelManager.getIntel(UnremovableIntel.class)) {
            UnremovableIntel cs = (UnremovableIntel) intel;
            if (cs.getEntity() == cryosleeper) {
                return false; // report exists
            }
        }

        return true;
    }

    public static boolean shouldCreateUnsearchedRuinsReport(SectorEntityToken entity, IntelManagerAPI intelManager) {
        if (RuinsIntelv2.doesNotHaveExploredRuins(entity)) {
            return false;
        }

        for (IntelInfoPlugin intel : intelManager.getIntel(RuinsIntelv2.class)) {
            RuinsIntelv2 r = (RuinsIntelv2) intel;
            if (r.getEntity() == entity) {
                return false; // report exists
            }
        }

        return true;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        interval.advance(amount);
        if (!Global.getSector().isInNewGameAdvance() && !Global.getSector().getCampaignUI().isShowingMenu() &&
                !Global.getSector().getCampaignUI().isShowingDialog() && interval.intervalElapsed()) {

            // at this point hopefully we are actually in the game...
            SectorAPI sector = Global.getSector();

            // TODO: run clean up on vanilla fleet log entries later
            // removeFleetLogEntries(sector);

            if (notRunYet) {
                runAtStart(sector);
            }
        }
    }

    private void removeFleetLogEntries(SectorAPI sector) {
        IntelManagerAPI intelManager = sector.getIntelManager();
        ArrayList<IntelInfoPlugin> toRemove = new ArrayList<>();

        for (IntelInfoPlugin i : intelManager.getIntel(BreadcrumbIntel.class)) {
            BreadcrumbIntel intel = (BreadcrumbIntel) i;

            if (intel.isNew() || intel.shouldRemoveIntel()) {
                continue; // no need to handle intel that hasn't been seen, or which hasn't been handled.
            }

            if (intel.getRemoveTrigger() != null) {
                SectorEntityToken entity = intel.getRemoveTrigger();
                // TODO: Handle these places where BreadcrumbIntel are created:
                // BreadcrumbSpecial.initEntityLocation()
                //     "Location: " + [something]"

                if (entity instanceof PlanetAPI && entity.getMarket() != null && intel.getDaysSincePlayerVisible() > 7f
                        && entity.getMarket().getSurveyLevel() == MarketAPI.SurveyLevel.FULL) {
                    // should catch "[Resource] Location", "Habitable Planet" fleet logs created by
                    // SurveyDataSpecial.initInterestingProperty()
                    //     "Habitable World" OR MarketConditionSpecAPI.getName() + " Location"
                    // SurveyDataSpecial.initPlanetSurveyData() "Survey Data for " + [thing]
                    //     "Survey Data for " + [thing]

                    log.info("Removing " + intel.getTitle() + ", completed.");
                    if (!notRunYet) {
                        sector.getCampaignUI().addMessage(intel);
                    } else {
                        intel.setTitle(intel.getTitle() + " - Completed");
                    }
                     toRemove.add(intel);
                }

                if (entity instanceof StarSystemAPI && intel.getDaysSincePlayerVisible() > 30f) {
                    // SurveyDataSpecial.initPreliminarySystemSurvey()
                    //     "Preliminary Survey of the " + system.getName()"
                    log.info("Removing " + intel.getTitle() + " from updateDefaultFleetLog()");
                    intel.setTitle(intel.getTitle() + " - Completed");
                    if (!notRunYet) {
                        sector.getCampaignUI().addMessage(intel);
                    }
                    toRemove.add(intel);
                }

                if (entity instanceof DebrisFieldTerrainPlugin && intel.getDaysSincePlayerVisible() > 30f) {
                    log.info("Removing " + intel.getTitle() + " from updateDefaultFleetLot()");
                }

                // TODO: "Location: Debris Field"
                // TODO: "Location: Orbital Habitat"

                // Creating Function: DomainSurveyDerelictSpecial.initSurveyParentEntity()
                // Title Format: "Derelict " subjectName " Location"
                // Does not need to be handled because it will clean itself up.
            }
        }

        for (IntelInfoPlugin i : toRemove) {
            intelManager.removeIntel(i);
        }

        if (notRunYet && toRemove.size() > 0) {
            sector.getCampaignUI().addMessage("Captain's Log removed " + toRemove.size() + " completed vanilla " +
                            "fleet log entries",
                    Misc.getTextColor(), Integer.toString(toRemove.size()), "", Misc.getHighlightColor(), Misc.getHighlightColor());
        }
    }

    private void runAtStart(SectorAPI sector) {

        int count = updateUnsearchedRuins(sector);
        count += updateSalvageables(sector);
        count += updateCryosleepers(sector);

        if (count > 0) {
            sector.getCampaignUI().addMessage("Captain's Log added " + count + " new intel entries",
                    Misc.getTextColor(), Integer.toString(count), "", Misc.getHighlightColor(), Misc.getHighlightColor());
        }

        interval.setInterval(5f, 5f);
        notRunYet = false;
    }
}