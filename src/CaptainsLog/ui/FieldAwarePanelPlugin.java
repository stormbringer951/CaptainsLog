package CaptainsLog.ui;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.ui.TextFieldAPI;

public interface FieldAwarePanelPlugin extends CustomUIPanelPlugin {
    void setTextField(TextFieldAPI field, float width, float height);
}
