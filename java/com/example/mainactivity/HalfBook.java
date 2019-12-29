package com.example.mainactivity;

import java.util.Vector;

public class HalfBook extends Combination {
    public HalfBook(){}

    public HalfBook(Vector<Card> pile)
    {
        super(pile);
    }

    public HalfBook(HalfBook hb)
    {
        this.m_pile = hb.getPile();
    }

    @Override
    public HalfBook clone()
    {
       return new HalfBook(this.m_pile);
    }

    // Utility Functions
    public boolean isIncomplete()
    {
        return true;
    }

    public void printPile()
    {
        if (getPile().size() > 2)
        {
            System.out.print(" make a book with ");

            for (Card card : getPile())
            {
                System.out.print(card.toString() + " ");
            }
            System.out.println(".");
        }

    }
}
