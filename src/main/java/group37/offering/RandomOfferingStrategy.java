package group37.offering;

import genius.core.Bid;
import genius.core.issue.*;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;
import group37.preference.PreferenceModel;
import genius.core.Domain;

import java.util.HashMap;
import java.util.Random;

public class RandomOfferingStrategy extends OfferingStrategy{

    private AbstractUtilitySpace utilitySpace;

    public RandomOfferingStrategy(NegotiationInfo info, AbstractUtilitySpace utilitySpace){
        super(info);
        this.utilitySpace = utilitySpace;
    }

    public Bid generateBid(double targetUtility) {
        Bid randomBid;
        double util;
        int i = 0;
        // try 100 times to find a bid under the target utility
        do
        {
            randomBid = super.generateRandomBid();
            util = utilitySpace.getUtility(randomBid);
        }
        while (util < targetUtility && i++ < 100);
        return randomBid;
    }
}
