package group37;

import genius.core.AgentID;
import genius.core.Bid;
import genius.core.actions.Accept;
import genius.core.actions.Action;
import genius.core.actions.EndNegotiation;
import genius.core.actions.Offer;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
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

    protected AbstractUtilitySpace opponentUtilitySpace;

    @Override
    public void init(NegotiationInfo info){
        super.init(info);

        AdditiveUtilitySpace additiveUtilitySpace = (AdditiveUtilitySpace) info.getUtilitySpace();

        /* Set target utility */
        try{
            highestBid = utilitySpace.getMaxUtilityBid();
            lowestBid = utilitySpace.getMinUtilityBid();
            initialMinTargetUtility = utilitySpace.getUtility(highestBid);
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
        for(Issue i : issues){
            IssueDiscrete issueDiscrete = (IssueDiscrete) issues;
            valuesMap.put(i, issueDiscrete.getValues());
        }

        // TODO : Add initialization logic for preference uncertainty if needed
        if(hasPreferenceUncertainty()){

        }
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
        if(lastOffer != null){
            double time = timeline.getTime();
            double utility = getUtility(lastOffer);
            double opponentUtility = opponentUtilitySpace.getUtility(lastOffer);

            if (timeline.getTime() >= 0.99) {
                if(utility >= utilitySpace.getReservationValueWithDiscount(time)) return new Accept(getPartyId(), lastOffer);
                else                                                              return new EndNegotiation(getPartyId());
            }

            if(isAcceptable(utility, opponentUtility, time)){
                return new Accept(getPartyId(), lastOffer);
            }else{
                concedeTargetUtility(utility, opponentUtility, time);
                return new Offer(getPartyId(), generateCounterOffer());
            }
        }
        return new Offer(getPartyId(), generateCounterOffer());
    }

    /**
     * Update utility space
     */
    @Override
    public AbstractUtilitySpace estimateUtilitySpace() {
        return super.estimateUtilitySpace();
    }

    /**
     * Update opponent's utility space
     */
    protected AbstractUtilitySpace estimateOpponentUtilitySpace() {
        return null;
    }

    /**
     * Check whether the offer is acceptable
     */
    protected boolean isAcceptable(double utility, double opponentUtility, double time){
        return utility >= targetUtility;
    }

    /**
     * Concede target utility
     * By default, using linear time-concession strategy
     */
    protected void concedeTargetUtility(double estimatedUtility, double estimatedOpponentUtility, double time){
        targetUtility = (1.0 - time) * initialTargetUtility;
    }

    /**
     * Generating counter-offer
     * By default, generate bid randomly until utility exceed targetUtility (maximum 100 times)
     */
    protected Bid generateCounterOffer(){
        Bid randomBid;
        double util;
        int i = 0;
        // try 100 times to find a bid under the target utility
        do
        {
            randomBid = generateRandomBid();
            util = getUtility(randomBid);
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
}
