package CaptainsLog.ui.button;

import CaptainsLog.campaign.intel.CustomMessageControlPanel;
import CaptainsLog.campaign.intel.CustomMessageIntel;
import CaptainsLog.ui.Button;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import java.util.List;

public class DeleteAllCustom implements Button {

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {}

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        ui.updateIntelList();
        List<IntelInfoPlugin> toRemove = intelManager.getIntel(CustomMessageIntel.class);

        for (IntelInfoPlugin i : toRemove) {
            if (i instanceof CustomMessageIntel) {
                CustomMessageIntel c = (CustomMessageIntel) i;
                c.endImmediately();
            }
        }
        ui.updateIntelList();
        ui.selectItem(intelManager.getFirstIntel(CustomMessageControlPanel.class));
        // ui.recreateIntelUI();
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        tooltip.addPara("Delete all " + getNumberOfEntries() + " entries from your Captain's Log?", 0);
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return true;
    }

    @Override
    public String getName() {
        return "Delete all entries";
    }

    private int getNumberOfEntries() {
        return Global.getSector().getIntelManager().getIntel(CustomMessageIntel.class).size();
    }

    @Override
    public int getShortcut() {
        return 0;
    }

    @Override
    public void setPosition(UIComponentAPI lastElement, UIComponentAPI thisElement) {}

    @Override
    public boolean shouldCreateButton() {
        return getNumberOfEntries() > 0;
    }
}
