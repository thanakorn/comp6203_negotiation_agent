package group37.offering.generator;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.utility.AdditiveUtilitySpace;
import genius.core.utility.EvaluatorDiscrete;

import java.util.*;

public class GreedyDFSOfferGenerator extends AbstractOfferGenerator{

    List<Issue> issueOrder;
    Map<Issue, List<ValueDiscrete>> valuesOrder;

    public GreedyDFSOfferGenerator(Domain domain, AdditiveUtilitySpace utilitySpace) {
        super(domain, utilitySpace);
        issueOrder = new LinkedList<Issue>();
        valuesOrder = new HashMap<>();
        for(Issue i: domain.getIssues()){
            issueOrder.add(i);
            List<ValueDiscrete> values = ((IssueDiscrete)i).getValues();
            EvaluatorDiscrete e = (EvaluatorDiscrete) utilitySpace.getEvaluator(i);
            values.sort(Comparator.comparing(v -> e.getValue(v)));
            valuesOrder.put(i, values);
        }
        issueOrder.sort(Comparator.comparing(i -> utilitySpace.getWeight(i), Comparator.reverseOrder()));
    }

    @Override
    public List<Bid> generateOffers(double minimumUtility, int maxBidNumber) {
        List<Bid> solutions = new LinkedList<>();
        Stack<HashMap<Integer, Value>> fringe = new Stack<>();
        fringe.add(new HashMap<>());
        do{
            HashMap<Integer, Value> node = fringe.pop();
            if(node.size() == issueOrder.size()){
                Bid bid = new Bid(domain, node);
                if(utilitySpace.getUtility(bid) >= minimumUtility) {
                    solutions.add(bid);
                }
            }else{
                int issueIndex = node.size();
                Issue i = issueOrder.get(issueIndex);
                for(ValueDiscrete v : valuesOrder.get(i)){
                    HashMap<Integer, Value> newNode = new HashMap<>(node);
                    newNode.put(i.getNumber(), v);
                    fringe.push(newNode);
                }
            }
        }while(solutions.size() < maxBidNumber && !fringe.isEmpty());

        return solutions;
    }
}
