package CaptainsLog.console;

import CaptainsLog.campaign.intel.CustomMessageIntel;
import com.fs.starfarer.api.Global;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

@SuppressWarnings("unused")
public class AddCustomMessageLog implements BaseCommand {

    @Override
    public BaseCommand.CommandResult runCommand(String args, BaseCommand.CommandContext context) {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return BaseCommand.CommandResult.WRONG_CONTEXT;
        }

        if (args.isEmpty()) {
            Console.showMessage("Please actually enter a message.");
            return BaseCommand.CommandResult.BAD_SYNTAX;
        }

        Global.getSector().getIntelManager().addIntel(new CustomMessageIntel(args));
        Console.showMessage("Adding Captain's Log: " + args);

        return BaseCommand.CommandResult.SUCCESS;
    }
}
