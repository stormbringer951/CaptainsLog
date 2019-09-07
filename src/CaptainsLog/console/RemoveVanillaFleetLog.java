package CaptainsLog.console;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.impl.campaign.intel.misc.BreadcrumbIntel;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class RemoveVanillaFleetLog implements BaseCommand {
    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        ArrayList<IntelInfoPlugin> all = new ArrayList<>();
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();

        Console.showMessage("Removing all (including unfinished) entries in the vanilla fleet log:");

        for (IntelInfoPlugin i : intelManager.getIntel(BreadcrumbIntel.class)) {
            Console.showIndentedMessage(null, ((BreadcrumbIntel) i).getTitle(), 4);
            all.add(i);
        }

        for (IntelInfoPlugin i : all) {
            intelManager.removeIntel(i);
        }

        return CommandResult.SUCCESS;
    }
}