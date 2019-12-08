package group37.offering;

import genius.core.Bid;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;
import group37.opponent.AdaptiveFrequencyOM;
import group37.opponent.OpponentModel;
import group37.preference.PreferenceModel;

import java.util.List;

public class HybridOfferingStrategy extends OfferingStrategy {

    private AbstractUtilitySpace utilitySpace;
    private OpponentModel opponentModel;
    private List<Bid> bids;

    public HybridOfferingStrategy(NegotiationInfo info, AbstractUtilitySpace utilitySpace, OpponentModel opponentModel, List<Bid> initialBids) {
        super(info);
        this.utilitySpace = utilitySpace;
        this.opponentModel = opponentModel;
        this.bids = initialBids;
    }

    @Override
    public Bid generateBid(double targetUtility) {
        Bid offer = null;
        double highestOppUtil = -1.0;
        for (Bid bid : bids) {
            double agentUtil = utilitySpace.getUtility(bid);
            double oppUtil = opponentModel.getUtility(offer);
            if (agentUtil >= targetUtility && oppUtil >= highestOppUtil) {
                offer = bid;
                highestOppUtil = oppUtil;
            }
        }
        return offer;
    }

    public Bid generateRandomBid(double targetUtility) {
        Bid randomBid;
        double util;
        int i = 0;
        // try 100 times to find a bid under the target utility
        do {
            randomBid = super.generateRandomBid();
            util = utilitySpace.getUtility(randomBid);
        }
        while (util < targetUtility && i++ < 100);
        return randomBid;
    }
}
