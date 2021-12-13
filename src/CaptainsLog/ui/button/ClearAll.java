package CaptainsLog.ui.button;

import CaptainsLog.ui.Button;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TextFieldAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

public class ClearAll implements Button {

    private final TextFieldAPI[] fields;

    public ClearAll(TextFieldAPI... fields) {
        this.fields = fields;
    }

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {}

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        for (TextFieldAPI field : fields) {
            field.deleteAll();
        }
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {}

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return false;
    }

    @Override
    public String getName() {
        return "Clear all";
    }

    @Override
    public int getShortcut() {
        return 0;
    }

    @Override
    public void setPosition(UIComponentAPI lastElement, UIComponentAPI thisElement) {
        thisElement.getPosition().inBR(10, 10);
    }
}
