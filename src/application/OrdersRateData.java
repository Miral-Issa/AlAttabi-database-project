package application;

public class OrdersRateData {
    private int month;
    private int orderCount;

    public OrdersRateData(int month, int orderCount) {
        this.month = month;
        this.orderCount = orderCount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }
}
