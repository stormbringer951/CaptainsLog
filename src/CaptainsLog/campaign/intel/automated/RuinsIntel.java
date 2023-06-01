package CaptainsLog.campaign.intel.automated;

import CaptainsLog.Constants;
import CaptainsLog.SettingsUtils;
import CaptainsLog.campaign.intel.button.IgnoreRuins;
import CaptainsLog.campaign.intel.button.LayInCourse;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.MarketConditionSpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.Set;

public class RuinsIntel extends AutomatedIntel {

    private final String ruinsType;

    // From Alex: http://fractalsoftworks.com/forum/index.php?topic=15563.msg250898
    // Markets on uncolonized planets are not persistent by default - they'd take up
    // way too much space in the savefile, so the game creates a more light-weight
    // PlanetConditionMarket to store in the save, and converts it into a regular
    // market on load. (The market memory does persist, btw, so it's safe to put
    // things there.) If a market is flagged as not being a condition-only market,
    // the game should stop doing this for the market. So, to sum it up: if you're
    // making any changes to the market and need them to stick around, call
    // setPlanetConditionMarketOnly(false), otherwise it'll get nuked on save.

    public RuinsIntel(SectorEntityToken token) {
        super(token);
        this.ruinsType = token.getMarket().getCondition(getRuinType(token.getMarket())).getSpec().getId();
        if (token.getMarket().getSurveyLevel() != MarketAPI.SurveyLevel.FULL) {
            // Survey Level not checked in calls to tryCreateUnexploredRuinsReport() in calls from RuinsObserver
            getMapLocation(null).addTag(Constants.PROXIMITY_SURVEYED_RUINS);
        }
    }

    private MarketConditionSpecAPI getRuinsSpec() {
        return Global.getSettings().getMarketConditionSpec(ruinsType);
    }

    private static String getRuinType(MarketAPI market) {
        if (market == null) {
            return null;
        }

        String[] ruinTypes = new String[] {
            Conditions.RUINS_SCATTERED,
            Conditions.RUINS_WIDESPREAD,
            Conditions.RUINS_EXTENSIVE,
            Conditions.RUINS_VAST,
        };

        for (String type : ruinTypes) {
            if (market.hasCondition(type)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public boolean shouldRemoveIntel() {
        return IntelValidityUtils.isRuinsIntelInvalid(token);
    }

    @Override
    public String getIcon() {
        return getRuinsSpec().getIcon();
    }

    @Override
    public String getSmallDescriptionTitle() {
        return "Unexplored " + Constants.STELNET_INTEL_TYPE_SUBSTRING_RUINS;
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);

        float initPad;
        if (mode == ListInfoMode.IN_DESC) {
            initPad = 10f;
        } else {
            initPad = 3f;
        }

        String title = getRuinsSpec().getName();
        String planetName = token.getName();
        String systemName = token.getStarSystem().getName();

        if (isEnding()) {
            title += " - Deleted";
        }

        info.addPara(title, c, 0f);

        bullet(info);

        info.addPara(
            "On " + planetName + " in the " + systemName,
            initPad,
            getBulletColorForMode(mode),
            Misc.getHighlightColor(),
            planetName,
            token.getStarSystem().getNameWithNoType()
        );

        int distanceLY = Math.round(Misc.getDistanceToPlayerLY(token));
        info.addPara(
            distanceLY + " light years away",
            initPad,
            getBulletColorForMode(mode),
            Misc.getHighlightColor(),
            Integer.toString(distanceLY)
        );

        unindent(info);
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;

        TooltipMakerAPI text = info.beginImageWithText(getIcon(), 48);
        text.addPara(Misc.getTokenReplaced(getRuinsSpec().getDesc(), token), Misc.getGrayColor(), opad);
        info.addImageWithText(opad);

        info.addPara(
            "Located in the " + token.getStarSystem().getNameWithLowercaseType() + ".",
            opad,
            Misc.getHighlightColor(),
            token.getStarSystem().getBaseName()
        );

        if (!isEnding()) {
            addGenericButton(info, width, new LayInCourse(token));
            addGenericButton(info, width, new IgnoreRuins(this));
        }
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        if (SettingsUtils.isStelnetEnabled()) {
            tags.add(Constants.STELNET_FILTERED_INTEL_TAG);
        } else {
            tags.add(Constants.RUINS_INTEL_TAG);
        }
        return tags;
    }

    @Override
    public String getCommMessageSound() {
        return "ui_discovered_entity";
    }

    @Override
    public IntelSortTier getSortTier() {
        if (isEnding()) {
            return IntelSortTier.TIER_COMPLETED;
        }
        return IntelSortTier.TIER_4;
    }
}
