package CaptainsLog.ui.delegate;

import CaptainsLog.scripts.LogCreatorInteractionDialog;
import CaptainsLog.ui.select.InteractionRadioGroup;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.ui.*;

public abstract class SelectorDelegate<T> implements CustomDialogDelegate {

    LogCreatorInteractionDialog plugin;
    InteractionRadioGroup<T> radioGroup = null;

    @Override
    public void createCustomDialog(CustomPanelAPI panel) {
        radioGroup.render(panel, true, panel.getPosition().getWidth(), panel.getPosition().getHeight(), 10f, 0, 0);
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
    public abstract void customDialogConfirm();

    @Override
    public void customDialogCancel() {}

    @Override
    public CustomUIPanelPlugin getCustomPanelPlugin() {
        return radioGroup;
    }
}
