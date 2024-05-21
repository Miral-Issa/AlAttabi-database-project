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

public class PurchasePaymentController {

	@FXML
	private TableView<PurchasePayment> purPay_table;

	//@FXML
	//private TableView<customer> customer_table2;

	@FXML
	private TableColumn<PurchasePayment, Integer> purPayIDCol;
	@FXML
	private TableColumn<PurchasePayment, String> payTypeCol;
	@FXML
	private TableColumn<PurchasePayment, Integer> amountCol;
	@FXML
	private TableColumn<PurchasePayment, Date> payDateCol;
	@FXML
	private TableColumn<PurchasePayment, Date> dueDateCol;
	@FXML
	private TableColumn<PurchasePayment, Integer> purIDCol;
	
	@FXML
	private TextField PurPayID;
	@FXML
	private TextField PayType;
	@FXML
	private TextField Ammount;
	@FXML
	private TextField PayDateText;
	@FXML
	private TextField DueDateText;
	@FXML
	private TextField purIDText;

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

	private ArrayList<PurchasePayment> data;

	private ObservableList<PurchasePayment> dataList;

	public void initialize() {

		// Initialize TableView and Columns
		purPayIDCol.setCellValueFactory(new PropertyValueFactory<PurchasePayment, Integer>("paymentID"));
		payTypeCol.setCellValueFactory(new PropertyValueFactory<PurchasePayment, String>("type"));
		amountCol.setCellValueFactory(new PropertyValueFactory<PurchasePayment, Integer>("amount"));
		payDateCol.setCellValueFactory(new PropertyValueFactory<PurchasePayment, Date>("paymentDate"));
		dueDateCol.setCellValueFactory(new PropertyValueFactory<PurchasePayment, Date>("dueDate"));
		purIDCol.setCellValueFactory(new PropertyValueFactory<PurchasePayment, Integer>("purID"));

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
		purPay_table.setItems(dataList);

		// Make TableView editable
		purPay_table.setEditable(true);

		// Make each editable column editable
		payTypeCol.setCellFactory(TextFieldTableCell.forTableColumn());
		payTypeCol.setOnEditCommit(this::updateType);

		amountCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		amountCol.setOnEditCommit(this::updateAmount);

		//payDateCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		//payDateCol.setOnEditCommit(this::updatePhone);
		
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

			purIDCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
			purIDCol.setOnEditCommit(this::updatePurID);

	}

	// Methods to handle updates for each column
	private void updateType(CellEditEvent<PurchasePayment, String> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setType(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updateAmount(CellEditEvent<PurchasePayment, Integer> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setAmount(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}
	private void updatePayDate(CellEditEvent<PurchasePayment, Date> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setPaymentDate(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}
	private void updateDueDate(CellEditEvent<PurchasePayment, Date> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setPaymentDate(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}
	private void updatePurID(CellEditEvent<PurchasePayment, Integer> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setPurID(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	// Method to update the database with the new values
	private void updateDatabase(PurchasePayment updatedPurPay) {
		try {
			connectDB();
			// Use an UPDATE SQL statement to update the product details in the database
			String SQL = "UPDATE purchase_payment SET payment_type=?, payment_amount=?, payment_date=?, due_date=?, purID=? WHERE purchase_payment.paymentID=?";
			try (PreparedStatement stmt = (PreparedStatement) con.prepareStatement(SQL)) {
				stmt.setInt(1, updatedPurPay.getPaymentID());
				stmt.setString(2, updatedPurPay.getType());
				stmt.setInt(3, updatedPurPay.getAmount());
				stmt.setDate(4, updatedPurPay.getPaymentDate());
				stmt.setDate(5, updatedPurPay.getDueDate());
				stmt.setInt(6, updatedPurPay.getPurID());
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

		SQL = "select paymentID, payment_type ,payment_amount, payment_date, due_date, purID from purchase_payment";
		java.sql.Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL);

		while (rs.next())
			data.add(new PurchasePayment(Integer.parseInt(rs.getString(1)), rs.getString(2), Integer.parseInt(rs.getString(3)),
					rs.getDate(4), rs.getDate(5),Integer.parseInt(rs.getString(6))));
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

			String SQL = "select paymentID, payment_type ,payment_amount, payment_date, due_date, purID from purchase_payment WHERE purchase_payment.paymentID = ?";
			try (java.sql.PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setString(1, searchTerm);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						data.add(new PurchasePayment(Integer.parseInt(rs.getString(1)), rs.getString(2),Integer.parseInt(rs.getString(3)),
								rs.getDate(4), rs.getDate(5),Integer.parseInt(rs.getString(6))));
					}
				}
			}

			System.out.println("Connection closed" + data.size());

			// Now, update the dataList
			dataList.setAll(data);

			if (data.isEmpty()) {
				// No customer found, show alert
				showAlert(AlertType.INFORMATION, "Purchase Payment Not Found", "No Payment found with the given ID.");
			} else {
				// Customer found, show details in a dialog
				showPaymentDetailsDialog(data.get(0));
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace(); // or log the exception using a logging framework
		}
	}

	private void showPaymentDetailsDialog(PurchasePayment payment) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Payment Details");
		alert.setHeaderText(null);

		// Customize the dialog content with customer details
		Label typeLabel = new Label("Type: " + payment.getType());
		Label amountLabel = new Label("Amount: " + payment.getAmount());
		Label payDateLabel = new Label("Payment Date: " + payment.getPaymentDate());
		Label dueDateLabel = new Label("Due Date: " + payment.getDueDate());
		Label purIDLabel = new Label("Purchase ID: " + payment.getPurID());

		alert.getDialogPane().setContent(new VBox(typeLabel, amountLabel, payDateLabel, dueDateLabel,purIDLabel)); // Add more
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
		PurchasePayment rc;

		// Check if the product with the same ID already exists
		int purPayId = Integer.valueOf(PurPayID.getText());
		int purID = Integer.parseInt(purIDText.getText());
		if (isPaymentIdExists(purPayId)) {
			showAlert(AlertType.WARNING, "Duplicate purchase Payment ID", "A payment with the same ID already exists.");
			return;
		}
		// Check if the customerID exists in the Customer table
					if (!isPurIDExists(purID)) {
						showAlert(Alert.AlertType.ERROR, "Purchase ID Error",
								"Purchase ID does not exist in the Purchase table.");
						return;
					}

		if (DueDateText.getText().equals("NULL"))
		{
			
			rc = new PurchasePayment(
					purPayId, 
					PayType.getText(),
					Integer.valueOf(Ammount.getText()),
					java.sql.Date.valueOf(PayDateText.getText()),
					null,
					Integer.valueOf(purIDText.getText())
					);
		}
					
		else
		{
			rc = new PurchasePayment(
					purPayId, 
					PayType.getText(),
					Integer.valueOf(Ammount.getText()),
					java.sql.Date.valueOf(PayDateText.getText()),
					java.sql.Date.valueOf(DueDateText.getText()),
					Integer.valueOf(purIDText.getText())
					);
			
		}


		dataList.add(rc);
		insertData(rc);

		PurPayID.clear();
		PayType.clear();
		Ammount.clear();
		PayDateText.clear();
		DueDateText.clear();
		purIDText.clear();

	}

	// Helper method to check if a product with the given ID already exists
	private boolean isPaymentIdExists(int purPayId) {
		for (PurchasePayment payment : dataList) {
			if (payment.getPaymentID() == purPayId) {
				return true;
			}
		}
		return false;
	}
	// Method to check if a customerID exists in the Customer table
		private boolean isPurIDExists(int PurID) {
			try {
				connectDB();
				String SQL = "SELECT * FROM PurchaseOrder WHERE purID=?";
				try (java.sql.PreparedStatement stmt =con.prepareStatement(SQL)) {
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
	private void insertData(PurchasePayment rc) {

		try {

			System.out.println("Insert into purchase_payment (paymentID, payment_type, payment_amount, payment_date, due_date,purID) values("
					+ rc.getPaymentID() + ",'" + rc.getType() + "'," + rc.getAmount() + ",'" + rc.getPaymentDate()
					+ "','" + rc.getDueDate()+"', "+ rc.getPurID() + ";");

			connectDB();
			
			if (rc.getDueDate() == null)
			{
				ExecuteStatement("Insert into purchase_payment (paymentID, payment_type, payment_amount, payment_date, due_date,purID) values("
						+ rc.getPaymentID() + ",'" + rc.getType() + "'," + rc.getAmount() + ",'" + rc.getPaymentDate()
						+ "'," + rc.getDueDate()+", "+ rc.getPurID() + ");");
				
			}
			
			else
			{
				ExecuteStatement("Insert into purchase_payment (paymentID, payment_type, payment_amount, payment_date, due_date,purID) values("
						+ rc.getPaymentID() + ",'" + rc.getType() + "'," + rc.getAmount() + ",'" + rc.getPaymentDate()
						+ "','" + rc.getDueDate()+"', "+ rc.getPurID() + ");");
				
			}

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
		ObservableList<PurchasePayment> selectedRows = purPay_table.getSelectionModel().getSelectedItems();
		ArrayList<PurchasePayment> rows = new ArrayList<>(selectedRows);
		rows.forEach(row -> {
			purPay_table.getItems().remove(row);
			deleteRow(row);
			purPay_table.refresh();
		});

	}

	private void deleteRow(PurchasePayment row) {
		// TODO Auto-generated method stub

		try {
			System.out.println("delete from  purchase_payment where paymentID=" + row.getPaymentID() + ";");
			connectDB();
			ExecuteStatement("delete from  purchase_payment where paymentID=" + row.getPaymentID() + ";");
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
		purPay_table.refresh();
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/showPurChecks.fxml"));
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