package group37.user;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.uncertainty.BidRanking;
import genius.core.uncertainty.User;
import genius.core.uncertainty.UserModel;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;

public class OrdinalUMSpec {

    Domain mockDomain = Mockito.mock(Domain.class);

    User mockUser = Mockito.mock(User.class);
    UserModel mockUserModel = Mockito.mock(UserModel.class);

    Issue issue1 = (Issue)(new IssueDiscrete("issue1", 1, new String[]{"1", "2", "3"}));
    Issue issue2 = (Issue)(new IssueDiscrete("issue2", 2, new String[]{"A", "B", "C"}));

    Bid bid1 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
        put(1, new ValueDiscrete("1"));
        put(2, new ValueDiscrete("A"));
    }});
    Bid bid2 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
        put(1, new ValueDiscrete("1"));
        put(2, new ValueDiscrete("B"));
    }});
    Bid bid3 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
        put(1, new ValueDiscrete("1"));
        put(2, new ValueDiscrete("C"));
    }});
    Bid bid4 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
        put(1, new ValueDiscrete("2"));
        put(2, new ValueDiscrete("A"));
    }});
    Bid bid5 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
        put(1, new ValueDiscrete("3"));
        put(2, new ValueDiscrete("C"));
    }});

    @Test
    public void testContructor(){
        OrdinalUM um = new OrdinalUM(mockUser, mockUserModel, 0.5,1);
    }

    @Test
    public void testUpdateUM(){
        UserModel userModel = new UserModel(
            new BidRanking(new ArrayList<Bid>(){{
                add(bid1);
                add(bid2);
            }}, 0.0, 1.0)
        );
        UserModel newUserModel = new UserModel(
            new BidRanking(new ArrayList<Bid>(){{
                add(bid1);
                add(bid2);
                add(bid3); // Add bid3 to UM with highest utility
            }}, 0.0, 1.0)
        );

        Mockito.when(mockUser.elicitRank(bid3, userModel)).thenReturn(newUserModel);
        OrdinalUM um = new OrdinalUM(mockUser, userModel, 0.5,1);
        um.updateUM(bid3);
        assertEquals(1.0, um.getUtility(bid3), 0.01);
    }

    @Test
    public void testUpdateDuplicateUM(){
        UserModel userModel = new UserModel(
                new BidRanking(new ArrayList<Bid>(){{
                    add(bid1);
                    add(bid2);
                }}, 0.0, 1.0)
        );
        UserModel newUserModel = new UserModel(
                new BidRanking(new ArrayList<Bid>(){{
                    add(bid1);
                    add(bid2);
                    add(bid3); // Add bid3 to UM with highest utility
                }}, 0.0, 1.0)
        );

        Mockito.when(mockUser.elicitRank(bid2, userModel)).thenReturn(newUserModel);
        OrdinalUM um = new OrdinalUM(mockUser, userModel, 0.5,1);
        um.updateUM(bid2);
        assertEquals(1.0, um.getUtility(bid2), 0.01);
    }

    @Test
    public void testGetUtility(){
        UserModel userModel = new UserModel(
                new BidRanking(new ArrayList<Bid>(){{
                    add(bid1);
                    add(bid2);
                }}, 0.0, 1.0)
        );
        UserModel newUserModel = new UserModel(
                new BidRanking(new ArrayList<Bid>(){{
                    add(bid1);
                    add(bid2);
                    add(bid3); // Add bid3 to UM with highest utility
                }}, 0.0, 1.0)
        );
        UserModel newUserModel2 = new UserModel(
                new BidRanking(new ArrayList<Bid>(){{
                    add(bid1);
                    add(bid2);
                    add(bid3);
                    add(bid4); // Add bid4 to UM with highest utility
                }}, 0.0, 1.0)
        );
        UserModel newUserModel3 = new UserModel(
                new BidRanking(new ArrayList<Bid>(){{
                    add(bid1);
                    add(bid2);
                    add(bid5); // Add bid5 to UM with 3rd highest utility
                    add(bid3);
                    add(bid4);
                }}, 0.0, 1.0)
        );
        Mockito.when(mockUser.elicitRank(bid3, userModel)).thenReturn(newUserModel);
        Mockito.when(mockUser.elicitRank(bid4, newUserModel)).thenReturn(newUserModel2);
        Mockito.when(mockUser.elicitRank(bid5, newUserModel2)).thenReturn(newUserModel3);
        OrdinalUM um = new OrdinalUM(mockUser, userModel, 0.5, 2);
        um.updateUM(bid3); // Bid3 should have highest utility at this step
        assertEquals(1.0, um.getUtility(bid3), 0.01);
        um.updateUM(bid4); // Bid4 has highest utility at this step
        assertEquals(1.0, um.getUtility(bid4), 0.01);
        um.updateUM(bid5); // Bid4 has highest utility at this step
        assertEquals(0.8, um.getUtility(bid3), 0.01);
        assertEquals(1.0, um.getUtility(bid4), 0.01);
        assertEquals(0.6, um.getUtility(bid5), 0.01);
    }

    @Test
    public void testMinOrderSize(){
        double initialUtility = 0.5;
        UserModel userModel = new UserModel(
                new BidRanking(new ArrayList<Bid>(){{
                    add(bid1);
                    add(bid2);
                }}, 0.0, 1.0)
        );
        UserModel newUserModel = new UserModel(
                new BidRanking(new ArrayList<Bid>(){{
                    add(bid1);
                    add(bid2);
                    add(bid3); // Add bid3 to UM with highest utility
                }}, 0.0, 1.0)
        );

        OrdinalUM um = new OrdinalUM(mockUser, userModel, initialUtility, 3);
        assertEquals(initialUtility, um.getUtility(bid3), 0.01);
        Mockito.when(mockUser.elicitRank(bid3, userModel)).thenReturn(newUserModel);
        um.updateUM(bid3);
        assertEquals(1.0, um.getUtility(bid3), 0.01);
    }

}
