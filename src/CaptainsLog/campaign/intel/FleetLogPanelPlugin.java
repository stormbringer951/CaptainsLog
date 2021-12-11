package CaptainsLog.campaign.intel;

import CaptainsLog.ui.FieldAwarePanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TextFieldAPI;
import java.util.List;

public class FleetLogPanelPlugin implements FieldAwarePanelPlugin {

    private TextFieldAPI field = Global.getSettings().createTextField("", Fonts.DEFAULT_SMALL);
    private float width = 0;
    private float height = 0;

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

    @Override
    public void processInput(List<InputEventAPI> events) {
        field.getTextLabelAPI().getPosition().setSize(width, height);
        for (InputEventAPI event : events) {
            if (!event.isKeyboardEvent()) {
                continue;
            }
            if (event.getEventValue() == 28) {
                field.setText(field.getText() + "\n");
                field.grabFocus();
            }
        }
    }
}
