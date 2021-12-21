package CaptainsLog.ui.select.types;

import CaptainsLog.ui.select.Selection;
import com.fs.starfarer.api.campaign.SectorEntityToken;

public class SelectToken implements Selection<SectorEntityToken> {

    SectorEntityToken token;

    public SelectToken(SectorEntityToken token) {
        this.token = token;
    }

    @Override
    public String getName() {
        return token.getName();
    }

    @Override
    public SectorEntityToken getValue() {
        return token;
    }
}
