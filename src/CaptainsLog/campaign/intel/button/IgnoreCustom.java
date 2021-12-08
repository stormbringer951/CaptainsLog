package CaptainsLog.campaign.intel.button;

import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import CaptainsLog.campaign.intel.CustomMessageIntel;

public class IgnoreCustom implements IntelButton {

    private final CustomMessageIntel intel;

    public IgnoreCustom(CustomMessageIntel intel) {
        this.intel = intel;
    }

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {
    }

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

}
