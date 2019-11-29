package group37.preference;

import genius.core.Bid;
import genius.core.uncertainty.User;
import genius.core.uncertainty.UserModel;
import java.util.List;

public class RankDependentPM implements PreferenceModel {

    private User user;
    private UserModel userModel;
    private int minBidOrderSize;
    private  double unknownUtility;

    public RankDependentPM(User user, UserModel userModel, double unknownUtility, int minBidOrderSize){
        this.user = user;
        this.userModel = userModel;
        this.minBidOrderSize = minBidOrderSize;
        this.unknownUtility = unknownUtility;
    }

    @Override
    public void updateModel(Bid bid){
        if(!userModel.getBidRanking().getBidOrder().contains(bid)){
            UserModel newUserModel = user.elicitRank(bid, userModel);
            userModel = newUserModel;
        }
    }

    @Override
    public double getUtility(Bid bid){
        List<Bid> bidOrder = userModel.getBidRanking().getBidOrder();
        if(bidOrder.size() >= minBidOrderSize)
            return (double)(bidOrder.indexOf(bid) + 1) / (double) bidOrder.size();
        else
            return unknownUtility;
    }

}
