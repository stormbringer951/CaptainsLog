package CaptainsLog.campaign.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.campaign.listeners.SurveyPlanetListener;
import org.apache.log4j.Logger;
import CaptainsLog.campaign.intel.RuinsIntelv2;
import CaptainsLog.scripts.CaptainsLogEveryFrame;

public class RuinsListener implements SurveyPlanetListener {
    private static final Logger log = Global.getLogger(RuinsListener.class);

    @Override
    public void reportPlayerSurveyedPlanet(PlanetAPI planet) {
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();

        if (CaptainsLogEveryFrame.shouldCreateUnsearchedRuinsReport(planet, intelManager)) {

            intelManager.addIntel(new RuinsIntelv2(planet), false);
            log.info("Listener: Created intel report for planet in");
        }
    }
}