package sample;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TriangularDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class Controller implements Initializable {
    private static final ObservableList<Double> spinnerValues
            = FXCollections.observableList(Arrays.asList(0.001, 0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001));
    private static final String[] ATTRIBUTES = {"c=8.4", "c=8.2"};
    private static final int DOLLARS_FOR_ONE = 5;

    private XYChart.Series[] data;
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
    private StackedBarChart<String, Double> barChart;
    @FXML
    private TextField iterationsTextField;
    @FXML
    private Label messLabel;
    @FXML
    private Spinner<Double> deviationSpinner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        data = new XYChart.Series[ATTRIBUTES.length];
        deviationSpinner.setValueFactory(
                new SpinnerValueFactory.ListSpinnerValueFactory<>(spinnerValues));
    }

    @FXML
    private void averageLineChart(ActionEvent event) {
        try {
            List<Integer> listOfTrials = getListOfTrialsIterations(8.4);
            logs(listOfTrials, iterations, "c=8.4\n");

            List<Integer> listOfTrials1 = getListOfTrialsIterations(8.2);
            logs(listOfTrials1, iterations, "c=8.2\n");

            createChart(listOfTrials, listOfTrials1);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void deviationLineChart(ActionEvent event) {
        List<Integer> listOfTrials = getListOfTrialsDeviation(8.4);
        logs(listOfTrials, listOfTrials.get(2), "c=8.4\n");

        List<Integer> listOfTrials1 = getListOfTrialsDeviation(8.2);
        logs(listOfTrials1, listOfTrials1.get(2), "c=8.2\n");
        createChart(listOfTrials, listOfTrials1);
    }

    private List<Integer> getListOfTrialsIterations(double c) throws IllegalArgumentException {
        NormalDistribution s1 = new NormalDistribution(7.0, 0.4);
        UniformRealDistribution s2 = new UniformRealDistribution(9.1, 9.9);
        TriangularDistribution s3 = new TriangularDistribution(7.6, 8.0, c);
        NormalDistribution s4 = new NormalDistribution(6.5, 0.45);
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
            double sum = s1.sample() + s2.sample() + (s3.sample() - p1 - p2) + s4.sample();
            if (sum >= b) {
                unSuccessfullyTrials++;
            } else {
                successfullyTrials++;
            }
        }
        List<Integer> listOfTrials = new ArrayList<>();
        listOfTrials.add(unSuccessfullyTrials);
        listOfTrials.add(successfullyTrials);
        return listOfTrials;
    }

    private List<Integer> getListOfTrialsDeviation(double c) {
        NormalDistribution s1 = new NormalDistribution(7.0, 0.4);
        UniformRealDistribution s2 = new UniformRealDistribution(9.1, 9.9);
        TriangularDistribution s3 = new TriangularDistribution(7.6, 8.0, c);
        NormalDistribution s4 = new NormalDistribution(6.5, 0.45);
        StandardDeviation standardDeviation = new StandardDeviation();
        int unSuccessfullyTrials = 0;
        int successfullyTrials = 0;
        double temp = 1.0;
        int counter = 0;
        double deviation = deviationSpinner.getValue();
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
        }
        List<Integer> listOfTrials = new ArrayList<>();
        listOfTrials.add(unSuccessfullyTrials);
        listOfTrials.add(successfullyTrials);
        listOfTrials.add(counter);
        return listOfTrials;
    }

    @SafeVarargs
    private final void createChart(List<Integer>... listOfTrials) {
        for (int i = 0; i < data.length; i++) {
            data[i] = new XYChart.Series();
            data[i].setName(ATTRIBUTES[i]);
        }
        for (int i = 0; i < ATTRIBUTES.length; i++) {
            data[0].getData().add(new XYChart.Data<>(i == 1
                    ? "Successfully when c = 8.4"
                    : "Unsuccessfully when c = 8.4", listOfTrials[0].get(i)));
            data[1].getData().add(new XYChart.Data<>(i == 1
                    ? "Successfully when c = 8.2"
                    : "Unsuccessfully when c = 8.2", listOfTrials[1].get(i)));
        }
        barChart.getData().setAll(data);
    }

    private void logs(List<Integer> list, int length, String mess) {
        System.out.print(mess);
        System.out.println("Unsuccessfully trials: " + list.get(0));
        System.out.println("Successfully trials: " + list.get(1));
        int a = list.get(0);
        double p = (double) list.get(0) / length * 100;
        System.out.println("P: " + String.format("%.2f%%\n", ((double) list.get(0) / length * 100)));
        if (logIndex == 2) {
            fileTitle.append("\n");
            fileData.append("\n");
            writeInCSVFile("data.csv", fileTitle.toString(), fileData.toString());
            logIndex = 0;
            message = "";
            fileData.setLength(0);
            fileTitle.setLength(0);
        }
        int expenses = list.get(0) * DOLLARS_FOR_ONE;
        double c = logIndex == 0 ? 8.4 : 8.2;
        message += String.format("%sUnsuccessfully trials: %d\nSuccessfully trials: %d\nP: %.2f%%\nExpenses (c=%.1f): %d$\n\n"
                , mess, list.get(0), list.get(1), (double) list.get(0) / length * 100, c, expenses);
        messLabel.setText(message);
        fileTitle.append("Neúspěšně").append(";")
                .append("Úspěšně").append(";");
        fileData.append(list.get(0)).append(";")
                .append(list.get(1)).append(";");
        logIndex++;
    }

    private void writeInCSVFile(String fileName, String... data) {
        File file = new File(fileName);
        try {
            Writer fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            for (String item : data) {
                fileWriter.append(item);
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
