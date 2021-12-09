package CaptainsLog.campaign.intel;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.util.Misc;

public abstract class DistanceSortedIntel extends BaseIntelPlugin {

    @Override
    public String getSortString() {
        SectorEntityToken mapLocation = getMapLocation(null);
        return String.format("%05.0f", Misc.getDistanceToPlayerLY(mapLocation));
    }
}
