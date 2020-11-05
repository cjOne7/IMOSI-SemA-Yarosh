package sample;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TriangularDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class Controller implements Initializable {
    private static final ObservableList<Double> SPINNER_VALUES
            = FXCollections.observableList(Arrays.asList(0.001, 0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001));
    private static final String[] ATTRIBUTES_FOR_CHARTS = {"c=8.4", "c=8.2"};
    private static final int DOLLARS_FOR_ONE = 5;

    private XYChart.Series[] dataBarChart;
    private XYChart.Series[] dataLineChart;
    private int iterations;

    private final double p1 = 2.0;
    private final double p2 = 1.5;
    private final double gap = 1.0;
    private final double b = 30.0 - gap * 2.0;

    private String message = "";
    private final StringBuilder fileTitle = new StringBuilder();
    private final StringBuilder fileData = new StringBuilder();
    private int logIndex;

    @FXML
    private LineChart<String, Double> lineChart;
    @FXML
    private StackedBarChart<String, Double> barChart;
    @FXML
    private TextField iterationsTextField;
    @FXML
    private Label messLabel;
    @FXML
    private Spinner<Double> deviationSpinner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataBarChart = new XYChart.Series[ATTRIBUTES_FOR_CHARTS.length];
        dataLineChart = new XYChart.Series[1];
        deviationSpinner.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(SPINNER_VALUES));
    }

    @FXML
    private void calcAverage(ActionEvent event) {
        try {
            Result listOfTrials = getListOfTrialsIterations(8.4);
            logs(listOfTrials, iterations, "c=8.4\n");

            Result listOfTrials1 = getListOfTrialsIterations(8.2);
            logs(listOfTrials1, iterations, "c=8.2\n");

            createBarChart(listOfTrials, listOfTrials1);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void calcDeviation(ActionEvent event) {
        Result listOfTrials = getListOfTrialsDeviation(8.4);
        Result listOfTrials1 = getListOfTrialsOfMyDeviation(8.4);
        createBarChart(listOfTrials, listOfTrials1);

        lineChart.setData(FXCollections.observableArrayList());
        dataLineChart[0] = new XYChart.Series();
        dataLineChart[0].setName("Standard deviation");
        Iterator<Double> it = listOfTrials.getListOfDeviations().iterator();
        int i = 1;
        while (it.hasNext()) {
            Double value = it.next();
            dataLineChart[0].getData().add(new XYChart.Data<>("" + i, value));
            i++;
        }
        lineChart.getData().addAll(dataLineChart);
        fileTitle.setLength(0);
        fileData.setLength(0);
        for (int j = 0; j < listOfTrials.getListOfDeviations().size(); j++) {
            fileData.append(String.format("%.2f", listOfTrials.getListOfDeviations().get(j))).append(";");
        }
        writeInCSVFile("StandardDeviation.csv", fileData.toString());

        logs(listOfTrials, listOfTrials.getCounter(), "c=8.4\n");
        logs(listOfTrials1, listOfTrials1.getCounter(), "c=8.2\n");
    }

    private Result getListOfTrialsIterations(double c) throws IllegalArgumentException {
        RandomGenerator randomGenerator = new JDKRandomGenerator(10);
        NormalDistribution s1 = new NormalDistribution(randomGenerator, 7.0, 0.4);
        UniformRealDistribution s2 = new UniformRealDistribution(randomGenerator, 9.1, 9.9);
        TriangularDistribution s3 = new TriangularDistribution(randomGenerator, 7.6, 8.0, c);
        NormalDistribution s4 = new NormalDistribution(randomGenerator, 6.5, 0.45);
        int unSuccessfullyTrials = 0;
        int successfullyTrials = 0;
        try {
            iterations = Integer.parseInt(iterationsTextField.getText());
        } catch (NumberFormatException ex) {
            System.err.println(ex.getMessage());
        }
        if (iterations == 0) {
            throw new IllegalArgumentException();
        }
        for (int j = 0; j < iterations; j++) {
            double sum = s1.sample() + s2.sample() + s3.sample() + s4.sample() - p1 - p2;
            if (sum >= b) {
                unSuccessfullyTrials++;
            } else {
                successfullyTrials++;
            }
        }
        return new Result(unSuccessfullyTrials, successfullyTrials);
    }

    private Result getListOfTrialsDeviation(double c) {
        RandomGenerator randomGenerator = new JDKRandomGenerator(123456789);
        NormalDistribution s1 = new NormalDistribution(randomGenerator, 7.0, 0.4);
        UniformRealDistribution s2 = new UniformRealDistribution(randomGenerator, 9.1, 9.9);
        TriangularDistribution s3 = new TriangularDistribution(randomGenerator, 7.6, 8.0, c);
        NormalDistribution s4 = new NormalDistribution(randomGenerator, 6.5, 0.45);
        StandardDeviation standardDeviation = new StandardDeviation();
        int unSuccessfullyTrials = 0;
        int successfullyTrials = 0;
        double temp = 1.0;
        int counter = 0;
        double deviation = deviationSpinner.getValue();
        List<Double> deviations = new ArrayList<>();
        while (true) {
            double sum = s1.sample() + s2.sample() + s3.sample() - p1 - p2 + s4.sample();
            standardDeviation.increment(sum);
            if (sum >= b) {
                unSuccessfullyTrials++;
            } else {
                successfullyTrials++;
            }
            counter++;
            double res = Math.abs(temp - standardDeviation.getResult());
            if (res < deviation) {
                System.out.println("Exit when counter = " + counter);
                break;
            }
            temp = standardDeviation.getResult();
            deviations.add(temp);
        }
        return new Result(unSuccessfullyTrials, successfullyTrials, counter, deviations);
    }

    private Result getListOfTrialsOfMyDeviation(double c) {
        StandardDeviation standardDeviation = new StandardDeviation();
        int unSuccessfullyTrials = 0;
        int successfullyTrials = 0;
        double temp = 1.0;
        int counter = 0;
        double deviation = deviationSpinner.getValue();
        List<Double> deviations = new ArrayList<>();
        Random random = new Random(123456789);
        Distribution distribution = new Distribution(random);
        while (true) {
            double s1 = distribution.normalDistribution(7.0, 0.4);
            double s2 = distribution.uniformDistribution(9.1, 9.9);
            double s3 = distribution.triangularDistribution(7.6, 8.0, c);
            double s4 = distribution.normalDistribution(6.5, 0.45);
            double sum = s1 + s2 + s3 - p1 - p2 + s4;
            standardDeviation.increment(sum);
            if (sum >= b) {
                unSuccessfullyTrials++;
            } else {
                successfullyTrials++;
            }
            counter++;
            double res = Math.abs(temp - standardDeviation.getResult());
            if (res < deviation) {
                System.out.println("Exit when counter = " + counter);
                break;
            }
            temp = standardDeviation.getResult();
            deviations.add(temp);
        }
        return new Result(unSuccessfullyTrials, successfullyTrials, counter, deviations);
    }

    @SafeVarargs
    private final void createBarChart(Result... listOfTrials) {
        for (int i = 0; i < dataBarChart.length; i++) {
            dataBarChart[i] = new XYChart.Series();
            dataBarChart[i].setName(ATTRIBUTES_FOR_CHARTS[i]);
        }
        for (int i = 0; i < ATTRIBUTES_FOR_CHARTS.length; i++) {
            dataBarChart[0].getData().add(new XYChart.Data<>(i == 1 ? "Successfully when c = 8.4" : "Unsuccessfully when c = 8.4"
                    , i == 1 ? listOfTrials[0].getSuccessfullyTrials() : listOfTrials[0].getUnSuccessfullyTrials()));
            dataBarChart[1].getData().add(new XYChart.Data<>(i == 1 ? "Successfully when c = 8.2" : "Unsuccessfully when c = 8.2"
                    , i == 1 ? listOfTrials[1].getSuccessfullyTrials() : listOfTrials[1].getUnSuccessfullyTrials()));
        }
        barChart.getData().setAll(dataBarChart);
    }

    private void logs(Result result, int length, String mess) {
        System.out.print(mess);
        System.out.println("Unsuccessfully trials: " + result.getUnSuccessfullyTrials());
        System.out.println("Successfully trials: " + result.getSuccessfullyTrials());
        System.out.println("P: " + String.format("%.2f%%\n", ((double) result.getUnSuccessfullyTrials() / length * 100)));
        if (logIndex == 2) {
            fileTitle.append("\n");
            fileData.append("\n");
            writeInCSVFile("data.csv", fileTitle.toString(), fileData.toString());
            logIndex = 0;
            message = "";
            fileData.setLength(0);
            fileTitle.setLength(0);
        }
        int expenses = result.getUnSuccessfullyTrials() * DOLLARS_FOR_ONE;
        double c = logIndex == 0 ? 8.4 : 8.2;
        message += String.format("%sUnsuccessfully trials: %d\nSuccessfully trials: %d\nP: %.2f%%\nExpenses (c=%.1f): %d$\n\n"
                , mess, result.getUnSuccessfullyTrials(), result.getSuccessfullyTrials()
                , (double) result.getUnSuccessfullyTrials() / length * 100, c, expenses);
        messLabel.setText(message);
        fileTitle.append("Neúspěšně").append(";").append("Úspěšně").append(";");
        fileData.append(result.getUnSuccessfullyTrials()).append(";").append(result.getSuccessfullyTrials()).append(";");
        logIndex++;
    }

    private void writeInCSVFile(String fileName, String... data) {
        File file = new File(fileName);
        try {
            Writer fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            for (String item : data) {
                fileWriter.append(item);
            }
            fileTitle.setLength(0);
            fileData.setLength(0);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
