package group37.preference.lp;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.uncertainty.BidRanking;
import genius.core.uncertainty.User;
import genius.core.uncertainty.UserModel;
import group37.preference.PreferenceModel;
import scpsolver.constraints.LinearConstraint;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.problems.LinearProgram;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LinearProgrammingPM implements PreferenceModel {

    private Domain domain;
    private User user;
    private UserModel userModel;
    private List<Value> allValues;
    private HashMap<Value, Double> valuesUtility;

    public LinearProgrammingPM(Domain domain, User user, UserModel userModel){
        this.domain = domain;
        this.user = user;
        this.userModel = userModel;
        this.allValues = new LinkedList<>();
        for (Issue i : domain.getIssues()){
            allValues.addAll(((IssueDiscrete)i).getValues());
        }
        this.valuesUtility = findValuesUtility(userModel.getBidRanking());
    }

    @Override
    public double getUtility(Bid bid) {
        List<Issue> issues = bid.getIssues();
        double totalUtility = 0.0;
        for(Issue i : issues){
            Value v = bid.getValue(i);
            totalUtility += valuesUtility.get(v);
        }
        return totalUtility;
    }

    @Override
    public void updateModel(Bid bid) {
        if(!userModel.getBidRanking().getBidOrder().contains(bid)){
            UserModel newUserModel = user.elicitRank(bid, userModel);
            userModel = newUserModel;
            valuesUtility = findValuesUtility(newUserModel.getBidRanking());
        }
    }

    private HashMap<Value, Double> findValuesUtility(BidRanking bidRank){
        List<Bid> bidOrder = bidRank.getBidOrder();
        HashMap<Value, Double> valuesUtility = new HashMap<>();
        LinearPMBuilder builder = new LinearPMBuilder(domain, bidRank);

        double[] objective = builder.getObjective();
        LinearProgram lp = new LinearProgram(objective);
        List<LinearConstraint> constraints = builder.getConstraints();
        for(LinearConstraint c : constraints){
            lp.addConstraint(c);
        }
        lp.setMinProblem(true);
        LinearProgramSolver solver  = SolverFactory.newDefault();
        double[] solution = solver.solve(lp);

        for(int i = 0; i < allValues.size(); i++){
            Value v = allValues.get(i);
            valuesUtility.put(v, solution[builder.getValueIndex(v)]);
        }
        return valuesUtility;
    }

    public void displayValuesUtility(){
        for(Map.Entry<Value, Double> e: valuesUtility.entrySet()){
            System.out.println(e.getKey() + " " + e.getValue());
        }
    }
}
