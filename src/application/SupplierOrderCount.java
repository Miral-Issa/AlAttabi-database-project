package application;

public class SupplierOrderCount {
    private int supplierID;
    private long orderCount;

    public SupplierOrderCount(int supplierID, long orderCount) {
        this.supplierID = supplierID;
        this.orderCount = orderCount;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public long getOrderCount() {
        return orderCount;
    }
}
