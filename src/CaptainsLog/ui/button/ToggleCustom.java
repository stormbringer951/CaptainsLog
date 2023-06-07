package CaptainsLog.ui.button;

import CaptainsLog.campaign.intel.CustomMessageIntel;
import CaptainsLog.ui.Button;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import org.lwjgl.input.Keyboard;

public class ToggleCustom implements Button {

    private final boolean showOnMap;
    private final CustomMessageIntel intel;

    public ToggleCustom(boolean showOnMap, CustomMessageIntel intel) {
        this.showOnMap = showOnMap;
        this.intel = intel;
    }

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {}

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        intel.toggleShow();
        ui.recreateIntelUI();
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {}

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return false;
    }

    @Override
    public String getName() {
        return showOnMap ? "Hide location" : "Show location";
    }

    @Override
    public int getShortcut() {
        return Keyboard.KEY_M;
    }

    @Override
    public boolean shouldCreateButton() {
        return true;
    }

    @Override
    public void setPosition(UIComponentAPI lastElement, UIComponentAPI thisElement) {}
}
