package application;

import java.sql.Date;

public class PurchaseOrder {
    private int purID;
    private int totalCost;
    private Date purDate;
    private int supplierID;

    @SuppressWarnings("exports")
	public PurchaseOrder(int purID, int totalCost, Date purDate, int supplierID) {
        this.purID = purID;
        this.totalCost = totalCost;
        this.purDate = purDate;
        this.supplierID = supplierID;
    }

    public int getPurID() {
        return purID;
    }

    public void setPurID(int purID) {
        this.purID = purID;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    @SuppressWarnings("exports")
	public Date getPurDate() {
        return purDate;
    }

    @SuppressWarnings("exports")
	public void setPurDate(Date purDate) {
        this.purDate = purDate;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }
}
