package group37.opponent;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.utility.AdditiveUtilitySpace;
import group37.opponent.jonnyblack.FrequencyTable;

public class DistributionFrequencyOM implements OpponentModel {

    private AdditiveUtilitySpace opponentUtilitySpace;
    private FrequencyTable frequencyTable;

    public DistributionFrequencyOM(Domain domain) {
        opponentUtilitySpace = new AdditiveUtilitySpace(domain);
        frequencyTable = new FrequencyTable(domain);
    }

    @Override
    public double getUtility(Bid bid) {
        return 0;
    }

    @Override
    public void updateModel(Bid bid) {
        frequencyTable.updateFrequency(bid);
    }
}
