package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.sql.PreparedStatement;



public class orderLineController {
	@FXML
	private TableView<orderline> orderline_table;

	@FXML
	private TableColumn<orderline, Integer> orderIDCol;
	@FXML
	private TableColumn<orderline, Integer> productIDCol;
	@FXML
	private TableColumn<orderline, Double> unitpriceCol;
	@FXML
	private TableColumn<orderline, Integer> quantityCol;

	@FXML
	private TextField ordID;
	@FXML
	private TextField prodID;
	@FXML
	private TextField quan;

	@FXML
	private Button Addbtn;
	@FXML
	private Button deletebtn;
	@FXML
	private Button refreshbtn;

	@FXML
	private Button backbtn;

	@FXML
	private TextField Search;

	private String dbURL;
	private String dbUsername = "root";
	private String dbPassword = "Root1234";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "project";
	private Connection con;
	
	private ArrayList<orderline> data;

	private ObservableList<orderline> dataList;


	public void initialize() {
		
		// Initialize TableView and Columns
		orderIDCol.setCellValueFactory(new PropertyValueFactory<orderline, Integer>("orderID"));
		productIDCol.setCellValueFactory(new PropertyValueFactory<orderline, Integer>("productID"));
		quantityCol.setCellValueFactory(new PropertyValueFactory<orderline, Integer>("quantity"));
		unitpriceCol.setCellValueFactory(new PropertyValueFactory<orderline, Double>("unitprice"));

		// Initialize data list
		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		// Retrieve data from the database
		try {
			getData();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Add a computed column for total price
		TableColumn<orderline, Double> computedTotalPriceCol = new TableColumn<>("Total Price");
		computedTotalPriceCol.setCellValueFactory(param -> {
			double unitPrice = param.getValue().getUnitprice();
			int quantity = param.getValue().getQuantity();
			return new SimpleDoubleProperty(unitPrice * quantity).asObject();
		});
		computedTotalPriceCol.setPrefWidth(150);

		// Add the computed column to the TableView
		orderline_table.getColumns().add(computedTotalPriceCol);

		// Populate TableView with data
		orderline_table.setItems(dataList);

		// Make TableView editable
		orderline_table.setEditable(true);

		quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		quantityCol.setOnEditCommit(arg0 -> {
			try {
				updateQuantity(arg0);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

	}

	// Methods to handle updates
	private void updateQuantity(CellEditEvent<orderline, Integer> event) throws ClassNotFoundException, SQLException {

		// Get the old value before the update
		int oldValue = event.getOldValue();

		event.getTableView().getItems().get(event.getTablePosition().getRow()).setQuantity(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));

		connectDB();
		int rowIndex = event.getTablePosition().getRow();
		orderline updatedOrderLine = event.getTableView().getItems().get(rowIndex);

		int newValue = event.getNewValue();
		int proid = updatedOrderLine.getProductID();

		if (oldValue > newValue) {
			
			updateProductQuantity(proid, getAvailableQuantityFromDatabase(proid) + (oldValue - newValue));
		}

		else
			updateProductQuantity(proid,getAvailableQuantityFromDatabase(proid) - (newValue - oldValue));

		con.close();

	}

	private void updateDatabase(orderline updateorderline) {
		try {
			connectDB();
			// Use an UPDATE SQL statement to update the orderline details in the database
			String SQL = "UPDATE orderline SET quantity=? WHERE orderID=? AND productID=?";
			try (PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setInt(1, updateorderline.getQuantity());
				stmt.setInt(2, updateorderline.getOrderID());
				stmt.setInt(3, updateorderline.getProductID());
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

		SQL = "select orderID, productID, quantity, unitprice from orderline";
		java.sql.Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL);

		while (rs.next())
			data.add(new orderline(Integer.parseInt(rs.getString(1)), Integer.parseInt(rs.getString(2)),
					Integer.parseInt(rs.getString(3)), Double.parseDouble(rs.getString(4))));

		rs.close();
		stmt.close();

		con.close();
		System.out.println("Connection closed" + data.size());

		// Now, update the dataList
		dataList.setAll(data);
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
	@FXML
	public void handleBackButton(ActionEvent event) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("welcomepage.fxml"));
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root, 1536.0, 820.0);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Event Listener on Button[#Addbtn].onAction

	@SuppressWarnings("exports")
	public void handleAddButton(ActionEvent event) throws SQLException, ClassNotFoundException {

		connectDB();
		orderline rc;

		// Check if the product with the same ID already exists
		int ordid = Integer.valueOf(ordID.getText());
		int prodid = Integer.valueOf(prodID.getText());

		// Check if the record with the same (orderID, productID) already exists
		if (isOrderLineExists(ordid, prodid)) {
			showAlert(AlertType.ERROR, "Duplicate Record", "Record with the same (orderID, productID) already exists.");
			return; // Do not proceed with adding the product
		}

		if (isOrderExists(ordID.getText())) {
			// Fetch unitprice from the database based on ordered product
			double unitpriceValue = getUnitPriceFromDatabase(prodid);
			int quantity = getAvailableQuantityFromDatabase(prodid);

			if (quantity < Integer.valueOf(quan.getText())) {
				showAlert(AlertType.ERROR, "Insufficient Quantity",
						"Not enough quantity available for the selected product or Product does not exist.");
				return; // Do not proceed with adding the product
			}

			updateProductQuantity(prodid, quantity - Integer.valueOf(quan.getText()));

			rc = new orderline(ordid, Integer.valueOf(prodID.getText()), Integer.valueOf(quan.getText()),
					unitpriceValue);

			dataList.add(rc);
			insertData(rc);

			ordID.clear();
			prodID.clear();
			quan.clear();
			con.close();

		}

		else {
			showAlert(AlertType.ERROR, "Order does not exists", "Not Order with this ID ! ");
			return; // Do not proceed with adding the product

		}

	}

	private boolean isOrderLineExists(int orderID, int productID) {
		for (orderline existingOrderLine : dataList) {
			if (existingOrderLine.getOrderID() == orderID && existingOrderLine.getProductID() == productID) {
				return true; // Record already exists
			}
		}
		return false; // Record does not exist
	}

	private double getUnitPriceFromDatabase(int productID) {
		double unitprice = 0;
		try {

			connectDB();
			String SQL = "SELECT cost FROM product WHERE productID = ?";
			try (PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setInt(1, productID);
				ResultSet rs = stmt.executeQuery();

				if (rs.next()) {
					unitprice = rs.getDouble("cost");
				}

				rs.close();
			}
			con.close();
			System.out.println("Connection closed");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return unitprice;
	}

	private int getAvailableQuantityFromDatabase(int productID) {
		int availableQuantity = 0;
		try {
			connectDB();
			String SQL = "SELECT quantity FROM product WHERE productID = ?";
			try (PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setInt(1, productID);
				ResultSet rs = stmt.executeQuery();

				if (rs.next()) {
					availableQuantity = rs.getInt("quantity");
				}

				rs.close();
			}
			con.close();
			System.out.println("Connection closed");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return availableQuantity;
	}

	private void updateProductQuantity(int productID, int newQuantity) {
		try {
			connectDB();
			String SQL = "UPDATE product SET quantity = ? WHERE productID = ?";
			try (PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setInt(1, newQuantity);
				stmt.setInt(2, productID);
				stmt.executeUpdate();
			}
			con.close();
			System.out.println("Connection closed");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Method to check if a customer with the given ID has an order
	private boolean isOrderExists(String orderid) throws SQLException {

		String checkCustomerSQL = "SELECT 1 FROM orders WHERE orderID = ? LIMIT 1;";

		try (PreparedStatement checkStmt = con.prepareStatement(checkCustomerSQL)) {
			checkStmt.setString(1, orderid);

			try (ResultSet checkResult = checkStmt.executeQuery()) {
				return checkResult.next();
			}
		}

	}

	private void insertData(orderline rc) {
		try {

			connectDB();
			String SQL = "Insert into orderline (orderID, productID, quantity, unitprice) values (?, ?, ?, ?)";
			try (PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setInt(1, rc.getOrderID());
				stmt.setInt(2, rc.getProductID());
				stmt.setInt(3, rc.getQuantity());
				stmt.setDouble(4, rc.getUnitprice());
				stmt.executeUpdate();
			}
			con.close();
			System.out.println("Connection closed");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Event Listener on Button[#Deletebtn].onAction
	@SuppressWarnings("exports")
	@FXML
	public void handleDeleteButton(ActionEvent event) {
		ObservableList<orderline> selectedRows = orderline_table.getSelectionModel().getSelectedItems();
		ArrayList<orderline> rows = new ArrayList<>(selectedRows);
		rows.forEach(row -> {
			orderline_table.getItems().remove(row);
			deleteRow(row);
			orderline_table.refresh();
		});

	}

	private void deleteRow(orderline row) {
		// TODO Auto-generated method stub

		try {
			System.out.println("delete from  orderline where orderID= " + row.getOrderID() + " AND productID ="
					+ row.getProductID() + ";");
			connectDB();
			ExecuteStatement("delete from orderline where orderID=" + row.getOrderID() + " AND productID ="
					+ row.getProductID() + ";");
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
		orderline_table.refresh();
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

	@FXML
	private void searchOrderLine(ActionEvent event) {
		try {
			data = new ArrayList<>();
			dataList = FXCollections.observableArrayList(data);

			String searchTerm = Search.getText().trim();

			connectDB();
			System.out.println("Connection established");

			String SQL = "SELECT orderID, productID, quantity, unitprice FROM orderline WHERE orderID = ?";

			try (java.sql.PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setString(1, searchTerm);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						data.add(new orderline(Integer.parseInt(rs.getString(1)), Integer.parseInt(rs.getString(2)),
								Integer.parseInt(rs.getString(3)), Double.parseDouble(rs.getString(4))));
					}
				}
			}

			System.out.println("Connection closed" + data.size());

			if (data.isEmpty()) {
				// No order found, show alert
				showAlert(Alert.AlertType.INFORMATION, "Order Not Found", "No order found with the given ID.");
			} else {
				// Order found, show details in a dialog
				showOrderDetailsDialog(data);
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace(); // or log the exception using a logging framework
		}
	}

	private void showOrderDetailsDialog(List<orderline> orders) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("OrderLine Details");
		alert.setHeaderText(null);

		VBox vbox = new VBox();
		for (orderline order : orders) {
			Label orderidLabel = new Label("OrderID: " + order.getOrderID());
			Label productidLabel = new Label("product ID: " + order.getProductID());
			Label qLabel = new Label("quantity: " + order.getQuantity());
			Label unitpriceLabel = new Label("unit price: " + order.getUnitprice());

			vbox.getChildren().addAll(orderidLabel, productidLabel, qLabel, unitpriceLabel);

			// Add a separator between each order
			vbox.getChildren().add(new Separator());
		}

		alert.getDialogPane().setContent(vbox);

		// Set the size of the DialogPane
		alert.getDialogPane().setMinWidth(400);
		alert.getDialogPane().setMinHeight(200);

		alert.showAndWait();
	}

	private void showAlert(Alert.AlertType alertType, String title, String contentText) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(contentText);
		alert.showAndWait();
	}


}
