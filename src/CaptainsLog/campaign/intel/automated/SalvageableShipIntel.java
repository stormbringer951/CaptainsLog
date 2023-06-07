package CaptainsLog.campaign.intel.automated;

import CaptainsLog.Constants;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;

public class SalvageableShipIntel extends SalvageableIntel {

    private static final Logger log = Global.getLogger(SalvageableShipIntel.class);
    private final ShipVariantAPI variant;

    public SalvageableShipIntel(SectorEntityToken token) {
        super(token);
        // Entities.WRECK.equals(type)
        DerelictShipEntityPlugin p = (DerelictShipEntityPlugin) token.getCustomPlugin();
        variant = Global.getSettings().getVariant(p.getData().ship.variantId);

        log.info("Adding intel for new " + getName());
    }

    @Override
    protected void addSalvageableSpecificInfo(TooltipMakerAPI info, float pad, ListInfoMode mode) {
        String hullSizeString = Misc.getHullSizeStr(variant.getHullSpec().getHullSize());
        info.addPara(
            hullSizeString + "-sized vessel",
            pad,
            getBulletColorForMode(mode),
            Misc.getHighlightColor(),
            hullSizeString
        );
    }

    @Override
    protected String getName() {
        return (
            Constants.SALVAGE_STELNET_INTEL_TYPE_SUBSTRING +
            " " +
            variant.getHullSpec().getHullName() +
            " " +
            variant.getHullSpec().getDesignation()
        );
    }

    @Override
    public String getIcon() {
        return getImage();
    }

    @Override
    protected boolean hasImage() {
        return getImage() != null && !getImage().equals("");
    }

    @Override
    protected String getImage() {
        return variant.getHullSpec().getSpriteName();
    }

    @Override
    protected Description getDesc() {
        return Global.getSettings().getDescription(variant.getHullSpec().getDescriptionId(), Description.Type.SHIP);
    }

    @Override
    public String getSmallDescriptionTitle() {
        return Constants.SALVAGE_STELNET_INTEL_TYPE_SUBSTRING + " Ship";
    }
}
