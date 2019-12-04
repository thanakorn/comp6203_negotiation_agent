package group37.preference;

import genius.core.Bid;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.utility.UtilitySpace;

public interface PreferenceModel {
    AbstractUtilitySpace estimateUtilitySpace();
    void updateModel(Bid bid);
}
