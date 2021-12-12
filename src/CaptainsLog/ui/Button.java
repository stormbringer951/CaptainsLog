package CaptainsLog.ui;

import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

public interface Button {
    public void buttonPressCancelled(IntelUIAPI ui);

    public void buttonPressConfirmed(IntelUIAPI ui);

    public void createConfirmationPrompt(TooltipMakerAPI tooltip);

    public boolean doesButtonHaveConfirmDialog();

    public String getName();

    public int getShortcut();

    public void setPosition(UIComponentAPI lastElement, UIComponentAPI thisElement);
}
