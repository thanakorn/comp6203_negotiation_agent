package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.utility.AbstractUtilitySpace;
import group37.opponent.OpponentModel;

import java.util.List;
import java.util.Random;

/**
 * Return the bid higher than target utility and has max opponent utility or the best bid from opponent
 * with some randomness to prevent opponent to model our agent.
 */
public class HybridOfferingStrategy extends OfferingStrategy {

    private OpponentModel opponentModel;
    private AbstractUtilitySpace userspace;
    private RandomOfferingStrategy randomOfferingStrategy;

    public HybridOfferingStrategy(Domain domain, AbstractUtilitySpace userModel, OpponentModel opponentModel) {
        super(domain);
        this.opponentModel = opponentModel;
        this.userspace = userModel;
        this.randomOfferingStrategy = new RandomOfferingStrategy(domain, userspace);
    }

    @Override
    public Bid generateBid(double targetUtility, List<Bid> offerSpace, Bid opponentBestOffer) {
        Bid highestOpponentBid = null;
        for (Bid bid : offerSpace) {
            if (highestOpponentBid == null || opponentModel.getUtility(bid) > opponentModel.getUtility(highestOpponentBid)) {
                highestOpponentBid = bid;
            }
        }

        Bid randomBid = randomOfferingStrategy.generateBid(targetUtility, offerSpace, opponentBestOffer);
        Bid[] bidChoices = new Bid[]{highestOpponentBid, randomBid};
        Bid chosenBid = bidChoices[new Random().nextInt(bidChoices.length)];

        if (userspace.getUtility(chosenBid) > userspace.getUtility(opponentBestOffer))
            return chosenBid;
        else
            return opponentBestOffer;
    }
}
