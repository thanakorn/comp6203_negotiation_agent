package group37.preference;

import genius.core.Bid;
import genius.core.utility.AbstractUtilitySpace;

public class StaticPM implements PreferenceModel {

    private AbstractUtilitySpace utilitySpace;

    public StaticPM(AbstractUtilitySpace utilitySpace){
        this.utilitySpace = utilitySpace;
    }

    @Override
    public double getUtility(Bid bid) {
        return utilitySpace.getUtility(bid);
    }

    @Override
    public void updateModel(Bid bid) {
        return ;
    }
}
