package org.example.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.example.calculations.Models.LoadInputModel;
import org.example.calculations.Models.LoadComputationalModel;
import org.example.calculations.CalculationInputBundle;
import org.example.calculations.GlobalStressCalculator;
import org.example.calculations.Models.GlobalStressModel;
import org.example.calculations.LocalStressCalculator;
import org.example.calculations.CalculationsOutputBundle;
import org.example.calculations.enums.LoadType;
import org.example.calculations.enums.TankType;
import org.example.calculations.enums.Variation;
import org.example.plotting.StressChartPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private JComboBox<TankType> tankTypeDropdown;
    private JTextField internalDiameterField, externalDiameterField;
    private JTextField thetaField; // <-- Add theta input
    private JButton calculateButton;
    private JPanel dimensionPanel;
    private JTabbedPane loadTabbedPane;
    private JPanel chartContainerPanel; // For displaying the chart
    private JPanel graphButtonPanel;    // For graph buttons
    private JScrollPane buttonScrollPane; // For scrollable buttons
    private JTextArea resultArea;       // For sigma results

    // Store each load tab's dropdown so we can listen for changes
    private Map<LoadType, JComboBox<Variation>> loadTypeVariationDropdowns = new HashMap<>();
    // Store data models and field references
    private Map<LoadType, LoadInputModel> loadInputModels = new HashMap<>();
    private Map<LoadType, JTextField> staticFields = new HashMap<>();
    private Map<LoadType, JTextField[]> variableFields = new HashMap<>();

    // Reset button
    private JButton resetButton;

    public MainFrame() {
        setTitle("Local Stress Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        // Tank type dropdown
        tankTypeDropdown = new JComboBox<>(TankType.values());
        tankTypeDropdown.addActionListener(e -> updateDimensionFields());

        // Dimension fields
        internalDiameterField = new JTextField(8);
        externalDiameterField = new JTextField(8);

        dimensionPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        // Load tabs
        loadTabbedPane = new JTabbedPane();

        // Chart and button panels
        chartContainerPanel = new JPanel(new BorderLayout());
        graphButtonPanel = new JPanel();
        graphButtonPanel.setLayout(new BoxLayout(graphButtonPanel, BoxLayout.X_AXIS));
        graphButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0)); // Padding above scrollbar
        buttonScrollPane = new JScrollPane(graphButtonPanel,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        buttonScrollPane.setBorder(null);

        // Result area for sigma values
        resultArea = new JTextArea(5, 22);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        resultArea.setBorder(BorderFactory.createTitledBorder("Sigma Results"));

        // Theta input field
        thetaField = new JTextField("0", 6);
        thetaField.setMaximumSize(new Dimension(60, 30));
        thetaField.setToolTipText("Theta in degrees");

        // Reset button
        resetButton = new JButton("Reset");
        resetButton.setPreferredSize(new Dimension(200, 40));
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tankTypeDropdown.setSelectedIndex(0);
                internalDiameterField.setText("");
                externalDiameterField.setText("");
                thetaField.setText("0");
                resultArea.setText("");
                // Reset all load input fields
                for (JTextField field : staticFields.values()) {
                    field.setText("");
                }
                for (JTextField[] fields : variableFields.values()) {
                    for (JTextField field : fields) {
                        field.setText("");
                    }
                }
                // Remove graph buttons and chart
                graphButtonPanel.removeAll();
                graphButtonPanel.revalidate();
                graphButtonPanel.repaint();
                chartContainerPanel.removeAll();
                chartContainerPanel.revalidate();
                chartContainerPanel.repaint();
            }
        });

        updateDimensionFields(); // Now it's safe, loadTabbedPane is initialized
        updateLoadTabs();

        // Calculate button
        calculateButton = new JButton("Calculate");
        calculateButton.setPreferredSize(new Dimension(200, 40));

        calculateButton.addActionListener(e -> {
            CalculationInputBundle bundle = collectCalculationInputs();
            GlobalStressCalculator calculator = new GlobalStressCalculator();
            GlobalStressModel global = calculator.calculateAllStresses(bundle);

            int n = 90;
            double[] thetas = new double[n + 1];
            double[] sigma1Avg = new double[n + 1];
            double[] sigma2Avg = new double[n + 1];
            double[] sigma6Avg = new double[n + 1];
            double[] sigma1Max = new double[n + 1];
            double[] sigma2Max = new double[n + 1];
            double[] sigma6Max = new double[n + 1];
            double[] sigma1Min = new double[n + 1];
            double[] sigma2Min = new double[n + 1];
            double[] sigma6Min = new double[n + 1];

            boolean isStatic = true;
            boolean isVariable = false;
            for (int i = 0; i <= n; i++) {
                double theta = i;
                thetas[i] = theta;
                CalculationsOutputBundle local = LocalStressCalculator.calculateLocalStress(
                    theta,
                    global.getSigmaX(),
                    global.getSigmaY(),
                    global.getTauXY()
                );

                boolean s1Var = !local.getSigma1().isStatic();
                boolean s2Var = !local.getSigma2().isStatic();
                boolean s6Var = !local.getSigma6().isStatic();
                isStatic = isStatic && !s1Var && !s2Var && !s6Var;
                isVariable = isVariable || s1Var || s2Var || s6Var;

                // Sigma 1
                if (local.getSigma1().isStatic()) {
                    double v = local.getSigma1().evaluate(0, 0);
                    sigma1Avg[i] = v;
                    sigma1Max[i] = v;
                    sigma1Min[i] = v;
                } else {
                    double a = local.getSigma1().getA();
                    double b = Math.abs(local.getSigma1().getB());
                    sigma1Avg[i] = a;
                    sigma1Max[i] = a + b;
                    sigma1Min[i] = a - b;
                }

                // Sigma 2
                if (local.getSigma2().isStatic()) {
                    double v = local.getSigma2().evaluate(0, 0);
                    sigma2Avg[i] = v;
                    sigma2Max[i] = v;
                    sigma2Min[i] = v;
                } else {
                    double a = local.getSigma2().getA();
                    double b = Math.abs(local.getSigma2().getB());
                    sigma2Avg[i] = a;
                    sigma2Max[i] = a + b;
                    sigma2Min[i] = a - b;
                }

                // Sigma 6
                if (local.getSigma6().isStatic()) {
                    double v = local.getSigma6().evaluate(0, 0);
                    sigma6Avg[i] = v;
                    sigma6Max[i] = v;
                    sigma6Min[i] = v;
                } else {
                    double a = local.getSigma6().getA();
                    double b = Math.abs(local.getSigma6().getB());
                    sigma6Avg[i] = a;
                    sigma6Max[i] = a + b;
                    sigma6Min[i] = a - b;
                }
            }

            // Show sigma1, sigma2, sigma6 at user-input theta in the UI
            double thetaVal = parseDoubleSafe(thetaField.getText());
            CalculationsOutputBundle localTheta = LocalStressCalculator.calculateLocalStress(
                thetaVal,
                global.getSigmaX(),
                global.getSigmaY(),
                global.getTauXY()
            );

            StringBuilder sb = new StringBuilder();
            sb.append("Sigma1: ");
            if (localTheta.getSigma1().isStatic()) {
                sb.append(localTheta.getSigma1().evaluate(0, 0));
            } else {
                sb.append(localTheta.getSigma1().getA())
                  .append(" + ")
                  .append(localTheta.getSigma1().getB())
                  .append(" * sin(wt + ")
                  .append(thetaVal)
                  .append("°)");
            }
            sb.append("\nSigma2: ");
            if (localTheta.getSigma2().isStatic()) {
                sb.append(localTheta.getSigma2().evaluate(0, 0));
            } else {
                sb.append(localTheta.getSigma2().getA())
                  .append(" + ")
                  .append(localTheta.getSigma2().getB())
                  .append(" * sin(wt + ")
                  .append(thetaVal)
                  .append("°)");
            }
            sb.append("\nSigma6: ");
            if (localTheta.getSigma6().isStatic()) {
                sb.append(localTheta.getSigma6().evaluate(0, 0));
            } else {
                sb.append(localTheta.getSigma6().getA())
                  .append(" + ")
                  .append(localTheta.getSigma6().getB())
                  .append(" * sin(wt + ")
                  .append(thetaVal)
                  .append("°)");
            }
            resultArea.setText(sb.toString());

            // Remove old buttons and chart
            graphButtonPanel.removeAll();
            chartContainerPanel.removeAll();

            if (isStatic) {
                JButton btn1 = new JButton("Sigma1 vs Theta");
                JButton btn2 = new JButton("Sigma2 vs Theta");
                JButton btn3 = new JButton("Sigma6 vs Theta");
                JButton btnAll = new JButton("All Stresses vs Theta");

                btn1.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, sigma1Avg, null, null, "Sigma1", null, null);
                    showChartInMainWindow(panel);
                });
                btn2.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, null, sigma2Avg, null, null, "Sigma2", null);
                    showChartInMainWindow(panel);
                });
                btn3.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, null, null, sigma6Avg, null, null, "Sigma6");
                    showChartInMainWindow(panel);
                });
                btnAll.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, sigma1Avg, sigma2Avg, sigma6Avg, "Sigma1", "Sigma2", "Sigma6");
                    showChartInMainWindow(panel);
                });

                graphButtonPanel.add(btn1);
                graphButtonPanel.add(btn2);
                graphButtonPanel.add(btn3);
                graphButtonPanel.add(btnAll);

            } else if (isVariable) {
                // Variable graphs
                JButton btn1 = new JButton("Sigma1 Avg vs Theta");
                JButton btn2 = new JButton("Sigma2 Avg vs Theta");
                JButton btn3 = new JButton("Sigma6 Avg vs Theta");
                JButton btnAllAvg = new JButton("All Avg vs Theta");
                JButton btn4 = new JButton("Sigma1 Max vs Theta");
                JButton btn5 = new JButton("Sigma2 Max vs Theta");
                JButton btn6 = new JButton("Sigma6 Max vs Theta");
                JButton btnAllMax = new JButton("All Max vs Theta");
                JButton btn7 = new JButton("Sigma1 Min vs Theta");
                JButton btn8 = new JButton("Sigma2 Min vs Theta");
                JButton btn9 = new JButton("Sigma6 Min vs Theta");
                JButton btnAllMin = new JButton("All Min vs Theta");

                btn1.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, sigma1Avg, null, null, "Sigma1 Avg", null, null);
                    showChartInMainWindow(panel);
                });
                btn2.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, null, sigma2Avg, null, null, "Sigma2 Avg", null);
                    showChartInMainWindow(panel);
                });
                btn3.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, null, null, sigma6Avg, null, null, "Sigma6 Avg");
                    showChartInMainWindow(panel);
                });
                btnAllAvg.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, sigma1Avg, sigma2Avg, sigma6Avg, "Sigma1 Avg", "Sigma2 Avg", "Sigma6 Avg");
                    showChartInMainWindow(panel);
                });

                btn4.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, sigma1Max, null, null, "Sigma1 Max", null, null);
                    showChartInMainWindow(panel);
                });
                btn5.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, null, sigma2Max, null, null, "Sigma2 Max", null);
                    showChartInMainWindow(panel);
                });
                btn6.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, null, null, sigma6Max, null, null, "Sigma6 Max");
                    showChartInMainWindow(panel);
                });
                btnAllMax.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, sigma1Max, sigma2Max, sigma6Max, "Sigma1 Max", "Sigma2 Max", "Sigma6 Max");
                    showChartInMainWindow(panel);
                });

                btn7.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, sigma1Min, null, null, "Sigma1 Min", null, null);
                    showChartInMainWindow(panel);
                });
                btn8.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, null, sigma2Min, null, null, "Sigma2 Min", null);
                    showChartInMainWindow(panel);
                });
                btn9.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, null, null, sigma6Min, null, null, "Sigma6 Min");
                    showChartInMainWindow(panel);
                });
                btnAllMin.addActionListener(ev -> {
                    StressChartPanel panel = new StressChartPanel();
                    panel.updateData(thetas, sigma1Min, sigma2Min, sigma6Min, "Sigma1 Min", "Sigma2 Min", "Sigma6 Min");
                    showChartInMainWindow(panel);
                });

                graphButtonPanel.add(btn1);
                graphButtonPanel.add(btn2);
                graphButtonPanel.add(btn3);
                graphButtonPanel.add(btnAllAvg);
                graphButtonPanel.add(btn4);
                graphButtonPanel.add(btn5);
                graphButtonPanel.add(btn6);
                graphButtonPanel.add(btnAllMax);
                graphButtonPanel.add(btn7);
                graphButtonPanel.add(btn8);
                graphButtonPanel.add(btn9);
                graphButtonPanel.add(btnAllMin);
            }

            graphButtonPanel.revalidate();
            graphButtonPanel.repaint();
            chartContainerPanel.revalidate();
            chartContainerPanel.repaint();
        });

        // Layout
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topPanel.add(new JLabel("Tank Type:"));
        topPanel.add(tankTypeDropdown);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(topPanel, BorderLayout.NORTH);
        inputPanel.add(dimensionPanel, BorderLayout.CENTER);
        inputPanel.add(loadTabbedPane, BorderLayout.SOUTH);

        // Add calculate button, theta field, reset button, and result area to the east side
        JPanel eastPanel = new JPanel(new BorderLayout());
        JPanel topEastPanel = new JPanel();
        topEastPanel.setLayout(new BoxLayout(topEastPanel, BoxLayout.Y_AXIS));
        topEastPanel.add(new JLabel("Theta (deg):"));
        topEastPanel.add(thetaField);
        topEastPanel.add(Box.createVerticalStrut(8));
        topEastPanel.add(calculateButton);
        topEastPanel.add(Box.createVerticalStrut(8));
        topEastPanel.add(resetButton); // <-- Add reset button here
        eastPanel.add(topEastPanel, BorderLayout.NORTH);
        eastPanel.add(resultArea, BorderLayout.CENTER);
        inputPanel.add(eastPanel, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(chartContainerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonScrollPane, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    // Show chart in the main window's chartContainerPanel
    private void showChartInMainWindow(StressChartPanel panel) {
        chartContainerPanel.removeAll();
        chartContainerPanel.add(panel, BorderLayout.CENTER);
        chartContainerPanel.revalidate();
        chartContainerPanel.repaint();
    }

    private void updateDimensionFields() {
        dimensionPanel.removeAll();
        dimensionPanel.add(new JLabel("Internal Diameter: (mm)"));
        dimensionPanel.add(internalDiameterField);
        dimensionPanel.add(new JLabel("External Diameter: (mm)"));
        dimensionPanel.add(externalDiameterField);
        dimensionPanel.revalidate();
        dimensionPanel.repaint();

        updateLoadTabs();
    }

    private void updateLoadTabs() {
        loadTabbedPane.removeAll();
        loadTypeVariationDropdowns.clear();
        staticFields.clear();
        variableFields.clear();

        TankType selectedTankType = (TankType) tankTypeDropdown.getSelectedItem();
        int internalPressureTabIndex = -1;
        int tabIndex = 0;

        for (LoadType lt : LoadType.values()) {
            JPanel tabPanel = createLoadPanel(lt);
            loadTabbedPane.addTab(lt.toString(), tabPanel);

            // Track the Internal Pressure tab index
            if (lt == LoadType.INTERNAL_PRESSURE) {
                internalPressureTabIndex = tabIndex;
            }

            // Disable all tabs except Internal Pressure if Spherical Tank is selected
            if (selectedTankType == TankType.SPHERICAL_TANK && lt != LoadType.INTERNAL_PRESSURE) {
                loadTabbedPane.setEnabledAt(tabIndex, false);
            } else {
                loadTabbedPane.setEnabledAt(tabIndex, true);
            }
            tabIndex++;
        }

        // If Spherical Tank, select Internal Pressure tab
        if (selectedTankType == TankType.SPHERICAL_TANK && internalPressureTabIndex != -1) {
            loadTabbedPane.setSelectedIndex(internalPressureTabIndex);
        }

        loadTabbedPane.revalidate();
        loadTabbedPane.repaint();
    }

    private JPanel createLoadPanel(LoadType lt) {
        JPanel panel = new JPanel(new BorderLayout());

        // Data model for this load type
        LoadInputModel model = loadInputModels.computeIfAbsent(lt, LoadInputModel::new);

        // Per-load-type variation dropdown (enum-based)
        JComboBox<Variation> variationDropdown = new JComboBox<>(Variation.values());
        variationDropdown.setSelectedItem(model.variation);
        loadTypeVariationDropdowns.put(lt, variationDropdown);

        // Use BoxLayout for vertical stacking, so each tab only takes the space it needs
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        // Initial fields
        addLoadFields(fieldsPanel, model.variation, lt);

        // Listen for changes
        variationDropdown.addActionListener(e -> {
            Variation selectedVar = (Variation) variationDropdown.getSelectedItem();
            model.variation = selectedVar;
            fieldsPanel.removeAll();
            addLoadFields(fieldsPanel, selectedVar, lt);
            fieldsPanel.revalidate();
            fieldsPanel.repaint();
        });

        panel.add(variationDropdown, BorderLayout.NORTH);
        panel.add(fieldsPanel, BorderLayout.CENTER);
        return panel;
    }

    private void addLoadFields(JPanel panel, Variation variation, LoadType lt) {
        LoadInputModel model = loadInputModels.computeIfAbsent(lt, LoadInputModel::new);
        if (variation == Variation.STATIC) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField valueField = new JTextField(model.staticValue, 8);
            valueField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
                model.staticValue = valueField.getText();
            }));
            row.add(new JLabel("Value: "));
            row.add(valueField);
            panel.add(row);
            staticFields.put(lt, valueField);
            variableFields.remove(lt);
        } else {
            JTextField aField = new JTextField(model.a, 8);
            JTextField bField = new JTextField(model.b, 8);

            aField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> model.a = aField.getText()));
            bField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> model.b = bField.getText()));

            JPanel rowA = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowA.add(new JLabel("A: Mean Value:"));
            rowA.add(aField);
            panel.add(rowA);

            JPanel rowB = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowB.add(new JLabel("B: Amplitude value"));
            rowB.add(bField);
            panel.add(rowB);

            variableFields.put(lt, new JTextField[]{aField, bField});
            staticFields.remove(lt);
        }
    }

    // Utility: Listen for text changes
    private static class SimpleDocumentListener implements DocumentListener {
        private final Runnable onChange;
        public SimpleDocumentListener(Runnable onChange) { this.onChange = onChange; }
        public void insertUpdate(DocumentEvent e) { onChange.run(); }
        public void removeUpdate(DocumentEvent e) { onChange.run(); }
        public void changedUpdate(DocumentEvent e) { onChange.run(); }
    }

    // Collect all UI input into a bundle for calculation
    private CalculationInputBundle collectCalculationInputs() {
        TankType tankType = (TankType) tankTypeDropdown.getSelectedItem();
        double internalDiameter = parseDoubleSafe(internalDiameterField.getText());
        double externalDiameter = parseDoubleSafe(externalDiameterField.getText());

        Map<LoadType, LoadComputationalModel> loads = new HashMap<>();
        for (LoadType lt : LoadType.values()) {
            LoadInputModel inputModel = loadInputModels.get(lt);
            if (inputModel == null) continue;
            try {
                loads.put(lt, inputModel.toComputationalModel());
            } catch (NumberFormatException ex) {
                // Handle invalid input (show error, skip, etc.)
            }
        }
        return new CalculationInputBundle(tankType, internalDiameter, externalDiameter, loads);
    }

    private double parseDoubleSafe(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

}