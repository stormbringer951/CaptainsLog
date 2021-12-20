package CaptainsLog.scripts;

import CaptainsLog.campaign.intel.CustomMessageIntel;
import CaptainsLog.campaign.intel.FleetLogPanelPlugin;
import CaptainsLog.ui.TextArea;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TextFieldAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogCreatorInteractionDialog implements InteractionDialogPlugin {

    public enum OptionId {
        CREATE,
        CANCEL,
        PICK_TARGET,
    }

    InteractionDialogAPI dialog;
    TextFieldAPI titleField;
    TextFieldAPI bodyField;
    SectorEntityToken selectedObject = null;

    String initialTitle;
    String initialBodyText;

    LogCreatorInteractionDialog() {
        this("Captain's Log", "");
    }

    LogCreatorInteractionDialog(String title, String text) {
        this.initialTitle = title;
        this.initialBodyText = text;
    }

    @Override
    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;

        float width = 640f; // TODO: deduce possible width
        float height = 500f; // TODO: deduce possible height
        initVisualPanel(dialog.getVisualPanel(), width, height);

        OptionPanelAPI options = dialog.getOptionPanel();
        options.addOption("Create", OptionId.CREATE);
        options.addOption("Cancel", OptionId.CANCEL);
        options.addOption("Attach log to nearby object", OptionId.PICK_TARGET);
        dialog.setOptionOnEscape(null, OptionId.CANCEL);

        redrawTextPanel();
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
        title.setInitialText(initialTitle);
        TextArea body = new TextArea(width, bodyHeight, pad, new FleetLogPanelPlugin());
        body.setInitialText(initialBodyText);
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
                Global
                    .getSector()
                    .getIntelManager()
                    .addIntel(new CustomMessageIntel(titleText, bodyText, selectedObject));
                dialog.dismiss();
                break;
            case PICK_TARGET:
                pickTarget();
                break;
        }
    }

    // TODO finish this logic
    private boolean isValidCaptainsLogTarget(SectorEntityToken entity) {
        return (
            !entity.hasSensorProfile() &&
            !entity.hasDiscoveryXP() &&
            entity.isVisibleToPlayerFleet() &&
            !entity.hasTag(Tags.ORBITAL_JUNK) &&
            !entity.hasTag(Tags.TERRAIN) &&
            !entity.getName().equals("Null") &&
            !(entity instanceof AsteroidAPI) &&
            isPrimaryEntity(entity)
        );
    }

    private boolean isPrimaryEntity(SectorEntityToken entity) {
        return (
            entity.getMarket() == null ||
            entity.getMarket().getPrimaryEntity() == null ||
            entity.getMarket().getPrimaryEntity() == entity
        );
    }

    private void pickTarget() {
        LocationAPI location = Global.getSector().getCurrentLocation();

        List<SectorEntityToken> shortList = new ArrayList<>();

        if (location.isHyperspace()) {
            return; // TODO: implement case for hyperspace
        }

        for (SectorEntityToken entity : location.getAllEntities()) {
            if (isValidCaptainsLogTarget(entity)) {
                shortList.add(entity);
            }
        }

        float height = 400;
        float width = 400;
        dialog.showCustomDialog(width, height, new TokenSelectorMenu(this, shortList, selectedObject));
    }

    public void setToken(SectorEntityToken token) {
        selectedObject = token;
        redrawTextPanel();
    }

    public void redrawTextPanel() {
        TextPanelAPI panel = dialog.getTextPanel();
        panel.clear();
        panel.addPara("Log metadata");
        String selected = "None";
        if (selectedObject != null) {
            selected = selectedObject.getName();
        }
        panel.addPara("Log attached to: " + selected);
        panel.highlightInLastPara(selected);
        CampaignClockAPI clock = Global
            .getSector()
            .getClock()
            .createClock(Global.getSector().getClock().getTimestamp());
        panel.addPara("Date: " + clock.getDateString());
        panel.highlightInLastPara(clock.getDateString());
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
