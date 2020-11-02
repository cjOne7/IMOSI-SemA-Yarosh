package sample;

import java.util.Random;

public class Distribution {

    private static final Random RANDOM = new Random();

    public static double uniformDistribution(double min, double max) {
        return RANDOM.nextDouble() * max + min;
    }

    public static double normalDistribution(double mean, double variance) {
        return variance + mean * RANDOM.nextGaussian();
    }

    public static double triangularDistribution(double a, double b, double c) {
        double F = (c - a) / (b - a);
        double rand = RANDOM.nextDouble();
        if (rand < F) {
            return a + Math.sqrt(rand * (b - a) * (c - a));
        } else {
            return b - Math.sqrt((1 - rand) * (b - a) * (b - c));
        }
    }
}
