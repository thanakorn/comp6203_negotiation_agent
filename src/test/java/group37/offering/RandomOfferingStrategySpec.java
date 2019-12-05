package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.DomainImpl;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.utility.AdditiveUtilitySpace;
import group37.offering.generator.GreedyDFSOfferGenerator;
import group37.offering.generator.OfferGenerator;
import group37.offering.generator.RandomOfferGenerator;
import group37.preference.PreferenceModel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class RandomOfferingStrategySpec {

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
        new RandomOfferingStrategy(domain, Mockito.mock(OfferGenerator.class), 5);
    }

    @Test
    public void testGenerateBid(){
        OfferGenerator generator = new RandomOfferGenerator(domain, utilitySpace);
        OfferingStrategy o = new RandomOfferingStrategy(domain, generator, 5);
        Bid bid1 = o.generateBid(0.9);
        Bid bid2 = o.generateBid(0.8);
        Bid bid3 = o.generateBid(0.95);
        assertTrue(utilitySpace.getUtility(bid1) >= 0.9);
        assertTrue(utilitySpace.getUtility(bid2) >= 0.8);
        assertTrue(utilitySpace.getUtility(bid3) >= 0.95);
    }
}
