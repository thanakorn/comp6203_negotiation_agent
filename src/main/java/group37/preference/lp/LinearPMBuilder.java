package group37.preference.lp;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
import scpsolver.constraints.LinearConstraint;
import scpsolver.constraints.LinearEqualsConstraint;
import java.util.*;

/**
 * Build equations for Linear Programming Preference Model
 */
public class LinearPMBuilder {

    private int numValues;
    private HashMap<Value, Integer> valueIndices;
    private double[][] deltaU;
    private int numSlackVar;
    private List<Bid> bidOrder;

    public LinearPMBuilder(Domain domain, List<Bid> bidOrder){
        valueIndices = new HashMap<>();
        List<Value> allValues = new LinkedList<>();

        for (Issue i : domain.getIssues()){
            allValues.addAll(((IssueDiscrete)i).getValues());
        }
        numValues = allValues.size();
        for(int i = 0; i < allValues.size(); i++){
            valueIndices.put(allValues.get(i), i);
        }

        if(bidOrder.size() > 0) deltaU = generateDeltaU(bidOrder);
        numSlackVar = bidOrder.size() - 1;
        this.bidOrder = bidOrder;
    }

    public double[] getObjective(){
        double[] objective = new double[numValues + numSlackVar];
        for(int i = 0; i < numValues + numSlackVar; i++){
            if(i < numValues) objective[i] = 0.0;
            else objective[i] = 1.0;
        }
        return objective;
    }

    public List<LinearConstraint> getConstraints(){
        List<LinearConstraint> constraints = new LinkedList<>();
        int length = numValues + numSlackVar;
        double[] emptyConstraint = new double[length];
        Arrays.fill(emptyConstraint, 0.0);
        // z >= 0
        for(int i = 0; i < numSlackVar; i++){
            double[] c = emptyConstraint.clone();
            c[numValues + i] = 1.0;
            constraints.add(new LinearBiggerThanEqualsConstraint(c, 0.0, "Z" + i));
        }
        // deltaU + z >= 0
        for(int i = 0; i < numSlackVar; i++){
            double[] c = emptyConstraint.clone();
            double[] delta = deltaU[i];
            System.arraycopy(delta, 0, c, 0, numValues);
            c[numValues + i] = 1.0;
            constraints.add(new LinearBiggerThanEqualsConstraint(c, 0.0, "DELTA" + i));
        }

        // u(x) >= 0
        for(int i = 0; i < numValues; i++){
            double[] c = emptyConstraint.clone();
            c[i] = 1.0;
            constraints.add(new LinearBiggerThanEqualsConstraint(c, 0.0, "U" + i));
        }

        // U(maxBid) = 1
        Bid topBid = bidOrder.get(bidOrder.size() - 1);
        double[] c = emptyConstraint.clone();
        for(Issue i: topBid.getIssues()){
            Value v = topBid.getValue(i);
            c[valueIndices.get(v)] = 1.0;
        }
        constraints.add(new LinearEqualsConstraint(c, 1.0, "UMAX"));

        return constraints;
    }

    public int getValueIndex(Value v){
        return valueIndices.get(v);
    }

    public double[][] getDeltaU(){ return deltaU; }

    private double[][] generateDeltaU(List<Bid> bidOrder){
        deltaU = new double[bidOrder.size() - 1][numValues];
        for(int i = bidOrder.size() - 1, j = 0; i > 0; i--, j++){
            deltaU[j] = getDeltaCoefficients(bidOrder.get(i), bidOrder.get(i - 1));
        }
        return deltaU;
    }

    public double[] getDeltaCoefficients(Bid a, Bid b){
        double[] aCoef = getBidCoefficients(a);
        double[] bCoef = getBidCoefficients(b);
        double[] delta = new double[aCoef.length];
        Arrays.setAll(delta, n -> aCoef[n] - bCoef[n]);
        return delta;
    }

    public double[] getBidCoefficients(Bid bid){
        double[] coefficients = new double[numValues];
        for (Issue i : bid.getIssues()){
            Value v = bid.getValue(i);
            coefficients[valueIndices.get(v)] = 1.0;
        }
        return coefficients;
    }

}
