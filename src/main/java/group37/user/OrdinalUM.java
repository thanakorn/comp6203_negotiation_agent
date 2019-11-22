package group37.user;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.uncertainty.BidRanking;
import genius.core.uncertainty.User;
import genius.core.uncertainty.UserModel;

import java.util.List;

public class OrdinalUM {

    private User user;
    private UserModel userModel;
    private int minBidOrderSize;
    private  double initialUtility;

    public OrdinalUM(User _user, UserModel _userModel, double _initialUtility, int _minBidOrderSize){
        user = _user;
        userModel = _userModel;
        minBidOrderSize = _minBidOrderSize;
        initialUtility = _initialUtility;
    }

    public void updateUM(Bid bid){
        if(!userModel.getBidRanking().getBidOrder().contains(bid)){
            UserModel newUserModel = user.elicitRank(bid, userModel);
            userModel = newUserModel;
        }
    }

    public double getUtility(Bid bid){
        List<Bid> bidOrder = userModel.getBidRanking().getBidOrder();
        if(bidOrder.size() >= minBidOrderSize)
            return (double)(bidOrder.indexOf(bid) + 1) / (double) bidOrder.size();
        else
            return initialUtility;
    }

}
