package CaptainsLog.scripts;

import CaptainsLog.ui.select.InteractionRadioGroup;
import CaptainsLog.ui.select.SelectToken;
import CaptainsLog.ui.select.SelectionButton;
import CaptainsLog.ui.select.VerticalSelectionButton;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.*;
import java.util.ArrayList;
import java.util.List;

public class TokenSelectorMenu implements CustomDialogDelegate {

    LogCreatorInteractionDialog plugin;
    InteractionRadioGroup<SectorEntityToken> radioGroup;

    public TokenSelectorMenu(LogCreatorInteractionDialog plugin, List<SectorEntityToken> tokens, SectorEntityToken selected) {
        this.plugin = plugin;
        List<SelectionButton<SectorEntityToken>> buttons = new ArrayList<>();
        for (SectorEntityToken token : tokens) {
            buttons.add(new VerticalSelectionButton<>(new SelectToken(token), selected == token));
        }
        radioGroup = new InteractionRadioGroup<>(buttons);
    }

    @Override
    public void createCustomDialog(CustomPanelAPI panel) {
        render(panel, panel.getPosition().getWidth(), panel.getPosition().getHeight());
    }

    private void render(CustomPanelAPI panel, float width, float height) {
        radioGroup.render(panel, true, width, height, 10f, 0, 0);
    }

    @Override
    public boolean hasCancelButton() {
        return true;
    }

    // Calls only when interaction is being created, doesn't update on exit
    @Override
    public String getConfirmText() {
        return null;
    }

    @Override
    public String getCancelText() {
        return null;
    }

    @Override
    public void customDialogConfirm() {
        plugin.setToken(radioGroup.getSelected());
    }

    @Override
    public void customDialogCancel() {
        // TODO do nothing?
    }

    @Override
    public CustomUIPanelPlugin getCustomPanelPlugin() {
        return radioGroup;
    }
}
