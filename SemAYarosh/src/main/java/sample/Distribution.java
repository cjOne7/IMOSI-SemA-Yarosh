package sample;

import org.apache.commons.math3.random.RandomGenerator;

import java.util.Random;

public class Distribution {

    private final Random RANDOM;

    public Distribution(Random random) {
        this.RANDOM = random;
    }

    public double uniformDistribution(double min, double max) {
        return RANDOM.nextDouble() * (max - min) + min;
    }

    public double normalDistribution(double mean, double variance) {
        return mean + variance * RANDOM.nextGaussian();
    }

    public double triangularDistribution(double a, double b, double c) {
        double F = (c - a) / (b - a);
        double rand = RANDOM.nextDouble();
        if (rand < F) {
            return a + Math.sqrt(rand * (b - a) * (c - a));
        } else {
            return b - Math.sqrt((1 - rand) * (b - a) * (b - c));
        }
    }
}
