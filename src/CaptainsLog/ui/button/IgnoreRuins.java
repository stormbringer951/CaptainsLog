package CaptainsLog.ui.button;

import CaptainsLog.campaign.intel.RuinsIntelv2;
import CaptainsLog.ui.Button;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import org.lwjgl.input.Keyboard;

public class IgnoreRuins implements Button {

    private final RuinsIntelv2 intel;

    public IgnoreRuins(RuinsIntelv2 intel) {
        this.intel = intel;
    }

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {}

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        MemoryAPI memory = intel.getEntity().getMemory();
        memory.set(RuinsIntelv2.IGNORE_RUINS_MEM_FLAG, true);
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
        return Keyboard.KEY_I;
    }

    @Override
    public void setPosition(UIComponentAPI lastElement, UIComponentAPI thisElement) {}
}
