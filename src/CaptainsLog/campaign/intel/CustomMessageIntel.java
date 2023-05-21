package CaptainsLog.campaign.intel;

import CaptainsLog.campaign.intel.button.IgnoreCustom;
import CaptainsLog.campaign.intel.button.LayInCourse;
import CaptainsLog.campaign.intel.button.ToggleCustom;
import CaptainsLog.scripts.Utils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignClockAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.Set;

public class CustomMessageIntel extends BaseIntel {

    private final String message;
    private final String locationString;
    private static final String CAPTAIN_LOG_INTEL = "Exploration";

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
        return Utils.getSystemNameOrHyperspace(locationCreated);
    }

    public void toggleShow() {
        showOnMap = !showOnMap;
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);
        Color tc = Misc.getTextColor();

        String title = CAPTAIN_LOG_INTEL;
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
            addGenericButton(info, width, new LayInCourse(locationCreated));
            addGenericButton(info, width, new ToggleCustom(showOnMap, this));
            addGenericButton(info, width, new IgnoreCustom(this));
        }
        // TODO: Add generic buttons for other intel classes
        // (Ignore this/Ignore all of this type)
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "captains_log");
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(CAPTAIN_LOG_INTEL);
        return tags;
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        if (!showOnMap || isEnding()) {
            return null;
        }
        return getEntity();
    }

    @Override
    public SectorEntityToken getEntity() {
        return locationCreated;
    }

    private void addTimeStamp(TooltipMakerAPI info, float opad) {
        if (timestamp == null) {
            timestamp = Global.getSector().getClock().getTimestamp();
        }

        CampaignClockAPI clock = Global.getSector().getClock().createClock(timestamp);
        float days = Math.max(1, getDaysSincePlayerVisible());
        info.addPara(clock.getDateString() + " (" + (int) days + " " + getDaysString(days) + " ago)", opad);
    }

    @Override
    public IntelSortTier getSortTier() {
        return IntelSortTier.TIER_2;
    }
}
