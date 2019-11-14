package group37;

import genius.core.AgentID;
import genius.core.Bid;
import genius.core.actions.Accept;
import genius.core.actions.Action;
import genius.core.actions.EndNegotiation;
import genius.core.actions.Offer;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;

import java.util.List;

public class Agent37 extends AbstractNegotiationParty {

    private double targetUtility;
    private Bid lastOffer;

    private AbstractUtilitySpace opponentUtilitySpace;

    @Override
    public void init(NegotiationInfo info){
        super.init(info);
        // TODO : Initialize variables
        if(hasPreferenceUncertainty()){
            // TODO : Initialize variables for preference uncertainty
        }
    }

    @Override
    public void receiveMessage(AgentID sender, Action action) {
        if (action instanceof Offer) {
            lastOffer = ((Offer) action).getBid();
            if(hasPreferenceUncertainty()) {
                /** Update utility space **/
                AbstractUtilitySpace newUtilitySpace = estimateUtilitySpace();
                AbstractUtilitySpace newOpponentUtilitySpace = estimateOpponentUtilitySpace();
                this.utilitySpace = newUtilitySpace;
                this.opponentUtilitySpace = newOpponentUtilitySpace;
            }
        }
    }

    @Override
    public Action chooseAction(List<Class<? extends Action>> possibleActions) {
        if(lastOffer != null){
            double time = timeline.getTime();

            if (timeline.getTime() >= 0.99) {
                // TODO : Implement last round strategy
                return new EndNegotiation(getPartyId());
            }

            double estimatedUtility = getUtility(lastOffer);
            double estimatedOpponentUtility = opponentUtilitySpace.getUtility(lastOffer);

            if(isAcceptable(estimatedUtility)){
                return new Accept(getPartyId(), lastOffer);
            }else{
                concedeTargetUtility(estimatedUtility, estimatedOpponentUtility, time);
                return new Offer(getPartyId(), generateCounterOffer());
            }
        }
        return null;
    }

    /**
     * Update utility space
     */
    @Override
    public AbstractUtilitySpace estimateUtilitySpace() {
        return null;
    }

    /**
     * Update opponent's utility space
     */
    private AbstractUtilitySpace estimateOpponentUtilitySpace() {
        return null;
    }

    /**
     * Check whether the offer is acceptable
     */
    private boolean isAcceptable(double estimatedUtility){
        return estimatedUtility >= targetUtility;
    }

    /**
     * Concede target utility
     */
    private double concedeTargetUtility(double estimatedUtility, double estimatedOpponentUtility,double time){
        return targetUtility;
    }

    /**
     * Generating counter-offer
     */
    private Bid generateCounterOffer(){
        return null;
    }

    @Override
    public String getDescription() { return "Agent37"; }
}
