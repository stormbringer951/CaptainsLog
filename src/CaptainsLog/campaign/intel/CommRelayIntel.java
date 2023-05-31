package CaptainsLog.campaign.intel;

import CaptainsLog.scripts.Utils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.econ.CommRelayCondition;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.*;
import java.util.Set;

public class CommRelayIntel extends BaseIntel {

    private final SectorEntityToken commRelay;
    private final String INTEL_TYPE_KEY = "Comm Relay";

    public CommRelayIntel(SectorEntityToken commRelay) {
        this.commRelay = commRelay;
        getMapLocation(null).getMemory().set(CAPTAINS_LOG_MEMORY_KEY, true);
    }

    @Override
    public SectorEntityToken getEntity() {
        return commRelay;
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);
        info.addPara(getName(), c, 0f);

        float initPad;
        if (mode == ListInfoMode.IN_DESC) {
            initPad = 10f;
        } else {
            initPad = 3f;
        }

        bullet(info);

        info.addPara(
            commRelay.getStarSystem().getName(),
            initPad,
            getBulletColorForMode(mode),
            Misc.getHighlightColor(),
            Utils.getSystemNameOrHyperspaceBase(commRelay)
        );
        unindent(info);
    }

    public String getSmallDescriptionTitle() {
        return INTEL_TYPE_KEY;
    }

    private float getBonus() {
        if (commRelay.hasTag(Tags.MAKESHIFT)) {
            return CommRelayCondition.MAKESHIFT_COMM_RELAY_BONUS;
        } else {
            return CommRelayCondition.COMM_RELAY_BONUS;
        }
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;

        Description desc = Global
            .getSettings()
            .getDescription(commRelay.getCustomDescriptionId(), Description.Type.CUSTOM);
        for (String para : desc.getText1Paras()) {
            info.addPara(para, opad);
        }

        bullet(info);
        String modifier = "+" + Math.round(getBonus());
        info.addPara(modifier + " stability for same-faction colonies in system", opad, Misc.getHighlightColor(), modifier);
        unindent(info);

        info.addPara(
            "This comm relay is located in the " + commRelay.getStarSystem().getNameWithLowercaseType() + ".",
            opad,
            Misc.getHighlightColor(),
            commRelay.getStarSystem().getBaseName()
        );
    }

    @Override
    public String getIcon() {
        return commRelay.getCustomEntitySpec().getIconName();
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        String INTEL_TAG = "Comm Relays";
        Set<String> tags = super.getIntelTags(map);
        if (true) {
            tags.add(INTEL_TAG);
        } else {
            tags.add(Tags.INTEL_EXPLORATION);
        }
        return tags;
    }

    @Override
    public FactionAPI getFactionForUIColors() {
        return super.getFactionForUIColors();
    }

    @Override
    protected String getName() {
        return commRelay.getName();
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return getEntity();
    }

    public static boolean intelShouldNotExist(SectorEntityToken commRelay) {
        if (commRelay == null || !commRelay.isAlive()) {
            return true; // comm relay destroyed
        }
        StarSystemAPI system = commRelay.getStarSystem();
        if (system == null) {
            return true; // safety check but comm relays should not be in hyperspace
        }
        if (!system.isInConstellation()) {
            return true; // assume all systems without constellation are inhabited core worlds
        }

        return !system.isEnteredByPlayer();
    }

    @Override
    public boolean shouldRemoveIntel() {
        boolean toRemove = intelShouldNotExist(commRelay);
        if (toRemove) {
            // making the assumption that this is being called by the IntelManagerAPI; this will make it unfilterable
            // by stelnet but the gap between this and removal should be short
            getMapLocation(null).getMemory().unset(CAPTAINS_LOG_MEMORY_KEY);
        }
        return toRemove;
    }
}
