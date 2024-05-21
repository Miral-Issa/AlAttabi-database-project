package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;


import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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

import javafx.scene.control.Button;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class showOrderChecksController {

	@FXML
	private TableView<showOrderChecks> Checks_table;

	@FXML
	private TableColumn<showOrderChecks, Integer> paymentID;
	@FXML
	private TableColumn<showOrderChecks, Integer> amountCol;
	@FXML
	private TableColumn<showOrderChecks, Date> payDateCol;
	@FXML
	private TableColumn<showOrderChecks, Date> dueDateCol;
	@FXML
	private TableColumn<showOrderChecks, Integer> cIDCol;
	@FXML
	private TableColumn<showOrderChecks, String> cnameCol;
	@FXML
	private TableColumn<showOrderChecks, Integer> phoneNumCol;

	@FXML
	private Button backbtn;

	private Parent root = null;

	private String dbURL;
	private String dbUsername = "root";
	private String dbPassword = "Root1234";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "project";
	private Connection con;

	private ArrayList<showOrderChecks> data;

	private ObservableList<showOrderChecks> dataList;

	public void initialize() {

		// Initialize TableView and Columns
		paymentID.setCellValueFactory(new PropertyValueFactory<showOrderChecks, Integer>("payID"));
		amountCol.setCellValueFactory(new PropertyValueFactory<showOrderChecks, Integer>("amount"));
		payDateCol.setCellValueFactory(new PropertyValueFactory<showOrderChecks, Date>("payDate"));
		dueDateCol.setCellValueFactory(new PropertyValueFactory<showOrderChecks, Date>("dueDate"));
		cIDCol.setCellValueFactory(new PropertyValueFactory<showOrderChecks, Integer>("cID"));
		cnameCol.setCellValueFactory(new PropertyValueFactory<showOrderChecks, String>("cname"));
		phoneNumCol.setCellValueFactory(new PropertyValueFactory<showOrderChecks, Integer>("phoneNum"));
		

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
		Checks_table.setItems(dataList);

		// Make TableView editable
		Checks_table.setEditable(false);
		
		//paymentID.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		amountCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		
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

				// Set cell factory with the custom StringConverter
				dueDateCol.setCellFactory(TextFieldTableCell.forTableColumn(dateConverter));

				
				cIDCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
				
				cnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
				
				phoneNumCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

	}

	private void getData() throws SQLException, ClassNotFoundException {
		// Initialize data list
		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		String SQL;

		connectDB();
		System.out.println("Connection established");

		SQL = "select op.paymentID,op.payed_amount,op.payment_date,op.due_date,C.CustomerID, C.Cname,C.phoneNumber from order_payment op,customer C,orders where C.CustomerID = orders.CustomerID and orders.orderID = op.orderID and op.payment_type=\"check\" order by op.due_date;\r\n"
				+ "";
		java.sql.Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL);

		while (rs.next())
			data.add(new showOrderChecks(Integer.parseInt(rs.getString(1)),
					Integer.parseInt(rs.getString(2)),
					rs.getDate(3),
					rs.getDate(4),
					Integer.parseInt(rs.getString(5)),
					rs.getString(6),
					Integer.parseInt(rs.getString(7))));
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
