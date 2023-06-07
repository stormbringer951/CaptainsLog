package CaptainsLog.ui;

import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

public interface Button {
    void buttonPressCancelled(IntelUIAPI ui);

    void buttonPressConfirmed(IntelUIAPI ui);

    void createConfirmationPrompt(TooltipMakerAPI tooltip);

    boolean doesButtonHaveConfirmDialog();

    String getName();

    int getShortcut();

    void setPosition(UIComponentAPI lastElement, UIComponentAPI thisElement);

    boolean shouldCreateButton();
}
