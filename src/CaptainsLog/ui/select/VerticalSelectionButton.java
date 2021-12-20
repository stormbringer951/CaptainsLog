package CaptainsLog.ui.select;

import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import java.awt.*;

public class VerticalSelectionButton<V, T extends Selection<V>> implements SelectionButton<V> {

    ButtonAPI button = null;
    T selection;
    boolean startSelected;

    public VerticalSelectionButton(T selection, boolean startSelected) {
        this.selection = selection;
        this.startSelected = startSelected;
    }

    @Override
    public boolean isChecked() {
        return button != null && button.isChecked();
    }

    @Override
    public boolean isClicked(InputEventAPI event) {
        return button != null && button.getPosition().containsEvent(event);
    }

    @Override
    public void setChecked(boolean checked) {
        button.setChecked(checked);
    }

    @Override
    public void setPosition(UIComponentAPI lastElement, UIComponentAPI thisElement) {
        thisElement.getPosition().belowLeft(lastElement, 5);
    }

    @Override
    public String getName() {
        return selection.getName();
    }

    @Override
    public V getValue() {
        return selection.getValue();
    }

    @Override
    public void render(TooltipMakerAPI tooltip, Color base, Color dark, Color bright, float height, float pad) {
        // TODO make prettier
        button =
            tooltip.addAreaCheckbox(
                getName(),
                new Object(),
                base,
                dark,
                bright,
                40 + tooltip.computeStringWidth(getName()),
                height,
                pad
            );
        button.setChecked(startSelected);
    }
}
