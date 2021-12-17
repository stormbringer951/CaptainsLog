package CaptainsLog.ui;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TextFieldAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class TextArea {

    private final float width;
    private final float height;
    private final float pad;
    private final FieldAwarePanelPlugin plugin;

    private String font = Fonts.DEFAULT_SMALL;
    private String initialText = "";

    public TextArea(float width, float height, float pad) {
        this(width, height, pad, null);
    }

    public TextArea(float width, float height, float pad, FieldAwarePanelPlugin plugin) {
        this.width = width;
        this.height = height;
        this.pad = pad;
        this.plugin = plugin;
    }

    public TextFieldAPI render(CustomPanelAPI panel, float x, float y) {
        CustomPanelAPI inner = panel.createCustomPanel(width, height, plugin);
        TooltipMakerAPI tooltip = inner.createUIElement(width, height, false);
        TextFieldAPI field = render(tooltip);
        if (plugin != null) {
            plugin.setTextField(field, width, height);
            // multi-lines break cursor display
            field.hideCursor();
        }
        field.setUndoOnEscape(false);
        inner.addUIElement(tooltip);
        panel.addComponent(inner).inTL(x, y);
        return field;
    }

    public TextFieldAPI render(TooltipMakerAPI tooltip) {
        TextFieldAPI field = tooltip.addTextField(width, height, font, pad);
        field.setText(initialText);
        return field;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public void setInitialText(String initialText) {
        this.initialText = initialText;
    }
}
