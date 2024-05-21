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
import javafx.util.StringConverter;
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
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class OrderPaymentController {

	@FXML
	private TableView<OrderPayment> orderPay_table;

	// @FXML
	// private TableView<customer> customer_table2;

	@FXML
	private TableColumn<OrderPayment, Integer> orderPayIDCol;
	@FXML
	private TableColumn<OrderPayment, String> payTypeCol;
	@FXML
	private TableColumn<OrderPayment, Integer> amountCol;
	@FXML
	private TableColumn<OrderPayment, Date> payDateCol;
	@FXML
	private TableColumn<OrderPayment, Date> dueDateCol;
	@FXML
	private TableColumn<OrderPayment, Integer> orderIDCol;

	@FXML
	private TextField orderPayID;
	@FXML
	private TextField PayType;
	@FXML
	private TextField Ammount;
	@FXML
	private TextField PayDateText;
	@FXML
	private TextField DueDateText;
	@FXML
	private TextField orderIDText;

	@FXML
	private Button Addbtn;
	@FXML
	private Button deletebtn;
	@FXML
	private Button refreshbtn;

	@FXML
	private Button backbtn;

	@FXML
	private Button checks;

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

	private ArrayList<OrderPayment> data;

	private ObservableList<OrderPayment> dataList;

	public void initialize() {

		// Initialize TableView and Columns
		orderPayIDCol.setCellValueFactory(new PropertyValueFactory<OrderPayment, Integer>("paymentID"));
		payTypeCol.setCellValueFactory(new PropertyValueFactory<OrderPayment, String>("type"));
		amountCol.setCellValueFactory(new PropertyValueFactory<OrderPayment, Integer>("amount"));
		payDateCol.setCellValueFactory(new PropertyValueFactory<OrderPayment, Date>("paymentDate"));
		dueDateCol.setCellValueFactory(new PropertyValueFactory<OrderPayment, Date>("dueDate"));
		orderIDCol.setCellValueFactory(new PropertyValueFactory<OrderPayment, Integer>("orderID"));

		// Initialize data list
		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		// Retrieve data from the database
		try {
			getData();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Populate TableView with data
		orderPay_table.setItems(dataList);

		// Make TableView editable
		orderPay_table.setEditable(true);

		// Make each editable column editable
		payTypeCol.setCellFactory(TextFieldTableCell.forTableColumn());
		payTypeCol.setOnEditCommit(this::updateType);

		amountCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		amountCol.setOnEditCommit(this::updateAmount);

		// payDateCol.setCellFactory(TextFieldTableCell.forTableColumn(new
		// IntegerStringConverter()));
		// payDateCol.setOnEditCommit(this::updatePhone);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// Create a custom StringConverter for java.sql.Date
		StringConverter<java.sql.Date> dateConverter = new StringConverter<java.sql.Date>() {
			@Override
			public String toString(java.sql.Date object) {
				return object == null ? "" : dateFormat.format(object);
			}

			@Override
			public java.sql.Date fromString(String string) {
				try {
					return string.isEmpty() ? null : new java.sql.Date(dateFormat.parse(string).getTime());
				} catch (ParseException e) {
					e.printStackTrace();
					return null;
				}
			}
		};

		// Set cell factory with the custom StringConverter
		payDateCol.setCellFactory(TextFieldTableCell.forTableColumn(dateConverter));
		// Set the event handler for cell editing
		payDateCol.setOnEditCommit(this::updatePayDate);

		// Set cell factory with the custom StringConverter
		dueDateCol.setCellFactory(TextFieldTableCell.forTableColumn(dateConverter));
		// Set the event handler for cell editing
		dueDateCol.setOnEditCommit(this::updateDueDate);

		orderIDCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		orderIDCol.setOnEditCommit(this::updateOrderID);

	}

	// Methods to handle updates for each column
	private void updateType(CellEditEvent<OrderPayment, String> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setType(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updateAmount(CellEditEvent<OrderPayment, Integer> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setAmount(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updatePayDate(CellEditEvent<OrderPayment, Date> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setPaymentDate(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updateDueDate(CellEditEvent<OrderPayment, Date> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setPaymentDate(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updateOrderID(CellEditEvent<OrderPayment, Integer> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setOrderID(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	// Method to update the database with the new values
	private void updateDatabase(OrderPayment updatedOrderPay) {
		try {
			connectDB();
			// Use an UPDATE SQL statement to update the product details in the database
			String SQL = "UPDATE order_payment SET payment_type=?, payed_amount=?, payment_date=?, due_date=?, orderID=? WHERE order_payment.paymentID=?";
			try (PreparedStatement stmt = (PreparedStatement) con.prepareStatement(SQL)) {
				stmt.setInt(1, updatedOrderPay.getPaymentID());
				stmt.setString(2, updatedOrderPay.getType());
				stmt.setInt(3, updatedOrderPay.getAmount());
				stmt.setDate(4, updatedOrderPay.getPaymentDate());
				stmt.setDate(5, updatedOrderPay.getDueDate());
				stmt.setInt(6, updatedOrderPay.getOrderID());
				stmt.executeUpdate();
			}
			con.close();
			System.out.println("Connection closed");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void getData() throws SQLException, ClassNotFoundException {
		// Initialize data list
		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		String SQL;

		connectDB();
		System.out.println("Connection established");

		SQL = "select paymentID, payment_type ,payed_amount, payment_date, due_date, orderID from order_payment";
		java.sql.Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL);

		while (rs.next())
			data.add(new OrderPayment(Integer.parseInt(rs.getString(1)), rs.getString(2),
					Integer.parseInt(rs.getString(3)), rs.getDate(4), rs.getDate(5),
					Integer.parseInt(rs.getString(6))));
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

			String SQL = "select paymentID, payment_type, payed_amount, payment_date, due_date, orderID from order_payment WHERE order_payment.paymentID = ?";
			try (java.sql.PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setString(1, searchTerm);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						data.add(new OrderPayment(Integer.parseInt(rs.getString(1)), rs.getString(2),
								Integer.parseInt(rs.getString(3)), rs.getDate(4), rs.getDate(5),
								Integer.parseInt(rs.getString(6))));
					}
				}
			}

			System.out.println("Connection closed" + data.size());

			// Now, update the dataList
			dataList.setAll(data);

			if (data.isEmpty()) {
				// No customer found, show alert
				showAlert(AlertType.INFORMATION, "Order Payment Not Found", "No Payment found with the given ID.");
			} else {
				// Customer found, show details in a dialog
				showPaymentDetailsDialog(data.get(0));
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace(); // or log the exception using a logging framework
		}
	}

	private void showPaymentDetailsDialog(OrderPayment payment) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Payment Details");
		alert.setHeaderText(null);

		// Customize the dialog content with customer details
		Label typeLabel = new Label("Type: " + payment.getType());
		Label amountLabel = new Label("Amount: " + payment.getAmount());
		Label payDateLabel = new Label("Payment Date: " + payment.getPaymentDate());
		Label dueDateLabel = new Label("Due Date: " + payment.getDueDate());
		Label orderIDLabel = new Label("Purchase ID: " + payment.getOrderID());

		alert.getDialogPane().setContent(new VBox(typeLabel, amountLabel, payDateLabel, dueDateLabel, orderIDLabel)); // Add
																														// more
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
		OrderPayment rc;

		// Check if the product with the same ID already exists
		int ordePayId = Integer.valueOf(orderPayID.getText());
		int orderID = Integer.parseInt(orderIDText.getText());
		if (isPaymentIdExists(ordePayId)) {
			showAlert(AlertType.WARNING, "Duplicate order Payment ID", "A payment with the same ID already exists.");
			return;
		}
		// Check if the customerID exists in the Customer table
		if (!isOrderIDExists(orderID)) {
			showAlert(Alert.AlertType.ERROR, "Order ID Error", "Order ID does not exist in the Purchase table.");
			return;
		}

		// if(DueDateText.getText())
		// System.out.println("due Date value = "+ DueDateText.getText());
		if (DueDateText.getText().equals("NULL")) {
			rc = new OrderPayment(ordePayId, PayType.getText(), Integer.valueOf(Ammount.getText()),
					java.sql.Date.valueOf(PayDateText.getText()),
					// java.sql.Date.valueOf(DueDateText.getText()),
					null, Integer.valueOf(orderIDText.getText()));
		} else {
			rc = new OrderPayment(ordePayId, PayType.getText(), Integer.valueOf(Ammount.getText()),
					java.sql.Date.valueOf(PayDateText.getText()), java.sql.Date.valueOf(DueDateText.getText()),
					Integer.valueOf(orderIDText.getText()));
		}

		dataList.add(rc);
		insertData(rc);

		orderPayID.clear();
		PayType.clear();
		Ammount.clear();
		PayDateText.clear();
		DueDateText.clear();
		orderIDText.clear();

	}

	// Helper method to check if a product with the given ID already exists
	private boolean isPaymentIdExists(int purPayId) {
		for (OrderPayment payment : dataList) {
			if (payment.getPaymentID() == purPayId) {
				return true;
			}
		}
		return false;
	}

	// Method to check if a customerID exists in the Customer table
	private boolean isOrderIDExists(int PurID) {
		try {
			connectDB();
			String SQL = "SELECT * FROM order_payment WHERE orderID=?";
			try (java.sql.PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setInt(1, PurID);
				try (ResultSet rs = stmt.executeQuery()) {
					return rs.next();
				}
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void insertData(OrderPayment rc) {

		try {

			System.out.println(
					"Insert into order_payment (paymentID, payment_type, paymed_amount, payment_date, due_date,orderID) values("
							+ rc.getPaymentID() + ",'" + rc.getType() + "'," + rc.getAmount() + ",'"
							+ rc.getPaymentDate() + "','" + rc.getDueDate() + "', " + rc.getOrderID() + ");");

			connectDB();
			if (rc.getDueDate() == null) {
				ExecuteStatement(
						"Insert into order_payment (paymentID, payment_type, payed_amount, payment_date, due_date,orderID) values("
								+ rc.getPaymentID() + ",'" + rc.getType() + "'," + rc.getAmount() + ",'"
								+ rc.getPaymentDate() + "'," + rc.getDueDate() + ", " + rc.getOrderID() + ");");

			} else {
				ExecuteStatement(
						"Insert into order_payment (paymentID, payment_type, payed_amount, payment_date, due_date,orderID) values("
								+ rc.getPaymentID() + ",'" + rc.getType() + "'," + rc.getAmount() + ",'"
								+ rc.getPaymentDate() + "','" + rc.getDueDate() + "', " + rc.getOrderID() + ");");
			}

			con.close();

			System.out.println("Connection closed" + data.size());

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL error");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Event Listener on Button[#Deletebtn].onAction
	@SuppressWarnings("exports")
	@FXML
	public void handleDeleteButton(ActionEvent event) {
		ObservableList<OrderPayment> selectedRows = orderPay_table.getSelectionModel().getSelectedItems();
		ArrayList<OrderPayment> rows = new ArrayList<>(selectedRows);
		rows.forEach(row -> {
			orderPay_table.getItems().remove(row);
			deleteRow(row);
			orderPay_table.refresh();
		});

	}

	private void deleteRow(OrderPayment row) {
		// TODO Auto-generated method stub

		try {
			System.out.println("delete from  order_payment where paymentID=" + row.getPaymentID() + ";");
			connectDB();
			ExecuteStatement("delete from  order_payment where paymentID=" + row.getPaymentID() + ";");
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
		orderPay_table.refresh();
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

	@SuppressWarnings("exports")
	public void handlecheckButton(ActionEvent event)
	{
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/showChecks.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root, 1536.0, 820.0);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}