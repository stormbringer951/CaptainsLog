package CaptainsLog.ui.delegate;

import CaptainsLog.scripts.LogCreatorInteractionDialog;
import CaptainsLog.ui.select.InteractionRadioGroup;
import CaptainsLog.ui.select.SelectionButton;
import CaptainsLog.ui.select.VerticalSelectionButton;
import CaptainsLog.ui.select.types.SelectStarSystem;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import java.util.ArrayList;
import java.util.List;

public class StarSystemSelectorDelegate extends SelectorDelegate<StarSystemAPI> {

    public StarSystemSelectorDelegate(
        LogCreatorInteractionDialog plugin,
        List<StarSystemAPI> systems,
        SectorEntityToken selected
    ) {
        this.plugin = plugin;
        List<SelectionButton<StarSystemAPI>> buttons = new ArrayList<>();
        for (StarSystemAPI system : systems) {
            buttons.add(new VerticalSelectionButton<>(new SelectStarSystem(system), isChecked(selected, system)));
        }
        radioGroup = new InteractionRadioGroup<>(buttons);
    }

    private boolean isChecked(SectorEntityToken token, StarSystemAPI system) {
        // TODO check this logic
        return token == system.getHyperspaceAnchor();
    }

    @Override
    public void createCustomDialog(CustomPanelAPI customPanelAPI, CustomDialogCallback customDialogCallback) {}

    @Override
    public void customDialogConfirm() {
        plugin.setToken(radioGroup.getSelected().getHyperspaceAnchor());
    }
}
