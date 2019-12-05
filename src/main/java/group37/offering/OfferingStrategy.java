package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.*;
import genius.core.parties.NegotiationInfo;
import group37.offering.generator.OfferGenerator;

import java.util.HashMap;
import java.util.Random;

public abstract class OfferingStrategy {


    protected Domain domain;
    protected OfferGenerator offerGenerator;
    protected  int numOfferGenerate;

    public OfferingStrategy(Domain domain, OfferGenerator offerGenerator, int numOfferGenerate){
        this.domain = domain;
        this.offerGenerator = offerGenerator;
        this.numOfferGenerate = numOfferGenerate;
    }

    public abstract Bid generateBid(double targetUtility);

}
