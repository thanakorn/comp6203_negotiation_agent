package group37;

import genius.core.AgentID;
import genius.core.Bid;
import genius.core.Domain;
import genius.core.actions.*;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;
import group37.concession.BoulwareStrategy;
import group37.concession.ConcessionStrategy;
import group37.concession.time.TimeConcessionStrategies;
import group37.offering.MaxOpponentUtilityOfferingStrategy;
import group37.offering.OfferingStrategy;
import group37.offering.generator.GreedyDFSOfferGenerator;
import group37.offering.generator.OfferGenerator;
import group37.opponent.AdaptiveFrequencyOM;
import group37.opponent.OpponentModel;
import group37.preference.PreferenceModel;
import group37.preference.lp.LinearProgrammingPM;
import scpsolver.problems.LinearProgram;

import java.util.List;
import java.util.Random;

public class Agent37 extends AbstractNegotiationParty {

    protected final double CONVESSION_VALUE = 0.1;
    protected final double EXP_FILTER = 0.8;
    protected final int OM_MAX_BID_ORDER_SIZE = 10000;
    protected final int NUM_BID_PREGENERATE = 1000;
    protected final double MAX_ELICIT_COST = 0.15;
    protected final int MAX_ELICITATION_ROUND = 500;

    private double maxUtility = 1.0;
    private double minUtility = 0.4;

    private ConcessionStrategy concessionStrategy;
    private OpponentModel opponentModel;
    private PreferenceModel preferenceModel;
    private OfferingStrategy offeringStrategy;

    private Bid lastOffer;
    protected List<Bid> pregeneratedBids;

    @Override
    public void init(NegotiationInfo info) {
        super.init(info);
        this.concessionStrategy = new BoulwareStrategy(maxUtility, minUtility, CONVESSION_VALUE);
        this.opponentModel = new AdaptiveFrequencyOM(getDomain(), OM_MAX_BID_ORDER_SIZE, EXP_FILTER);
        OfferGenerator offerGenerator = new GreedyDFSOfferGenerator(getDomain(), utilitySpace);
        // TODO : Return sorted list
        pregeneratedBids = offerGenerator.generateOffers( (minUtility + maxUtility) / 2.0, NUM_BID_PREGENERATE);
        this.offeringStrategy = new MaxOpponentUtilityOfferingStrategy(getDomain(), opponentModel);
    }

    @Override
    public Action chooseAction(List<Class<? extends Action>> list) {
        Action action;
        if(lastOffer != null){
            double time = timeline.getTime();
            double targetUtility = concessionStrategy.getTargetUtility(time);
            double utility = getUtility(lastOffer);
            double opponentUtility = opponentModel.getUtility(lastOffer);

            if(time >= 0.99){
                if(utility >= minUtility) action = new Accept(getPartyId(), lastOffer);
                else                      action = new EndNegotiation(getPartyId());
            }else{
                if(utility >= targetUtility) action = new Accept(getPartyId(), lastOffer);
                else                         action = new Offer(getPartyId(),offeringStrategy.generateBid(targetUtility, pregeneratedBids));
            }
        }else{
            action = new Offer(getPartyId(), pregeneratedBids.get(pregeneratedBids.size() - 1));
        }
        System.out.println("Action taken : " + action);
        if (action instanceof DefaultActionWithBid) {
            DefaultActionWithBid lastBid = (DefaultActionWithBid) action;
            System.out.println("Action utility : " + getUtility(lastBid.getBid()));
            System.out.println("Actionr opponent utility : " + opponentModel.getUtility(lastBid.getBid()));
        }
        return action;
    }

    @Override
    public void receiveMessage(AgentID sender, Action action) {
        if (action instanceof Offer) {
            lastOffer = ((Offer) action).getBid();
            opponentModel.updateModel(lastOffer);
        }
    }

    @Override
    public AbstractUtilitySpace estimateUtilitySpace(){
        /**
         * Pre-generate bid order
         */
        int counter = 0;
        do{
            Bid bid = generateRandomBid();
            userModel = user.elicitRank(bid, userModel);
            counter++;
        }while(user.getTotalBother() < MAX_ELICIT_COST && counter < MAX_ELICITATION_ROUND);
        this.preferenceModel = new LinearProgrammingPM(getDomain(), user, userModel);
        return this.preferenceModel.estimateUtilitySpace();
    }

    @Override
    public String getDescription() {
        return "Agent37";
    }

    public void displaySummary(){
        double time = timeline.getTime();
        System.out.println("Time : " + time);
        System.out.println("LastOffer : " + lastOffer);
        System.out.println("LastOffer Utility : " + getUtility(lastOffer));
        System.out.println("LastOffer OpponentUtility : " + opponentModel.getUtility(lastOffer));
        System.out.println("Target Utility : " + concessionStrategy.getTargetUtility(time));
    }

}
