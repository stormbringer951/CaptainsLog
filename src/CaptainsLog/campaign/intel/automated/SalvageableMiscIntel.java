package CaptainsLog.campaign.intel.automated;

import CaptainsLog.Constants;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.apache.log4j.Logger;

public class SalvageableMiscIntel extends SalvageableIntel {

    private static final Logger log = Global.getLogger(SalvageableMiscIntel.class);

    public SalvageableMiscIntel(SectorEntityToken token) {
        super(token);
        log.info("Adding intel for new " + getName());
    }

    @Override
    protected void addSalvageableSpecificInfo(TooltipMakerAPI info, float pad, ListInfoMode mode) {}

    @Override
    protected String getName() {
        return Constants.SALVAGE_STELNET_INTEL_TYPE_SUBSTRING + " " + token.getFullName();
    }

    @Override
    protected boolean hasImage() {
        return getImage() != null && !getImage().equals("");
    }

    @Override
    protected String getImage() {
        return token.getCustomEntitySpec().getSpriteName();
    }

    @Override
    protected Description getDesc() {
        return Global.getSettings().getDescription(token.getCustomDescriptionId(), Description.Type.CUSTOM);
    }

    @Override
    public String getSmallDescriptionTitle() {
        return Constants.SALVAGE_STELNET_INTEL_TYPE_SUBSTRING + " " + token.getFullName();
    }
}
