package application;

public class PurchaseLine {
    private int purID;
    private int productID;
    private int quantity;
    private int cost;

    public PurchaseLine(int purID, int productID, int quantity, int cost) {
        this.purID = purID;
        this.productID = productID;
        this.quantity = quantity;
        this.cost = cost;
    }

    public int getPurID() {
        return purID;
    }

    public void setPurID(int purID) {
        this.purID = purID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
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
}