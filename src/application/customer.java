package application;

public class customer {
	private int customerID;
	private String cname;
	private String address;
	private int phoneNumber;
	private String ctype;

	// Constructors, getters, and setters

	public customer(int customerID, String cname, String address, int phoneNumber, String ctype) {
		this.customerID = customerID;
		this.cname = cname;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.ctype = ctype;
	}

	public int getCustomerID() {
		return customerID;
	}

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(int phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
}
