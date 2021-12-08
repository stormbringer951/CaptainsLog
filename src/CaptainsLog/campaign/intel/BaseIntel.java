package CaptainsLog.campaign.intel;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import CaptainsLog.campaign.intel.button.IntelButton;

public abstract class BaseIntel extends BaseIntelPlugin {

    @Override
    public String getSortString() {
        SectorEntityToken mapLocation = getMapLocation(null);
        return String.format("%07.0f", Misc.getDistanceToPlayerLY(mapLocation));
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if (buttonId instanceof IntelButton) {
            ((IntelButton) buttonId).buttonPressConfirmed(ui);
        }
        super.buttonPressConfirmed(buttonId, ui);
    }

    @Override
    public void buttonPressCancelled(Object buttonId, IntelUIAPI ui) {
        if (buttonId instanceof IntelButton) {
            ((IntelButton) buttonId).buttonPressCancelled(ui);
        }
        super.buttonPressCancelled(buttonId, ui);
    }

    @Override
    public void createConfirmationPrompt(Object buttonId, TooltipMakerAPI prompt) {
        if (buttonId instanceof IntelButton) {
            ((IntelButton) buttonId).createConfirmationPrompt(prompt);
        }
        super.createConfirmationPrompt(buttonId, prompt);
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        if (buttonId instanceof IntelButton) {
            return ((IntelButton) buttonId).doesButtonHaveConfirmDialog();
        }
        return super.doesButtonHaveConfirmDialog(buttonId);
    }

    protected ButtonAPI addGenericButton(TooltipMakerAPI info, float width, IntelButton intelButton) {
        ButtonAPI button = addGenericButton(info, width, intelButton.getName(), intelButton);
        if (intelButton.getShortcut() > 0) {
            button.setShortcut(intelButton.getShortcut(), false);
        }
        return button;
    }
}
