package CaptainsLog.ui.button;

import CaptainsLog.Constants;
import CaptainsLog.campaign.intel.automated.RuinsIntel;
import CaptainsLog.ui.Button;
import CaptainsLog.ui.Button;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import org.lwjgl.input.Keyboard;

public class IgnoreRuins implements Button {

    private final RuinsIntel intel;

    public IgnoreRuins(RuinsIntel intel) {
        this.intel = intel;
    }

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {}

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        MemoryAPI memory = intel.getEntity().getMemory();
        memory.set(Constants.IGNORE_RUINS_MEM_FLAG, true);
        intel.endImmediately();
        ui.recreateIntelUI();
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        tooltip.addPara(
            "Are you sure you want to ignore this intel report? It will be removed and no more Captain's " +
            "Log intel reports will be generated for this item.",
            0
        );
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return true;
    }

    @Override
    public String getName() {
        return "Ignore intel report";
    }

    @Override
    public int getShortcut() {
        return Keyboard.KEY_G;
    }

    @Override
    public boolean shouldCreateButton() {
        return true;
    }

    @Override
    public void setPosition(UIComponentAPI lastElement, UIComponentAPI thisElement) {}
}
