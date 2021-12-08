package CaptainsLog.campaign.intel.button;

import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public interface IntelButton {
    public void buttonPressCancelled(IntelUIAPI ui);

    public void buttonPressConfirmed(IntelUIAPI ui);

    public void createConfirmationPrompt(TooltipMakerAPI tooltip);

    public boolean doesButtonHaveConfirmDialog();

    public String getName();
}
