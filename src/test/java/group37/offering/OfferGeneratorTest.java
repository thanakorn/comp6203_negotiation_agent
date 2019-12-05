package group37.offering;

import genius.core.Domain;
import genius.core.DomainImpl;
import genius.core.utility.AdditiveUtilitySpace;
import org.junit.Test;

public class OfferGeneratorTest {

    String[] configs = new String[]{
            "src/test/resources/party1_utility.xml",
            "src/test/resources/energy_consumer.xml",
            "src/test/resources/energy_distributor.xml",
            "src/test/resources/SmartEnergyGrid_util1.xml",
            "src/test/resources/SmartGridDomain_util1.xml"
    };

    @Test
    public void testOfferGenerator(){
        for(String file : configs){
            try{
                Domain domain = new DomainImpl(file);
                AdditiveUtilitySpace utilitySpace = new AdditiveUtilitySpace(domain, file);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
