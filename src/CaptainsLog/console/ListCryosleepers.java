package CaptainsLog.console;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

@SuppressWarnings("unused")
public class ListCryosleepers implements BaseCommand {
    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        for (SectorEntityToken cryosleeper : Global.getSector().getEntitiesWithTag(Tags.CRYOSLEEPER)) {
            boolean found = !cryosleeper.hasSensorProfile() && !cryosleeper.isDiscoverable();
            Console.showIndentedMessage(null, "Cryosleeper in the "
                    + cryosleeper.getStarSystem().getNameWithLowercaseType()
                    + (found ? "" : " (unknown)"), 4);
        }

        return CommandResult.SUCCESS;
    }
}