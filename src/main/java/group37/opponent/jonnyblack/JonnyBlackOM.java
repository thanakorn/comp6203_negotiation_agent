package group37.opponent.jonnyblack;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JonnyBlackOM {

    private HashMap<Issue, List<Value>> issueValues;
    private FrequencyTable ft;

    public JonnyBlackOM(Domain domain){
        ft = new FrequencyTable(domain);

        issueValues = new HashMap<>();
        for(Issue i : domain.getIssues()){
            IssueDiscrete issueDiscrete = (IssueDiscrete)i;
            List<Value> values = new ArrayList<>();
            for(Value v : issueDiscrete.getValues()){
                values.add(v);
            }
            issueValues.put(i, values);
        }
    }

    public void updateOM(Bid opponentBid){
        opponentBid.getIssues().stream().forEach(i -> ft.updateFrequency(i, opponentBid.getValue(i)));
    }

    public double getUtility(Bid opponentBid){
        Map<Issue, Double> weights = issueValues.keySet().stream()
                                        .collect(Collectors.toMap(Function.identity(), i -> getWeight(i)));

        double totalWeight = weights.values().stream().mapToDouble(w -> w).sum();

        Map<Issue, Double> normalizedWeights = issueValues.keySet().stream()
                                                .collect(Collectors.toMap(Function.identity(), i -> weights.get(i) / totalWeight));

        return opponentBid.getIssues().stream()
                .mapToDouble(issue -> normalizedWeights.get(issue) * getValue(issue, opponentBid.getValue(issue)))
                .sum();
    }

    private double getValue(Issue issue, Value v){
        int rank = ft.getValueRank(issue, v);
        int numOptions = issueValues.get(issue).size();
        return ((double)(numOptions - rank + 1) / (double) numOptions);
    }

    private double getWeight(Issue issue){
        Integer totalFrequency = ft.getTotalFrequency(issue);
        return issueValues.get(issue).stream()
                .mapToDouble(v -> Math.pow((double)ft.getFrequency(issue, v) / (double)totalFrequency, 2))
                .sum();
    }
}
