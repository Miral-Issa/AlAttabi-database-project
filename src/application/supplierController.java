package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import java.sql.PreparedStatement;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellEditEvent;

public class supplierController {

	@FXML
	private TextField supplierIDField;

	@FXML
	private TextField snameField;

	@FXML
	private TextField phoneNumberField;

	@FXML
	private TextField saddressField;

	@FXML
	private TableView<supplier> supplier_table; // Change the variable name

	@FXML
	private TableColumn<supplier, Integer> supplierIDCol;

	@FXML
	private TableColumn<supplier, String> snameCol;

	@FXML
	private TableColumn<supplier, Integer> phoneNumberCol;

	@FXML
	private TableColumn<supplier, String> saddressCol;

	@FXML
	private Button addSupplierBtn;

	@FXML
	private Button deleteSupplierBtn;

	@FXML
	private Button refreshSupplierBtn;

	@FXML
	private Button backSupplierBtn;

	@FXML
	private TextField Search;

	private String dbURL;
	private String dbUsername = "root";
	private String dbPassword = "Root1234";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "project";
	private Connection con;

	private ObservableList<supplier> dataList;
	private ArrayList<supplier> data;
	// private Parent root;

	public void initialize() {
		if (supplier_table != null) {
			//initialize table and columns
			supplierIDCol.setCellValueFactory(new PropertyValueFactory<>("supplierID"));
			snameCol.setCellValueFactory(new PropertyValueFactory<>("Sname"));
			phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
			saddressCol.setCellValueFactory(new PropertyValueFactory<>("Saddress"));
			// Initialize data list
			data = new ArrayList<>();
			dataList = FXCollections.observableArrayList(data);

			// Retrieve data from the database
			try {
				getSupplierData();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}

			// Populate TableView with data
			supplier_table.setItems(dataList);

			// Make TableView editable
			supplier_table.setEditable(true);

			snameCol.setCellFactory(TextFieldTableCell.forTableColumn());
			snameCol.setOnEditCommit(this::updateSName);

			phoneNumberCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
			phoneNumberCol.setOnEditCommit(this::updatePhoneNumber);

			saddressCol.setCellFactory(TextFieldTableCell.forTableColumn());
			saddressCol.setOnEditCommit(this::updateSAddress);
		}
	}

	// Methods to handle updates for each column
	private void updateSName(CellEditEvent<supplier, String> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setSname(event.getNewValue());
		updateSupplierDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updateSAddress(CellEditEvent<supplier, String> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setSaddress(event.getNewValue());
		updateSupplierDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updatePhoneNumber(CellEditEvent<supplier, Integer> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setPhoneNumber(event.getNewValue());
		updateSupplierDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	//update database
	private void updateSupplierDatabase(supplier updatedSupplier) {
		try {
			connectDB();
			// Use an UPDATE SQL statement to update the product details in the database
			String SQL = "UPDATE Supplier SET Sname=?, phoneNumber=?, Saddress=? WHERE supplierID=?";
			try (PreparedStatement stmt = (PreparedStatement) con.prepareStatement(SQL)) {
				stmt.setString(1, updatedSupplier.getSname());
				stmt.setInt(2, updatedSupplier.getPhoneNumber());
				stmt.setString(3, updatedSupplier.getSaddress());
				stmt.setInt(4, updatedSupplier.getSupplierID());
				stmt.executeUpdate();
			}
			con.close();
			System.out.println("Connection closed");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	//function to get data from database
	private void getSupplierData() throws SQLException, ClassNotFoundException {
		// Initialize data list
		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		String SQL;

		connectDB(); 

		System.out.println("Connection established");

		SQL = "select supplierID, Sname, phoneNumber, Saddress from supplier";
		java.sql.Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL);

		while (rs.next())
			data.add(new supplier(Integer.parseInt(rs.getString(1)), rs.getString(2), Integer.parseInt(rs.getString(3)),
					rs.getString(4)));
		rs.close();
		stmt.close();

		con.close();
		System.out.println("Connection closed" + data.size());

		// Now, update the dataList
		dataList.setAll(data);
	}

	//search function according to primary key
	@SuppressWarnings("exports")
	@FXML
	public void search(ActionEvent event) {
		try {
			data = new ArrayList<>();
			dataList = FXCollections.observableArrayList(data);

			String searchTerm = Search.getText().trim();

			connectDB();
			System.out.println("Connection established");

			String SQL = "SELECT supplierID, Sname, phoneNumber, Saddress FROM Supplier WHERE supplierID = ?";
			try (java.sql.PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setString(1, searchTerm);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						// add new instance
						data.add(new supplier(Integer.parseInt(rs.getString(1)), rs.getString(2),
								Integer.parseInt(rs.getString(3)), rs.getString(4)));
					}
				}
			}

			System.out.println("Connection closed" + data.size());

			if (data.isEmpty()) {
				// No supplier found, show alert
				showAlert(AlertType.INFORMATION, "Supplier Not Found", "No supplier found with the given ID.");
			} else {
				// Supplier found, show details in a dialog
				showSupplierDetailsDialog(data.get(0));
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace(); // or log the exception using a logging framework
		}
	}

	//show information of search
	private void showSupplierDetailsDialog(supplier supplier) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Supplier Details");
		alert.setHeaderText(null);

		Label nameLabel = new Label("Supplier Name: " + supplier.getSname());
		Label phoneLabel = new Label("Phone Number: " + supplier.getPhoneNumber());
		Label addressLabel = new Label("Supplier Address: " + supplier.getSaddress());

		alert.getDialogPane().setContent(new VBox(nameLabel, phoneLabel, addressLabel));

		// Set the size of the DialogPane
		alert.getDialogPane().setMinWidth(400);
		alert.getDialogPane().setMinHeight(200);

		alert.showAndWait();
	}

	//connection function
	private Connection connectDB() throws ClassNotFoundException, SQLException {
		try {
			dbURL = "jdbc:mysql://" + URL + ":" + port + "/" + dbName + "?verifyServerCertificate=false";
			Properties properties = new Properties();
			properties.setProperty("user", dbUsername);
			properties.setProperty("password", dbPassword);
			properties.setProperty("useSSL", "false");
			properties.setProperty("autoReconnect", "true");
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbURL, properties);
			return con;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			throw e; // re throw the exception to indicate connection failure
		}
	}

	@SuppressWarnings("exports")
	@FXML
	//back to main page
	public void handleBackSupplierButton(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcomepage.fxml"));

		try {
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root, 1536.0, 820.0);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("exports")
	// add function
	public void handleAddSupplierButton(ActionEvent event) {
		supplier rc;

		// Check if the product with the same ID already exists
		int supId = Integer.valueOf(supplierIDField.getText());
		if (isSupplierIdExists(supId)) {
			showAlert(AlertType.WARNING, "Duplicate Supplier ID", "A supplier with the same ID already exists.");
			return;
		}

		//new instance
		rc = new supplier(supId, snameField.getText(), Integer.valueOf(phoneNumberField.getText()),
				saddressField.getText());

		dataList.add(rc);
		insertSupplierData(rc);

		//clear text
		supplierIDField.clear();
		snameField.clear();
		phoneNumberField.clear();
		saddressField.clear();

	}
//check duplicated primary key
	private boolean isSupplierIdExists(int supId) {
		for (supplier supplier : dataList) {
			if (supplier.getSupplierID() == supId) {
				return true;
			}
		}
		return false;

	}

	private void insertSupplierData(supplier supplier) {
		try {
			// Connect to the database and get the connection
			con = connectDB();

			// the SQL insert statement
			String SQL = "INSERT INTO supplier (supplierID, Sname, phoneNumber, Saddress) VALUES (?, ?, ?, ?)";

			// Use a PreparedStatement to avoid SQL injection
			try (PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setInt(1, supplier.getSupplierID());
				stmt.setString(2, supplier.getSname());
				stmt.setInt(3, supplier.getPhoneNumber());
				stmt.setString(4, supplier.getSaddress());

				// Execute the SQL statement
				stmt.executeUpdate();
			}

			// Close the connection
			con.close();
			System.out.println("Connection closed");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	//funcion to execute SQL 
	public void ExecuteStatement(String SQL) throws SQLException {

		try {
			java.sql.Statement stmt = con.createStatement();
			stmt.executeUpdate(SQL);
			stmt.close();

		} catch (SQLException s) {
			s.printStackTrace();
			System.out.println("SQL statement is not executed!");

		}
	}

	//delete function
	@SuppressWarnings("exports")
	@FXML
	public void handleDeleteSupplierButton(ActionEvent event) {
		ObservableList<supplier> selectedRows = supplier_table.getSelectionModel().getSelectedItems();
		ArrayList<supplier> rows = new ArrayList<>(selectedRows);
		rows.forEach(row -> {//delete specific rows
			supplier_table.getItems().remove(row);
			deleteSupplierRow(row);
			supplier_table.refresh();
		});
	}

	//delete SQL query
	private void deleteSupplierRow(supplier row) {
		try {
			System.out.println("DELETE FROM supplier WHERE supplierID=" + row.getSupplierID() + ";");
			connectDB();
			ExecuteStatement("DELETE FROM supplier WHERE supplierID=" + row.getSupplierID() + ";");
			con.close();
			System.out.println("Connection closed");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("exports")
	@FXML
	public void handleRefreshSupplierButton(ActionEvent event) {
		supplier_table.refresh();
	}

	private void showAlert(AlertType alertType, String title, String contentText) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

}