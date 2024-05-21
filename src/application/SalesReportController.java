package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SalesReportController {

	private String dbURL;
	private String dbUsername = "root";
	private String dbPassword = "Root1234";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "project";
	private Connection con;

	@FXML
	private BarChart<String, Number> salesBarChart;
	

	@FXML
	private Button backbtn;

	public void initialize() {

	    List<SaleRateData> saleRateDataList = getSaleRateDataList();

	    int totalOrderedQuantity = saleRateDataList.stream()
	            .mapToInt(SaleRateData::getTotalQuantity)
	            .sum();

	    for (SaleRateData saleRateData : saleRateDataList) {
	        double percentage = (double) saleRateData.getTotalQuantity() / totalOrderedQuantity * 100;

	        XYChart.Series<String, Number> series = new XYChart.Series<>();
	        series.getData().add(new XYChart.Data<>(String.valueOf(saleRateData.getProductID()), percentage));
	        salesBarChart.getData().add(series);
	    }

	    // Set labels for axes
	    salesBarChart.getXAxis().setLabel("Product ID");
	    salesBarChart.getYAxis().setLabel("Percentage of Ordered Quantity");
	}


	// Method to get sale rate data for the current month
	public List<SaleRateData> getSaleRateDataList() {

		List<SaleRateData> saleRateDataList = new ArrayList<>();

		try {
			connectDB();

			// Retrieve data for the current month
			String SQL = "SELECT productID, SUM(quantity) AS total_quantity FROM orderline "
					+ "JOIN orders ON orderline.orderID = orders.orderID " 
					+ "GROUP BY productID";
			try (PreparedStatement stmt = con.prepareStatement(SQL)) {
				
				ResultSet rs = stmt.executeQuery();

				// Calculate sale rates and create SaleRateData objects
				while (rs.next()) {
					int productID = rs.getInt("productID");
					int totalQuantity = rs.getInt("total_quantity");
																					// rate
					saleRateDataList.add(new SaleRateData(productID, totalQuantity));
				}

				rs.close();
			}

			con.close();
			System.out.println("Connection closed" + saleRateDataList.size());
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return saleRateDataList;
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

}
