package group37.opponent.jonnyblack;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class JonnyBlackOMSpec {

    Domain mockDomain = Mockito.mock(Domain.class);
    Issue issue1 = (Issue)(new IssueDiscrete("issue1", 1, new String[]{"1", "2", "3"}));
    Issue issue2 = (Issue)(new IssueDiscrete("issue2", 2, new String[]{"A", "B", "C"}));
    List<Issue> issues = Arrays.asList(new Issue[]{issue1, issue2});

    @Test
    public void testUpdateOMUpdateFreq(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        JonnyBlackOM om = new JonnyBlackOM(mockDomain);
        HashMap<Integer, Value> issueValues = new HashMap<Integer, Value>();
        issueValues.put(1, new ValueDiscrete("1"));
        issueValues.put(2, new ValueDiscrete("B"));
        Bid opponentBid = new Bid(mockDomain, issueValues);
        om.updateOM(opponentBid);
        assertEquals(1.0, om.getUtility(opponentBid), 0.01);
    }

    @Test
    public void testGetUtility(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        JonnyBlackOM om = new JonnyBlackOM(mockDomain);
        Bid bid1 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("1"));
            put(2, new ValueDiscrete("A"));
        }});
        om.updateOM(bid1);
        om.updateOM(bid1);
        om.updateOM(bid1);
        Bid bid2 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("1"));
            put(2, new ValueDiscrete("B"));
        }});
        om.updateOM(bid2);
        om.updateOM(bid2);
        om.updateOM(bid2);
        om.updateOM(bid2);
        om.updateOM(bid2);
        Bid bid3 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("1"));
            put(2, new ValueDiscrete("C"));
        }});
        om.updateOM(bid3);
        Bid bid4 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("2"));
            put(2, new ValueDiscrete("C"));
        }});
        om.updateOM(bid4);

        Bid lastOffer = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("1"));
            put(2, new ValueDiscrete("C"));
        }});
        Bid lastOffer2 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("3"));
            put(2, new ValueDiscrete("A"));
        }});
//        assertEquals(0.789, om.getUtility(lastOffer), 0.001);
        assertEquals(0.434, om.getUtility(lastOffer2), 0.01);
    }
}
