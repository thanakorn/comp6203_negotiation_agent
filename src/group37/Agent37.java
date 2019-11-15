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

public class Agent37 extends StandardNegotiationAgent {

    private double targetUtility;
    private Bid lastOffer;

    private AbstractUtilitySpace opponentUtilitySpace;

    @Override
    public void init(NegotiationInfo info){
        super.init(info);
        // TODO : Add anything if needed
    }

    /**
     * Estimate utility space
     */
    @Override
    public AbstractUtilitySpace estimateUtilitySpace() {
        // TODO : Implement user mo
        return null;
    }

    /**
     * Estimate opponent's utility space
     */
    @Override
    protected AbstractUtilitySpace estimateOpponentUtilitySpace() {
        return null;
    }

    /**
     * Check whether the offer is acceptable
     */
    @Override
    protected boolean isAcceptable(double utility, double opponentUtility, double time){
        return true;
    }

    /**
     * Concession strategy
     */
    @Override
    protected void concedeTargetUtility(double estimatedUtility, double estimatedOpponentUtility,double time){
    }

    /**
     * Generating counter-offer
     */
    @Override
    protected Bid generateBid(){
        return null;
    }

    @Override
    public String getDescription() { return "Agent37"; }
}
