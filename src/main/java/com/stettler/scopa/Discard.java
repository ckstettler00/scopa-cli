package com.stettler.scopa;

public class Discard implements Move {
    public Card getDiscarded() {
        return discarded;
    }

    public void setDiscarded(Card discarded) {
        this.discarded = discarded;
    }

    private Card discarded;

    public Discard(Card c) {
        discarded = c;
    }
}
