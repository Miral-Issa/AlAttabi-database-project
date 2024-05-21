package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

public class OrderCountByYear {
    private final IntegerProperty orderYear;
    private final LongProperty orderCount;

    public OrderCountByYear(int orderYear, long orderCount) {
        this.orderYear = new SimpleIntegerProperty(orderYear);
        this.orderCount = new SimpleLongProperty(orderCount);
    }

    public int getOrderYear() {
        return orderYear.get();
    }

    @SuppressWarnings("exports")
	public IntegerProperty orderYearProperty() {
        return orderYear;
    }

    public long getOrderCount() {
        return orderCount.get();
    }

    @SuppressWarnings("exports")
	public LongProperty orderCountProperty() {
        return orderCount;
    }

    // Corrected method
    public long getOrderCountByYear() {
        return orderCount.get(); // Return the order count for the year
    }
}

