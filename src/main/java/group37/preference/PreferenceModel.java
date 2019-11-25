package group37.preference;

import genius.core.Bid;

public interface PreferenceModel {
    double getUtility(Bid bid);
    void updateModel(Bid bid);
}
