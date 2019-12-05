package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.DomainImpl;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.utility.AdditiveUtilitySpace;
import group37.offering.generator.GreedyDFSOfferGenerator;
import group37.offering.generator.OfferGenerator;
import group37.opponent.OpponentModel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class MaxOpponentUtilityOfferingStrategySpec {

    private static Domain domain;
    private static AbstractUtilitySpace utilitySpace;

    @BeforeClass
    public static void init(){
        try {
            domain = new DomainImpl("src/test/resources/test_domain.xml");
            utilitySpace = new AdditiveUtilitySpace(domain, "src/test/resources/test_domain.xml");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testConstructor(){
        new MaxOpponentUtilityOfferingStrategy(domain, Mockito.mock(OpponentModel.class), Mockito.mock(OfferGenerator.class), 5);
    }

    @Test
    public void testGenerateBid(){
        OfferGenerator generator = new GreedyDFSOfferGenerator(domain, utilitySpace);
        List<Bid> bids = generator.generateOffers(0.7, 4);
        OpponentModel om = Mockito.mock(OpponentModel.class);

        Mockito.when(om.getUtility(bids.get(0))).thenReturn(0.45);
        Mockito.when(om.getUtility(bids.get(1))).thenReturn(0.5);
        Mockito.when(om.getUtility(bids.get(2))).thenReturn(0.65);
        Mockito.when(om.getUtility(bids.get(3))).thenReturn(0.6);

        OfferingStrategy s = new MaxOpponentUtilityOfferingStrategy(domain, om, generator, 4);
        assertEquals(bids.get(2), s.generateBid(0.7));
    }


}
