package application;

import java.sql.Date;

public class Orders {
	private int orderID;
	private int CustomerID;
	private Date orderdate;
	private Date requireddate;

	@SuppressWarnings("exports")
	public Orders(int orderID, int customerID, Date orderDate, Date requiredDate) {
		super();
		this.orderID = orderID;
		CustomerID = customerID;
		this.orderdate = orderDate;
		this.requireddate = requiredDate;
	}

	public int getOrderID() {
		return orderID;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	public int getCustomerID() {
		return CustomerID;
	}

	public void setCustomerID(int customerID) {
		CustomerID = customerID;
	}

	@SuppressWarnings("exports")
	public Date getOrderdate() {
		return orderdate;
	}

	@SuppressWarnings("exports")
	public void setOrderdate(Date orderdate) {
		this.orderdate = orderdate;
	}

	@SuppressWarnings("exports")
	public Date getRequireddate() {
		return requireddate;
	}

	@SuppressWarnings("exports")
	public void setRequireddate(Date requireddate) {
		this.requireddate = requireddate;
	}
}
