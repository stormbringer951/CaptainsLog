package CaptainsLog.ui;

import CaptainsLog.ui.button.radio.TokenSelectionRadioButton;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.*;
import java.util.ArrayList;
import java.util.List;

public class TokenSelectorMenu implements CustomDialogDelegate {

    InteractionDialogPlugin plugin;
    InteractionRadioGroup radioGroup;

    public TokenSelectorMenu(InteractionDialogPlugin plugin, List<SectorEntityToken> tokens) {
        this.plugin = plugin;
        List<InteractionButton> tokenButtons = new ArrayList<>();
        for (SectorEntityToken token : tokens) {
            tokenButtons.add(new TokenSelectionRadioButton(token));
        }
        radioGroup = new InteractionRadioGroup(tokenButtons.toArray(new InteractionButton[0]));
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

    @Override
    public String getConfirmText() {
        // TODO
        return null;
    }

    @Override
    public String getCancelText() {
        return null;
    }

    @Override
    public void customDialogConfirm() {
        // TODO
        // plugin.setAttachedToken(token);
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
