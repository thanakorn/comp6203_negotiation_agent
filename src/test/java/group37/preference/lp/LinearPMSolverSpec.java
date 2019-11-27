package group37.preference.lp;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.ValueDiscrete;
import genius.core.uncertainty.User;
import genius.core.uncertainty.UserModel;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
}
