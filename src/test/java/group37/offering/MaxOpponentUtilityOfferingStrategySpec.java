package group37.offering;

import agents.anac.y2016.caduceus.agents.Caduceus.Opponent;
import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;
import group37.opponent.OpponentModel;
import group37.preference.PreferenceModel;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class MaxOpponentUtilityOfferingStrategySpec {

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
        new MaxOpponentUtilityOfferingStrategy(mockInfo, Mockito.mock(AbstractUtilitySpace.class), Mockito.mock(OpponentModel.class));
    }

    @Test
    public void testGenerateBid(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        Mockito.when(mockUtilSpace.getDomain()).thenReturn(mockDomain);
        Mockito.when(mockInfo.getUtilitySpace()).thenReturn(mockUtilSpace);

        AbstractUtilitySpace utilitySpace = Mockito.mock(AbstractUtilitySpace.class);
        OpponentModel om = Mockito.mock(OpponentModel.class);
        Bid targetBid = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("1"));
            put(2, new ValueDiscrete("A"));
        }});
        Bid targetBid2 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("1"));
            put(2, new ValueDiscrete("B"));
        }});
        Bid nonTargetBid = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("2"));
            put(2, new ValueDiscrete("A"));
        }});
        Bid nonTargetBid2 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("2"));
            put(2, new ValueDiscrete("B"));
        }});

        Mockito.when(utilitySpace.getUtility(targetBid)).thenReturn(0.9);
        Mockito.when(utilitySpace.getUtility(targetBid2)).thenReturn(0.8);
        Mockito.when(utilitySpace.getUtility(nonTargetBid)).thenReturn(0.1);
        Mockito.when(utilitySpace.getUtility(nonTargetBid2)).thenReturn(0.1);
        Mockito.when(om.getUtility(targetBid)).thenReturn(0.45);
        Mockito.when(om.getUtility(targetBid2)).thenReturn(0.5);
        Mockito.when(om.getUtility(nonTargetBid)).thenReturn(0.6);
        Mockito.when(om.getUtility(nonTargetBid2)).thenReturn(0.6);
        OfferingStrategy o = new MaxOpponentUtilityOfferingStrategy(mockInfo, utilitySpace, om);
        assertEquals(targetBid2, o.generateBid(0.7));
        assertEquals(targetBid2, o.generateBid(0.7));
    }


}
