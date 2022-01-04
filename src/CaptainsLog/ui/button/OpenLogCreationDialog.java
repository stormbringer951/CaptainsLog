package CaptainsLog.ui.button;

import CaptainsLog.scripts.LogCreatorInteractionDialog;
import CaptainsLog.ui.Button;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

public class OpenLogCreationDialog implements Button {

    final IntelInfoPlugin plugin;

    public OpenLogCreationDialog(IntelInfoPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {}

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        ui.showDialog(Global.getSector().getPlayerFleet(), new LogCreatorInteractionDialog(ui));
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {}

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return false;
    }

    @Override
    public String getName() {
        return "Create log entry";
    }

    @Override
    public int getShortcut() {
        return 0;
    }

    @Override
    public void setPosition(UIComponentAPI lastElement, UIComponentAPI thisElement) {
        thisElement.getPosition().inBL(10, 10);
    }
}
