package CaptainsLog.campaign.listeners;

import CaptainsLog.scripts.Utils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.listeners.SurveyPlanetListener;
import org.apache.log4j.Logger;

public class RuinsListener implements SurveyPlanetListener {
    private static final Logger log = Global.getLogger(RuinsListener.class);

    @Override
    public void reportPlayerSurveyedPlanet(PlanetAPI planet) {
        Utils.tryCreateUnsearchedRuinsReport(planet, log, true);
    }
}