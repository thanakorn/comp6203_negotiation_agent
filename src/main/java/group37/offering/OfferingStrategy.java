package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.*;
import genius.core.parties.NegotiationInfo;

import java.util.HashMap;
import java.util.Random;

public abstract class OfferingStrategy {

    private Random rand;
    protected Domain domain;

    public OfferingStrategy(NegotiationInfo info){
        this.domain = info.getUtilitySpace().getDomain();
        this.rand = new Random(info.getRandomSeed());
    }

    public abstract Bid generateBid(double targetUtility);

    public Bid generateRandomBid() {
        try {
            HashMap<Integer, Value> values = new HashMap<Integer, Value>();
            for (Issue currentIssue : domain.getIssues()) {
                values.put(currentIssue.getNumber(), getRandomValue(currentIssue));
            }

            return new Bid(domain, values);

        } catch (Exception e) {
            return new Bid(domain);
        }
    }

    private Value getRandomValue(Issue currentIssue) throws Exception {
        Value currentValue;
        int index;
        switch (currentIssue.getType()) {
            case DISCRETE:
                IssueDiscrete discreteIssue = (IssueDiscrete) currentIssue;
                index = (rand.nextInt(discreteIssue.getNumberOfValues()));
                currentValue = discreteIssue.getValue(index);
                break;
            case REAL:
                IssueReal realIss = (IssueReal) currentIssue;
                index = rand.nextInt(realIss.getNumberOfDiscretizationSteps()); // check
                // this!
                currentValue = new ValueReal(
                        realIss.getLowerBound() + (((realIss.getUpperBound() - realIss.getLowerBound()))
                                / (realIss.getNumberOfDiscretizationSteps())) * index);
                break;
            case INTEGER:
                IssueInteger integerIssue = (IssueInteger) currentIssue;
                index = rand.nextInt(integerIssue.getUpperBound() - integerIssue.getLowerBound() + 1);
                currentValue = new ValueInteger(integerIssue.getLowerBound() + index);
                break;
            default:
                throw new Exception("issue type " + currentIssue.getType() + " not supported");
        }
        return currentValue;
    }

}
