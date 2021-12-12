package CaptainsLog.campaign.intel.button;

import CaptainsLog.campaign.intel.SalvageableIntel;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.lwjgl.input.Keyboard;

public class IgnoreSalvage implements IntelButton {

    private final SalvageableIntel intel;

    public IgnoreSalvage(SalvageableIntel intel) {
        this.intel = intel;
    }

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {}

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        MemoryAPI mem = intel.getEntity().getMemory();
        mem.set(SalvageableIntel.IGNORE_SALVAGEABLE_MEM_FLAG, true);
        intel.endImmediately();
        ui.recreateIntelUI();
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        tooltip.addPara("Are you sure you want to ignore this salvageable item?", 0);
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return true;
    }

    @Override
    public String getName() {
        return "Ignore entity";
    }

    @Override
    public int getShortcut() {
        return Keyboard.KEY_I;
    }
}
