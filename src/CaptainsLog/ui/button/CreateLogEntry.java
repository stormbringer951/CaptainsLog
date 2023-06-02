package CaptainsLog.ui.button;

import CaptainsLog.campaign.intel.CustomMessageIntel;
import CaptainsLog.ui.Button;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TextFieldAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

public class CreateLogEntry implements Button {

    private final TextFieldAPI title;
    private final TextFieldAPI body;

    public CreateLogEntry(TextFieldAPI title, TextFieldAPI body) {
        this.title = title;
        this.body = body;
    }

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {}

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        String titleText = title.getText().trim();
        String bodyText = body.getText().trim();
        Global.getSector().getIntelManager().addIntel(new CustomMessageIntel(titleText, bodyText));
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

    @Override
    public boolean shouldCreateButton() {
        return true;
    }
}
