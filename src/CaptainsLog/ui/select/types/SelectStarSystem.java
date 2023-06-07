package CaptainsLog.ui.select.types;

import CaptainsLog.ui.select.Selection;
import com.fs.starfarer.api.campaign.StarSystemAPI;

public class SelectStarSystem implements Selection<StarSystemAPI> {

    private final StarSystemAPI system;

    public SelectStarSystem(StarSystemAPI system) {
        this.system = system;
    }

    @Override
    public String getName() {
        return system.getName();
    }

    @Override
    public StarSystemAPI getValue() {
        return system;
    }
}
