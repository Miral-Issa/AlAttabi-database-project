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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class PurchaseOrderController implements Initializable{

    @FXML
    private TableView<PurchaseOrder> purchaseOrderTable;

    @FXML
    private TableColumn<PurchaseOrder, Integer> purIDCol;

    @FXML
    private TableColumn<PurchaseOrder, Integer> totalCostCol;

    @FXML
    private TableColumn<PurchaseOrder, Date> purDateCol;

    @FXML
    private TableColumn<PurchaseOrder, Integer> supplierIDCol;
    
    @FXML
    private BarChart<String, Number> supplierOrderCountChart;

    @FXML
    private CategoryAxis supplierOrderCountXAxis;

    @FXML
    private NumberAxis supplierOrderCountYAxis;

    @FXML
    private TextField purIDField;
    
    @FXML
    private TextField totalCostField;

    @FXML
    private TextField purDateField;

    @FXML
    private TextField supplierIDField;
    

    @FXML
    private TextField supplierIdInput;

    @FXML
    private Button getCostButton;

    @FXML
    private Button checks;
    
    @FXML
    private TextField totalPerSupplierField;
    
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
	
	
	//date format
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	//connection information
    private String dbURL;
    private String dbUsername = "root";
    private String dbPassword = "Root1234";
    private String URL = "127.0.0.1";
    private String port = "3306";
    private String dbName = "project";
    private Connection con;

    //instance data for purshaseOrder class
	private ArrayList<PurchaseOrder> data;
    //update on javafx
	private ObservableList<PurchaseOrder> dataList;
	
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	
    	
    	// Initialize TableView and Columns
    	 purIDCol.setCellValueFactory(new PropertyValueFactory<>("purID"));
    	 totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
    	 purDateCol.setCellValueFactory(new PropertyValueFactory<>("purDate"));
    	 supplierIDCol.setCellValueFactory(new PropertyValueFactory<>("supplierID"));



 		// Initialize data list
 		data = new ArrayList<>();
 		
 	// Retrieve data from the database
		try {
			getData();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// Populate TableView with data
		purchaseOrderTable.setItems(dataList);

		// Make TableView editable
		purchaseOrderTable.setEditable(true);


		// Create a custom StringConverter for java.sql.Date
		StringConverter<java.sql.Date> dateConverter = new StringConverter<java.sql.Date>() {
			@Override
			public String toString(java.sql.Date object) {
				return object == null ? "" : dateFormat.format(object);
			}

			//convert string back to java.sql.Date
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
		purDateCol.setCellFactory(TextFieldTableCell.forTableColumn(dateConverter));

		// Set the event handler for cell editing
		purDateCol.setOnEditCommit(arg0 -> {
			try {
				updatePurDate(arg0);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		});

	    	    
	    // Set cell factory for totalCostCol
	    totalCostCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

	    // Set the event handler for cell editing
	    totalCostCol.setOnEditCommit(arg0 -> {
	    	try {
	    		updateTotalCost(arg0);
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    			}
	    	});

	    // Set cell factory for supplierIDCol
	    supplierIDCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

	    // Set event handler for supplierIDCol edit commit
	    supplierIDCol.setOnEditCommit(arg0 -> {
	        try {
	            updateSupplierID(arg0);
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	    });

        
    }
    
	//function update suuplierID
	private void updateSupplierID(CellEditEvent<PurchaseOrder, Integer> event) throws ParseException {
		PurchaseOrder updatedOrder = event.getTableView().getItems().get(event.getTablePosition().getRow());
		try {
			int newSupplierID = event.getNewValue();

			// Check if the newSupplierID exists in the supplier table
			if (!isSupplierIDExists(newSupplierID)) {
				showAlert(Alert.AlertType.ERROR, "Supplier ID Error",
						"Supplier ID does not exist in the Supplier table.");
				purchaseOrderTable.refresh();//clear the value
				
				return;
			}

			//supplierID set to the new value
			updatedOrder.setSupplierID(newSupplierID);
			updateDatabase(updatedOrder);//update database

		} catch (NumberFormatException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid numeric value for Supplier ID.");
		}
	}
	// Method to check if a customerID exists in the Customer table
	private boolean isSupplierIDExists(int SupplierID) {
		try {
			connectDB();
			String SQL = "SELECT * FROM supplier WHERE supplierID=?";
			//Execute SQL
			try (PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setInt(1, SupplierID);
				//ResultSet after execute query
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
	
    //update date
	private void updatePurDate(CellEditEvent<PurchaseOrder, Date> event) throws ParseException {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setPurDate(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}
    
	//update total Cost
	private void updateTotalCost(CellEditEvent<PurchaseOrder, Integer> event) {
		event.getTableView().getItems().get(event.getTablePosition().getRow()).setTotalCost(event.getNewValue());
		updateDatabase(event.getTableView().getItems().get(event.getTablePosition().getRow()));
	}
	
	//function to update database
	private void updateDatabase(PurchaseOrder updatePurchaseOrder) {
		try {
			connectDB();
			// Use an UPDATE SQL statement to update the purchase order details in the database
			String SQL = "UPDATE PurchaseOrder SET totalCost=?, purDate=?, supplierID=?  WHERE purID=?";
			try (PreparedStatement stmt = con.prepareStatement(SQL)) {
				stmt.setInt(1, updatePurchaseOrder.getTotalCost());
				stmt.setDate(2, updatePurchaseOrder.getPurDate());
				stmt.setInt(3, updatePurchaseOrder.getSupplierID());
				stmt.setInt(4, updatePurchaseOrder.getPurID());
				stmt.executeUpdate();
			}
			con.close();
			System.out.println("Connection closed");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//function to get data
	private void getData() throws SQLException, ClassNotFoundException {
	    // Initialize data list
	    data = new ArrayList<>();
	    dataList = FXCollections.observableArrayList(data);

	    String SQL;

	    connectDB();
	    System.out.println("Connection established");

	    try {
	        SQL = "select purID, totalCost, purDate, supplierID from PurchaseOrder";
	        java.sql.Statement stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery(SQL);

	        //new instance from purchase order class added to array list data
	        while (rs.next()) {
	            data.add(new PurchaseOrder(Integer.parseInt(rs.getString(1)), Integer.parseInt(rs.getString(2)), rs.getDate(3),
	                    Integer.parseInt(rs.getString(4))
	            ));
	        }

	        rs.close();
	        stmt.close();
	    } catch (SQLException e) {
	        // Handle SQLException or rethrow if necessary
	        throw e;
	    } finally {
	        con.close();
	        System.out.println("Connection closed");
	    }

	    // Now, update the dataList => FXML
	    dataList.setAll(data);
	}


	//function for connection
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
	
	//back to main page
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
	
	//function to add data 
	@FXML
	private void handleAddOrder() {
	    try {
	        int purID = Integer.parseInt(purIDField.getText());
	        int totalCost = Integer.parseInt(totalCostField.getText());
	        int supplierID = Integer.parseInt(supplierIDField.getText());

	        String dateString = purDateField.getText();

	        // Validate date format
	        if (!dateString.matches("\\d{4}-\\d{2}-\\d{2}")) {
	            showAlert(Alert.AlertType.ERROR, "Date Format Error", "Please enter a valid date in the format 'yyyy-MM-dd'.");
	            return;
	        }

	        try {
	            java.util.Date purDate = dateFormat.parse(dateString);
	            PurchaseOrder newOrder = new PurchaseOrder(purID, totalCost, new java.sql.Date(purDate.getTime()), supplierID);

	            // Check if the supplierID exists in the Supplier table
	            if (!isSupplierIDExists(supplierID)) {
	                showAlert(Alert.AlertType.ERROR, "Supplier ID Error", "Supplier ID does not exist in the Supplier table.");
	                return;
	            }

	            // Check if the purchase order ID already exists
	            if (!isPurIDExists(newOrder.getPurID())) {
	                // Add data to table
	                dataList.add(newOrder);
	                insertData(newOrder);

	                // Clear the text fields after adding
	                purIDField.clear();
	                totalCostField.clear();
	                purDateField.clear();
	                supplierIDField.clear();
	            } else {
	                showAlert(Alert.AlertType.WARNING, "Duplicate Purchase Order ID", "A purchase order with the same ID already exists.");
	            }
	        } catch (ParseException e) {
	            showAlert(Alert.AlertType.ERROR, "Date Parsing Error", "Error parsing the date. Please enter a valid date.");
	        }

	    } catch (NumberFormatException e) {
	        showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter valid numeric values.");
	    }
	}

	//function to check the purID duplicated or not
	private boolean isPurIDExists(int purID) {
	    for (PurchaseOrder order : dataList) {
	        if (order.getPurID() == purID) {
	            return true;
	        }
	    }
	    return false;
	}
	
    private void insertData(PurchaseOrder purchaseOrder) {
        try {
            connectDB();
            String SQL = "Insert into PurchaseOrder (purID, totalCost, purDate, supplierID) values (?, ?, ?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(SQL)) {
                stmt.setInt(1, purchaseOrder.getPurID());
                stmt.setInt(2, purchaseOrder.getTotalCost());
                stmt.setDate(3, purchaseOrder.getPurDate());
                stmt.setInt(4, purchaseOrder.getSupplierID());
                stmt.executeUpdate();
            }
            con.close();
            System.out.println("Connection closed");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
	
    @FXML
    private void handleDeleteButton(ActionEvent event) {
    	//select row to delete
        ObservableList<PurchaseOrder> selectedRows = purchaseOrderTable.getSelectionModel().getSelectedItems();
        ArrayList<PurchaseOrder> rows = new ArrayList<>(selectedRows);
        //delete specific ones
        rows.forEach(row -> {
            purchaseOrderTable.getItems().remove(row);
            deleteRow(row);
            purchaseOrderTable.refresh();
        });
    }
    //delete row function
    private void deleteRow(PurchaseOrder purchaseOrder) {
        try {
            System.out.println("delete from PurchaseOrder where purID= " + purchaseOrder.getPurID() + ";");
            connectDB();
            //SQL statement
            ExecuteStatement("delete from PurchaseOrder where purID=" + purchaseOrder.getPurID() + ";");
            con.close();
            System.out.println("Connection closed");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    //refresh table
    @FXML
    private void handleRefreshButton(ActionEvent event) {
        purchaseOrderTable.refresh();
    }

    //search according to primary key
    @FXML
    private void searchPurchaseOrder(ActionEvent event) {
        try {
            data = new ArrayList<>();
            dataList = FXCollections.observableArrayList(data);

            //get search value
            String searchTerm = Search.getText().trim();

            connectDB();
            System.out.println("Connection established");

            String SQL = "SELECT purID, totalCost, purDate, supplierID FROM PurchaseOrder WHERE purID = ?";

            try (java.sql.PreparedStatement stmt = con.prepareStatement(SQL)) {
                stmt.setString(1, searchTerm);//purID = searchTerm (search value)
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                    	//same purID add to array list
                        data.add(new PurchaseOrder(Integer.parseInt(rs.getString(1)),
                                Integer.parseInt(rs.getString(2)), Date.valueOf(rs.getString(3)),
                                Integer.parseInt(rs.getString(4))));
                    }
                }
            }

            System.out.println("Connection closed" + data.size());

            //if nothing in array list so not found
            if (data.isEmpty()) {
                // No purchase order found, show alert
                showAlert(Alert.AlertType.INFORMATION, "Purchase Order Not Found",
                        "No purchase order found with the given ID.");
            } else {
                // Purchase order found, show details in a dialog
                showPurchaseOrderDetailsDialog(data);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace(); // or log the exception using a logging framework
        }
    }
    
    //show purchase order details
    private void showPurchaseOrderDetailsDialog(List<PurchaseOrder> purchaseOrders) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);//information message
        alert.setTitle("Purchase Order Details");
        alert.setHeaderText(null);

        VBox vbox = new VBox();
        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            Label purIDLabel = new Label("Purchase Order ID: " + purchaseOrder.getPurID());
            Label totalCostLabel = new Label("Total Cost: " + purchaseOrder.getTotalCost());
            Label purDateLabel = new Label("Purchase Date: " + purchaseOrder.getPurDate());
            Label supplierIDLabel = new Label("Supplier ID: " + purchaseOrder.getSupplierID());

            vbox.getChildren().addAll(purIDLabel, totalCostLabel, purDateLabel, supplierIDLabel);

            // Add a separator between each purchase order
            vbox.getChildren().add(new Separator());
        }

        alert.getDialogPane().setContent(vbox);

        // Set the size of the DialogPane
        alert.getDialogPane().setMinWidth(400);
        alert.getDialogPane().setMinHeight(200);

        alert.showAndWait();
    }

    //show message to user
    private void showAlert(Alert.AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    

    //execute SQL 
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
	private void getSupplierTotalCost() throws SQLException {
	    try {
	        int supplierID = Integer.parseInt(supplierIdInput.getText());
	        int totalCost = fetchTotalCostForSupplier(supplierID);
	        totalPerSupplierField.setText(String.valueOf(totalCost));
	    } catch (NumberFormatException e) {
	        showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid numeric value for Supplier ID.");
	    }
	}
	
    private int fetchTotalCostForSupplier(int supplierID) throws SQLException {
        int totalCost = 0;
        try {
            connectDB();
            String sql = "SELECT SUM(totalCost) AS totalCost FROM PurchaseOrder WHERE supplierID = ?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setInt(1, supplierID);
                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet.next()) {
                        totalCost = resultSet.getInt("totalCost");
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            con.close();
        }
        return totalCost;
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
    
    




