package CaptainsLog.ui.select;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import java.awt.*;
import java.util.List;

public class InteractionRadioGroup<T> implements CustomUIPanelPlugin {

    private final List<SelectionButton<T>> buttons;

    public InteractionRadioGroup(List<SelectionButton<T>> buttons) {
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
        float horizonalPad = 20;

        TooltipMakerAPI tooltip = panel.createUIElement(width, height, withScroller);
        tooltip.setButtonFontVictor14();

        UIComponentAPI lastElement = tooltip.addSpacer(0);
        UIComponentAPI currentElement;

        for (SelectionButton<T> button : buttons) {
            button.render(tooltip, base, dark, bright, width - horizonalPad, buttonHeight, pad);
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
            // mouse up event cannot accurately keep track of things that are left-clicked (mouse down event consumed)
            // and then dragged so mouse up event occurs outside getPosition().containsEvent()
            // especially with a scroller where you must check getPosition().containsEvent() to make sure you are only
            // selecting visible elements
            for (SelectionButton<T> button : buttons) {
                if (button.isChecked() && button.isClicked()) {
                    redrawButtonSelection(button);
                }
            }
        }
    }

    @Override
    public void buttonPressed(Object o) {

    }

    public T getSelected() {
        for (SelectionButton<T> button : buttons) {
            if (button.isChecked()) {
                return button.getValue();
            }
        }
        return null;
    }

    private void redrawButtonSelection(SelectionButton<T> selectedButton) {
        for (SelectionButton<T> button : buttons) {
            button.setChecked(button == selectedButton);
        }
    }
}
