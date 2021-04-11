package CaptainsLog.campaign.intel;

import CaptainsLog.scripts.Utils;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.MarketConditionSpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.Global;

import java.awt.*;
import java.util.Set;

public class RuinsIntelv2 extends BaseIntelPlugin {
    private static final String IGNORE_RUINS_MEM_FLAG = "$captainsLog_ignoreRuins";
    private static final String INTEL_RUINS = "Unexplored Ruins";
    private final SectorEntityToken marketToken;
    private final String ruinsType;

    private static final String IGNORE = "ignore";

    private static final String ignoreButtonCaption = "Ignore intel report";
    private static final String ignoreConfirmMessage = "Are you sure you want to ignore this intel report? It will be " +
            "removed and no more Captain's Log intel reports will be generated for this item.";

    // From Alex: http://fractalsoftworks.com/forum/index.php?topic=15563.msg250898
    // Markets on uncolonized planets are not persistent by default - they'd take up way too much space in the savefile,
    // so the game creates a more light-weight PlanetConditionMarket to store in the save, and converts it into a
    // regular market on load. (The market memory does persist, btw, so it's safe to put things there.) If a market is
    // flagged as not being a condition-only market, the game should stop doing this for the market.
    //
    // So, to sum it up: if you're making any changes to the market and need them to stick around, call
    // setPlanetConditionMarketOnly(false), otherwise it'll get nuked on save.

    public RuinsIntelv2(SectorEntityToken marketToken) {
        this.marketToken = marketToken;
        this.ruinsType = marketToken.getMarket().getCondition(getRuinType(marketToken.getMarket())).getSpec().getId();
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

        String[] ruinTypes = new String[]{
                Conditions.RUINS_SCATTERED,
                Conditions.RUINS_WIDESPREAD,
                Conditions.RUINS_EXTENSIVE,
                Conditions.RUINS_VAST
        };

        for (String type : ruinTypes) {
            if (market.hasCondition(type)) {
                return type;
            }
        }
        return null;
    }

    public static boolean doesNotHaveExploredRuins(SectorEntityToken token) {
        MarketAPI market = token.getMarket();
        return market == null ||
                token.getMemoryWithoutUpdate().getBoolean(IGNORE_RUINS_MEM_FLAG) ||
                !market.isPlanetConditionMarketOnly() ||
                !hasRuins(market) ||
                market.getSurveyLevel() != MarketAPI.SurveyLevel.FULL ||
                market.getName().equals("Praetorium") || // manually override Sylphon hardcoded world
                market.getMemoryWithoutUpdate().getBoolean("$ruinsExplored") ||
                Utils.isInUnexploredSystem(token);
    }

    @Override
    public boolean shouldRemoveIntel() {
        return doesNotHaveExploredRuins(marketToken);
    }

    @Override
    public String getIcon() {;
        return getRuinsSpec().getIcon();
    }

    @Override
    public String getSmallDescriptionTitle() {
        return getRuinsSpec().getName() + " on " + marketToken.getName();
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);

        String title = "Unexplored Ruins";
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

        info.addPara(getRuinsSpec().getName(), Misc.getRelColor(getFraction()), initPad);

        String location = marketToken.getName();
        if (marketToken.getStarSystem() != null) {
            location += ", " + marketToken.getStarSystem().getName();
        }
        info.addPara(location, initPad, getBulletColorForMode(mode));

        unindent(info);
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;

        TooltipMakerAPI text = info.beginImageWithText(getIcon(), 48);
        text.addPara(Misc.getTokenReplaced(getRuinsSpec().getDesc(), marketToken), Misc.getGrayColor(), opad);
        info.addImageWithText(opad);

        info.addPara("Located in the " + marketToken.getStarSystem().getNameWithLowercaseType() + ".", opad,
                Misc.getPositiveHighlightColor(), marketToken.getStarSystem().getBaseName());

        if (!isEnding()) {
            addGenericButton(info, width, ignoreButtonCaption, IGNORE);
        }
//        addGenericButton(info, width, deleteButtonCaption, IGNORE_ALL);
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Tags.INTEL_FLEET_LOG);
        tags.add(INTEL_RUINS);
        return tags;
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        if (isEnding()) {
            return null;
        } else {
            return marketToken.getMarket().getStarSystem().getHyperspaceAnchor();
        }
    }

    @Override
    public String getCommMessageSound() {
        return "ui_discovered_entity";
    }

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

    private int getRuinsTier() {
        switch (ruinsType) {
            case (Conditions.RUINS_SCATTERED):
                return 1;
            case (Conditions.RUINS_WIDESPREAD):
                return 2;
            case (Conditions.RUINS_EXTENSIVE):
                return 3;
            case (Conditions.RUINS_VAST):
                return 4;
            default:
                return 0;
        }
    }

    private float getFraction() {
        return getRuinsTier() / 4f;
    }

    @Override
    public String getSortString() {
        return "Ruins " + getRuinsTier();
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if (buttonId == IGNORE) {
            MemoryAPI mem = this.marketToken.getMemory();
            mem.set(IGNORE_RUINS_MEM_FLAG, true);
            endImmediately();

            if (ui != null) {
                ui.recreateIntelUI();
            }
        }
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        return true;
    }

    @Override
    public void createConfirmationPrompt(Object buttonId, TooltipMakerAPI prompt) {
        if (buttonId == IGNORE) {
            prompt.addPara(ignoreConfirmMessage, 0f); // TODO
        }
    }
}