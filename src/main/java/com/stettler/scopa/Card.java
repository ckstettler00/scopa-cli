package com.stettler.scopa;

public class Card implements Comparable<Card>{
    private int val = 0;
    private Suit suit= null;
    final int[] primeVal = {16,12,13,14,15,16,18,21,10,10,10};

    public Card(int val, Suit suit) {
        this.val = val;
        this.suit = suit;
    }

    public int getVal() {
        return val;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return "com.stettler.scopa.Card{" +
                "val=" + val +
                ", suit=" + suit +
                '}';
    }
    public int getPrime(){
        return primeVal[val];
    }
    @Override
    public int compareTo(Card o) {
        return Integer.compare(this.getVal(), o.getVal());
    }

}
