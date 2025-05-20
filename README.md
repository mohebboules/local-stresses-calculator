# Pressure Vessel Stress Analyzer

## Overview
The Pressure Vessel Stress Analyzer is a Java desktop application designed for stress analysis of pressure vessels, specifically cylindrical and spherical tanks. The application provides a user-friendly GUI that allows users to input parameters such as radius and internal pressure, select the tank shape, and calculate the principal and shear stresses for various angles.

## Features
- Input fields for radius and internal pressure.
- Dropdown menu for selecting tank shapes: Closed Cylinder, Open Cylinder, Spherical Tank.
- Calculation of hoop, longitudinal, and spherical stresses.
- Visualization of stress results plotted as curves using JFreeChart.
- Status area for displaying calculation status and error messages.

## Project Structure
```
pressure-vessel-stress-analyzer
├── src
│   ├── gui
│   │   └── MainFrame.java
│   ├── calculations
│   │   └── StressCalculator.java
│   ├── plotting
│   │   └── ChartPanel.java
│   └── App.java
├── lib
│   └── (add JFreeChart .jar here)
├── README.md
└── build.gradle
```

## Setup Instructions
1. **Clone the repository**:
   ```
   git clone <repository-url>
   cd pressure-vessel-stress-analyzer
   ```

2. **Add JFreeChart**:
   Download the JFreeChart library and place the `.jar` file in the `lib` directory.

3. **Build the project**:
   Ensure you have Gradle installed. Run the following command in the project root:
   ```
   gradle build
   ```

4. **Run the application**:
   You can run the application using the following command:
   ```
   gradle run
   ```

## Usage
- Launch the application.
- Enter the radius and internal pressure in the respective fields.
- Select the desired tank shape from the dropdown menu.
- Click the "Calculate" button to compute the stresses.
- View the plotted results and any status messages in the designated area.

## Extending the Application
The application is designed to be easily extendable. You can add more tank types or variable stress calculations by modifying the `StressCalculator` class and updating the GUI accordingly.

## License
This project is licensed under the MIT License - see the LICENSE file for details.