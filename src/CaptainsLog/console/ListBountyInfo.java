package CaptainsLog.console;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.impl.campaign.intel.PersonBountyIntel;
import com.fs.starfarer.api.impl.campaign.intel.PersonBountyManager;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

@SuppressWarnings("unused")
public class ListBountyInfo implements BaseCommand {

    @Override
    public BaseCommand.CommandResult runCommand(String args, BaseCommand.CommandContext context) {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return BaseCommand.CommandResult.WRONG_CONTEXT;
        }

        for (EveryFrameScript s : PersonBountyManager.getInstance().getActive()) {
            PersonBountyIntel bounty = (PersonBountyIntel) s;
            Console.showIndentedMessage(
                null,
                bounty.getName() + " - " + bounty.getLevel() + (bounty.isPlayerVisible() ? "" : " (hidden)"),
                4
            );
        }

        return BaseCommand.CommandResult.SUCCESS;
    }
}
