package group37.offering;

import genius.core.Bid;
import genius.core.issue.*;
import genius.core.parties.NegotiationInfo;
import group37.preference.PreferenceModel;
import genius.core.Domain;

import java.util.HashMap;
import java.util.Random;

public class RandomOfferingStrategy extends OfferingStrategy{

    private PreferenceModel preferenceModel;
    private Random rand;

    public RandomOfferingStrategy(NegotiationInfo info, PreferenceModel preferenceModel){
        super(info);
        this.preferenceModel = preferenceModel;
    }

    public Bid generateBid(double targetUtility) {
        Bid randomBid;
        double util;
        int i = 0;
        // try 100 times to find a bid under the target utility
        do
        {
            randomBid = super.generateRandomBid();
            util = preferenceModel.getUtility(randomBid);
        }
        while (util < targetUtility && i++ < 100);
        return randomBid;
    }
}
