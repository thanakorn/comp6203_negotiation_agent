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
    private List<Double[]> deltaU;

    public LinearPMSolver(Domain domain, List<Bid> bidOrder){
        valueIndices = new HashMap<>();
        List<Value> allValues = new ArrayList<Value>();

        for (Issue i : domain.getIssues()){
            allValues.addAll(((IssueDiscrete)i).getValues());
        }

        for(int i = 0; i < allValues.size(); i++){
            valueIndices.put(allValues.get(i), i);
        }

        deltaU = generateDeltaU(bidOrder);
    }

    public int getValueIndex(Value v){
        return valueIndices.get(v);
    }

    public List<Double[]> getDeltaU(){ return deltaU; }

    private List<Double[]> generateDeltaU(List<Bid> bidOrder){
        deltaU = new ArrayList<>();
        for(int i = bidOrder.size() - 1; i > 0; i--){
            deltaU.add(getDeltaCoefficients(bidOrder.get(i), bidOrder.get(i - 1)));
        }
        return deltaU;
    }

    public Double[] getDeltaCoefficients(Bid a, Bid b){
        double[] aCoef = getBidCoefficients(a);
        double[] bCoef = getBidCoefficients(b);
        Double[] delta = new Double[aCoef.length];
        Arrays.setAll(delta, n -> aCoef[n] - bCoef[n]);
        return delta;
    }

    public double[] getBidCoefficients(Bid bid){
        double[] coefficients = new double[valueIndices.keySet().size()];
        for (Issue i : bid.getIssues()){
            Value v = bid.getValue(i);
            coefficients[getValueIndex(v)] = 1.0;
        }
        return coefficients;
    }

}
