package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import com.mysql.jdbc.PreparedStatement;

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

public class customerController {

	@FXML
	private TableView<customer> customer_table;

	@FXML
	private TableView<customer> customer_table2;

	@FXML
	private TableColumn<customer, Integer> customerIDCol;
	@FXML
	private TableColumn<customer, String> CnameCol;
	@FXML
	private TableColumn<customer, String> addressCol;
	@FXML
	private TableColumn<customer, Integer> phoneNumberCol;
	@FXML
	private TableColumn<customer, String> ctypeCol;

	@FXML
	private TableColumn<customer, Integer> customerIDCol2;
	@FXML
	private TableColumn<customer, String> CnameCol2;
	@FXML
	private TableColumn<customer, String> addressCol2;
	@FXML
	private TableColumn<customer, Integer> phoneNumberCol2;
	@FXML
	private TableColumn<customer, String> ctypeCol2;

	@FXML
	private TextField cusID;
	@FXML
	private TextField Name;
	@FXML
	private TextField Address;
	@FXML
	private TextField Phonenumber;
	@FXML
	private TextField Type;

	@FXML
	private Button Addbtn;
	@FXML
	private Button deletebtn;
	@FXML
	private Button refreshbtn;

	@FXML
	private Button backbtn;

	private Parent root = null;

	@FXML
	private TextField Search;

	private String dbURL;
	private String dbUsername = "root";
	private String dbPassword = "Root1234";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "project";
	private Connection con;

	private ArrayList<customer> data;

	private ObservableList<customer> dataList;

	public void initialize() {

		// Initialize TableView and Columns
		customerIDCol.setCellValueFactory(new PropertyValueFactory<customer, Integer>("customerID"));
		CnameCol.setCellValueFactory(new PropertyValueFactory<customer, String>("cname"));
		addressCol.setCellValueFactory(new PropertyValueFactory<customer, String>("address"));
		phoneNumberCol.setCellValueFactory(new PropertyValueFactory<customer, Integer>("phoneNumber"));
		ctypeCol.setCellValueFactory(new PropertyValueFactory<customer, String>("ctype"));

		// Initialize data list
		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		// Retrieve data from the database
		try {
			String SQL = "select CustomerID, Cname ,address, phoneNumber, Ctype from customer";
			getData(SQL);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Populate TableView with data
		customer_table.setItems(dataList);

		// Make TableView editable
		customer_table.setEditable(true);

		// Make each editable column editable
		CnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		CnameCol.setOnEditCommit(this::updateName);

		addressCol.setCellFactory(TextFieldTableCell.forTableColumn());
		addressCol.setOnEditCommit(this::updateAddress);

		phoneNumberCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		phoneNumberCol.setOnEditCommit(this::updatePhone);

		ctypeCol.setCellFactory(TextFieldTableCell.forTableColumn());
		ctypeCol.setOnEditCommit(this::updatetype);

		initialize2();
	}
	
	
	public void initialize2() {

		// Initialize TableView and Columns
		customerIDCol2.setCellValueFactory(new PropertyValueFactory<customer, Integer>("customerID"));
		CnameCol2.setCellValueFactory(new PropertyValueFactory<customer, String>("cname"));
		addressCol2.setCellValueFactory(new PropertyValueFactory<customer, String>("address"));
		phoneNumberCol2.setCellValueFactory(new PropertyValueFactory<customer, Integer>("phoneNumber"));
		ctypeCol2.setCellValueFactory(new PropertyValueFactory<customer, String>("ctype"));

		// Initialize data list
		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		// Retrieve data from the database
		try {
			String SQL = "select C.CustomerID, C.Cname ,C.address, C.phoneNumber, C.Ctype from customer C,orders,order_payment where C.CustomerID = orders.CustomerID and orders.orderID = order_payment.orderID and order_payment.payment_type = 'debt' and order_payment.due_date is null";
			getData(SQL);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Populate TableView with data
		customer_table2.setItems(dataList);

		// Make TableView editable
		customer_table2.setEditable(true);

		// Make each editable column editable
		CnameCol2.setCellFactory(TextFieldTableCell.forTableColumn());
		CnameCol2.setOnEditCommit(this::updateName);

		addressCol2.setCellFactory(TextFieldTableCell.forTableColumn());
		addressCol2.setOnEditCommit(this::updateAddress);

		phoneNumberCol2.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		phoneNumberCol2.setOnEditCommit(this::updatePhone);

		ctypeCol2.setCellFactory(TextFieldTableCell.forTableColumn());
		ctypeCol2.setOnEditCommit(this::updatetype);

	}

	// Methods to handle updates for each column
	private void updateName(CellEditEvent<customer, String> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setCname(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updateAddress(CellEditEvent<customer, String> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setAddress(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updatePhone(CellEditEvent<customer, Integer> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setPhoneNumber(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updatetype(CellEditEvent<customer, String> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setCtype(event.getNewValue());
		;
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	// Method to update the database with the new values
	private void updateDatabase(customer updatedcustomer) {
		try {
			connectDB();
			// Use an UPDATE SQL statement to update the product details in the database
			String SQL = "UPDATE customer SET CustomerID=?, Cname=?, address=?, phoneNumber=?, Ctype=? WHERE customer.CustomerID=?";
			try (PreparedStatement stmt = (PreparedStatement) con.prepareStatement(SQL)) {
				stmt.setInt(1, updatedcustomer.getCustomerID());
				stmt.setString(2, updatedcustomer.getCname());
				stmt.setString(3, updatedcustomer.getAddress());
				stmt.setInt(4, updatedcustomer.getPhoneNumber());
				stmt.setString(5, updatedcustomer.getCtype());
				stmt.setInt(6, updatedcustomer.getCustomerID());
				stmt.executeUpdate();
			}
			con.close();
			System.out.println("Connection closed");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void getData(String SQL) throws SQLException, ClassNotFoundException {
		// Initialize data list
		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);
		connectDB();
		System.out.println("Connection established");
		
		java.sql.Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL);

		while (rs.next())
			data.add(new customer(Integer.parseInt(rs.getString(1)), rs.getString(2), rs.getString(3),
					Integer.parseInt(rs.getString(4)), rs.getString(5)));
		rs.close();
		stmt.close();

		con.close();
		System.out.println("Connection closed" + data.size());

		// Now, update the dataList
		dataList.setAll(data);
	}

	// Event Listener on Button[#Backbtn].onAction
	@SuppressWarnings("exports")
	public void handleBackButton(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcomepage.fxml"));

		try {
			root = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Set the root as the new scene
		// this.root = root;

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root, 1536.0, 820.0);
		stage.setScene(scene);
		stage.show();

	}

	@SuppressWarnings("exports")
	@FXML
	public void Search(ActionEvent event) {
		try {
			data = new ArrayList<>();
			dataList = FXCollections.observableArrayList(data);

			String searchTerm = Search.getText().trim();

			connectDB();
			System.out.println("Connection established");

			String SQL = "select CustomerID, Cname ,address, phoneNumber, Ctype from customer WHERE customer.customerID = ?";
			try (java.sql.PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setString(1, searchTerm);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						data.add(new customer(Integer.parseInt(rs.getString(1)), rs.getString(2), rs.getString(3),
								Integer.parseInt(rs.getString(4)), rs.getString(5)));
					}
				}
			}

			System.out.println("Connection closed" + data.size());

			// Now, update the dataList
			dataList.setAll(data);

			if (data.isEmpty()) {
				// No customer found, show alert
				showAlert(AlertType.INFORMATION, "Customer Not Found", "No customer found with the given ID.");
			} else {
				// Customer found, show details in a dialog
				showProductDetailsDialog(data.get(0));
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace(); // or log the exception using a logging framework
		}
	}

	private void showProductDetailsDialog(customer customer) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Customer Details");
		alert.setHeaderText(null);

		// Customize the dialog content with customer details
		Label nameLabel = new Label("Name: " + customer.getCname());
		Label addressLabel = new Label("Address: " + customer.getAddress());
		Label phoneLabel = new Label("Phone Number: " + customer.getPhoneNumber());
		Label typeLabel = new Label("Type: " + customer.getCtype());

		alert.getDialogPane().setContent(new VBox(nameLabel, addressLabel, phoneLabel, typeLabel)); // Add more
																									// labels...

		// Set the size of the DialogPane
		alert.getDialogPane().setMinWidth(400);
		alert.getDialogPane().setMinHeight(200);

		alert.showAndWait();
	}

	private void showAlert(AlertType alertType, String title, String contentText) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

	// Event Listener on Button[#Addbtn].onAction

	@SuppressWarnings("exports")
	public void handleAddButton(ActionEvent event) {
		customer rc;

		// Check if the product with the same ID already exists
		int cusId = Integer.valueOf(cusID.getText());
		if (isCustomerIdExists(cusId)) {
			showAlert(AlertType.WARNING, "Duplicate customer ID", "A customer with the same ID already exists.");
			return;
		}

		rc = new customer(cusId, Name.getText(), Address.getText(), Integer.valueOf(Phonenumber.getText()),
				Type.getText());

		dataList.add(rc);
		insertData(rc);

		cusID.clear();
		Name.clear();
		Address.clear();
		Phonenumber.clear();
		Type.clear();

	}

	// Helper method to check if a product with the given ID already exists
	private boolean isCustomerIdExists(int cusId) {
		for (customer customer : dataList) {
			if (customer.getCustomerID() == cusId) {
				return true;
			}
		}
		return false;
	}

	private void insertData(customer rc) {

		try {

			System.out.println("Insert into customer (CustomerID, Cname, address, phoneNumber, Ctype) values("
					+ rc.getCustomerID() + ",'" + rc.getCname() + "','" + rc.getAddress() + "'," + rc.getPhoneNumber()
					+ ",'" + rc.getCtype() + "';");

			connectDB();
			ExecuteStatement("Insert into customer (CustomerID, Cname, address, phoneNumber, Ctype) values("
					+ rc.getCustomerID() + ",'" + rc.getCname() + "','" + rc.getAddress() + "'," + rc.getPhoneNumber()
					+ ",'" + rc.getCtype() + "')");

			con.close();

			System.out.println("Connection closed" + data.size());

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Event Listener on Button[#Deletebtn].onAction
	@SuppressWarnings("exports")
	@FXML
	public void handleDeleteButton(ActionEvent event) {
		ObservableList<customer> selectedRows = customer_table.getSelectionModel().getSelectedItems();
		ArrayList<customer> rows = new ArrayList<>(selectedRows);
		rows.forEach(row -> {
			customer_table.getItems().remove(row);
			deleteRow(row);
			customer_table.refresh();
		});

	}

	private void deleteRow(customer row) {
		// TODO Auto-generated method stub

		try {
			System.out.println("delete from  customer where CustomerID=" + row.getCustomerID() + ";");
			connectDB();
			ExecuteStatement("delete from  customer where CustomerID=" + row.getCustomerID() + ";");
			con.close();
			System.out.println("Connection closed");

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Event Listener on Button[#Refreshbtn].onAction
	@SuppressWarnings("exports")
	public void handleRefreshButton(ActionEvent event) {
		customer_table.refresh();
	}

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

	private void connectDB() throws ClassNotFoundException, SQLException {

		dbURL = "jdbc:mysql://" + URL + ":" + port + "/" + dbName + "?verifyServerCertificate=false";
		Properties p = new Properties();
		p.setProperty("user", dbUsername);
		p.setProperty("password", dbPassword);
		p.setProperty("useSSL", "false");
		p.setProperty("autoReconnect", "true");
		Class.forName("com.mysql.jdbc.Driver");

		con = DriverManager.getConnection(dbURL, p);

	}
	


}