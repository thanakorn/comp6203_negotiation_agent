package group37;

import genius.core.AgentID;
import genius.core.Bid;
import genius.core.actions.*;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.utility.AdditiveUtilitySpace;
import java.util.HashMap;
import java.util.List;

public class StandardNegotiationAgent extends AbstractNegotiationParty {

    protected double DEFAULT_INITIAL_TARGET_UTILITY = 0.9;
    protected double DEFAULT_INITIAL_MIN_TARGET_UTILITY = 0.3;

    protected Bid highestBid;
    protected Bid lowestBid;

    protected double discountFactor;
    protected double initialReservedValue;
    protected double initialTargetUtility;
    protected double targetUtility;
    protected double initialMinTargetUtility;
    protected double minTargetUtility;

    protected List<Issue> issues;
    protected HashMap<Issue, List<ValueDiscrete>> valuesMap;
    protected Bid lastOffer;
    protected Action lastAction;

    protected AbstractUtilitySpace opponentUtilitySpace;

    @Override
    public void init(NegotiationInfo info){
        super.init(info);

        AdditiveUtilitySpace additiveUtilitySpace = (AdditiveUtilitySpace) info.getUtilitySpace();

        /* Set target utility */
        try{
            highestBid = utilitySpace.getMaxUtilityBid();
            lowestBid = utilitySpace.getMinUtilityBid();
            initialTargetUtility = utilitySpace.getUtility(highestBid);
            initialMinTargetUtility = utilitySpace.getUtility(lowestBid);
        } catch (Exception e){
            initialTargetUtility = DEFAULT_INITIAL_TARGET_UTILITY;
            initialMinTargetUtility = DEFAULT_INITIAL_MIN_TARGET_UTILITY;
        }
        discountFactor = utilitySpace.getDiscountFactor();
        initialReservedValue = utilitySpace.getReservationValue();
        targetUtility = initialTargetUtility;
        minTargetUtility = Math.max(initialReservedValue, initialMinTargetUtility);

        /* Store issues and values for future uses */
        issues = additiveUtilitySpace.getDomain().getIssues();
        valuesMap = new HashMap<Issue, List<ValueDiscrete>>();
        for(Issue i : issues){
            IssueDiscrete issueDiscrete = (IssueDiscrete) i;
            valuesMap.put(i, issueDiscrete.getValues());
        }

        // TODO : Add initialization logic for preference uncertainty if needed
        if(hasPreferenceUncertainty()){

        }

        System.out.println("Finish initialize agent");
        System.out.println("Target utility : " + targetUtility);
        System.out.println("Minimum target utility : " + minTargetUtility);
        System.out.println("Highest bid : " + highestBid);
        System.out.println("Lowest bid : " + lowestBid);
    }

    @Override
    public void receiveMessage(AgentID sender, Action action) {
        if (action instanceof Offer) {
            lastOffer = ((Offer) action).getBid();
            if(hasPreferenceUncertainty()) {
                /* Update utility space */
                overrideUtilitySpace(estimateUtilitySpace());
                this.opponentUtilitySpace = estimateOpponentUtilitySpace();
            }
        }
    }

    @Override
    public Action chooseAction(List<Class<? extends Action>> possibleActions) {
        Action action;
        if(lastOffer != null){
            double time = timeline.getTime();
            double utility = getUtilityWithDiscount(lastOffer);
            double opponentUtility = (opponentUtilitySpace != null) ? opponentUtilitySpace.getUtilityWithDiscount(lastOffer, time): 0.0;

            if (timeline.getTime() >= 0.99) {
                if(utility >= utilitySpace.getReservationValueWithDiscount(time)) action = new Accept(getPartyId(), lastOffer);
                else                                                              action = new EndNegotiation(getPartyId());
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
            util = getUtilityWithDiscount(randomBid);
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
    public String getDescription() { return "StandardNegotiationAgent"; }

    protected void reportNegotiationSummary(){
        System.out.println("________________________________________________________");
        System.out.println("Time : " + (int)(timeline.getTime() * 60));
        System.out.println("Last offer receive : " + lastOffer);
        if(lastOffer != null){
            System.out.println("Last offer utility : " + getUtilityWithDiscount(lastOffer));
        }
        System.out.println("Target utility : " + targetUtility);
        System.out.println("Minimum target utility : " + minTargetUtility);
        System.out.println("Action taken : " + lastAction);
        if(lastAction instanceof DefaultActionWithBid){
            DefaultActionWithBid lastBid = (DefaultActionWithBid)lastAction;
            System.out.println("Offer utility : " + getUtilityWithDiscount(lastBid.getBid()));
        }
    }
}
