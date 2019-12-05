package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;
import group37.offering.generator.OfferGenerator;
import group37.opponent.OpponentModel;
import group37.preference.PreferenceModel;
import java.util.LinkedList;
import java.util.List;

/**
 * Return the bid higher than target utility and has max opponent utility
 */
public class MaxOpponentUtilityOfferingStrategy extends OfferingStrategy{

    private OpponentModel opponentModel;

    public MaxOpponentUtilityOfferingStrategy(Domain domain, OpponentModel opponentModel, OfferGenerator offerGenerator, int numOfferGenerate) {
        super(domain, offerGenerator, numOfferGenerate);
        this.opponentModel = opponentModel;
    }

    @Override
    public Bid generateBid(double targetUtility) {
        List<Bid> acceptableBids = offerGenerator.generateOffers(targetUtility, numOfferGenerate);
        Bid highestOpponentBid = null;
        for(Bid bid : acceptableBids){
            if(highestOpponentBid == null || opponentModel.getUtility(bid) > opponentModel.getUtility(highestOpponentBid)){
                highestOpponentBid = bid;
            }
        }
        return highestOpponentBid;
    }
}
