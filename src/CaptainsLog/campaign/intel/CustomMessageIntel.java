package CaptainsLog.campaign.intel;

import CaptainsLog.scripts.Utils;
import CaptainsLog.ui.button.IgnoreCustom;
import CaptainsLog.ui.button.LayInCourse;
import CaptainsLog.ui.button.ToggleCustom;
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

    private final String title;
    private final String message;
    private final String locationString;
    public static final String CAPTAIN_LOG_INTEL = "Captain's Log";

    private boolean showOnMap;
    private SectorEntityToken locationCreated;
    private long timeCreated;

    public CustomMessageIntel(String title, String message) {
        // todo: handle newlines
        LocationAPI location = Global.getSector().getPlayerFleet().getContainingLocation();

        this.locationCreated = location.createToken(Global.getSector().getPlayerFleet().getLocation());
        this.timeCreated = Global.getSector().getClock().getTimestamp();
        this.locationString = "Location: " + getLocation();
        this.title = title;
        this.message = message;
        this.showOnMap = true;
        setImportant(true);
    }

    public CustomMessageIntel(String message) {
        this(null, message);
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
        Color tc = getBulletColorForMode(mode);

        String title = this.title;
        if (title == null) {
            title = CAPTAIN_LOG_INTEL;
        }

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

        float days = Math.max(1, getDaysSincePlayerVisible());
        String dateOrLegacy = "%s " + getDaysString(days) + " ago";
        if (timeCreated != 0) {
            CampaignClockAPI clock = Global.getSector().getClock().createClock(timeCreated);
            dateOrLegacy = "Added " + clock.getDateString() + " (" + dateOrLegacy + ")";
        }

        bullet(info);
        info.addPara(dateOrLegacy, initPad, tc, Misc.getHighlightColor(), getDays(days));
        info.addPara(locationString, tc, initPad);
        unindent(info);
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
        return locationCreated;
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
}
