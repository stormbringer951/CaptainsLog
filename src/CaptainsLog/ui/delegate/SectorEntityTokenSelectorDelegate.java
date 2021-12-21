package CaptainsLog.ui.delegate;

import CaptainsLog.scripts.LogCreatorInteractionDialog;
import CaptainsLog.ui.select.InteractionRadioGroup;
import CaptainsLog.ui.select.SelectionButton;
import CaptainsLog.ui.select.VerticalSelectionButton;
import CaptainsLog.ui.select.types.SelectToken;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import java.util.ArrayList;
import java.util.List;

public class SectorEntityTokenSelectorDelegate extends SelectorDelegate<SectorEntityToken> {

    public SectorEntityTokenSelectorDelegate(
        LogCreatorInteractionDialog plugin,
        List<SectorEntityToken> tokens,
        SectorEntityToken selected
    ) {
        this.plugin = plugin;
        List<SelectionButton<SectorEntityToken>> buttons = new ArrayList<>();
        for (SectorEntityToken token : tokens) {
            buttons.add(new VerticalSelectionButton<>(new SelectToken(token), selected == token));
        }
        radioGroup = new InteractionRadioGroup<>(buttons);
    }

    @Override
    public void customDialogConfirm() {
        plugin.setToken(radioGroup.getSelected());
    }
}
