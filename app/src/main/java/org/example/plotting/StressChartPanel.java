package org.example.plotting;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class StressChartPanel extends JPanel {
    private XYSeries sigma1Series;
    private XYSeries sigma2Series;
    private XYSeries sigma6Series;
    private XYSeriesCollection dataset;
    private JFreeChart chart;

    public StressChartPanel() {
        setLayout(new BorderLayout());
        sigma1Series = new XYSeries("Sigma1");
        sigma2Series = new XYSeries("Sigma2");
        sigma6Series = new XYSeries("Sigma6");
        dataset = new XYSeriesCollection();
        dataset.addSeries(sigma1Series);
        dataset.addSeries(sigma2Series);
        dataset.addSeries(sigma6Series);

        chart = ChartFactory.createXYLineChart(
                "Stress vs Theta",
                "Theta (degrees)",
                "Stress",
                dataset
        );
        add(new ChartPanel(chart), BorderLayout.CENTER);
    }

    public void updateData(double[] thetas, double[] sigma1, double[] sigma2, double[] sigma6, String legend1, String legend2, String legend6) {
        dataset.removeAllSeries();

        if (sigma1 != null) {
            XYSeries s1 = new XYSeries(legend1 != null ? legend1 : "Sigma1");
            for (int i = 0; i < thetas.length; i++) {
                s1.add(thetas[i], sigma1[i]);
            }
            dataset.addSeries(s1);
        }
        if (sigma2 != null) {
            XYSeries s2 = new XYSeries(legend2 != null ? legend2 : "Sigma2");
            for (int i = 0; i < thetas.length; i++) {
                s2.add(thetas[i], sigma2[i]);
            }
            dataset.addSeries(s2);
        }
        if (sigma6 != null) {
            XYSeries s6 = new XYSeries(legend6 != null ? legend6 : "Sigma6");
            for (int i = 0; i < thetas.length; i++) {
                s6.add(thetas[i], sigma6[i]);
            }
            dataset.addSeries(s6);
        }
    }
}