package CaptainsLog.ui.select;

import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import java.awt.*;

public interface SelectionButton<T> {
    boolean isChecked();

    boolean isClicked();

    void setChecked(boolean checked);

    void setPosition(UIComponentAPI lastElement, UIComponentAPI thisElement);

    void render(TooltipMakerAPI tooltip, Color base, Color dark, Color bright, float width, float height, float pad);

    String getName();

    T getValue();
}
