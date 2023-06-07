package CaptainsLog.ui.button;

import CaptainsLog.campaign.intel.CustomMessageIntel;
import CaptainsLog.ui.Button;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

public class DeleteAllCustom implements Button {

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {}

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        for (IntelInfoPlugin i : intelManager.getIntel(CustomMessageIntel.class)) {
            intelManager.removeIntel(i);
        }
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        int count = Global.getSector().getIntelManager().getIntel(CustomMessageIntel.class).size();
        tooltip.addPara("Delete all " + count + " entries from your Captain's Log?", 0);
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return true;
    }

    @Override
    public String getName() {
        return "Delete all entries";
    }

    @Override
    public int getShortcut() {
        return 0;
    }

    @Override
    public void setPosition(UIComponentAPI lastElement, UIComponentAPI thisElement) {}

    @Override
    public boolean shouldCreateButton() {
        return true;
    }
}
