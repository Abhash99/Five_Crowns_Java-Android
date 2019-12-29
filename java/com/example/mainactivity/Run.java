package com.example.mainactivity;

import java.util.*;

public class Run extends Combination {

    public Run(){}

    public Run(Vector<Card> pile)
    {
        super(pile);
    }

    public Run(Run r)
    {
        this.m_pile = r.getPile();
    }

    @Override
    public Run clone()
    {
        return new Run(this.m_pile);
    }

    // Utility Functions
    public boolean isIncomplete()
    {
        return false;
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
