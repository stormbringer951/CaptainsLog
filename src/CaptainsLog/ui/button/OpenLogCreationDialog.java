package CaptainsLog.ui.button;

import CaptainsLog.Constants;
import CaptainsLog.scripts.LogCreatorInteractionDialog;
import CaptainsLog.ui.Button;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import lunalib.lunaSettings.LunaSettings;
import org.lwjgl.input.Keyboard;

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
        return "Create new entry";
    }

    @Override
    public int getShortcut() {
        Integer hotkey = LunaSettings.getInt(Constants.MOD_ID, Constants.CUSTOM_LOG_CREATE_HOTKEY);
        if (hotkey == null) {
            return 0;
        } else {
            return hotkey;
        }
    }

    @Override
    public void setPosition(UIComponentAPI lastElement, UIComponentAPI thisElement) {
        thisElement.getPosition().inBL(10, 10);
    }

    @Override
    public boolean shouldCreateButton() {
        return true;
    }
}
