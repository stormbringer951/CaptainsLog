package CaptainsLog.campaign.intel.button;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.lwjgl.input.Keyboard;

public class LayInCourse implements IntelButton {

    private final SectorEntityToken target;

    public LayInCourse(SectorEntityToken target) {
        this.target = target;
    }

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {}

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        Global.getSector().layInCourseFor(target);
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {}

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return false;
    }

    @Override
    public String getName() {
        return "Lay In Course";
    }

    @Override
    public int getShortcut() {
        return Keyboard.KEY_L;
    }

    @Override
    public boolean shouldCreateButton() {
        return !target.hasTag(Tags.FADING_OUT_AND_EXPIRING) && target.hasTag(Tags.NON_CLICKABLE);
    }
}
