package group37.offering;

import genius.core.Bid;
import genius.core.actions.Offer;
import genius.core.issue.*;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;
import group37.offering.generator.OfferGenerator;
import group37.preference.PreferenceModel;
import genius.core.Domain;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Randomly pick an offer from a set of acceptable offers
 */
public class RandomOfferingStrategy extends OfferingStrategy{

    private static Random random = new Random();

    private OfferGenerator offerGenerator;

    public RandomOfferingStrategy(Domain domain, OfferGenerator offerGenerator, int numOfferGenerate){
        super(domain, offerGenerator, numOfferGenerate);
        this.offerGenerator = offerGenerator;
    }

    public Bid generateBid(double targetUtility) {
        List<Bid> acceptableBids = offerGenerator.generateOffers(targetUtility, numOfferGenerate);
        return acceptableBids.get(random.nextInt(acceptableBids.size()));
    }
}
