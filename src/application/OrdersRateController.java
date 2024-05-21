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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class OrdersRateController {

	private String dbURL;
	private String dbUsername = "root";
	private String dbPassword = "Root1234";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "project";
	private Connection con;

	@FXML
	private LineChart<String, Number> OrdersLineChart;
	 
	@FXML
	private Button backbtn;
	
	public void initialize() {
		
		 
		 List<OrdersRateData> OrdersRateDataList = getOrdersRateDataList();

	        int totalOrders = OrdersRateDataList.stream().mapToInt(OrdersRateData::getOrderCount).sum();
	        
	        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();

	        for (OrdersRateData orderRateData : OrdersRateDataList) {
	            double percentage = (double) orderRateData.getOrderCount() / totalOrders * 100;

	            lineSeries.getData().add(new XYChart.Data<>(String.valueOf(orderRateData.getMonth()), percentage));
	        }

	        OrdersLineChart.getData().add(lineSeries);
	}

	// Method to get sale rate data for the current month
	public List<OrdersRateData> getOrdersRateDataList() {
		List<OrdersRateData> OrdersRateDataList = new ArrayList<>();

		try {
			connectDB();

			// Retrieve data for all orders grouped by month
			String SQL = "SELECT MONTH(orderDate) AS month, COUNT(*) AS orderCount FROM orders "
					+ "GROUP BY MONTH(orderDate)";
			try (PreparedStatement stmt = con.prepareStatement(SQL)) {

				ResultSet rs = stmt.executeQuery();

				// Create SaleRateData objects
				while (rs.next()) {
					int month = rs.getInt("month");
					int orderCount = rs.getInt("orderCount");
					OrdersRateDataList.add(new OrdersRateData(month, orderCount));
				}

				rs.close();
			}

			con.close();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return OrdersRateDataList;
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
