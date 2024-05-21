package application;

public class CustomerOrderCount {
    private int customerID;
    private long orderCount;

    public CustomerOrderCount(int customerID, long orderCount) {
        this.customerID = customerID;
        this.orderCount = orderCount;
    }

    public int getCustomerID() {
        return customerID;
    }

    public long getOrderCount() {
        return orderCount;
    }
}
