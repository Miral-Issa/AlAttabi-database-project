package application;

import java.io.IOException;
import java.sql.Connection;

import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class inventory {

	@FXML
	private TableView<Product> ExpireTableView;

	@FXML
	private TableColumn<Product, Integer> ProductIdColumn;

	@FXML
	private TableColumn<Product, String> NameColumn;

	@FXML
	private TableColumn<Product, Date> Expiredate;

	@FXML
	private TableColumn<Product, Integer> q1;

	@FXML
	private TableView<Product> outOfStockSoonTableView;

	@FXML
	private TableColumn<Product, Integer> outOfStockSoonProductIdColumn;

	@FXML
	private TableColumn<Product, String> outOfStockSoonNameColumn;

	@FXML
	private TableColumn<Product, Integer> outOfStockSoonSupColumn;

	@FXML
	private TableColumn<Product, Integer> q2;

	@FXML
	private TableView<Product> outOfStockTableView;

	@FXML
	private TableColumn<Product, Integer> outOfStockProductIdColumn;

	@FXML
	private TableColumn<Product, Integer> outOfStockSupColumn;

	@FXML
	private TableColumn<Product, String> outOfStockNameColumn;

	@FXML
	private TableColumn<Product, Integer> q3;

	@FXML
	private Button backbtn;

	private Parent root;

	private String dbURL;
	private String dbUsername = "root";
	private String dbPassword = "maya";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "project";
	private Connection con;

	private ArrayList<Product> data;

	private ObservableList<Product> dataList;

	public void initialize() {
		// Initialize your tables and columns
		initializeTables_expire(ExpireTableView, ProductIdColumn, NameColumn, q1, Expiredate);
		initializeTables_soon(outOfStockSoonTableView, outOfStockSoonProductIdColumn, outOfStockSoonNameColumn,
				outOfStockSoonSupColumn, q2);
		initializeTables_outofstock(outOfStockTableView, outOfStockProductIdColumn, outOfStockNameColumn,
				outOfStockSupColumn, q3);
	}

	private void initializeTables_expire(TableView<Product> tableView, TableColumn<Product, Integer> idColumn,
			TableColumn<Product, String> nameColumn, TableColumn<Product, Integer> q,
			TableColumn<Product, Date> expiredate) {
		// Initialize TableView and Columns
		idColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("productID"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
		expiredate.setCellValueFactory(new PropertyValueFactory<>("arrivedate"));
		q.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantity"));

		// Initialize data list
		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		// Retrieve data from the database
		try {
			getData("select productID, name ,quantity, cost, supplierID, shelfLife, arrivedate from Product WHERE DATE_ADD(arrivedate, INTERVAL shelfLife DAY) < CURDATE();");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Populate TableView with data
		tableView.setItems(dataList);

	}

	private void initializeTables_soon(TableView<Product> tableView, TableColumn<Product, Integer> idColumn,
	    TableColumn<Product, String> nameColumn, TableColumn<Product, Integer> SupColumn,
	    TableColumn<Product, Integer> q) {
		// Initialize TableView and Columns
		idColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("productID"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
		SupColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("supplierID"));
		q.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantity"));

		// Initialize data list
		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		// Retrieve data from the database
		try {
			getData("select productID, name ,quantity, cost, supplierID, shelfLife, arrivedate  from Product where Product.quantity < 50");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Populate TableView with data
		tableView.setItems(dataList);

	}

	private void initializeTables_outofstock(TableView<Product> tableView, TableColumn<Product, Integer> idColumn,
			TableColumn<Product, String> nameColumn, TableColumn<Product, Integer> SupColumn,
			TableColumn<Product, Integer> q) {
		// Initialize TableView and Columns
		idColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("productID"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
		SupColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("supplierID"));
		q.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantity"));

		// Initialize data list
		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		// Retrieve data from the database
		try {
			getData("select productID, name ,quantity, cost, supplierID, shelfLife, arrivedate from Product where Product.quantity = 0");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Populate TableView with data
		tableView.setItems(dataList);

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

}
