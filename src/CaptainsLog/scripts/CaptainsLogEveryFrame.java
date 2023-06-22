package CaptainsLog.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.util.IntervalUtil;

public class CaptainsLogEveryFrame implements EveryFrameScript {

    private final IntervalUtil interval = new IntervalUtil(0.1f, 0.1f);
    private boolean notRunYet = true;

    @Override
    public boolean isDone() {
        return !notRunYet;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        interval.advance(amount);
        if (
            !Global.getSector().isInNewGameAdvance() &&
            !Global.getSector().getCampaignUI().isShowingMenu() &&
            !Global.getSector().getCampaignUI().isShowingDialog() &&
            interval.intervalElapsed()
        ) {
            // at this point hopefully we are actually in the game...
            if (notRunYet) {
                Utils.tryCreateIntels();
                Utils.removeFleetLogIntel(true);
                interval.setInterval(5f, 5f);
                notRunYet = false;
            }
        }
    }
}
