package com.example.automechapp.detail;

public class Detail {
    private int price;
    private int id;
    private int breakdownId;

    public Detail(int id, int breakdownId, int price) {
        this.price = price;
        this.id = id;
        this.breakdownId = breakdownId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBreakdownId() {
        return breakdownId;
    }

    public void setBreakdownId(int breakdownId) {
        this.breakdownId = breakdownId;
    }
}
