package CaptainsLog.console;

import CaptainsLog.campaign.intel.CustomMessageIntel;
import CaptainsLog.campaign.intel.RuinsIntel;
import CaptainsLog.campaign.intel.SalvageableIntel;
import CaptainsLog.campaign.intel.automated.MegastructureIntel;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import java.util.ArrayList;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

@SuppressWarnings("unused")
public class RemoveCaptainsLog implements BaseCommand {

    @Override
    public BaseCommand.CommandResult runCommand(String args, BaseCommand.CommandContext context) {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return BaseCommand.CommandResult.WRONG_CONTEXT;
        }

        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        ArrayList<IntelInfoPlugin> all = new ArrayList<>();
        all.addAll(intelManager.getIntel(RuinsIntel.class));
        all.addAll(intelManager.getIntel(SalvageableIntel.class));
        all.addAll(intelManager.getIntel(MegastructureIntel.class));
        all.addAll(intelManager.getIntel(CustomMessageIntel.class));

        Console.showMessage("Removing " + all.size() + " Captain's Log entries.");

        for (IntelInfoPlugin i : all) {
            intelManager.removeIntel(i);
        }

        return BaseCommand.CommandResult.SUCCESS;
    }
}
