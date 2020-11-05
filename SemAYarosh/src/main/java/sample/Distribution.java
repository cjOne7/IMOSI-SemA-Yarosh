package sample;

import org.apache.commons.math3.random.RandomGenerator;

import java.util.Random;

public class Distribution {

    private final Random random;

    public Distribution(Random random) {
        this.random = random;
    }

    public double uniformDistribution(double min, double max) {
        return random.nextDouble() * max + min;
    }

    public double normalDistribution(double mean, double variance) {
        return variance + mean * random.nextGaussian();
    }

    public double triangularDistribution(double a, double b, double c) {
        double F = (c - a) / (b - a);
        double rand = random.nextDouble();
        if (rand < F) {
            return a + Math.sqrt(rand * (b - a) * (c - a));
        } else {
            return b - Math.sqrt((1 - rand) * (b - a) * (b - c));
        }
    }
}
