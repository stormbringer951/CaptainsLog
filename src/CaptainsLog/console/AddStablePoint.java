package CaptainsLog.console;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

import java.util.LinkedHashMap;
import java.util.Random;

@SuppressWarnings("unused")
public class AddStablePoint implements BaseCommand {
    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        LocationAPI currentLoc = Global.getSector().getPlayerFleet().getContainingLocation();

        if (currentLoc.isHyperspace() || !(currentLoc instanceof StarSystemAPI)) {
            Console.showMessage("Can't generate stable points, not in a star system.");
            return CommandResult.WRONG_CONTEXT;
        }

        StarSystemAPI system = (StarSystemAPI) currentLoc;

        // Use method from StarSystemGenerator addStableLocations()
        Random random = new Random();
        LinkedHashMap<BaseThemeGenerator.LocationType, Float> weights = new LinkedHashMap<>();
        weights.put(BaseThemeGenerator.LocationType.STAR_ORBIT, 10f);
        weights.put(BaseThemeGenerator.LocationType.OUTER_SYSTEM, 10f);
        weights.put(BaseThemeGenerator.LocationType.L_POINT, 10f);
        weights.put(BaseThemeGenerator.LocationType.IN_SMALL_NEBULA, 2f);
        WeightedRandomPicker<BaseThemeGenerator.EntityLocation> locs = BaseThemeGenerator.getLocations(random, system, null, 100f, weights);
        BaseThemeGenerator.EntityLocation loc = locs.pick();

        BaseThemeGenerator.AddedEntity added = BaseThemeGenerator.addNonSalvageEntity(system, loc, Entities.STABLE_LOCATION, Factions.NEUTRAL);

        if (added != null) {
            BaseThemeGenerator.convertOrbitPointingDown(added.entity);
            Console.showMessage("Added stable location");
            return CommandResult.SUCCESS;
        } else {
            Console.showMessage("Something went wrong");
            return CommandResult.ERROR;
        }
    }
}
