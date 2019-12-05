package group37;

import genius.core.Bid;
import genius.core.parties.NegotiationInfo;
import group37.concession.time.TimeConcessionStrategies;
import scpsolver.problems.LinearProgram;

import java.util.List;

public class Agent37 extends StandardNegotiationAgent {

    private final int PM_BID_ORDER_SIZE = 20;

    @Override
    public void init(NegotiationInfo info) {
        super.init(info);
        this.concessionStrategy = TimeConcessionStrategies.CubicTimeConcessionStrategy(targetUtility, minUtility);
        if (hasPreferenceUncertainty()) {
            // Pre-generate bid order
            LinearProgram lp = new LinearProgram(new double[]{1.0});
            List<Bid> currentBidOrder = userModel.getBidRanking().getBidOrder();
            if (currentBidOrder.size() < PM_BID_ORDER_SIZE) {
                for (int i = 0; i < PM_BID_ORDER_SIZE - currentBidOrder.size(); i++) {
                    preferenceModel.updateModel(offeringStrategy.generateRandomBid());
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return "Agent37";
    }

}
