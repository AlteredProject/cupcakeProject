package app.entities;

public class Muffins {
    private Bottoms bottom;
    private Toppings topping;
    private int quantity;

    public Muffins(Bottoms bottom, Toppings topping, int quantity) {
        this.bottom = bottom;
        this.topping = topping;
        this.quantity = quantity;
    }

    public Toppings getTopping() {
        return topping;
    }

    public void setTopping(Toppings topping) {
        this.topping = topping;
    }

    public Bottoms getBottom() {
        return bottom;
    }

    public void setBottom(Bottoms bottom) {
        this.bottom = bottom;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
