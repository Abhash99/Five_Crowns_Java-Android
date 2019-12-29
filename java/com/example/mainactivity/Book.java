package com.example.mainactivity;

import java.util.Vector;

public class Book extends Combination{
    public Book(){}

    public Book(Vector<Card> pile)
    {
        super(pile);
    }

    public Book(Book b)
    {
        this.m_pile = b.getPile();
    }

    @Override
    public Book clone()
    {
        Book copy = new Book(this.m_pile);
        return copy;
    }

    // Utility Functions
    public boolean isIncomplete()
    {
        return false;
    }

    public void printPile()
    {
        if (this.getPile().size() > 2)
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
