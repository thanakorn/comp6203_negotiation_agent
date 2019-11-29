package group37.offering;

import genius.core.Bid;
import genius.core.parties.NegotiationInfo;
import group37.opponent.OpponentModel;
import group37.preference.PreferenceModel;
import java.util.LinkedList;
import java.util.List;

/**
 * Return the bid higher than target utility and has max opponent utility
 */
public class MaxOpponentUtilityOfferingStrategy extends OfferingStrategy{

    private final int NUM_OFFER_GENERATE = 5;
    private final int MAXIMUM_ROUND_GENERATE = 100;
    private RandomOfferingStrategy randomOffering;
    private OpponentModel opponentModel;

    public MaxOpponentUtilityOfferingStrategy(NegotiationInfo info, PreferenceModel preferenceModel, OpponentModel opponentModel) {
        super(info);
        this.randomOffering = new RandomOfferingStrategy(info, preferenceModel);
        this.opponentModel = opponentModel;
    }

    @Override
    public Bid generateBid(double targetUtility) {
        List<Bid> acceptableBids = new LinkedList<>();
        for(int i = 0; i < MAXIMUM_ROUND_GENERATE; i++){
            Bid b = randomOffering.generateBid(targetUtility);
            if(!acceptableBids.contains(b)) acceptableBids.add(b);
            if(acceptableBids.size() == NUM_OFFER_GENERATE) break;
        }
        Bid highestOpponentBid = null;
        for(int i = 0; i < acceptableBids.size(); i++){
            Bid b = acceptableBids.get(i);
            if(highestOpponentBid == null || opponentModel.getUtility(b) > opponentModel.getUtility(highestOpponentBid)){
                highestOpponentBid = b;
            }
        }
        return highestOpponentBid;
    }
}
