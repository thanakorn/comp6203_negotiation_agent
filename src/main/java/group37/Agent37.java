package group37;

import genius.core.AgentID;
import genius.core.Bid;
import genius.core.actions.*;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;
import group37.opponent.jonnyblack.JonnyBlackOM;
import group37.preference.RankDependentPM;

import java.util.List;

public class Agent37 extends AbstractNegotiationParty {

    protected double DEFAULT_INITIAL_TARGET_UTILITY = 0.9;
    protected double DEFAULT_INITIAL_MIN_TARGET_UTILITY = 0.3;

    protected double discountFactor;
    protected double initialReservedValue;
    protected double initialTargetUtility;
    protected double targetUtility;
    protected double initialMinTargetUtility;
    protected double minTargetUtility;

    protected Bid lastOffer;
    protected Action lastAction;

    protected JonnyBlackOM om;
    protected RankDependentPM um;

    @Override
    public void init(NegotiationInfo info){
        super.init(info);

        System.out.println("Initialize agent");

        om = new JonnyBlackOM(getDomain(), 0.5, 10);

        /* Set target utility */
        Bid highestBid;
        Bid lowestBid;
        try{
            highestBid = utilitySpace.getMaxUtilityBid();
            lowestBid = utilitySpace.getMinUtilityBid();
            System.out.println("Highest bid : " + highestBid);
            System.out.println("Lowest bid : " + lowestBid);
            initialTargetUtility = Math.max(utilitySpace.getUtility(highestBid), DEFAULT_INITIAL_TARGET_UTILITY);
            initialMinTargetUtility = Math.max(utilitySpace.getUtility(lowestBid), DEFAULT_INITIAL_MIN_TARGET_UTILITY);
        } catch (Exception e){
            initialTargetUtility = DEFAULT_INITIAL_TARGET_UTILITY;
            initialMinTargetUtility = DEFAULT_INITIAL_MIN_TARGET_UTILITY;
        }
        discountFactor = utilitySpace.getDiscountFactor();
        initialReservedValue = utilitySpace.getReservationValue();
        targetUtility = initialTargetUtility;
        minTargetUtility = Math.max(initialReservedValue, initialMinTargetUtility);

        System.out.println("Target utility : " + targetUtility);
        System.out.println("Minimum target utility : " + minTargetUtility);
        System.out.println("Discount factor : " + discountFactor);
        System.out.println("Reserved value : " + initialReservedValue);

        // TODO : Add initialization logic for preference uncertainty if needed
        if(hasPreferenceUncertainty()){
            System.out.println("Has preference uncertainty, initiate UserModel");
            um = new RankDependentPM(info.getUser(), info.getUserModel(), minTargetUtility, 10);
        }
    }

    @Override
    public void receiveMessage(AgentID sender, Action action) {
        if (action instanceof Offer) {
            lastOffer = ((Offer) action).getBid();
            if(hasPreferenceUncertainty()) {
                /* Update utility space */
                om.updateOM(lastOffer);
                um.updateUM(lastOffer);
            }
        }
    }

    @Override
    public Action chooseAction(List<Class<? extends Action>> possibleActions) {
        Action action;
        if(lastOffer != null){
            double time = timeline.getTime();
            double utility = um.getUtility(lastOffer);
            double opponentUtility = om.getUtility(lastOffer);

            if (timeline.getTime() >= 0.99) {
                if(utility >= utilitySpace.getReservationValueWithDiscount(time)) {
                    action = new Accept(getPartyId(), lastOffer);
                }
                else {
                    action = new EndNegotiation(getPartyId());
                }
            }else{
                if (isAcceptable(utility, opponentUtility, time)) {
                    action = new Accept(getPartyId(), lastOffer);
                } else {
                    concedeTargetUtility(utility, opponentUtility, time);
                    action = new Offer(getPartyId(), generateBid());
                }
            }
        }else{
            action = new Offer(getPartyId(), generateBid());
        }
        lastAction = action;
        reportNegotiationSummary();
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

    /**
     * Update opponent's utility space
     * By default, do not model the opponent's utility
     */
    protected AbstractUtilitySpace estimateOpponentUtilitySpace() {
        return null;
    }

    /**
     * Check whether the offer is acceptable
     * By default, accept any offer which utility is higher than target utility
     */
    protected boolean isAcceptable(double utility, double opponentUtility, double time){
        return utility >= targetUtility;
    }

    /**
     * Concede target utility
     * By default, using linear time-concession strategy
     */
    protected void concedeTargetUtility(double estimatedUtility, double estimatedOpponentUtility, double time){
        targetUtility = Math.max((1.0 - time) * initialTargetUtility, minTargetUtility);
    }

    /**
     * Generating counter-offer
     * By default, generate bid randomly until utility exceed targetUtility (maximum 100 times)
     */
    protected Bid generateBid(){
        Bid randomBid;
        double util;
        int i = 0;
        // try 100 times to find a bid under the target utility
        do
        {
            randomBid = generateRandomBid();
            um.updateUM(randomBid);
            util = um.getUtility(randomBid);
        }
        while (util < targetUtility && i++ < 100);
        return randomBid;
    }

    private void overrideUtilitySpace(AbstractUtilitySpace newUtilitySpace){
        newUtilitySpace.setReservationValue(initialReservedValue);
        newUtilitySpace.setDiscount(discountFactor);
        this.utilitySpace = newUtilitySpace;
    }

    @Override
    public String getDescription() { return "Agent37"; }

    protected void reportNegotiationSummary(){
        System.out.println("________________________________________________________");
        System.out.println("Time : " + (int)(timeline.getTime() * 60));
        System.out.println("Last offer receive : " + lastOffer);
        if(lastOffer != null){
            System.out.println("Estimated last offer utility : " + um.getUtility(lastOffer));
            System.out.println("Estimated last offer opponent utility : " + om.getUtility(lastOffer));
        }
        System.out.println("Target utility : " + targetUtility);
        System.out.println("Minimum target utility : " + minTargetUtility);
        System.out.println("Action taken : " + lastAction);
        if(lastAction instanceof DefaultActionWithBid){
            DefaultActionWithBid lastBid = (DefaultActionWithBid)lastAction;
            System.out.println("Offer utility : " + om.getUtility(lastBid.getBid()));
        }
    }
}
