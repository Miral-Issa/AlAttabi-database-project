package application;

public class supplier {
	private int supplierID;
	private String Sname;
	private int phoneNumber;
	private String Saddress;

	public supplier(int supplierID, String sname, int phoneNumber, String saddress) {
		super();
		this.supplierID = supplierID;
		Sname = sname;
		this.phoneNumber = phoneNumber;
		Saddress = saddress;
	}

	public int getSupplierID() {
		return supplierID;
	}

	public void setSupplierID(int supplierID) {
		this.supplierID = supplierID;
	}

	public String getSname() {
		return Sname;
	}

	public void setSname(String sname) {
		Sname = sname;
	}

	public int getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(int phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getSaddress() {
		return Saddress;
	}

	public void setSaddress(String saddress) {
		Saddress = saddress;
	}

}
