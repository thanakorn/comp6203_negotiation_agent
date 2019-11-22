package group37.opponent.jonnyblack;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.IntStream;

public class FrequencyTable {

    private static final Comparator<Entry<Value, Integer>> frequencyComparator  = new Comparator<Entry<Value, Integer>>() {
        @Override
        public int compare(Entry<Value, Integer> e1, Entry<Value, Integer> e2) {
            Integer v1 = e1.getValue();
            Integer v2 = e2.getValue();
            return v1.compareTo(v2);
        }
    };

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

    public void updateFrequency(Bid bid){
        bid.getIssues().stream().forEach(issue -> {
            Value v = bid.getValue(issue);
            frequencyTable.get(issue).put(v, frequencyTable.get(issue).get(v) + 1);
        });

    }

    public int getFrequency(Issue i, Value v){
        return frequencyTable.get(i).get(v);
    }

    public int getTotalFrequency(){
        return frequencyTable.get(frequencyTable.keySet().iterator().next())
                            .entrySet()
                            .stream()
                            .mapToInt(x -> x.getValue())
                            .sum();
    }

    public int getValueRank(Issue issue, Value v){
        List<Entry<Value, Integer>> valueFrequencies = new ArrayList<Entry<Value, Integer>>(frequencyTable.get(issue).entrySet());
        Collections.sort(valueFrequencies, Collections.reverseOrder(frequencyComparator));
        return IntStream.range(0, valueFrequencies.size())
                        .filter(i -> valueFrequencies.get(i).getKey().equals(v))
                        .findFirst().getAsInt() + 1;
    }

}
