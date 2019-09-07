package CaptainsLog.console;

import com.fs.starfarer.api.impl.campaign.intel.bases.PirateBaseManager;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

@SuppressWarnings("unused")
public class BountyLevel implements BaseCommand {
    @Override
    public BaseCommand.CommandResult runCommand(String args, BaseCommand.CommandContext context) {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return BaseCommand.CommandResult.WRONG_CONTEXT;
        }

        SharedData.getData().getPersonBountyEventData();

        if (args.isEmpty()) {
            float MAX_TIME_BASED_ADDED_LEVEL = 3;
            int base = SharedData.getData().getPersonBountyEventData().getLevel();

            float timeFactor = (PirateBaseManager.getInstance().getDaysSinceStart() - 180f) / (365f * 2f);
            if (timeFactor < 0) timeFactor = 0;
            if (timeFactor > 1) timeFactor = 1;
            int add = Math.round(MAX_TIME_BASED_ADDED_LEVEL * timeFactor);

            int total = base + add;

            // Calculations copied from PersonBountyIntel.java. Obviously if they change, this whole section will be wrong.
            Console.showMessage("Total bounty level: " + total + ", base: " + base + ", time factor: " + add);
        } else {
            try {
                int arg = Integer.parseInt(args);
                SharedData.getData().getPersonBountyEventData().setLevel(arg);
                Console.showMessage("Set base bounty level to " + arg);
            } catch (NumberFormatException e) {
                return CommandResult.BAD_SYNTAX;
            }
        }
        return CommandResult.SUCCESS;
    }
}
