package group37;

import genius.core.parties.NegotiationInfo;
import group37.concession.time.TimeConcessionStrategies;

public class Agent37 extends StandardNegotiationAgent {

    @Override
    public void init(NegotiationInfo info){
        super.init(info);
        this.concessionStrategy = TimeConcessionStrategies.QuadraticTimeConcessionStrategy(targetUtility, minTargetUtility);
    }

    @Override
    public String getDescription() { return "Agent37"; }

}
