package group37.opponent.jonnyblack;

import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import java.util.HashMap;

public class FrequencyTable {

    private HashMap<Issue, HashMap<Value, Integer>> frequencyTable;

    public FrequencyTable(Domain domain){
        frequencyTable = new HashMap<Issue, HashMap<Value, Integer>>();
        for(Issue i : domain.getIssues()){
            IssueDiscrete issueDiscrete = (IssueDiscrete)i;
            HashMap<Value, Integer> valueFrequency = new HashMap<Value, Integer>();
            for(Value v : issueDiscrete.getValues()){
                valueFrequency.put(v, 0);
            }
            frequencyTable.put(i, valueFrequency);
        }
    }

    public void updateFrequency(Issue i, Value v){
        frequencyTable.get(i).put(v, frequencyTable.get(i).get(v) + 1);
    }

    public int getFrequency(Issue i, Value v){
        return frequencyTable.get(i).get(v);
    }

}
