package group37.concession;

public class BoulwareStrategy implements ConcessionStrategy {

    private double minUtility;
    private double maxUtility;
    private double concessionRate;

    public BoulwareStrategy(double maxTargetUtility, double minTargetUtility, double concessionValue) {
        this.minUtility = minTargetUtility;
        this.maxUtility = maxTargetUtility;
        this.concessionRate = concessionValue;
    }

    @Override
    public double getTargetUtility(double time) {
        return minUtility + (1 - Math.pow(time, 1 / concessionRate)) * (maxUtility - minUtility);
    }

}
