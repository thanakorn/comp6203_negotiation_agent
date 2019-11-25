package group37;

import genius.core.AgentID;
import genius.core.Bid;
import genius.core.actions.*;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;
import group37.concession.ConcessionStrategy;
import group37.concession.time.TimeConcessionStrategies;
import group37.offering.OfferingStrategy;
import group37.offering.RandomOfferingStrategy;
import group37.opponent.OpponentModel;
import group37.opponent.jonnyblack.JonnyBlackOM;
import group37.preference.PreferenceModel;
import group37.preference.RankDependentPM;
import group37.preference.StaticPM;
import java.util.List;

public class StandardNegotiationAgent extends AbstractNegotiationParty {

    protected double DEFAULT_TARGET_UTILITY = 0.9;
    protected double DEFAULT_MIN_TARGET_UTILITY = 0.3;
    protected int MINIMUM_BID_ORDER_SIZE = 10;

    protected double discountFactor;
    protected double initialReservedValue;

    protected Bid lastOffer;

    protected OpponentModel opponentModel;
    protected PreferenceModel preferenceModel;
    protected OfferingStrategy offeringStrategy;
    protected ConcessionStrategy concessionStrategy;

    @Override
    public void init(NegotiationInfo info){
        super.init(info);

        System.out.println("Initialize agent");

        /* Set target utility */
        Bid highestBid;
        Bid lowestBid;
        double targetUtility;
        double minTargetUtility;
        try{
            highestBid = utilitySpace.getMaxUtilityBid();
            lowestBid = utilitySpace.getMinUtilityBid();
            System.out.println("Highest bid : " + highestBid);
            System.out.println("Lowest bid : " + lowestBid);
            targetUtility = Math.max(utilitySpace.getUtility(highestBid), DEFAULT_TARGET_UTILITY);
            minTargetUtility = Math.max(utilitySpace.getUtility(lowestBid), DEFAULT_MIN_TARGET_UTILITY);
        } catch (Exception e){
            targetUtility = DEFAULT_TARGET_UTILITY;
            minTargetUtility = DEFAULT_MIN_TARGET_UTILITY;
        }
        discountFactor = utilitySpace.getDiscountFactor();
        initialReservedValue = utilitySpace.getReservationValue();
        minTargetUtility = Math.max(initialReservedValue, minTargetUtility);

        opponentModel = new JonnyBlackOM(getDomain(), 0.5, 10);
        concessionStrategy = TimeConcessionStrategies.LinearTimeConcessionStrategy(targetUtility, minTargetUtility);

        System.out.println("Target utility : " + targetUtility);
        System.out.println("Minimum target utility : " + minTargetUtility);
        System.out.println("Discount factor : " + discountFactor);
        System.out.println("Reserved value : " + initialReservedValue);

        if(hasPreferenceUncertainty()){
            System.out.println("Has preference uncertainty, initiate UserModel");
            preferenceModel = new RankDependentPM(info.getUser(), info.getUserModel(), minTargetUtility, MINIMUM_BID_ORDER_SIZE);
        }else{
            preferenceModel = new StaticPM(info.getUtilitySpace());
        }

        offeringStrategy = new RandomOfferingStrategy(info, this.preferenceModel);
    }

    @Override
    public void receiveMessage(AgentID sender, Action action) {
        if (action instanceof Offer) {
            lastOffer = ((Offer) action).getBid();
            if(hasPreferenceUncertainty()) {
                /* Update user and opponent models */
                preferenceModel.updateModel(lastOffer);
                opponentModel.updateModel(lastOffer);
            }
        }
    }

    @Override
    public Action chooseAction(List<Class<? extends Action>> possibleActions) {
        System.out.println("________________________________________________________");
        System.out.println("Time : " + (int)(timeline.getTime() * 60));
        System.out.println("Last offer receive : " + lastOffer);

        Action action;
        if(lastOffer != null){
            double time = timeline.getTime();
            double targetUtility = concessionStrategy.getTargetUtility(time);
            double utility = preferenceModel.getUtility(lastOffer);
            double opponentUtility = opponentModel.getUtility(lastOffer);

            System.out.println("Estimated last offer utility : " + utility);
            System.out.println("Estimated last offer opponent utility : " + opponentUtility);
            System.out.println("Target utility : " + targetUtility);

            if (timeline.getTime() >= 0.99) {
                if(utility >= utilitySpace.getReservationValueWithDiscount(time)) {
                    action = new Accept(getPartyId(), lastOffer);
                }
                else {
                    action = new EndNegotiation(getPartyId());
                }
            }else{
                if (utility >= targetUtility) {
                    action = new Accept(getPartyId(), lastOffer);
                } else {
                    action = new Offer(getPartyId(), offeringStrategy.generateBid(targetUtility));
                }
            }
        }else{
            action = new Offer(getPartyId(), offeringStrategy.generateRandomBid());
        }

        System.out.println("Action taken : " + action);
        if(action instanceof DefaultActionWithBid){
            DefaultActionWithBid lastBid = (DefaultActionWithBid)action;
            System.out.println("Counter offer utility : " + preferenceModel.getUtility(lastBid.getBid()));
            System.out.println("Counter offer opponent utility : " + opponentModel.getUtility(lastBid.getBid()));
        }
        return action;
    }

    /**
     * Update utility space
     * By default, using default estimator
     */
    @Override
    public AbstractUtilitySpace estimateUtilitySpace() {
        return super.estimateUtilitySpace();
    }

    @Override
    public String getDescription() { return "StandardNegotiationAgent"; }
}
