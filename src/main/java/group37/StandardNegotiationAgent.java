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

import java.util.List;

public class StandardNegotiationAgent extends AbstractNegotiationParty {

    protected double DEFAULT_TARGET_UTILITY = 1.0;
    protected double DEFAULT_MIN_TARGET_UTILITY = 0.5;

    protected double discountFactor;

    protected Bid lastOffer;
    protected double targetUtility;
    protected double minTargetUtility;

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
        minTargetUtility = Math.max(utilitySpace.getReservationValue(), minTargetUtility);

        opponentModel = new JonnyBlackOM(this.getDomain(), 0.5, 10);
        concessionStrategy = TimeConcessionStrategies.LinearTimeConcessionStrategy(targetUtility, minTargetUtility);

        System.out.println("Target utility : " + targetUtility);
        System.out.println("Minimum target utility : " + minTargetUtility);
        System.out.println("Discount factor : " + discountFactor);
        System.out.println("Reserved value : " + utilitySpace.getReservationValue());

        offeringStrategy = new RandomOfferingStrategy(info, utilitySpace);
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
            double utility = getUtility(lastOffer);
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
            System.out.println("Counter offer utility : " + getUtility(lastBid.getBid()));
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
        System.out.println("Max Utility : " + userModel.getBidRanking().getHighUtility());
        System.out.println("Min Utility : " + userModel.getBidRanking().getLowUtility());
        preferenceModel = new RankDependentPM(getDomain(), user, userModel, minTargetUtility - 0.1, 10);
        return preferenceModel.estimateUtilitySpace();
    }

    @Override
    public String getDescription() { return "StandardNegotiationAgent"; }
}
