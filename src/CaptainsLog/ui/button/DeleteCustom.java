package CaptainsLog.ui.button;

import CaptainsLog.campaign.intel.CustomMessageIntel;
import CaptainsLog.ui.Button;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import org.lwjgl.input.Keyboard;

public class DeleteCustom implements Button {

    private final CustomMessageIntel intel;

    public DeleteCustom(CustomMessageIntel intel) {
        this.intel = intel;
    }

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {}

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        intel.endImmediately();
        ui.recreateIntelUI();
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        tooltip.addPara("Are you sure you want to delete this message?", 0);
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return true;
    }

    @Override
    public String getName() {
        return "Delete this message";
    }

    @Override
    public int getShortcut() {
        return Keyboard.KEY_D;
    }

    @Override
    public void setPosition(UIComponentAPI lastElement, UIComponentAPI thisElement) {}

    @Override
    public boolean shouldCreateButton() {
        return true;
    }
}
