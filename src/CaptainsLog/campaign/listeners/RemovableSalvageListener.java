package CaptainsLog.campaign.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.campaign.listeners.DiscoverEntityListener;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import org.apache.log4j.Logger;
import CaptainsLog.CaptainsLogModPlugin;
import CaptainsLog.campaign.intel.SalvageableIntel;
import CaptainsLog.campaign.intel.UnremovableIntel;
import CaptainsLog.scripts.CaptainsLogEveryFrame;

public class RemovableSalvageListener implements DiscoverEntityListener {
    private static final Logger log = Global.getLogger(CaptainsLogModPlugin.class);

    @Override
    public void reportEntityDiscovered(SectorEntityToken entity) {

        IntelManagerAPI intelManager = Global.getSector().getIntelManager();

        if (entity.hasTag(Tags.CRYOSLEEPER)
                && CaptainsLogEveryFrame.shouldCreateCryosleeperReport(entity, intelManager)) {
            intelManager.addIntel(new UnremovableIntel(entity), true);
            log.info("Listener created intel report for cryosleeper in " + entity.getStarSystem());
        } else if (entity.hasTag(Tags.SALVAGEABLE)
                && CaptainsLogEveryFrame.shouldCreateSalvageableReport(entity, intelManager)) {
            intelManager.addIntel(new SalvageableIntel(entity), true);
            log.info("Listener created intel report for salvagable in " + entity.getStarSystem());
        }
    }
}
