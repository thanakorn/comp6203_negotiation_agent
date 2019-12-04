package group37.offering.generator.search;

import genius.core.Domain;
import genius.core.issue.*;
import genius.core.utility.AdditiveUtilitySpace;
import genius.core.utility.Evaluator;
import java.util.Map;

public abstract class AbstractOfferSearch {

    protected Map<Objective, Evaluator> evaluators;

    public AbstractOfferSearch(Domain domain, AdditiveUtilitySpace utilitySpace){
        evaluators = utilitySpace.getfEvaluators();
    }
}
