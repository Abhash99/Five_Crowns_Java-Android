package com.example.mainactivity;

import java.lang.String;
import java.util.*;

public class Deck {

    public static final int MAX_DECK_SIZE = 58;

    private Vector<Card> m_deck;
    private int m_size;

    public Deck()
    {
        this.m_deck = new Vector<Card>(MAX_DECK_SIZE);
        this.m_size = 0;
    }
    // Parametrized Constructor
    public Deck(Vector<Card> deck, int size)
    {
        this.m_deck = deck;
        this.m_size = size;
    }

    // Accessors
    public Vector<Card> getDeck()
    {
        return this.m_deck;
    }

    public int getSize()
    {
        return this.m_size;
    }

    // Mutators
    public void setDeck(Vector<Card> deck)
    {
        this.m_deck = deck;
    }
    public void setSize(int size)
    {
        this.m_size = size;
    }


    // Utility Functions
    public void initializeDeck()
    {
        m_deck.clear();
        // Load all the cards in the deck (not including the joker)
        for (int suit = 1; suit <= Card.M_NUM_SUITS; suit++)
        {
            for (int rank = 3; rank <= Card.M_NUM_RANKS; rank++)
            {
                Card newCard = new Card(suit, rank);
                m_deck.add(newCard);
                this.m_size++;
            }
        }

        // Load all the jokers in the deck
        for (int i = 1; i <= 3; i++)
        {
            Card newCard = new Card(6, i);
            m_deck.add(newCard);
            this.m_size++;
        }
    }
    void shuffle()
    {
        Collections.shuffle(this.m_deck);
    }

    public void printDeck()
    {
        for (Card card : this.m_deck)
        {
            System.out.println(card.toString() + " ");
        }

        System.out.println("\n");
    }
    // *********************************************************
    // ******************** Debugging Methods ******************
    // *********************************************************
    public static void main(String [] args)
    {
        Deck d1 = new Deck();
        d1.initializeDeck();
        d1.printDeck();
        d1.shuffle();
        d1.printDeck();
    }

}
