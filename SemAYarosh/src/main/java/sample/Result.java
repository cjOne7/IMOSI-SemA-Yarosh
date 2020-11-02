package sample;

import java.util.List;

public class Result {
    private int unSuccessfullyTrials;
    private int successfullyTrials;
    private int counter = -1;
    private List<Double> list;

    public Result(int unSuccessfullyTrials, int successfullyTrials, int counter, List<Double> list) {
        this.unSuccessfullyTrials = unSuccessfullyTrials;
        this.successfullyTrials = successfullyTrials;
        this.counter = counter;
        this.list = list;
    }

    public Result(int unSuccessfullyTrials, int successfullyTrials, List<Double> list) {
        this.unSuccessfullyTrials = unSuccessfullyTrials;
        this.successfullyTrials = successfullyTrials;
        this.list = list;
    }

    public Result(int unSuccessfullyTrials, int successfullyTrials, int counter) {
        this.unSuccessfullyTrials = unSuccessfullyTrials;
        this.successfullyTrials = successfullyTrials;
        this.counter = counter;
    }

    public Result(int unSuccessfullyTrials, int successfullyTrials) {
        this.unSuccessfullyTrials = unSuccessfullyTrials;
        this.successfullyTrials = successfullyTrials;
    }

    public int getUnSuccessfullyTrials() {
        return unSuccessfullyTrials;
    }

    public void setUnSuccessfullyTrials(int unSuccessfullyTrials) {
        this.unSuccessfullyTrials = unSuccessfullyTrials;
    }

    public int getSuccessfullyTrials() {
        return successfullyTrials;
    }

    public void setSuccessfullyTrials(int successfullyTrials) {
        this.successfullyTrials = successfullyTrials;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    @Override
    public String toString() {
        return "Unsuccessfully trials: " + unSuccessfullyTrials +
                ", successfully trials: " + successfullyTrials +
                ", counter: " + counter;
    }
}
