package com.stettler.scopa;

import java.util.ArrayList;
import java.util.List;

public class Pickup implements Move {
    private Card playerCard;
    private List<Card> tableCards = new ArrayList<>();

    public Pickup() {}
    public Pickup(Card player, List<Card> pickedUp) {
        playerCard = player;
        tableCards.addAll(pickedUp);
    }
    public void addCardToPickUp(Card c) {
        tableCards.add(c);
    }

    public Card getPlayerCard() {
        return playerCard;
    }

    public void setPlayerCard(Card playerCard) {
        this.playerCard = playerCard;
    }

    public List<Card> getTableCards() {
        return tableCards;
    }

    public void setTableCards(List<Card> tableCards) {
        this.tableCards = tableCards;
    }
}
