package CaptainsLog.scripts;

import CaptainsLog.campaign.intel.CustomMessageIntel;
import CaptainsLog.campaign.intel.FleetLogPanelPlugin;
import CaptainsLog.ui.TextArea;
import CaptainsLog.ui.delegate.SectorEntityTokenSelectorDelegate;
import CaptainsLog.ui.delegate.StarSystemSelectorDelegate;
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
import com.fs.starfarer.api.util.Highlights;
import com.fs.starfarer.api.util.Misc;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogCreatorInteractionDialog implements InteractionDialogPlugin {

    public enum OptionId {
        CREATE,
        PICK_TARGET,
        TOGGLE_IMPORTANT,
        CANCEL,
    }

    InteractionDialogAPI dialog;
    TextFieldAPI titleField;
    TextFieldAPI bodyField;
    SectorEntityToken selectedObject;
    boolean isImportant = true;

    String initialTitle;
    String initialBodyText;

    LogCreatorInteractionDialog() {
        this("Captain's Log", "", null);
    }

    LogCreatorInteractionDialog(String title, String text, SectorEntityToken selectedObject) {
        this.initialTitle = title;
        this.initialBodyText = text;
        this.selectedObject = selectedObject;
    }

    @Override
    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;

        float width = 640f; // TODO: deduce possible width
        float height = 500f; // TODO: deduce possible height
        initVisualPanel(dialog.getVisualPanel(), width, height);

        OptionPanelAPI options = dialog.getOptionPanel();
        options.addOption("Create", OptionId.CREATE);
        options.addOption("Select object to attach to log", OptionId.PICK_TARGET);
        options.addOption("Toggle log importance", OptionId.TOGGLE_IMPORTANT);
        options.addOption("Cancel", OptionId.CANCEL);
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
                    .addIntel(new CustomMessageIntel(titleText, bodyText, selectedObject, isImportant));
                dialog.dismiss();
                break;
            case PICK_TARGET:
                pickTarget();
                break;
            case TOGGLE_IMPORTANT:
                isImportant = !isImportant;
                redrawTextPanel();
                break;
        }
    }

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

    private boolean isValidCaptainsLogTarget(StarSystemAPI system) {
        return !(system.hasTag(Tags.THEME_HIDDEN) && !system.isEnteredByPlayer());
    }

    private void pickTarget() {
        float height = 400;
        float width = 400;

        LocationAPI location = Global.getSector().getCurrentLocation();

        if (location.isHyperspace()) {
            List<StarSystemAPI> shortList = new ArrayList<>();

            for (StarSystemAPI system : Misc.getSystemsInRange(
                Global.getSector().getPlayerFleet(),
                null,
                false,
                100000000
            )) {
                if (isValidCaptainsLogTarget(system)) {
                    shortList.add(system);
                }
            }
            dialog.showCustomDialog(width, height, new StarSystemSelectorDelegate(this, shortList, selectedObject));
        } else {
            List<SectorEntityToken> shortList = new ArrayList<>();
            for (SectorEntityToken entity : location.getAllEntities()) {
                if (isValidCaptainsLogTarget(entity)) {
                    shortList.add(entity);
                }
            }
            dialog.showCustomDialog(
                width,
                height,
                new SectorEntityTokenSelectorDelegate(this, shortList, selectedObject)
            );
        }
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
            // TODO: this does not work for StarSystemAPI getHyperspaceAnchor - "unknown location"
            selected = selectedObject.getName();
        }
        panel.addPara("Log attached to: " + selected);
        panel.highlightInLastPara(selected);
        panel.addPara("Important: " + isImportant);
        Highlights highlights = new Highlights();
        highlights.append(Boolean.toString(true), Misc.getPositiveHighlightColor());
        highlights.append(Boolean.toString(false), Misc.getNegativeHighlightColor());
        panel.setHighlightsInLastPara(highlights);
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
