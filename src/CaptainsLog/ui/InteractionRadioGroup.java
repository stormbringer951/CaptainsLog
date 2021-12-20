package CaptainsLog.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import java.awt.*;
import java.util.List;

public class InteractionRadioGroup implements CustomUIPanelPlugin {

    private final InteractionButton[] buttons;

    public InteractionRadioGroup(InteractionButton... buttons) {
        this.buttons = buttons;
    }

    public void render(
        CustomPanelAPI panel,
        boolean withScroller,
        float width,
        float height,
        float pad,
        float x,
        float y
    ) {
        Color base = Global.getSettings().getBasePlayerColor();
        Color bright = Global.getSettings().getBrightPlayerColor();
        Color dark = Global.getSettings().getDarkPlayerColor();
        float buttonHeight = 30;

        TooltipMakerAPI tooltip = panel.createUIElement(width, height, withScroller);
        tooltip.setButtonFontVictor14();

        UIComponentAPI lastElement = tooltip.addSpacer(0);
        UIComponentAPI currentElement;

        for (InteractionButton button : buttons) {
            button.render(tooltip, base, dark, bright, buttonHeight, pad);
            currentElement = tooltip.getPrev();
            button.setPosition(lastElement, currentElement);
            lastElement = currentElement;
        }

        panel.addUIElement(tooltip).inTL(x, y);
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
        for (InputEventAPI event : events) {
            if (!event.isLMBEvent()) {
                continue;
            }
            // TODO do stuff
            for (InteractionButton button : buttons) {
                if (button.isClicked(event)) {
                    if (button.isSelected()) {
                        redrawButtonSelection(event);
                    } else {
                        // TODO: possibly error check in case no other buttons are selected
                        button.setChecked(true); // prevent unselection of current choice
                    }
                    break;
                }
            }
        }
    }

    private void redrawButtonSelection(InputEventAPI event) {
        for (InteractionButton button : buttons) {
            button.setChecked(button.isClicked(event));
        }
    }
}
