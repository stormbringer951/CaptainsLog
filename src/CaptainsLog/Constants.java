package CaptainsLog;

import com.fs.starfarer.api.impl.campaign.ids.Tags;

public final class Constants {

    public static final String MOD_ID = "CaptainsLog";
    public static final String CUSTOM_LOG_CREATE_HOTKEY = "CaptainsLog_Create_Custom_Log_Hotkey";

    // Keys used by Stelnet to classify intel for exploration visibility board
    public static final String MEGASTRUCTURE_STELNET_INTEL_TYPE_SUBSTRING = "Structure";
    public static final String SALVAGE_STELNET_INTEL_TYPE_SUBSTRING = "Salvageable";
    public static final String STELNET_INTEL_TYPE_SUBSTRING_RUINS = "Ruins";

    public static final String STELNET_INTEL_TYPE_SUBSTRING_COMM_RELAY = "Comm Relay";

    // Memory Keys
    public static final String CAPTAINS_LOG_MEMORY_KEY = "$captainsLogIntel"; // checked by Stelnet

    // SectorEntityToken tags
    public static final String PROXIMITY_SURVEYED_RUINS = "captains_log_proximity_surveyed_ruins";
    public static final String IGNORE_RUINS_MEM_FLAG = "$captainsLog_ignoreRuins";
    public static final String IGNORE_SALVAGEABLE_MEM_FLAG = "$captainsLog_ignoreSalvageable";

    // Intel category tags
    public static final String STELNET_FILTERED_INTEL_TAG = Tags.INTEL_EXPLORATION;
    public static final String COMM_RELAY_INTEL_TAG = "Comm Relays";
    public static final String CUSTOM_MESSAGE_INTEL_TAG = Tags.INTEL_FLEET_LOG;
    public static final String MEGASTRUCTURE_INTEL_TAG = Tags.INTEL_EXPLORATION;
    public static final String RUINS_INTEL_TAG = "Unexplored Ruins";
    public static final String SALVAGEABLE_INTEL_TAG = "Salvage";
}
