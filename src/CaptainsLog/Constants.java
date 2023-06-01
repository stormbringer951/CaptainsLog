package CaptainsLog;

import com.fs.starfarer.api.impl.campaign.ids.Tags;

public final class Constants {

    public static final String MOD_ID = "CaptainsLog";

    // Keys used by Stelnet to classify intel for exploration visibility board
    public static final String MEGASTRUCTURE_STELNET_INTEL_TYPE_SUBSTRING = "Structure";
    public static final String SALVAGE_STELNET_INTEL_TYPE_SUBSTRING = "Salvageable";
    public static final String STELNET_INTEL_TYPE_SUBSTRING_RUINS = "Ruins";

    public static final String STELNET_INTEL_TYPE_SUBSTRING_COMM_RELAY = "Comm Relay";
    public static final String CAPTAINS_LOG_MEMORY_KEY = "$captainsLogIntel";

    // Intel category tags
    public static String STELNET_FILTERED_INTEL_TAG = Tags.INTEL_EXPLORATION;
    public static String COMM_RELAY_INTEL_TAG = "Comm Relays";
    public static String CUSTOM_MESSAGE_INTEL_TAG = Tags.INTEL_FLEET_LOG;
    public static String MEGASTRUCTURE_INTEL_TAG = Tags.INTEL_EXPLORATION;
    public static String RUINS_INTEL_TAG = "Unexplored Ruins";
    public static String SALVAGEABLE_INTEL_TAG = "Salvage";
}
