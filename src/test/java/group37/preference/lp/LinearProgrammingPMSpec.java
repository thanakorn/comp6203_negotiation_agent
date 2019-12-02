package group37.preference.lp;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.uncertainty.BidRanking;
import genius.core.uncertainty.User;
import genius.core.uncertainty.UserModel;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LinearProgrammingPMSpec {

    Domain mockDomain = Mockito.mock(Domain.class);
    Issue issue1 = new IssueDiscrete("issue1", 1, new String[]{"1", "2", "3"});
    Issue issue2 = new IssueDiscrete("issue2", 2, new String[]{"A", "B"});
    List<Issue> issues = Arrays.asList(new Issue[]{issue1, issue2});

    User mockUser = Mockito.mock(User.class);
    UserModel mockUserModel = Mockito.mock(UserModel.class);
    BidRanking mockBidRank = Mockito.mock(BidRanking.class);

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
    Bid bid4 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
        put(1, new ValueDiscrete("2"));
        put(2, new ValueDiscrete("B"));
    }});

    @Test
    public void testConstructor(){
        BidRanking bidRank = new BidRanking(new ArrayList<Bid>(){{
            add(bid3);
            add(bid2);
            add(bid1);
        }}, 0.0, 1.0);
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        Mockito.when(mockUserModel.getBidRanking()).thenReturn(bidRank);
        LinearProgrammingPM pm = new LinearProgrammingPM(mockDomain, mockUser, mockUserModel);
    }

    @Test
    public void testConstructorGetUtility(){
        BidRanking bidRank = new BidRanking(new ArrayList<Bid>(){{
            add(bid3);
            add(bid2);
            add(bid1);
        }}, 0.0, 1.0);
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        Mockito.when(mockUserModel.getBidRanking()).thenReturn(bidRank);
        LinearProgrammingPM pm = new LinearProgrammingPM(mockDomain, mockUser, mockUserModel);
        assertEquals(1.0, pm.getUtility(bid1), 0.00);
    }

    @Test
    public void testValuesUtility(){
        BidRanking bidRank = new BidRanking(new ArrayList<Bid>(){{
            add(bid3);
            add(bid2);
            add(bid1);
        }}, 0.3, 1.0);
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        Mockito.when(mockUserModel.getBidRanking()).thenReturn(bidRank);
        LinearProgrammingPM pm = new LinearProgrammingPM(mockDomain, mockUser, mockUserModel);
        assertEquals(1.0, pm.getUtility(bid1), 0.00);
        assertEquals(0.3, pm.getUtility(bid3), 0.00);
        assertTrue(pm.getUtility(bid1) >= pm.getUtility(bid2));
        assertTrue(pm.getUtility(bid2) >= pm.getUtility(bid3));
    }

    @Test
    public void testUpdateModel(){
        BidRanking bidRank = new BidRanking(new ArrayList<Bid>(){{
            add(bid3);
            add(bid2);
            add(bid1);
        }}, 0.3, 0.9);
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        Mockito.when(mockUserModel.getBidRanking()).thenReturn(bidRank);
        LinearProgrammingPM pm = new LinearProgrammingPM(mockDomain, mockUser, mockUserModel);

        UserModel newUserModel = new UserModel(
                new BidRanking(new ArrayList<Bid>(){{
                    add(bid3);
                    add(bid2);
                    add(bid1);
                    add(bid4);
                }}, 0.25, 1.0)
        );
        Mockito.when(mockUser.elicitRank(bid4, mockUserModel)).thenReturn(newUserModel);
        pm.updateModel(bid4);

        assertEquals(1.0, pm.getUtility(bid4), 0.00);
        assertEquals(0.25, pm.getUtility(bid3), 0.00);
        assertTrue(pm.getUtility(bid4) >= pm.getUtility(bid1));
        assertTrue(pm.getUtility(bid1) >= pm.getUtility(bid2));
        assertTrue(pm.getUtility(bid3) >= pm.getUtility(bid3));
    }

}
