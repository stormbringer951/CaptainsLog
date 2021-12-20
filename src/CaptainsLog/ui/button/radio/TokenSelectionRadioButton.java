package CaptainsLog.ui.button.radio;

import CaptainsLog.ui.InteractionButton;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import java.awt.*;

public class TokenSelectionRadioButton implements InteractionButton {

    ButtonAPI button = null;
    SectorEntityToken token;

    public TokenSelectionRadioButton(SectorEntityToken token) {
        this.token = token;
    }

    @Override
    public boolean isSelected() {
        return button.isChecked();
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
        thisElement.getPosition().belowLeft(lastElement, 10);
    }

    @Override
    public String getName() {
        return token.getName();
    }

    @Override
    public void render(TooltipMakerAPI tooltip, Color base, Color dark, Color bright, float height, float pad) {
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
    }
}
