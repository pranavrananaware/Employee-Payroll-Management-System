import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Abstract Employee Class
abstract class Employee {
    private final StringProperty name;
    private final IntegerProperty id;

    public Employee(String name, int id) {
        this.name = new SimpleStringProperty(name);   //this. keyword of constructor
        this.id = new SimpleIntegerProperty(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public abstract double calculateSalary();

    public abstract DoubleProperty salaryProperty();
}

// Full-Time Employee Class
class FullTimeEmployee extends Employee {
    private final DoubleProperty monthlySalary;

    public FullTimeEmployee(String name, int id, double monthlySalary) {
        super(name, id);
        this.monthlySalary = new SimpleDoubleProperty(monthlySalary);
    }

    @Override
    public double calculateSalary() {
        return monthlySalary.get();
    }

    @Override
    public DoubleProperty salaryProperty() {
        return monthlySalary;
    }
}

// Part-Time Employee Class
class PartTimeEmployee extends Employee {
    private final IntegerProperty hoursWorked;
    private final DoubleProperty hourlyRate;

    public PartTimeEmployee(String name, int id, int hoursWorked, double hourlyRate) {
        super(name, id);
        this.hoursWorked = new SimpleIntegerProperty(hoursWorked);
        this.hourlyRate = new SimpleDoubleProperty(hourlyRate);
    }

    @Override
    public double calculateSalary() {
        return hoursWorked.get() * hourlyRate.get();
    }

    @Override
    public DoubleProperty salaryProperty() {
        return new SimpleDoubleProperty(calculateSalary());
    }
}

// Main Application Class
public class Main extends Application {
    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Payroll System");

        // TableView Setup
        TableView<Employee> tableView = new TableView<>(employeeList);

        TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());

        TableColumn<Employee, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> data.getValue().idProperty().asObject());

        TableColumn<Employee, Double> salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setCellValueFactory(data -> data.getValue().salaryProperty().asObject());

        tableView.getColumns().addAll(nameColumn, idColumn, salaryColumn);

        // Input Fields
        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField idField = new TextField();
        idField.setPromptText("ID");

        TextField salaryField = new TextField();
        salaryField.setPromptText("Monthly Salary (Full-Time) or Hourly Rate (Part-Time)");

        TextField hoursField = new TextField();
        hoursField.setPromptText("Hours Worked (Part-Time Only)");
        hoursField.setDisable(true);

        ComboBox<String> employeeType = new ComboBox<>();
        employeeType.setItems(FXCollections.observableArrayList("Full-Time", "Part-Time"));
        employeeType.setPromptText("Select Employee Type");

        employeeType.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Part-Time".equals(newVal)) {
                hoursField.setDisable(false);
            } else {
                hoursField.setDisable(true);
                hoursField.clear();
            }
        });

        Button addButton = new Button("Add Employee");
        addButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                int id = Integer.parseInt(idField.getText());

                if ("Full-Time".equals(employeeType.getValue())) {
                    double monthlySalary = Double.parseDouble(salaryField.getText());
                    employeeList.add(new FullTimeEmployee(name, id, monthlySalary));
                } else if ("Part-Time".equals(employeeType.getValue())) {
                    int hoursWorked = Integer.parseInt(hoursField.getText());
                    double hourlyRate = Double.parseDouble(salaryField.getText());
                    employeeList.add(new PartTimeEmployee(name, id, hoursWorked, hourlyRate));
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an employee type.");
                    alert.showAndWait();
                }

                // Clear input fields
                nameField.clear();
                idField.clear();
                salaryField.clear();
                hoursField.clear();
                employeeType.getSelectionModel().clearSelection();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input. Please check your entries.");
                alert.showAndWait();
            }
        });

        Button removeButton = new Button("Remove Employee");
        removeButton.setOnAction(e -> {
            Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();
            if (selectedEmployee != null) {
                employeeList.remove(selectedEmployee);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "No employee selected.");
                alert.showAndWait();
            }
        });

        // Layouts
        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10));
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.add(new Label("Name:"), 0, 0);
        inputGrid.add(nameField, 1, 0);
        inputGrid.add(new Label("ID:"), 0, 1);
        inputGrid.add(idField, 1, 1);
        inputGrid.add(new Label("Salary/Rate:"), 0, 2);
        inputGrid.add(salaryField, 1, 2);
        inputGrid.add(new Label("Hours Worked:"), 0, 3);
        inputGrid.add(hoursField, 1, 3);
        inputGrid.add(new Label("Type:"), 0, 4);
        inputGrid.add(employeeType, 1, 4);

        HBox buttons = new HBox(10, addButton, removeButton);

        VBox root = new VBox(10, inputGrid, buttons, tableView);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
