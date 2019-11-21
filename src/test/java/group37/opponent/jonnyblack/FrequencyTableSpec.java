package group37.opponent.jonnyblack;

import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.ValueDiscrete;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FrequencyTableSpec {

    Domain mockDomain = Mockito.mock(Domain.class);
    Issue issue1 = (Issue)(new IssueDiscrete("issue1", 1, new String[]{"1", "2", "3"}));
    Issue issue2 = (Issue)(new IssueDiscrete("issue2", 2, new String[]{"A", "B"}));
    List<Issue> issues = Arrays.asList(new Issue[]{issue1, issue2});

    @Test(expected = Exception.class)
    public void testConstructorInitiateAllValues(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        FrequencyTable ft = new FrequencyTable(mockDomain);
        assertEquals(ft.getFrequency(issue1, new ValueDiscrete("1")), 0);
        assertEquals(ft.getFrequency(issue1, new ValueDiscrete("2")), 0);
        assertEquals(ft.getFrequency(issue1, new ValueDiscrete("3")), 0);
        assertEquals(ft.getFrequency(issue2, new ValueDiscrete("A")), 0);
        assertEquals(ft.getFrequency(issue2, new ValueDiscrete("B")), 0);
        ft.getFrequency(issue2, new ValueDiscrete("Z"));
    }

    @Test(expected = Exception.class)
    public void testUpdateAndGetFrequency(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        FrequencyTable ft = new FrequencyTable(mockDomain);
        ft.updateFrequency(issue1, new ValueDiscrete("1"));
        ft.updateFrequency(issue1, new ValueDiscrete("1"));
        ft.updateFrequency(issue1, new ValueDiscrete("1"));
        ft.updateFrequency(issue2, new ValueDiscrete("A"));
        ft.updateFrequency(issue2, new ValueDiscrete("B"));
        assertEquals(ft.getFrequency(issue1, new ValueDiscrete("1")), 3);
        assertEquals(ft.getFrequency(issue2, new ValueDiscrete("A")), 1);
        assertEquals(ft.getFrequency(issue2, new ValueDiscrete("B")), 1);
        ft.updateFrequency(issue2, new ValueDiscrete("C"));
    }

    @Test
    public void testGetTotalFrequency(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        FrequencyTable ft = new FrequencyTable(mockDomain);
        ft.updateFrequency(issue1, new ValueDiscrete("1"));
        ft.updateFrequency(issue1, new ValueDiscrete("2"));
        ft.updateFrequency(issue1, new ValueDiscrete("3"));
        ft.updateFrequency(issue1, new ValueDiscrete("1"));
        ft.updateFrequency(issue1, new ValueDiscrete("1"));
        ft.updateFrequency(issue2, new ValueDiscrete("A"));
        ft.updateFrequency(issue2, new ValueDiscrete("B"));
        assertEquals(ft.getTotalFrequency(issue1), 5);
        assertEquals(ft.getTotalFrequency(issue2), 2);
    }

    @Test
    public void testGetValueRank(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        FrequencyTable ft = new FrequencyTable(mockDomain);
        ft.updateFrequency(issue1, new ValueDiscrete("1"));
        ft.updateFrequency(issue1, new ValueDiscrete("2"));
        ft.updateFrequency(issue1, new ValueDiscrete("3"));
        ft.updateFrequency(issue1, new ValueDiscrete("1"));
        ft.updateFrequency(issue1, new ValueDiscrete("1"));
        ft.updateFrequency(issue2, new ValueDiscrete("A"));
        ft.updateFrequency(issue2, new ValueDiscrete("B"));
        assertEquals(ft.getValueRank(issue1, new ValueDiscrete("1")), 1);
        assertEquals(ft.getValueRank(issue2, new ValueDiscrete("A")), 1);
        assertTrue(ft.getValueRank(issue2, new ValueDiscrete("A")) == 1 || ft.getValueRank(issue2, new ValueDiscrete("B")) == 2);
        assertTrue(ft.getValueRank(issue2, new ValueDiscrete("B")) == 1 || ft.getValueRank(issue2, new ValueDiscrete("B")) == 2);
    }
}
