package CaptainsLog.ui.select;

import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import java.awt.*;

public class VerticalSelectionButton<V, T extends Selection<V>> implements SelectionButton<V> {

    private ButtonAPI button = null;
    private final T selection;
    private boolean isChecked; // used to detect clicks

    public VerticalSelectionButton(T selection, boolean isChecked) {
        this.selection = selection;
        this.isChecked = isChecked;
    }

    @Override
    public boolean isChecked() {
        return button != null && button.isChecked();
    }

    @Override
    public boolean isClicked() {
        return isChecked != button.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
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
    public void render(
        TooltipMakerAPI tooltip,
        Color base,
        Color dark,
        Color bright,
        float width,
        float height,
        float pad
    ) {
        button = tooltip.addAreaCheckbox(getName(), new Object(), base, dark, bright, width, height, pad);
        button.setChecked(isChecked);
    }
}
