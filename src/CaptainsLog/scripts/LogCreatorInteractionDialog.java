package CaptainsLog.scripts;

import CaptainsLog.campaign.intel.CustomMessageIntel;
import CaptainsLog.campaign.intel.FleetLogPanelPlugin;
import CaptainsLog.ui.ButtonRow;
import CaptainsLog.ui.TextArea;
import CaptainsLog.ui.button.ClearAll;
import CaptainsLog.ui.button.CreateLogEntry;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TextFieldAPI;
import java.util.List;
import java.util.Map;

public class LogCreatorInteractionDialog implements InteractionDialogPlugin {

    public enum OptionId {
        CREATE,
        CANCEL,
    }

    InteractionDialogAPI dialog;
    TextFieldAPI titleField;
    TextFieldAPI bodyField;

    @Override
    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;

        float width = 640f; // TODO: deduce possible width
        float height = 300f; // TODO: deduce possible height
        initVisualPanel(dialog.getVisualPanel(), width, height);

        OptionPanelAPI options = dialog.getOptionPanel();
        options.addOption("Create", OptionId.CREATE);
        options.addOption("Cancel", OptionId.CANCEL);
        dialog.setOptionOnEscape(null, OptionId.CANCEL);

        dialog.hideTextPanel();
    }

    void initVisualPanel(VisualPanelAPI visual, float width, float height) {
        CustomPanelAPI panel = visual.showCustomPanel(
            width,
            height,
            new CustomUIPanelPlugin() {
                @Override
                public void positionChanged(PositionAPI position) {}

                @Override
                public void renderBelow(float alphaMult) {}

                @Override
                public void render(float alphaMult) {}

                @Override
                public void advance(float amount) {}

                @Override
                public void processInput(List<InputEventAPI> events) {}
            }
        );

        width -= 10; // give 10px padding on the right
        float pad = 5;
        float titleHeight = 28;
        float bodyHeight = height - titleHeight - pad;
        TextArea title = new TextArea(width, titleHeight, pad);
        TextArea body = new TextArea(width, bodyHeight, pad, new FleetLogPanelPlugin());
        title.setFont(Fonts.ORBITRON_16);
        titleField = title.render(panel, 0, 0);
        bodyField = body.render(panel, 0, titleHeight + pad);
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {
        OptionId option = (OptionId) optionData;
        switch (option) {
            case CANCEL:
                dialog.dismissAsCancel();
                return;
            case CREATE:
                String titleText = titleField.getText().trim();
                String bodyText = bodyField.getText().trim();
                Global.getSector().getIntelManager().addIntel(new CustomMessageIntel(titleText, bodyText));
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void optionMousedOver(String optionText, Object optionData) {}

    @Override
    public void advance(float amount) {}

    @Override
    public void backFromEngagement(EngagementResultAPI battleResult) {}

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }
}
