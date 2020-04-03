package com.example.livestream;

public class Chief {

    private int id;
    private int card;
    private String food;

    public Chief(int card, String food) {
//        this.id = id;
        this.card = card;
        this.food = food;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }
}
