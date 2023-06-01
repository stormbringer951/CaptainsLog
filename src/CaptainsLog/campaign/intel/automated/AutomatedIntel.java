package CaptainsLog.campaign.intel.automated;

import CaptainsLog.Constants;
import CaptainsLog.campaign.intel.BaseIntel;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.SectorMapAPI;

public abstract class AutomatedIntel extends BaseIntel {

    protected final SectorEntityToken token;

    AutomatedIntel(SectorEntityToken token) {
        this.token = token;
        getMapLocation(null).getMemoryWithoutUpdate().set(Constants.CAPTAINS_LOG_MEMORY_KEY, true);
    }

    @Override
    public SectorEntityToken getEntity() {
        return token;
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return getEntity();
    }

    // Force implementation, titles require Stelnet-compatible strings
    @Override
    public abstract String getSmallDescriptionTitle();

    // Force implementation, checks if intel should be created by mod or removed by vanilla intelManager
    @Override
    public abstract boolean shouldRemoveIntel();

    @Override
    public void reportRemovedIntel() {
        getMapLocation(null).getMemoryWithoutUpdate().unset(Constants.CAPTAINS_LOG_MEMORY_KEY);
        super.reportRemovedIntel();
    }
}
