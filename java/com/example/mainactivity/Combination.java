package com.example.mainactivity;

import java.util.*;

public class Combination {

    protected Vector<Card> m_pile;

    // Default Constructor
    public Combination()
    {
        this.m_pile = new Vector<Card>();
    }


    // Parametrized Constructor
    public Combination(Vector<Card> pile)
    {
        this.m_pile = pile;
    }

    public Combination(Combination c)
    {
        this.m_pile = c.getPile();
    }

    @Override
    public Combination clone()
    {
        Combination copy = new Combination(this.m_pile);
        return copy;
    }


    // Accessor
    public Vector<Card> getPile()
    {
        return this.m_pile;
    }

    // Mutators
    public void setPile(Vector<Card> pile)
    {
        this.m_pile = pile;
    }


    //Utility Functions
    public boolean isIncomplete() { return false; }
    public void printPile() {}

}
