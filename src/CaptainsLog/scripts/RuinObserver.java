package CaptainsLog.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;

public class RuinObserver implements EveryFrameScript {

    private static final Logger log = Global.getLogger(RuinObserver.class);

    public static void register() {
        RuinObserver observer = new RuinObserver();
        Global.getSector().addTransientScript(observer);
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
        CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
        StarSystemAPI system = fleet.getStarSystem();
        if (system == null) {
            return;
        }
        for (PlanetAPI planet : system.getPlanets()) {
            if (!isInRange(fleet, planet)) {
                log.debug("Skipping " + planet.getName() + " as it is too far away");
                continue;
            }
            Utils.tryCreateUnsearchedRuinsReport(planet, log, true);
        }
    }

    private boolean isInRange(CampaignFleetAPI fleet, SectorEntityToken token) {
        return Misc.getDistance(fleet, token) < fleet.getSensorStrength();
    }
}
