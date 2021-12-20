package CaptainsLog.ui.select;

import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import java.awt.*;

public interface SelectionButton<T>  {
    boolean isChecked();

    boolean isClicked(InputEventAPI event);

    void setChecked(boolean checked);

    void setPosition(UIComponentAPI lastElement, UIComponentAPI thisElement);

    void render(TooltipMakerAPI tooltip, Color base, Color dark, Color bright, float height, float pad);

    String getName();

    // TODO parameterize
    T getValue();
}
