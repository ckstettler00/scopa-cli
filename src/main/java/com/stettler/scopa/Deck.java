package com.stettler.scopa;

import java.util.Arrays;

public class Deck {
    private Card[] deck = new Card[40];

    private int index = 0;
    public Deck() {
        shuffle();
    }

    public void shuffle(){
        int deckInt;
        int[] deckCheck = new int[40];
        this.index = 0;

        for(int i = 0; i < 40; i++){
            deckCheck[i] = 0;
        }
        for(int i = 0; i < 40; i++){
            deckInt = (int)(Math.random()*(40));
            if(deckCheck[deckInt] == 0){
                deckCheck[deckInt] = 1;
                deck[i] = new Card(faceVal(deckInt), cardSuit(deckInt));
            }
            else{
                i--;
            }

        }

    }
    private int faceVal(int num){
        return (num % 10)+1;
    }

    private Suit cardSuit(int num){
        if (num >= 30){
            return Suit.COINS;
        }
        else if (num >= 20){
            return Suit.CUPS;
        }
        else if (num >= 10){
            return Suit.SWORDS;
        }
        else{
            return Suit.SCEPTERS;
        }
    }

    @Override
    public String toString() {

        return "com.stettler.scopa.Deck{" +
                "deck=" + Arrays.toString(deck) +
                '}';
    }

    public Card draw() {
        index++;
        return deck[index-1];
    }

    public boolean hasNext(){
        return (index < 39);
    }

    public int size(){
        return 40-index;
    }
}
