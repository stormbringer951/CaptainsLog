package CaptainsLog;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;

public final class SettingsUtils {

    public static boolean excludeCommRelays() {
        return !getBooleanSetting("CaptainsLog_Enable_Comm_Relays", true);
    }

    public static boolean excludeMegastructures() {
        return !getBooleanSetting("CaptainsLog_Enable_Colony_Structures", true);
    }

    public static boolean excludeRuinsReports() {
        return !getBooleanSetting("CaptainsLog_Enable_Ruins", true);
    }

    public static boolean excludeSalvageableReports() {
        return !getBooleanSetting("CaptainsLog_Enable_Salvageable", true);
    }

    private static boolean getBooleanSetting(String s, boolean defaultValue) {
        Boolean setting = LunaSettings.getBoolean(Constants.MOD_ID, s);
        if (setting == null) {
            return defaultValue;
        }
        return setting;
    }

    public static boolean isStelnetEnabled() {
        return Global.getSettings().getModManager().isModEnabled("stelnet");
    }
}
