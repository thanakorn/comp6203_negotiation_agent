package group37.offering.generator;

import genius.core.Domain;
import genius.core.issue.*;
import genius.core.utility.AdditiveUtilitySpace;
import genius.core.utility.Evaluator;
import group37.offering.generator.OfferGenerator;

import java.util.Map;

public abstract class AbstractOfferGenerator implements OfferGenerator {

    protected Domain domain;
    protected AdditiveUtilitySpace utilitySpace;

    public AbstractOfferGenerator(Domain domain, AdditiveUtilitySpace utilitySpace){
        this.domain = domain;
        this.utilitySpace = utilitySpace;
    }
}
