package application;

import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.VBox;


public class ordersController implements Initializable {
	
    @FXML
    private TableView<Orders> ordersTable;

    @FXML
    private TableColumn<Orders, Integer> orderIDCol;

    @FXML
    private TableColumn<Orders, Integer> customerIDCol;

    @FXML
    private TableColumn<Orders, Date> orderDateCol;

    @FXML
    private TableColumn<Orders, Date> requiredDateCol;
 
    
    @FXML
    private TextField orderIDField;

    @FXML
    private TextField customerIDField;

    @FXML
    private TextField orderDateField;

    @FXML
    private TextField requiredDateField;
    
	@FXML
	private TextField BillField;

	@FXML
	private Button cal_bill;

    @FXML
    private Button addOrderButton;

    @FXML
    private Button deleteOrderButton;

    @FXML
    private Button refreshOrdersButton;

    @FXML
    private Button backSupplierBtn;
   

    @FXML
    private TextField Search;
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String dbURL;
    private String dbUsername = "root";
    private String dbPassword = "Root1234";
    private String URL = "127.0.0.1";
    private String port = "3306";
    private String dbName = "project";
    private Connection con;

    private ObservableList<Orders> dataList;
    private ArrayList<Orders> data;

	@FXML
	private void handleAddOrder() throws ParseException {

		try {

			int orderID = Integer.parseInt(orderIDField.getText());
			int customerID = Integer.parseInt(customerIDField.getText());

			// Check if the customerID exists in the Customer table
			if (!isCustomerIDExists(customerID)) {
				showAlert(Alert.AlertType.ERROR, "Customer ID Error",
						"Customer ID does not exist in the Customer table.");
				return;
			}

			// Parse orderDate and requiredDate using DateTimeFormatter
			java.util.Date orderDate = dateFormat.parse(orderDateField.getText());
			java.util.Date requiredDate = dateFormat.parse(requiredDateField.getText());

			Orders newOrder = new Orders(orderID, customerID, new java.sql.Date(orderDate.getTime()),
					new java.sql.Date(requiredDate.getTime()));

			if (!isOrderIdExists(newOrder.getOrderID())) {
				dataList.add(newOrder);
				insertOrder(newOrder);

				orderIDField.clear();
				customerIDField.clear();
				orderDateField.clear();
				requiredDateField.clear();

				// Refresh TableView
				ordersTable.refresh(); // Instead of setting the entire dataList, just refresh the table

			} else {
				showAlert(Alert.AlertType.WARNING, "Duplicate Order ID", "An order with the same ID already exists.");
			}
		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter valid numeric values.");
		} catch (DateTimeParseException e) {
			showAlert(Alert.AlertType.ERROR, "Date and Time Error",
					"Please enter valid date and time in the format 'yyyy-MM-dd'.");
		}
	}
    private boolean isOrderIdExists(int orderId) {
        for (Orders orders : dataList) {
            if (orders.getOrderID() == orderId) {
                return true;
            }
        }
        return false;
    }
    @FXML
    private void handleDeleteOrder() {
        ObservableList<Orders> selectedRows = ordersTable.getSelectionModel().getSelectedItems();
        ArrayList<Orders> rows = new ArrayList<>(selectedRows);
        rows.forEach(row -> {
            ordersTable.getItems().remove(row);
            deleteRow(row);
            ordersTable.refresh();

        });
    }
    @FXML
    private void handleRefreshOrders() {
        ordersTable.refresh();
    }
	private void insertOrder(Orders rc) throws ParseException {
		try {

			String formattedOrderDate = dateFormat.format(rc.getOrderdate());
			String formattedRequiredDate = dateFormat.format(rc.getRequireddate());

			System.out.println(
					"Insert into Orders (orderID, customerID, orderDate, requiredDate) values(" + rc.getOrderID() + ","
							+ rc.getCustomerID() + ",'" + formattedOrderDate + "','" + formattedRequiredDate + "');");

			connectDB();

			ExecuteStatement(
					"Insert into Orders (orderID, customerID, orderDate, requiredDate) values(" + rc.getOrderID() + ","
							+ rc.getCustomerID() + ",'" + formattedOrderDate + "','" + formattedRequiredDate + "');");

			System.out.println("Connection closed" + dataList.size());

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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
	private void deleteRow(Orders row) {
		try {
			System.out.println("delete from  Orders where orderID=" + row.getOrderID() + ";");
			connectDB();
			ExecuteStatement("delete from  Orders where orderID=" + row.getOrderID() + ";");
			con.close();
			System.out.println("Connection closed");

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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

	private void getData() throws SQLException, ClassNotFoundException, ParseException {

		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		String SQL;

		connectDB();
		System.out.println("Connection established");

		SQL = "SELECT orderID, customerID, orderDate, requiredDate FROM Orders ORDER BY orderID";
		java.sql.Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL);

		while (rs.next()) {
			data.add(new Orders(Integer.parseInt(rs.getString(1)), Integer.parseInt(rs.getString(2)), rs.getDate(3),
					rs.getDate(4)

			));
		}

		rs.close();
		stmt.close();
		con.close();
		System.out.println("Connection closed" + data.size());

		dataList.setAll(data);
	}

    
    
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		System.out.println("Orders Controller Initialized");
		System.out.println(getClass().getResource("Orders.fxml"));

		orderIDCol.setCellValueFactory(new PropertyValueFactory<>("orderID"));
		customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
		orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderdate"));
		requiredDateCol.setCellValueFactory(new PropertyValueFactory<>("requireddate"));

		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		try {
			getData();
		} catch (SQLException | ClassNotFoundException | ParseException e) {
			e.printStackTrace();
		}

		ordersTable.setItems(dataList);
		ordersTable.setEditable(true);

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
		orderDateCol.setCellFactory(TextFieldTableCell.forTableColumn(dateConverter));

		// Set the event handler for cell editing
		orderDateCol.setOnEditCommit(arg0 -> {
			try {
				updateOrderDate(arg0);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		});

		// Set cell factory with the custom StringConverter
		requiredDateCol.setCellFactory(TextFieldTableCell.forTableColumn(dateConverter));

		// Set the event handler for cell editing
		requiredDateCol.setOnEditCommit(arg0 -> {
			try {
				updateRequiredDate(arg0);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		// Set cell factory for customerIDCol
		customerIDCol.setCellFactory(col -> new TableCell<Orders, Integer>() {
			@Override
			protected void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setText(String.valueOf(item));
				}
			}
		});

		// Set cell factory for customerIDCol
		customerIDCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		// Set event handler for customerIDCol edit commit
		customerIDCol.setOnEditCommit(arg0 -> {
			try {
				updateCustomerID(arg0);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

	}
    
	@FXML
	private void updateCustomerID(CellEditEvent<Orders, Integer> event) throws ParseException {
		Orders updatedOrder = event.getTableView().getItems().get(event.getTablePosition().getRow());
		try {
			int newCustomerID = event.getNewValue();

			// Check if the newCustomerID exists in the Customer table
			if (!isCustomerIDExists(newCustomerID)) {
				showAlert(Alert.AlertType.ERROR, "Customer ID Error",
						"Customer ID does not exist in the Customer table.");
				return;
			}

			updatedOrder.setCustomerID(newCustomerID);
			updateOrderDatabase(updatedOrder);

		} catch (NumberFormatException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid numeric value for Customer ID.");
		}
	}
	// Method to check if a customerID exists in the Customer table
	private boolean isCustomerIDExists(int customerID) {
		try {
			connectDB();
			String SQL = "SELECT * FROM Customer WHERE customerID=?";
			try (PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setInt(1, customerID);
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
    @FXML
	private void updateRequiredDate(CellEditEvent<Orders, Date> event) throws ParseException {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setRequireddate(event.getNewValue());
		updateOrderDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updateOrderDate(CellEditEvent<Orders, Date> event) throws ParseException {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setOrderdate(event.getNewValue());
		updateOrderDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}
	
	private void updateOrderDatabase(Orders updatedOrder) throws ParseException {
		try {
			connectDB();

			// Use an UPDATE SQL statement to update the order details in the database
			String SQL = "UPDATE Orders SET CustomerID=?, orderDate=?, requiredDate=? WHERE orderID=?";

			try (PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setInt(1, updatedOrder.getCustomerID());
				stmt.setDate(2, updatedOrder.getOrderdate());
				stmt.setDate(3, updatedOrder.getRequireddate());
				stmt.setInt(4, updatedOrder.getOrderID());
				stmt.executeUpdate();
			}


			con.close();
			System.out.println("Connection closed");

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error",
					"Error updating order in the database: " + e.getMessage());
		}

	}
	@FXML
	private void searchOrder(ActionEvent event) {
		try {
			data = new ArrayList<>();
			dataList = FXCollections.observableArrayList(data);

			String searchTerm = Search.getText().trim();

			connectDB();
			System.out.println("Connection established");

			String SQL = "SELECT orderID, customerID, orderDate, requiredDate FROM Orders WHERE orderID = ?";
			System.out.println("SQL Update Statement: " + SQL);

			try (java.sql.PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setString(1, searchTerm);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						data.add(new Orders(Integer.parseInt(rs.getString(1)), Integer.parseInt(rs.getString(2)),
								rs.getDate(3), rs.getDate(4)));
					}
				}
			}

			System.out.println("Connection closed" + data.size());

			if (data.isEmpty()) {
				// No order found, show alert
				showAlert(Alert.AlertType.INFORMATION, "Order Not Found", "No order found with the given ID.");
			} else {
				// Order found, show details in a dialog
				showOrderDetailsDialog(data.get(0));
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace(); // or log the exception using a logging framework
		}
	}


	private void showAlert(Alert.AlertType alertType, String title, String contentText) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

	@SuppressWarnings("exports")
	public void handleBillButton(ActionEvent event) throws ClassNotFoundException, SQLException {
		// Get the value from the BillField
		String cusid = BillField.getText();

		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		connectDB();
		System.out.println("Connection established");

		if (isCustomerWithOrderExists(cusid)) {
			String SQL = "SELECT " + "o.CustomerID, " + "ol.orderID, " + "SUM(ol.quantity * ol.unitprice) AS totalBill "
					+ "FROM " + "orders o " + "JOIN " + "orderline ol ON o.orderID = ol.orderID " + "WHERE "
					+ "o.CustomerID = ? " + "GROUP BY " + "o.CustomerID, ol.orderID;";


			try (PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setString(1, cusid);

				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						int customerID = rs.getInt("CustomerID");
						int orderID = rs.getInt("orderID");
						double totalBill = rs.getDouble("totalBill");

						// You can customize the dialog to display the result
						showResultDialog(customerID, orderID, totalBill);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				con.close();
			}

		}

		else {
			if (!isCustomerWithOrderExists(cusid)) {
				showAlert(Alert.AlertType.ERROR, "Customer ID Error",
						"Customer ID does not exist in the Customer-orders table.");
				return;
			}

		}

	}
	private void showOrderDetailsDialog(Orders order) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Order Details");
		alert.setHeaderText(null);

		Label orderidLabel = new Label("OrderID: " + order.getOrderID());
		Label customeridLabel = new Label("CustomerID: " + order.getCustomerID());
		Label orderdateLabel = new Label("order date date: " + order.getOrderdate());
		Label requireddateLabel = new Label("require date: " + order.getRequireddate());

		VBox vbox = new VBox();
		vbox.getChildren().addAll(orderidLabel, customeridLabel, orderdateLabel, requireddateLabel);

		alert.getDialogPane().setContent(vbox);

		// Set the size of the DialogPane
		alert.getDialogPane().setMinWidth(400);
		alert.getDialogPane().setMinHeight(200);

		alert.showAndWait();
	}
    

		// Method to check if a customer with the given ID has an order
		private boolean isCustomerWithOrderExists(String cusid) throws SQLException {

			String checkCustomerSQL = "SELECT 1 FROM orders WHERE CustomerID = ? LIMIT 1;";

			try (PreparedStatement checkStmt = con.prepareStatement(checkCustomerSQL)) {
				checkStmt.setString(1, cusid);

				try (ResultSet checkResult = checkStmt.executeQuery()) {
					return checkResult.next();
				}
			}

		}
		
		// Method to show a dialog with the result
		private void showResultDialog(int customerID, int orderID, double totalBill) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Total Bill Information");
			alert.setHeaderText(null);
			alert.setContentText(
					"Customer ID: " + customerID + "\nOrder ID: " + orderID + "\nTotal Bill: " + totalBill + " shekels");

			alert.showAndWait();
		}
    
    
	// Event Listener on Button[#Backbtn].onAction
    @SuppressWarnings("exports")
	public void handleBackButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Welcomepage.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1536.0, 820.0);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
