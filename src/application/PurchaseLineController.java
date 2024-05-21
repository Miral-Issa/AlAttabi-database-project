package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.sql.PreparedStatement;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class PurchaseLineController {

	@FXML
	private TextField purIDField;

	@FXML
	private TextField productIDField;

	@FXML
	private TextField quantityField;

	// @FXML
	// private TextField costField;

	@FXML
	private TableView<PurchaseLine> purLine_table; // Change the variable name

	@FXML
	private TableColumn<PurchaseLine, Integer> purIDCol;

	@FXML
	private TableColumn<PurchaseLine, String> productIDCol;

	@FXML
	private TableColumn<PurchaseLine, Integer> quantityCol;

	@FXML
	private TableColumn<PurchaseLine, String> costCol;

	@FXML
	private Button addBtn;

	@FXML
	private Button deleteBtn;

	@FXML
	private Button refreshBtn;

	@FXML
	private Button backBtn;

	@FXML
	private TextField Search;

	private String dbURL;
	private String dbUsername = "root";
	private String dbPassword = "Root1234";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "project";
	private Connection con;

	private ObservableList<PurchaseLine> dataList;
	private ArrayList<PurchaseLine> data;
	// private Parent root;

	public void initialize() {
		if (purLine_table != null) {
			purIDCol.setCellValueFactory(new PropertyValueFactory<>("purID"));
			productIDCol.setCellValueFactory(new PropertyValueFactory<>("productID"));
			quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
			costCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
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
			purLine_table.setItems(dataList);

			// Make TableView editable
			purLine_table.setEditable(true);
		}
	}

	private void getData() throws SQLException, ClassNotFoundException {
		// Initialize data list
		data = new ArrayList<>();
		dataList = FXCollections.observableArrayList(data);

		String SQL;

		connectDB(); // <-- Establishes the connection

		System.out.println("Connection established");

		SQL = "select purID, productID, quantity, cost from purchase_line";
		java.sql.Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL);

		while (rs.next())
			data.add(new PurchaseLine(Integer.parseInt(rs.getString(1)), Integer.parseInt(rs.getString(2)),
					Integer.parseInt(rs.getString(3)), Integer.parseInt(rs.getString(4))));
		rs.close();
		stmt.close();

		con.close();
		System.out.println("Connection closed" + data.size());

		// Now, update the dataList
		dataList.setAll(data);
	}

	@SuppressWarnings("exports")
	@FXML
	public void search(ActionEvent event) {
		try {
			data = new ArrayList<>();
			dataList = FXCollections.observableArrayList(data);

			String searchTerm = Search.getText().trim();

			connectDB();
			System.out.println("Connection established");

			String SQL = "SELECT purID, productID, quantity, cost FROM purchase_line WHERE purID = ?";

			try (java.sql.PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setString(1, searchTerm);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						data.add(new PurchaseLine(Integer.parseInt(rs.getString(1)), Integer.parseInt(rs.getString(2)),
								Integer.parseInt(rs.getString(3)), Integer.parseInt(rs.getString(4))));
					}
				}
			}

			System.out.println("Connection closed" + data.size());

			if (data.isEmpty()) {
				// No supplier found, show alert
				showAlert(AlertType.INFORMATION, "Purchase Line Not Found",
						"No Purchase Line found with the given ID.");
			} else {
				// Supplier found, show details in a dialog
				showDetailsDialog(data);
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace(); // or log the exception using a logging framework
		}
	}

	private void showDetailsDialog(List<PurchaseLine> purlinelist) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Purchase Line Details");
		alert.setHeaderText(null);

		VBox vbox = new VBox();

		for (PurchaseLine purline : purlinelist) {
			Label puridLabel = new Label("Purchase Line ID: " + purline.getPurID());
			Label pidLabel = new Label("Product ID: " + purline.getProductID());
			Label qLabel = new Label("quantity: " + purline.getQuantity());
			Label costLabel = new Label("cost: " + purline.getCost());
			
			vbox.getChildren().addAll(puridLabel, pidLabel, qLabel, costLabel);

			// Add a separator between each order
			vbox.getChildren().add(new Separator());
		}
		
		alert.getDialogPane().setContent(vbox);

		// Set the size of the DialogPane
		alert.getDialogPane().setMinWidth(400);
		alert.getDialogPane().setMinHeight(200);

		alert.showAndWait();
	}

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
	public void handleBackButton(ActionEvent event) {
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
	public void handleAddButton(ActionEvent event) throws ClassNotFoundException, SQLException {

		connectDB();
		PurchaseLine rc;

		// Check if the product with the same ID already exists
		int purid = Integer.valueOf(purIDField.getText());
		int prodid = Integer.valueOf(productIDField.getText());

		// Check if the record with the same (orderID, productID) already exists
		if (ispurLineExists(purid, prodid)) {
			showAlert(AlertType.ERROR, "Duplicate Record",
					"Record with the same (PurchaseID, productID) already exists.");
			return; // Do not proceed
		}

		if (ispurExists(purIDField.getText()) && isproductExists(productIDField.getText())) {

			int quantity = getAvailableQuantityFromDatabase(prodid);
			int unitpriceValue = (int) getUnitPriceFromDatabase(prodid);

			if (quantity < Integer.valueOf(quantityField.getText())) {
				showAlert(AlertType.ERROR, "Insufficient Quantity",
						"Not enough quantity available for the selected product or Product does not exist.");
				return; // Do not proceed with adding the product
			} else {

				updateProductQuantity(prodid, quantity - Integer.valueOf(quantityField.getText()));

				rc = new PurchaseLine(Integer.valueOf(purIDField.getText()), Integer.valueOf(productIDField.getText()),
						Integer.valueOf(quantityField.getText()),
						unitpriceValue * Integer.valueOf(quantityField.getText()));

				dataList.add(rc);
				insertData(rc);

				purIDField.clear();
				productIDField.clear();
				quantityField.clear();
			}

		}

		else {
			showAlert(AlertType.ERROR, "not found ", " (PurchaseID, productID) are not found.");
			return; // Do not proceed

		}

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

	private boolean ispurLineExists(int purID, int productID) {
		for (PurchaseLine existingpurLine : dataList) {
			if (existingpurLine.getPurID() == purID && existingpurLine.getProductID() == productID) {
				return true; // Record already exists
			}
		}
		return false; // Record does not exist
	}

	private boolean ispurExists(String purid) throws SQLException {

		String checkpurSQL = "SELECT 1 FROM PurchaseOrder WHERE purID = ? LIMIT 1;";

		try (PreparedStatement checkStmt = con.prepareStatement(checkpurSQL)) {
			checkStmt.setString(1, purid);

			try (ResultSet checkResult = checkStmt.executeQuery()) {
				return checkResult.next();
			}
		}

	}

	private boolean isproductExists(String proid) throws SQLException {

		String checkproductSQL = "SELECT 1 FROM product WHERE productID = ? LIMIT 1;";

		try (PreparedStatement checkStmt = con.prepareStatement(checkproductSQL)) {
			checkStmt.setString(1, proid);

			try (ResultSet checkResult = checkStmt.executeQuery()) {
				return checkResult.next();
			}
		}

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

	private void insertData(PurchaseLine pline) {
		try {
			// Connect to the database and get the connection
			con = connectDB();

			// Prepare the SQL statement
			String SQL = "INSERT INTO purchase_line (purID, productID, quantity, cost) VALUES (?, ?, ?, ?)";

			// Use a PreparedStatement to avoid SQL injection
			try (PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setInt(1, pline.getPurID());
				stmt.setInt(2, pline.getProductID());
				stmt.setInt(3, pline.getQuantity());
				stmt.setInt(4, pline.getCost());

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

	@SuppressWarnings("exports")
	@FXML
	public void handleDeleteButton(ActionEvent event) {
		ObservableList<PurchaseLine> selectedRows = purLine_table.getSelectionModel().getSelectedItems();
		ArrayList<PurchaseLine> rows = new ArrayList<>(selectedRows);
		rows.forEach(row -> {
			purLine_table.getItems().remove(row);
			deleteRow(row);
			purLine_table.refresh();
		});
	}

	private void deleteRow(PurchaseLine row) {
		try {
			System.out.println("DELETE FROM purchase_line WHERE purID=" + row.getPurID() + ";");
			connectDB();
			ExecuteStatement("DELETE FROM purchase_line WHERE purID= " + row.getPurID() + ";");
			con.close();
			System.out.println("Connection closed");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("exports")
	@FXML
	public void handleRefreshButton(ActionEvent event) {
		purLine_table.refresh();
	}

	private void showAlert(AlertType alertType, String title, String contentText) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

}
