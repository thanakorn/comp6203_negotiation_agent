package group37.preference.lp;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LinearPMSolverSpec {

    Domain mockDomain = Mockito.mock(Domain.class);
    Issue issue1 = (Issue)(new IssueDiscrete("issue1", 1, new String[]{"1", "2", "3"}));
    Issue issue2 = (Issue)(new IssueDiscrete("issue2", 2, new String[]{"A", "B"}));
    List<Issue> issues = Arrays.asList(new Issue[]{issue1, issue2});

    @Test
    public void testConstructorValueIndices(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        LinearPMSolver s = new LinearPMSolver(mockDomain, new ArrayList<Bid>());
        assertEquals(0, s.getValueIndex(new ValueDiscrete("1")));
        assertEquals(1, s.getValueIndex(new ValueDiscrete("2")));
        assertEquals(2, s.getValueIndex(new ValueDiscrete("3")));
        assertEquals(3, s.getValueIndex(new ValueDiscrete("A")));
        assertEquals(4, s.getValueIndex(new ValueDiscrete("B")));
    }

    @Test
    public void testGetBidCoefficients(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        LinearPMSolver s = new LinearPMSolver(mockDomain, new ArrayList<Bid>());
        Bid bid1 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("1"));
            put(2, new ValueDiscrete("A"));
        }});
        Bid bid2 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("3"));
            put(2, new ValueDiscrete("B"));
        }});
        Bid bid3 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("2"));
            put(2, new ValueDiscrete("A"));
        }});
        assertTrue(Arrays.equals(new double[]{1.0, 0.0, 0.0, 1.0, 0.0}, s.getBidCoefficients(bid1)));
        assertTrue(Arrays.equals(new double[]{0.0, 0.0, 1.0, 0.0, 1.0}, s.getBidCoefficients(bid2)));
        assertTrue(Arrays.equals(new double[]{0.0, 1.0, 0.0, 1.0, 0.0}, s.getBidCoefficients(bid3)));
    }

    @Test
    public void testGenerateDeltaCoefficients(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        Bid bid1 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("1"));
            put(2, new ValueDiscrete("A"));
        }});
        Bid bid2 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("3"));
            put(2, new ValueDiscrete("B"));
        }});
        Bid bid3 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("2"));
            put(2, new ValueDiscrete("A"));
        }});
        LinearPMSolver s = new LinearPMSolver(mockDomain, new ArrayList<Bid>(){{
            add(bid1);
            add(bid2);
        }});
        assertTrue(Arrays.equals(new Double[]{1.0, 0.0, -1.0, 1.0, -1.0}, s.getDeltaCoefficients(bid1, bid2)));
        assertTrue(Arrays.equals(new Double[]{1.0, -1.0, 0.0, 0.0, 0.0}, s.getDeltaCoefficients(bid1, bid3)));
        assertTrue(Arrays.equals(new Double[]{0.0, -1.0, 1.0, -1.0, 1.0}, s.getDeltaCoefficients(bid2, bid3)));
        assertTrue(Arrays.equals(new Double[]{-1.0, 1.0, 0.0, 0.0, 0.0}, s.getDeltaCoefficients(bid3, bid1)));
    }

    @Test
    public void testConstructorGenerateDeltaU(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        Bid bid1 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("1"));
            put(2, new ValueDiscrete("A"));
        }});
        Bid bid2 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("3"));
            put(2, new ValueDiscrete("B"));
        }});
        Bid bid3 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
            put(1, new ValueDiscrete("2"));
            put(2, new ValueDiscrete("A"));
        }});
        LinearPMSolver s = new LinearPMSolver(mockDomain, new ArrayList<Bid>(){{
            add(bid3);
            add(bid2);
            add(bid1);
        }});
        List<Double[]> deltaU = s.getDeltaU();
        assertEquals(2, s.getDeltaU().size());
        assertTrue(Arrays.equals(new Double[]{1.0, 0.0, -1.0, 1.0, -1.0}, deltaU.get(0)));
        assertTrue(Arrays.equals(new Double[]{0.0, -1.0, 1.0, -1.0, 1.0}, deltaU.get(1)));
    }

}
