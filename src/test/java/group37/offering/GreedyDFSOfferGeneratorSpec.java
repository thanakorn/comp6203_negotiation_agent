package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.DomainImpl;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.utility.AdditiveUtilitySpace;
import group37.offering.generator.GreedyDFSOfferGenerator;
import group37.offering.generator.OfferGenerator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import static org.junit.Assert.assertTrue;

public class GreedyDFSOfferGeneratorSpec {

    private static String config = "src/test/resources/test_domain.xml";
    private static Domain domain;
    private static AdditiveUtilitySpace utilitySpace;

    @BeforeClass
    public static void init(){
        try{
            domain = new DomainImpl(config);
            utilitySpace = new AdditiveUtilitySpace(domain, config);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testConstructor(){
        OfferGenerator generator = new GreedyDFSOfferGenerator(domain, utilitySpace);
    }

    @Test
    public void testGenerateOffers(){
        OfferGenerator generator = new GreedyDFSOfferGenerator(domain, utilitySpace);
        double minimumUtility = 0.9;
        int maxOfferNumber = 5;
        List<Bid> bids = generator.generateOffers(minimumUtility, maxOfferNumber);
        assertTrue(bids.size() <= maxOfferNumber);
        for(Bid bid : bids){
            assertTrue(utilitySpace.getUtility(bid) >= minimumUtility);
        }
    }

}
