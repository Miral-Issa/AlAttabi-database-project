package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.Group;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

import com.mysql.jdbc.PreparedStatement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;

public class Welcomepage implements Initializable {
	@FXML
	private TableView<Product> product_table;

	@FXML
	private TableColumn<Product, Integer> IDCol;
	@FXML
	private TableColumn<Product, String> nameCol;
	@FXML
	private TableColumn<Product, Integer> QuantityCol;
	@FXML
	private TableColumn<Product, Integer> costCol;
	@FXML
	private TableColumn<Product, Integer> supplierIDCol;
	@FXML
	private TableColumn<Product, Integer> shelflifeCol;
	@FXML
	private TableColumn<Product, Date> arrivedateCol;

	@FXML
	private TextField proID;
	@FXML
	private TextField Name;
	@FXML
	private TextField quantity;
	@FXML
	private TextField cost;
	@FXML
	private TextField supplierID;
	@FXML
	private TextField shelflife;
	@FXML
	private TextField arrivedate;

	@FXML
	private Button Addbtn;
	@FXML
	private Button deletebtn;
	@FXML
	private Button refreshbtn;
	@FXML
	private Button salesreport;
	@FXML
	private Button ordersreport;

	@FXML
	private Button customerOrderCountChartButton;

	@FXML
	private Button orderCountByYearChartButton;

	@FXML
	private TextField Search;

	private String dbURL;
	private String dbUsername = "root";
	private String dbPassword = "Root1234";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "project";
	private Connection con;

	private ArrayList<Product> data;

	private ObservableList<Product> dataList;

	private Parent root;

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@FXML
	private CategoryAxis supplierOrderCountXAxis;

	@FXML
	private NumberAxis supplierOrderCountYAxis;

	@FXML
	private Button supplierOrderCountChartButton;

	@FXML
	private BarChart<String, Number> supplierOrderCountChart;

	@FXML
	private BarChart<String, Number> customerOrderCountChart;

	@FXML
	private CategoryAxis customerOrderCountXAxis;

	@FXML
	private NumberAxis customerOrderCountYAxis;

	@FXML
	private BarChart<String, Number> orderCountByYearChart;

	@FXML
	private CategoryAxis orderCountByYearXAxis;

	@FXML
	private NumberAxis orderCountByYearYAxis;

	// Event Listener on Button Add button onAction

	@SuppressWarnings("exports")
	public void handleAddButton(ActionEvent event) {
		Product rc;

		try {
			java.util.Date utilDate = dateFormat.parse(arrivedate.getText());

			// Check if the product with the same ID already exists
			int productId = Integer.valueOf(proID.getText());
			if (isProductIdExists(productId)) {
				showAlert(AlertType.WARNING, "Duplicate Product ID", "A product with the same ID already exists.");
				return;
			}

			rc = new Product(productId, Name.getText(), Integer.valueOf(quantity.getText()),
					Integer.valueOf(cost.getText()), Integer.valueOf(supplierID.getText()),
					Integer.valueOf(shelflife.getText()), new java.sql.Date(utilDate.getTime()));

			dataList.add(rc);
			insertData(rc);

			proID.clear();
			Name.clear();
			quantity.clear();
			cost.clear();
			supplierID.clear();
			shelflife.clear();
			arrivedate.clear();

		} catch (ParseException e1) {
			e1.printStackTrace();
		}
	}

	// Helper method to check if a product with the given ID already exists
	private boolean isProductIdExists(int productId) {
		for (Product product : dataList) {
			if (product.getProductID() == productId) {
				return true;
			}
		}
		return false;
	}

	// Event Listener on Button delete button onAction
	@SuppressWarnings("exports")
	@FXML
	public void handleDeleteButton(ActionEvent event) {
		ObservableList<Product> selectedRows = product_table.getSelectionModel().getSelectedItems();
		ArrayList<Product> rows = new ArrayList<>(selectedRows);
		rows.forEach(row -> {
			product_table.getItems().remove(row);
			deleteRow(row);
			product_table.refresh();
		});

	}

	// Event Listener on Button Refresh button onAction
	@SuppressWarnings("exports")
	public void handleRefreshButton(ActionEvent event) {
		product_table.refresh();
	}

	private void insertData(Product rc) {

		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String formattedDate = dateFormat.format(rc.getArrivedate());

			System.out.println(
					"Insert into Product (productID, name ,quantity, cost, supplierID, shelfLife, arrivedate) values("
							+ rc.getProductID() + ",'" + rc.getName() + "'," + rc.getQuantity() + "," + rc.getCost()
							+ "," + rc.getSupplierID() + "," + rc.getShelfLife() + "," + rc.getSupplierID() + ",'"
							+ formattedDate + "');");

			connectDB();
			ExecuteStatement(
					"Insert into Product (productID, name ,quantity, cost, supplierID, shelfLife, arrivedate) values("
							+ rc.getProductID() + ",'" + rc.getName() + "'," + rc.getQuantity() + "," + rc.getCost()
							+ "," + rc.getSupplierID() + "," + rc.getShelfLife() + ",'" + formattedDate + "');");

			con.close();

			System.out.println("Connection closed" + data.size());

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void deleteRow(Product row) {
		// TODO Auto-generated method stub

		try {
			System.out.println("delete from  Product where productID=" + row.getProductID() + ";");
			connectDB();
			ExecuteStatement("delete from  Product where productID=" + row.getProductID() + ";");
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

	private void getData() throws SQLException, ClassNotFoundException {
		// Initialize data list
		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		String SQL;

		connectDB();
		System.out.println("Connection established");

		SQL = "select productID, name ,quantity, cost, supplierID, shelfLife, arrivedate from Product order by productID";
		java.sql.Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL);

		while (rs.next())
			data.add(new Product(Integer.parseInt(rs.getString(1)), rs.getString(2), Integer.parseInt(rs.getString(3)),
					Integer.parseInt(rs.getString(4)), Integer.parseInt(rs.getString(5)),
					Integer.parseInt(rs.getString(6)), rs.getDate(7)));
		rs.close();
		stmt.close();

		con.close();
		System.out.println("Connection closed" + data.size());

		// Now, update the dataList
		dataList.setAll(data);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Initialize TableView and Columns
		IDCol.setCellValueFactory(new PropertyValueFactory<Product, Integer>("productID"));
		nameCol.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
		QuantityCol.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantity"));
		costCol.setCellValueFactory(new PropertyValueFactory<Product, Integer>("cost"));
		supplierIDCol.setCellValueFactory(new PropertyValueFactory<Product, Integer>("supplierID"));
		shelflifeCol.setCellValueFactory(new PropertyValueFactory<Product, Integer>("shelfLife"));
		arrivedateCol.setCellValueFactory(new PropertyValueFactory<>("arrivedate"));

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
		product_table.setItems(dataList);

		// Make TableView editable
		product_table.setEditable(true);

		// Make each editable column editable
		nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		nameCol.setOnEditCommit(this::updateName);

		QuantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		QuantityCol.setOnEditCommit(this::updateQuantity);

		costCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		costCol.setOnEditCommit(this::updateCost);

		supplierIDCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		supplierIDCol.setOnEditCommit(this::updateSupplierID);

		shelflifeCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		shelflifeCol.setOnEditCommit(this::updateShelfLife);

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
		arrivedateCol.setCellFactory(TextFieldTableCell.forTableColumn(dateConverter));

		// Set the event handler for cell editing
		arrivedateCol.setOnEditCommit(this::updateArriveDate);

	}

	// Methods to handle updates for each column
	private void updateName(CellEditEvent<Product, String> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setName(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updateQuantity(CellEditEvent<Product, Integer> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setQuantity(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updateCost(CellEditEvent<Product, Integer> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setCost(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updateSupplierID(CellEditEvent<Product, Integer> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setSupplierID(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updateShelfLife(CellEditEvent<Product, Integer> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setShelfLife(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	private void updateArriveDate(CellEditEvent<Product, Date> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setArrivedate(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}

	// Method to update the database with the new values
	private void updateDatabase(Product updatedProduct) {
		try {
			connectDB();
			// Use an UPDATE SQL statement to update the product details in the database
			String SQL = "UPDATE Product SET name=?, quantity=?, cost=?, supplierID=?, shelfLife=?, arrivedate=? WHERE productID=?";
			try (PreparedStatement stmt = (PreparedStatement) con.prepareStatement(SQL)) {
				stmt.setString(1, updatedProduct.getName());
				stmt.setInt(2, updatedProduct.getQuantity());
				stmt.setInt(3, updatedProduct.getCost());
				stmt.setInt(4, updatedProduct.getSupplierID());
				stmt.setInt(5, updatedProduct.getShelfLife());
				stmt.setDate(6, updatedProduct.getArrivedate());
				stmt.setInt(7, updatedProduct.getProductID());
				stmt.executeUpdate();
			}
			con.close();
			System.out.println("Connection closed");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("exports")
	public void inventory(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("inventory.fxml"));

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

			String SQL = "SELECT productID, name, quantity, cost, supplierID, shelfLife, arrivedate FROM Product WHERE product.productID = ?";
			try (java.sql.PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setString(1, searchTerm);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						data.add(new Product(Integer.parseInt(rs.getString(1)), rs.getString(2),
								Integer.parseInt(rs.getString(3)), Integer.parseInt(rs.getString(4)),
								Integer.parseInt(rs.getString(5)), Integer.parseInt(rs.getString(6)), rs.getDate(7)));
					}
				}
			}

			System.out.println("Connection closed" + data.size());

			if (data.isEmpty()) {
				// No customer found, show alert
				showAlert(AlertType.INFORMATION, "Customer Not Found", "No Product found with the given ID.");
			} else {
				// Customer found, show details in a dialog
				showProductDetailsDialog(data.get(0));
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace(); // or log the exception using a logging framework
		}
	}

	private void showAlert(AlertType alertType, String title, String contentText) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

	private void showProductDetailsDialog(Product product) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Product Details");
		alert.setHeaderText(null);

		// Customize the dialog content with product details
		Label nameLabel = new Label("Name: " + product.getName());
		Label quantityLabel = new Label("Quantity: " + product.getQuantity());
		Label costLabel = new Label("cost: " + product.getCost());
		Label suppLabel = new Label("supplier ID: " + product.getSupplierID());
		Label shelflifeLabel = new Label("shelf life: " + product.getShelfLife());
		Label arrivedateLabel = new Label("Arrive date: " + product.getArrivedate());
		// Add more labels for other product details...

		alert.getDialogPane()
				.setContent(new VBox(nameLabel, quantityLabel, costLabel, suppLabel, shelflifeLabel, arrivedateLabel));

		// Set the size of the DialogPane
		alert.getDialogPane().setMinWidth(400);
		alert.getDialogPane().setMinHeight(200);

		alert.showAndWait();
	}

	@SuppressWarnings("exports")
	public void customer(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("customer.fxml"));

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
	public void Supplier(ActionEvent event) {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/supplierController.fxml"));
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
	public void orders(ActionEvent event) {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Orders.fxml"));
		try {
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root, 1536, 820);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("exports")
	public void orderline(ActionEvent event) {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/orderline.fxml"));
		try {
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root, 1536, 820);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("exports")
	public void handleSalesReportButton(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/ProductsReport.fxml"));
		try {
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root, 1536, 820);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("exports")
	public void handleOrdersReportButton(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/OrdersReport.fxml"));
		try {
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root, 1536, 820);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("exports")
	public void PurchaseOrder(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/PurchaseOrder.fxml"));
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
	public void PurchasePayment(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/purPay.fxml"));
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
	public void OrderPayment(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/orderPay.fxml"));
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
	public void purchaseline(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/PurchaseLine.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root, 1536.0, 820.0);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeConnection() {
		try {
			if (con != null && !con.isClosed()) {
				con.close();
				System.out.println("Connection closed");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// chart for count orders related to same supplier
	@FXML
	private void showSupplierOrderCountChart(ActionEvent event) {
		// Execute SQL query for supplierOrderCountChart
		ObservableList<SupplierOrderCount> data = executeSupplierOrderCountQuery();

		// Create a BarChart
		BarChart<String, Number> chart = createSupplierOrderCountChart();

		// Populate the chart
		displayChart("Supplier Order Count Chart", chart, "Supplier ID", "Order Count", data);
	}

	// execute SQL QUERY for count orders related to same supplier
	private ObservableList<SupplierOrderCount> executeSupplierOrderCountQuery() {
		ObservableList<SupplierOrderCount> supplierOrderCountList = FXCollections.observableArrayList();

		try {
			connectDB();

			String SQL = "SELECT supplierID, COUNT(purID) AS orderCount " + "FROM PurchaseOrder "
					+ "GROUP BY supplierID";

			try (PreparedStatement stmt = (PreparedStatement) con.prepareStatement(SQL)) {
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						int supplierID = rs.getInt("supplierID");
						long orderCount = rs.getLong("orderCount");

						// add the values to list
						supplierOrderCountList.add(new SupplierOrderCount(supplierID, orderCount));
					}
				}
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error",
					"Error retrieving supplier order count: " + e.getMessage());
		} finally {
			closeConnection();
		}

		return supplierOrderCountList;
	}

	@SuppressWarnings("unchecked")
	private void displayChart(String title, BarChart<String, Number> chart, String xAxisLabel, String yAxisLabel,
			ObservableList<SupplierOrderCount> data) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName(chart.getTitle());

		for (SupplierOrderCount item : data) {
			// add data of list to chart
			series.getData().add(new XYChart.Data<>(String.valueOf(item.getSupplierID()), item.getOrderCount()));
		}

		// Add series to the chart
		chart.getData().addAll(series);

		// Create a new scene to display the chart
		// size of scene
		Scene scene = new Scene(new Group(chart), 600, 400);
		Stage stage = new Stage();
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
	}

	private BarChart<String, Number> createSupplierOrderCountChart() {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();

		BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
		chart.setTitle("Supplier Order Count Chart");
		xAxis.setLabel("Supplier ID");
		yAxis.setLabel("Order Count");

		return chart;
	}

	@FXML
	private void showCustomerOrderCountChart(ActionEvent event) {
		// Execute SQL query for customerOrderCountChart
		ObservableList<CustomerOrderCount> data = executeCustomerOrderCountQuery();

		// Create a BarChart
		BarChart<String, Number> chart = createCustomerOrderCountChart();

		// Populate the chart
		displayChart2("Customer Order Count Chart", chart, "Customer ID", "Order Count", data);
	}

	@FXML
	private void showOrderCountByYearChart(ActionEvent event) {
		// Execute SQL query for orderCountByYearChart
		ObservableList<OrderCountByYear> data = executeOrderCountByYearQuery();

		// Create a BarChart
		BarChart<String, Number> chart = createOrderCountByYearChart();

		// Populate the chart
		displayChart2("Order Count by Year Chart", chart, "Order Year", "Order Count", data);
	}

	private BarChart<String, Number> createOrderCountByYearChart() {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();

		BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
		chart.setTitle("Order Count by Year Chart");
		xAxis.setLabel("Order Year");
		yAxis.setLabel("Order Count");

		return chart;
	}

	private ObservableList<CustomerOrderCount> executeCustomerOrderCountQuery() {
		ObservableList<CustomerOrderCount> customerOrderCountList = FXCollections.observableArrayList();

		try {
			connectDB();

			String SQL = "SELECT Customer.customerID, COUNT(Orders.orderID) AS orderCount "
					+ "FROM Customer JOIN Orders ON Customer.customerID = Orders.customerID "
					+ "GROUP BY Customer.customerID";

			try (PreparedStatement stmt = (PreparedStatement) con.prepareStatement(SQL)) {
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						int customerID = rs.getInt("customerID");
						long orderCount = rs.getLong("orderCount");

						customerOrderCountList.add(new CustomerOrderCount(customerID, orderCount));
					}
				}
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error",
					"Error retrieving customer order count: " + e.getMessage());
		} finally {
			closeConnection();
		}

		return customerOrderCountList;
	}

	private ObservableList<OrderCountByYear> executeOrderCountByYearQuery() {
		ObservableList<OrderCountByYear> orderCountByYearList = FXCollections.observableArrayList();

		try {
			connectDB();

			String SQL = "SELECT YEAR(orderDate) AS orderYear, COUNT(*) AS orderCount FROM Orders GROUP BY YEAR(orderDate) ORDER BY orderYear";
			try (PreparedStatement stmt = (PreparedStatement) con.prepareStatement(SQL)) {
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						int orderYear = rs.getInt("orderYear");
						long orderCount = rs.getLong("orderCount");

						orderCountByYearList.add(new OrderCountByYear(orderYear, orderCount));
					}
				}
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error",
					"Error retrieving order count by year: " + e.getMessage());
		} finally {
			closeConnection();
		}

		return orderCountByYearList;
	}

	private BarChart<String, Number> createCustomerOrderCountChart() {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();

		BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
		chart.setTitle("Customer Order Count Chart");
		xAxis.setLabel("Customer ID");
		yAxis.setLabel("Order Count");

		return chart;
	}

	@SuppressWarnings("unchecked")
	private void addDataToChart(BarChart<String, Number> chart, ObservableList<? extends Object> data) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName(chart.getTitle());

		for (Object item : data) {
			if (item instanceof CustomerOrderCount) {
				CustomerOrderCount customerOrderCount = (CustomerOrderCount) item;
				series.getData().add(new XYChart.Data<>(String.valueOf(customerOrderCount.getCustomerID()),
						customerOrderCount.getOrderCount()));
			} else if (item instanceof OrderCountByYear) {
				OrderCountByYear orderCountByYear = (OrderCountByYear) item;
				series.getData().add(new XYChart.Data<>(String.valueOf(orderCountByYear.getOrderYear()),
						orderCountByYear.getOrderCount()));
			}
		}

		// Add series to the chart
		chart.getData().addAll(series);
	}

	private void displayChart2(String title, BarChart<String, Number> chart, String xAxisLabel, String yAxisLabel,
			ObservableList<? extends Object> data) {
		// Your existing method implementation remains unchanged
		addDataToChart(chart, data);

		// Create a new scene to display the chart
		Scene scene = new Scene(new Group(chart), 600, 400);
		Stage stage = new Stage();
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
	}

}
