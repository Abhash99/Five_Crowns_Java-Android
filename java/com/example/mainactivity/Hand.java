package com.example.mainactivity;

import java.lang.String;
import java.util.*;

public class Hand {

    // Member Variables
    private Vector<Card> m_hand;
    private int m_handSize;


    // Constructor
    Hand()
    {
        this.m_hand = new Vector<Card>(3);
        this.m_handSize = 0;
    }

    @Override
    public Hand clone()
    {
        Hand copy = new Hand();
        Hand.cardListDeepCopy(this.getHand(), copy.getHand());
        return copy;
    }

    // Accessor Function
    Vector<Card> getHand()
    {
        return this.m_hand;
    }

     int getSize()
    {
        return this.m_handSize;
    }


    // Mutator Function
     void setHand(Vector<Card> hand)
    {
        this.m_hand = hand;
        this.m_handSize = hand.size();
    }

     void setHandSize(int size)
    {
        this.m_handSize = size;
    }



    // Public Utility Functions

    /*****************************************************************************************************************
     /* Function Name: displayHand
     /* Purpose: To print all the card in the hand
     /* Parameters: None
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm:
     For each card in player's hand, convert the card to its String representation using toString()
     and print the cardString.
     /* Assistance Received: None

     ******************************************************************************************************************/
    void displayHand()
    {
        // If handSize is 0 or less, we print the hand is empty
        if (this.m_hand.size() <= 0)
        {
            System.out.println("Hand: Empty");
        }
	    else
        {
            System.out.println("Hand: ");
            for (Card card : m_hand)
            {
                System.out.print(card.toString() + " ");
            }
            System.out.println();
        }
    }

    /*****************************************************************************************************************
     /* Function Name: clearHand
     /* Purpose: To clear (remove) all cards in player's hand
     /* Parameters: None
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm:
     Simply use the clear function of Vectors to remove all the cards in the current hand.
     /* Assistance Received: None

     ******************************************************************************************************************/
    void clearHand()
    {
        this.m_hand.clear();
    }

    /*****************************************************************************************************************
     /* Function Name: addCardToHand
     /* Purpose: To add a given card to current hand.
     /* Parameters:
     Card c - the card that is to be added to the hand
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm: Simply add the card to hand by using push_back function in Vectors. Increase hand size.
     /* Assistance Received: None

     ******************************************************************************************************************/
    void addCardToHand(Card c)
    {
        m_hand.add(c);
        this.m_handSize++;
    }


    /*****************************************************************************************************************
     /* Function Name: removeCard
     /* Purpose: To remove a card from the hand
     /* Parameters:
     String card_String - the String representation of the card to be deleted.
     /* Return Value:
     Returns the card that is removed from the hand. We return the card so that we can use it again.
     (Maybe add it to discard pile after it is removed)
     /* Local Variables:
     Card temp - temporary card that will hold the card to be removed. It will be returned at the end of the program.
     /* Algorithm:
     Iterate through the hand to find the card that matches the card String.
     If it is present, find the card's position and remove the card from hand.
     Return the removed card.
     /* Assistance Received: None.

     ******************************************************************************************************************/
    Card removeCard(String card_String)
    {
        Card temp = new Card();

        // If the hand is not empty, iterate through the hand to find the card that matches the card String.
        // Remove the card if it is present in the array and return it.
        if (!this.getHand().isEmpty())
        {
            for (int i = 0; i < this.m_hand.size(); i++)
            {
                Card now = m_hand.elementAt(i);
                String s = m_hand.elementAt(i).toString();
                if (s.equals(card_String))
                {
                    temp = m_hand.elementAt(i);
                    m_hand.remove(i);
                    return temp;
                }
            }
            // Get the iterator to point to the card to be deleted.
        }

        return temp;
    }

    // To do: Deep copy vector of cards. Needs the copy constructor for card objects.
    public static void cardListDeepCopy(Vector<Card> source, Vector<Card> destination)
    {
        destination.clear();
        for (int i = 0; i < source.size(); i++) {
            Card newCard = (Card) source.elementAt(i).clone();
            destination.add(newCard);
        }
    }

    // *********************************************************
    // ******************** Debugging Methods ******************
    // *********************************************************
    public static void main(String [] args)
    {
        Hand hand = new Hand();
        Card c1 = new Card(3,2);
        Card c2 = new Card(5,3);
        Card c3 = new Card (4,9);
        hand.addCardToHand(c1);
        hand.addCardToHand(c2);
        hand.addCardToHand(c3);
        hand.displayHand();
        hand.removeCard("3T");
        hand.displayHand();
        hand.clearHand();
        hand.displayHand();
    }


}
