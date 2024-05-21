package application;

import java.sql.Date;

public class Product {
	private int productID;
	private String name;
	private int quantity;
	private int cost;
	private int supplierID;
	private int shelfLife;
	private Date arrivedate;

	@SuppressWarnings("exports")
	// Constructor
	public Product(int productID, String name, int quantity, int cost, int supplierID, int shelfLife, Date arrivedate) {
		this.productID = productID;
		this.name = name;
		this.quantity = quantity;
		this.cost = cost;
		this.supplierID = supplierID;
		this.shelfLife = shelfLife;
		this.arrivedate = arrivedate;

	}

	// Getters and Setters
	public int getProductID() {
		return productID;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getSupplierID() {
		return supplierID;
	}

	public void setSupplierID(int supplierID) {
		this.supplierID = supplierID;
	}

	public int getShelfLife() {
		return shelfLife;
	}

	public void setShelfLife(int shelfLife) {
		this.shelfLife = shelfLife;
	}

	@SuppressWarnings("exports")
	public Date getArrivedate() {
		return arrivedate;
	}

	public void setArrivedate(@SuppressWarnings("exports") Date arrivedate) {
		this.arrivedate = arrivedate;
	}

}
