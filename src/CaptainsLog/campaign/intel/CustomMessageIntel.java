package CaptainsLog.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Set;

public class CustomMessageIntel extends BaseIntelPlugin {

    private static final String REMOVE = "remove";
    private static final String HIDE_SHOW_TOGGLE = "hide_show";
    private final String message;
    private final String locationString;

    private static final String CAPTAIN_LOG_INTEL = "Captain's Log";

    private static final String deleteButtonCaption = "Delete this message";
    private static final String deleteConfirmMessage = "Are you sure you want to delete this message?";

    private static final String hideMessageButtonPrompt = "Hide On Map";
    private static final String showMessageButtonPrompt = "Show On Map";

    private boolean showOnMap;
    private SectorEntityToken locationCreated;

    public CustomMessageIntel(String message) {
        // todo: handle newlines
        LocationAPI location = Global.getSector().getPlayerFleet().getContainingLocation();

        this.locationCreated = location.createToken(Global.getSector().getPlayerFleet().getLocation());
        this.locationString = "Location: " + getLocation();
        this.message = message;
        this.showOnMap = true;
        setImportant(true);
    }

    private String getLocation() {
        if (locationCreated.getContainingLocation() instanceof StarSystemAPI) {
            return locationCreated.getContainingLocation().getName();
        }

        float maxRangeLY = 2f;

        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            float dist = Misc.getDistanceLY(locationCreated.getLocationInHyperspace(), system.getLocation());
            if (dist <= maxRangeLY) {
                return "Near " + system.getName();
            }
        }

        return "Hyperspace";
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);
        Color tc = Misc.getTextColor();

        String title = "Captain's Log";
        if (isEnding()) {
            title += " - Deleted";
        }

        info.addPara(title, c, 0f);

        float initPad;
        if (mode == ListInfoMode.IN_DESC) {
            initPad = 10f;
        } else {
            initPad = 3f;
        }

        bullet(info);
        addDays(info, "ago.", getDaysSincePlayerVisible(), tc, initPad + 10);
        info.addPara(locationString, initPad, getBulletColorForMode(mode));
        unindent(info);

        // TODO: format
        // Captain's Log
        // -- [date] ([number] days ago)
        // -- location
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        Color tc = Misc.getTextColor();

        info.addPara("The log entry you composed:", opad);

        info.addPara(message, Misc.getPositiveHighlightColor(), opad);

        float days = getDaysSincePlayerVisible();
        if (days >= 1) {
            addDays(info, "ago.", days, tc, opad + 10);
        }

        addTimeStamp(info, opad);
        info.addPara(locationString, opad);

        if (!isEnding()) {
            if (showOnMap) {
                addGenericButton(info, width, hideMessageButtonPrompt, HIDE_SHOW_TOGGLE);
            } else {
                addGenericButton(info, width, showMessageButtonPrompt, HIDE_SHOW_TOGGLE);
            }

            addGenericButton(info, width, deleteButtonCaption, REMOVE);
        }

        // TODO: add generic buttons for other intel classes (Ignore this/Ignore all of this type)
        // button.setShortcut(Keyboard.KEY_U, true);
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "captains_log");
    }

    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(CAPTAIN_LOG_INTEL);
        return tags;
    }

    public String getSortString() {
        return "Captain's Log";
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        if (!showOnMap || isEnding()) {
            return null;
        }
        if (locationCreated.isInHyperspace()) {
            return locationCreated;
        } else {
            return locationCreated.getStarSystem().getHyperspaceAnchor();
        }
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if (buttonId == REMOVE) {
            endImmediately();
            if (ui != null) {
                ui.recreateIntelUI();
            }
        } else if (buttonId == HIDE_SHOW_TOGGLE) {
            showOnMap = !showOnMap;
            if (ui != null) {
                ui.recreateIntelUI();
            }
        }
    }

    private void addTimeStamp(TooltipMakerAPI info, float opad) {
        if (timestamp == null) {
            timestamp = Global.getSector().getClock().getTimestamp();
        }

        CampaignClockAPI clock = Global.getSector().getClock().createClock(timestamp);
        info.addPara(clock.getDateString() + " (" + (int) getDaysSincePlayerVisible() + " days ago)", opad);
    }

    @Override
    public IntelSortTier getSortTier() {
        return IntelSortTier.TIER_2;
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        return buttonId == REMOVE;
    }

    @Override
    public void createConfirmationPrompt(Object buttonId, TooltipMakerAPI prompt) {
        prompt.addPara(deleteConfirmMessage, 0f);
    }
}
