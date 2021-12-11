package CaptainsLog.campaign.intel;

import CaptainsLog.ui.FieldAwarePanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TextFieldAPI;
import java.util.List;
(??)

public class FleetLogPanelPlugin implements FieldAwarePanelPlugin {

    private TextFieldAPI field = Global.getSettings().createTextField("", Fonts.DEFAULT_SMALL);
    private float width = 0;
    private float height = 0;
    private boolean shouldRecaptureFocus = false;

    @Override
    public void setTextField(TextFieldAPI field, float width, float height) {
        if (field == null) {
            return;
        }
        this.field = field;
        this.width = width;
        this.height = height;
    }

    @Override
    public void positionChanged(PositionAPI position) {}

    @Override
    public void renderBelow(float alphaMult) {}

    @Override
    public void render(float alphaMult) {}

    @Override
    public void advance(float amount) {}

    private boolean clickedOutsideTextArea(InputEventAPI event) {
        return !field.getTextLabelAPI().getPosition().containsEvent(event);
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        field.getTextLabelAPI().getPosition().setSize(width, height);
        if (field.hasFocus() && !shouldRecaptureFocus) {
            // sync with ui state
            shouldRecaptureFocus = true;
        }

        for (InputEventAPI event : events) {
            if (event.isMouseDownEvent() && clickedOutsideTextArea(event)) {
                shouldRecaptureFocus = false;
                continue;
            }

            // note: if focused, key down events have already been consumed
            if (!shouldRecaptureFocus && !event.isKeyboardEvent()) {
                continue;
            }

            if (event.getEventValue() == Keyboard.KEY_ESCAPE) {
                // match behaviour with standard TextFieldAPI
                shouldRecaptureFocus = false;
            } else if (event.getEventValue() == Keyboard.KEY_RETURN) {
                field.setText(field.getText() + "\n");
                field.grabFocus();
            }
        }
    }

    @Override
    public void buttonPressed(Object o) {
        // TODO nothing
    }
}
