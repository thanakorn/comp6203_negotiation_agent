package group37.preference.lp;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LinearPMSolver {

    private HashMap<Value, Integer> valueIndices;

    public LinearPMSolver(Domain domain, List<Bid> bidOrder){
        valueIndices = new HashMap<>();
        List<Value> allValues = new ArrayList<Value>();
        for (Issue i : domain.getIssues()){
            allValues.addAll(((IssueDiscrete)i).getValues());
        }
        for(int i = 0; i < allValues.size(); i++){
            valueIndices.put(allValues.get(i), i);
        }
    }

    public int getValueIndex(Value v){
        return valueIndices.get(v);
    }

//    public double[] getCoefficientMatrix(Bid bid){
//        int totalOptions = bid.getIssues().stream().mapToInt(i -> ((IssueDiscrete)i).getValues().size()).sum();
//        double[] coefficients = new double[totalOptions];
//        Arrays.fill(coefficients, 0.0f);
//        bid.getIssues().stream().forEach(i -> {
//            bid.getValue(i);
//        });
//        return coefficients;
//    }

}
