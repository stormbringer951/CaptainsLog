package CaptainsLog;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;

public final class SettingsUtils {

    public static boolean excludeCommRelays() {
        return !getBooleanSetting("CaptainsLog_Enable_Comm_Relays");
    }

    public static boolean excludeMegastructures() {
        return !getBooleanSetting("CaptainsLog_Enable_Colony_Structures");
    }

    public static boolean excludeRuinsReports() {
        return !getBooleanSetting("CaptainsLog_Enable_Ruins");
    }

    public static boolean excludeSalvageableReports() {
        return !getBooleanSetting("CaptainsLog_Enable_Salvageable");
    }

    private static boolean getBooleanSetting(String s) {
        Boolean setting = LunaSettings.getBoolean(Constants.MOD_ID, s);
        if (setting == null) {
            return false; // default faulse
        }
        return setting;
    }

    public static boolean isStelnetEnabled() {
        return Global.getSettings().getModManager().isModEnabled("stelnet");
    }
}
