package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
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

    public HybridOfferingStrategy(Domain domain, AbstractUtilitySpace utilitySpace, OpponentModel opponentModel, List<Bid> initialBids) {
        super(domain);
        this.utilitySpace = utilitySpace;
        this.opponentModel = opponentModel;
        this.bids = initialBids;
    }

    @Override
    public Bid generateBid(double targetUtility, List<Bid> offerSpace) {
        System.out.println(((AdaptiveFrequencyOM) opponentModel).opponentUtilitySpace.toString());
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
}
