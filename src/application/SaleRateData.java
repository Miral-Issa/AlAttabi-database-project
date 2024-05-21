package application;


public class SaleRateData {

    private int productID;
    private int totalQuantity;

	public SaleRateData(int productID, int totalQuantity) {
		super();
		this.productID = productID;
		this.totalQuantity = totalQuantity;
	}

	public int getProductID() {
		return productID;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}


    
}

