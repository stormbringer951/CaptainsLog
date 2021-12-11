package CaptainsLog.console;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

@SuppressWarnings("unused")
public class ListFreeStorage implements BaseCommand {

    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        for (StarSystemAPI star : Global.getSector().getEconomy().getStarSystemsWithMarkets()) {
            for (SectorEntityToken entity : star.getAllEntities()) {
                if (entity.getMarket() != null) {
                    MarketAPI market = entity.getMarket();

                    if (
                        market.hasCondition(Conditions.ABANDONED_STATION) &&
                        market.hasSubmarket(Submarkets.SUBMARKET_STORAGE)
                    ) {
                        Console.showIndentedMessage(
                            null,
                            entity.getName() + ", " + entity.getStarSystem().getBaseName(),
                            4
                        );
                    }
                }
            }
        }

        return CommandResult.SUCCESS;
    }
}
