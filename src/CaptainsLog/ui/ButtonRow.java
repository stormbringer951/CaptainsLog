package CaptainsLog.ui;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

public class ButtonRow {

    private final Button[] buttons;
    private final float width;
    private final float height;
    private final float pad;

    public ButtonRow(float width, float height, Button... buttons) {
        this(width, height, 0, buttons);
    }

    public ButtonRow(float width, float height, float pad, Button... buttons) {
        this.width = width;
        this.height = height - pad;
        this.buttons = buttons;
        this.pad = pad;
    }

    public float getHeight() {
        return height + pad;
    }

    public void render(TooltipMakerAPI tooltip) {
        UIComponentAPI lastElement = tooltip.addSpacer(0);
        UIComponentAPI currentElement;
        tooltip.setButtonFontVictor14();
        for (Button button : buttons) {
            tooltip.addButton(button.getName(), button, 40 + tooltip.computeStringWidth(button.getName()), height, pad);
            currentElement = tooltip.getPrev();
            button.setPosition(lastElement, currentElement);
            lastElement = currentElement;
        }
    }

    public void render(CustomPanelAPI panel, float x, float y) {
        TooltipMakerAPI tooltip = panel.createUIElement(width, height, false);
        render(tooltip);
        panel.addUIElement(tooltip).inTL(x, y);
    }
}
