package group37.offering.generator;

import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.utility.AdditiveUtilitySpace;
import java.util.*;

public class SimulatedAnnealingOfferGenerator extends GreedyDFSOfferGenerator {

    private static Random random = new Random();

    public SimulatedAnnealingOfferGenerator(Domain domain, AdditiveUtilitySpace utilitySpace) {
        super(domain, utilitySpace);
    }

    @Override
    protected void expand(HashMap<Integer, Value> node){
        // Temperature will reduce when move to next important issue, and probShuffle will reduce quadratically with temperature
        double temperature = 1.0 - ((double)node.size() / (double) issueOrder.size());
        double probShuffle = Math.pow(temperature, 2);

        int nextIssueIndex = node.size();
        Issue nextIssue = issueOrder.get(nextIssueIndex);
        double rand = random.nextDouble();
        List<ValueDiscrete> valuesToExpand = valuesOrder.get(nextIssue);
        if(probShuffle > rand){
            valuesToExpand = new LinkedList<>(valuesToExpand);
            Collections.shuffle(valuesToExpand);
        }
        for(ValueDiscrete v : valuesToExpand){
            HashMap<Integer, Value> newNode = new HashMap<>(node);
            newNode.put(nextIssue.getNumber(), v);
            fringe.push(newNode);
        }
    }
}
