package group37;

import genius.core.AgentID;
import genius.core.Bid;
import genius.core.actions.*;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.parties.NegotiationInfo;
import genius.core.uncertainty.UserModel;
import genius.core.utility.AbstractUtilitySpace;
import group37.concession.BoulwareStrategy;
import group37.concession.ConcessionRate;
import group37.concession.ConcessionStrategy;
import group37.offering.MaxOpponentUtilityOfferingStrategy;
import group37.offering.OfferingStrategy;
import group37.offering.generator.GreedyDFSOfferGenerator;
import group37.offering.generator.OfferGenerator;
import group37.opponent.AdaptiveFrequencyOM;
import group37.opponent.OpponentConcessionModel;
import group37.opponent.OpponentModel;
import group37.preference.PreferenceModel;
import group37.preference.UserModelScaler;
import group37.preference.lp.LinearPreferenceModel;

import java.util.LinkedList;
import java.util.List;

public class Agent37 extends AbstractNegotiationParty {

    protected final double INITIAL_CONCESSION = 0.05;
    protected final double EXP_FILTER = 0.8;
    protected final int OM_MAX_BID_ORDER_SIZE = 10000;
    protected final double PERCENT_BID_GENERATE = 0.2;
    protected final int MAX_BID_GENERATE = 1000;
    protected final int MIN_BID_GENERATE = 20;
    protected final double MAX_ELICIT_COST = 0.1;
    protected final int MAX_ELICITATION_ROUND = 50;
    protected final int MAX_BID_ORDER_SIZE = 100;

    private double maxUtility = 1.0;
    private double minUtility = 0.4;

    private ConcessionStrategy concessionStrategy;
    private OpponentModel opponentModel;
    private OpponentConcessionModel opponentConcessionModel;
    private PreferenceModel preferenceModel;
    private OfferingStrategy offeringStrategy;

    private Bid lastOffer;
    protected List<Bid> offerSubSpace;

    @Override
    public void init(NegotiationInfo info) {
        super.init(info);
        this.concessionStrategy = new BoulwareStrategy(maxUtility, minUtility, INITIAL_CONCESSION, ConcessionRate.HARD_HEAD);
        this.opponentModel = new AdaptiveFrequencyOM(getDomain(), OM_MAX_BID_ORDER_SIZE, EXP_FILTER);
        opponentConcessionModel = new OpponentConcessionModel(100);


        long numBidGenerate = (int) (PERCENT_BID_GENERATE * getDomain().getNumberOfPossibleBids());
        if (numBidGenerate > MAX_BID_GENERATE) numBidGenerate = MAX_BID_GENERATE;
        else if (numBidGenerate < MIN_BID_GENERATE) numBidGenerate = MIN_BID_GENERATE;

        OfferGenerator offerGenerator = new GreedyDFSOfferGenerator(getDomain(), utilitySpace);
        offerSubSpace = offerGenerator.generateOffers((minUtility + maxUtility) / 2.0, numBidGenerate); // Sorted from max to min
        this.offeringStrategy = new MaxOpponentUtilityOfferingStrategy(getDomain(), opponentModel);
    }

    @Override
    public Action chooseAction(List<Class<? extends Action>> list) {
        Action action;
        if (lastOffer != null) {
            double time = timeline.getTime();
            double targetUtility = concessionStrategy.getTargetUtility(time);
            double utility = getUtility(lastOffer);

            if (time >= 0.99) {
                if (utility >= minUtility) action = new Accept(getPartyId(), lastOffer);
                else action = new EndNegotiation(getPartyId());
            } else {
                List<Bid> targetOffers = selectTargetOffers(targetUtility);
                Bid counterOffer = offeringStrategy.generateBid(targetUtility, targetOffers);
                if (utility >= targetUtility) action = new Accept(getPartyId(), lastOffer);
                else action = new Offer(getPartyId(), counterOffer);
            }
        } else {
            action = new Offer(getPartyId(), offerSubSpace.get(0));
        }
        displaySummary();
        System.out.println("Action taken              : " + action);
        if (action instanceof DefaultActionWithBid) {
            DefaultActionWithBid lastBid = (DefaultActionWithBid) action;
            System.out.println("Action Utility            : " + getUtility(lastBid.getBid()));
            System.out.println("Action Opponent Utility   : " + opponentModel.getUtility(lastBid.getBid()));
        }
        System.out.println("________________________________________________________________________________________________________________________________________");
        return action;
    }

    @Override
    public void receiveMessage(AgentID sender, Action action) {
        if (action instanceof Offer) {
            lastOffer = ((Offer) action).getBid();
            opponentModel.updateModel(lastOffer);
            opponentConcessionModel.updateModel(timeline.getTime(), getUtility(lastOffer));

            if (opponentConcessionModel.isOpponentConcess())
                concessionStrategy.adjustRate(ConcessionRate.SUPER_HARD_HEAD);
            else {
                concessionStrategy.adjustRate(ConcessionRate.HARD_HEAD);
            }
        }
    }

    @Override
    public AbstractUtilitySpace estimateUtilitySpace() {
        /**
         * Pre-generate bid order
         */
        try {
//            int counter = 0;
//            Random random = new Random();
//            do{
//                Bid bid = getDomain().getRandomBid(random);
//                userModel = user.elicitRank(bid, userModel);
//                counter++;
//            }while(user.getTotalBother() < MAX_ELICIT_COST && counter < MAX_ELICITATION_ROUND);
            UserModel scaledUserModel = UserModelScaler.scaleUserModel(userModel, MAX_BID_ORDER_SIZE);
            preferenceModel = new LinearPreferenceModel(getDomain(), user, scaledUserModel);
            AbstractUtilitySpace estimatedUtilitySpace = preferenceModel.estimateUtilitySpace();
            return estimatedUtilitySpace;
        } catch (Exception ex) {
            ex.printStackTrace();
            return super.estimateUtilitySpace();
        }
    }

    @Override
    public String getDescription() {
        return "Agent37";
    }

    private List<Bid> selectTargetOffers(double targetUtility) {
        List<Bid> acceptableOffers = new LinkedList<>();
        try {
            acceptableOffers.add(utilitySpace.getMaxUtilityBid());
            for (Bid b : offerSubSpace) {
                if (getUtility(b) >= targetUtility) acceptableOffers.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return acceptableOffers;
    }

    private void displaySummary() {
        double time = timeline.getTime();
        System.out.println("Time                      : " + time);
        System.out.println("LastOffer                 : " + lastOffer);
        System.out.println("LastOffer Utility         : " + getUtility(lastOffer));
        System.out.println("LastOffer OpponentUtility : " + opponentModel.getUtility(lastOffer));
        System.out.println("Target Utility            : " + concessionStrategy.getTargetUtility(time));
        System.out.println("Opponent Concession       : " + opponentConcessionModel.isOpponentConcess());
    }

}
