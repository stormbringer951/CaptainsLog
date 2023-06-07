package CaptainsLog.campaign.intel;

import CaptainsLog.Constants;
import CaptainsLog.SettingsUtils;
import CaptainsLog.scripts.Utils;
import CaptainsLog.ui.button.DeleteCustom;
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

    private final SectorEntityToken locationCreated;
    private final long timeCreated;

    private String message;
    private String title;
    private SectorEntityToken targetLocation;
    private long timeModified;
    private boolean showOnMap;

    public CustomMessageIntel(String title, String message) {
        this(title, message, null, SettingsUtils.markCustomMessagesAsImportant());
    }

    public CustomMessageIntel(String title, String message, SectorEntityToken selected, boolean isImportant) {
        LocationAPI location = Global.getSector().getPlayerFleet().getContainingLocation();
        this.locationCreated = location.createToken(Global.getSector().getPlayerFleet().getLocation());
        if (selected != null) {
            this.targetLocation = selected;
        } else {
            this.targetLocation = this.locationCreated;
        }
        this.timeCreated = Global.getSector().getClock().getTimestamp();
        this.timeModified = this.timeCreated;
        this.title = title;
        this.message = message;
        this.showOnMap = true;
        setImportant(isImportant);
    }

    public CustomMessageIntel(String message) {
        this("Captain's Log", message);
    }

    private String getLocationString() {
        String locationString = "Location: ";
        if (targetLocation != locationCreated) {
            locationString += targetLocation.getName(); // for locationCreated this is just "Waypoint"
        }
        locationString += Utils.getSystemNameOrHyperspace(locationCreated);
        return locationString;
    }

    public void toggleShow() {
        showOnMap = !showOnMap;
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);
        Color tc = getBulletColorForMode(mode);

        String title = this.title;

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
        addTimeStamp(info, initPad);
        info.addPara(getLocationString(), tc, initPad);
        unindent(info);
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;

        info.addPara(message, Misc.getTextColor(), 0);

        addTimeStamp(info, opad);
        info.addPara(getLocationString(), Misc.getGrayColor(), opad);

        if (!isEnding()) {
            addGenericButton(info, width, new LayInCourse(targetLocation));
            addGenericButton(info, width, new ToggleCustom(showOnMap, this));
            addGenericButton(info, width, new DeleteCustom(this));
        }
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "captains_log");
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Constants.CUSTOM_MESSAGE_INTEL_TAG);
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
        return targetLocation;
    }

    private void addTimeStamp(TooltipMakerAPI info, float opad) {
        int getDays = Math.round(Global.getSector().getClock().getElapsedDaysSince(timeCreated));
        CampaignClockAPI clock = Global.getSector().getClock().createClock(timeCreated);
        info.addPara(clock.getDateString() + " (" + getDays + " days ago)", Misc.getGrayColor(), opad);
    }

    @Override
    public IntelSortTier getSortTier() {
        return IntelSortTier.TIER_2;
    }
}
