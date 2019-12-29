package com.example.mainactivity;

import java.util.Vector;

public class HalfRun extends Combination {
    public HalfRun(){}

    public HalfRun(Vector<Card> pile)
    {
        super(pile);
    }

    public HalfRun(HalfRun hr)
    {
        this.m_pile = hr.getPile();
    }

    @Override
    public HalfRun clone()
    {
        return new HalfRun(this.m_pile);
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
            System.out.print(" make a run with ");

            for (Card card : getPile())
            {
                System.out.print(card.toString() + " ");
            }
            System.out.println(".");
        }

    }
}
