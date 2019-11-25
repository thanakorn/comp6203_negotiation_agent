package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;
import group37.preference.PreferenceModel;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RandomOfferingStrategySpec {

    NegotiationInfo mockInfo = Mockito.mock(NegotiationInfo.class);
    AbstractUtilitySpace mockUtilSpace = Mockito.mock(AbstractUtilitySpace.class);
    Domain mockDomain = Mockito.mock(Domain.class);
    Issue issue1 = (Issue)(new IssueDiscrete("issue1", 1, new String[]{"1", "2"}));
    Issue issue2 = (Issue)(new IssueDiscrete("issue2", 2, new String[]{"A", "B"}));
    List<Issue> issues = Arrays.asList(new Issue[]{issue1, issue2});

    @Test
    public void testConstructor(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        Mockito.when(mockUtilSpace.getDomain()).thenReturn(mockDomain);
        Mockito.when(mockInfo.getUtilitySpace()).thenReturn(mockUtilSpace);
        new RandomOfferingStrategy(mockInfo, Mockito.mock(PreferenceModel.class));
    }

    @Test
    public void testGenerateRandomBid(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        Mockito.when(mockUtilSpace.getDomain()).thenReturn(mockDomain);
        Mockito.when(mockInfo.getUtilitySpace()).thenReturn(mockUtilSpace);

        PreferenceModel pm = Mockito.mock(PreferenceModel.class);
        Bid targetBid = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("1"));
            put(2, new ValueDiscrete("A"));
        }});
        Bid nonTargetBid = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("1"));
            put(2, new ValueDiscrete("B"));
        }});
        Bid nonTargetBid2 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("2"));
            put(2, new ValueDiscrete("A"));
        }});
        Bid nonTargetBid3 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("2"));
            put(2, new ValueDiscrete("B"));
        }});
        Mockito.when(pm.getUtility(targetBid)).thenReturn(0.9);
        Mockito.when(pm.getUtility(nonTargetBid)).thenReturn(0.1);
        Mockito.when(pm.getUtility(nonTargetBid2)).thenReturn(0.1);
        Mockito.when(pm.getUtility(nonTargetBid3)).thenReturn(0.1);
        OfferingStrategy o = new RandomOfferingStrategy(mockInfo, pm);
        assertEquals(targetBid, o.generateBid(0.8));
    }

    @Test
    public void testGenerateRandomBidNoInfiniteLoop(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        Mockito.when(mockUtilSpace.getDomain()).thenReturn(mockDomain);
        Mockito.when(mockInfo.getUtilitySpace()).thenReturn(mockUtilSpace);

        PreferenceModel pm = Mockito.mock(PreferenceModel.class);
        Bid targetBid = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("1"));
            put(2, new ValueDiscrete("A"));
        }});
        Bid nonTargetBid = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("1"));
            put(2, new ValueDiscrete("B"));
        }});
        Bid nonTargetBid2 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("2"));
            put(2, new ValueDiscrete("A"));
        }});
        Bid nonTargetBid3 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("2"));
            put(2, new ValueDiscrete("B"));
        }});
        Mockito.when(pm.getUtility(targetBid)).thenReturn(0.7);
        Mockito.when(pm.getUtility(nonTargetBid)).thenReturn(0.1);
        Mockito.when(pm.getUtility(nonTargetBid2)).thenReturn(0.1);
        Mockito.when(pm.getUtility(nonTargetBid3)).thenReturn(0.1);
        OfferingStrategy o = new RandomOfferingStrategy(mockInfo, pm);
        assertNotNull(o.generateBid(0.8));
    }


}
