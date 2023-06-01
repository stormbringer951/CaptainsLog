package CaptainsLog.campaign.intel;

import CaptainsLog.Constants;
import CaptainsLog.SettingsUtils;
import CaptainsLog.campaign.intel.button.IgnoreRuins;
import CaptainsLog.campaign.intel.button.LayInCourse;
import CaptainsLog.scripts.Utils;
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

public class RuinsIntel extends BaseIntel {

    public static final String IGNORE_RUINS_MEM_FLAG = "$captainsLog_ignoreRuins";

    private final SectorEntityToken marketToken;
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

    public RuinsIntel(SectorEntityToken marketToken) {
        this.marketToken = marketToken;
        this.ruinsType = marketToken.getMarket().getCondition(getRuinType(marketToken.getMarket())).getSpec().getId();
        getMapLocation(null).getMemoryWithoutUpdate().set(CAPTAINS_LOG_MEMORY_KEY, true);
    }

    private MarketConditionSpecAPI getRuinsSpec() {
        return Global.getSettings().getMarketConditionSpec(ruinsType);
    }

    private static boolean hasRuins(MarketAPI market) {
        return market != null && getRuinType(market) != null;
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

    public static boolean doesNotHaveUnexploredRuins(SectorEntityToken token) {
        if (SettingsUtils.excludeRuinsReports()) {
            return false;
        }
        MarketAPI market = token.getMarket();
        return (market == null ||
            token.getMemoryWithoutUpdate().getBoolean(IGNORE_RUINS_MEM_FLAG) ||
            !market.isPlanetConditionMarketOnly() ||
            !hasRuins(market) ||
            market.getName().equals("Praetorium") || // manually override Sylphon hardcoded world
            market.getMemoryWithoutUpdate().getBoolean("$ruinsExplored") ||
            Utils.isInUnexploredSystem(token)
        );
    }

    @Override
    public boolean shouldRemoveIntel() {
        boolean shouldRemove = doesNotHaveUnexploredRuins(marketToken);
        if (shouldRemove) {
            setHidden(true);
            getMapLocation(null).getMemoryWithoutUpdate().unset(CAPTAINS_LOG_MEMORY_KEY);
        }
        return shouldRemove;
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
        String planetName = marketToken.getName();
        String systemName = marketToken.getStarSystem().getName();

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
            marketToken.getStarSystem().getNameWithNoType()
        );

        int distanceLY = Math.round(Misc.getDistanceToPlayerLY(marketToken));
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
        text.addPara(Misc.getTokenReplaced(getRuinsSpec().getDesc(), marketToken), Misc.getGrayColor(), opad);
        info.addImageWithText(opad);

        info.addPara(
            "Located in the " + marketToken.getStarSystem().getNameWithLowercaseType() + ".",
            opad,
            Misc.getHighlightColor(),
            marketToken.getStarSystem().getBaseName()
        );

        if (!isEnding()) {
            addGenericButton(info, width, new LayInCourse(marketToken));
            addGenericButton(info, width, new IgnoreRuins(this));
        }
        // addGenericButton(info, width, deleteButtonCaption, IGNORE_ALL);
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
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        if (isEnding()) {
            return null;
        } else {
            return getEntity();
        }
    }

    @Override
    public String getCommMessageSound() {
        return "ui_discovered_entity";
    }

    @Override
    public SectorEntityToken getEntity() {
        return marketToken;
    }

    @Override
    public IntelSortTier getSortTier() {
        if (isEnding()) {
            return IntelSortTier.TIER_COMPLETED;
        }
        return IntelSortTier.TIER_4;
    }

    @Override
    public void endAfterDelay(float days) {
        getMapLocation(null).getMemoryWithoutUpdate().unset(CAPTAINS_LOG_MEMORY_KEY);
        super.endAfterDelay(days);
    }
}
