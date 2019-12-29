package com.example.mainactivity; /*******************************************************
 One line description of what the class does
 @author Abhash Panta
 @since Date 11/2/2019
  ******************************************************** */

import java.lang.String;
public class Card {

// *********************************************************
// ******************** Class Constants ********************
// *********************************************************
    public static final int M_NUM_SUITS = 5;
    public static final int M_NUM_RANKS = 13;
    public static final char DEFAULT_SUIT = '2';
    public static final char DEFAULT_RANK = 'X';

// *********************************************************
// ******************** Class Variables ********************
// *********************************************************

/** One line description of the variable */

    private int m_suit;
    private int m_rank;
    private boolean m_isWildcard;
    private int m_value;


// *********************************************************
// ******************** Constructor ************************
// *********************************************************
    // Default Constructor for Card Class
    public Card()
    {
        this.m_suit = 0;
        this.m_rank = 0;
    }


    // Parametrized Constructor for the Card Class
    public Card(int suit, int rank)
    {
        this.m_suit = suit;
        this.m_rank = rank;
        this.m_isWildcard = false;

        if (this.m_suit == 6)
        {
            this.m_value = 50;
        }
		else
        {
            this.m_value = rank;
        }
    }


    // Constructor for loading serialized game data.
    public Card(String symbol)
    {
        char suit = symbol.charAt(1);
        char rank = symbol.charAt(0);
        switch(suit)
        {
            case 'S':
               m_suit = 1;
               break;
            case 'H':
               m_suit = 2;
               break;
            case 'C':
               m_suit = 3;
               break;
            case 'D':
               m_suit = 4;
               break;
            case 'T':
               m_suit = 5;
               break;
        }

        switch(rank)
        {
            case '3':
                m_rank = 3;
                m_value = m_rank;
                break;
            case '4':
                m_rank = 4;
                m_value = m_rank;
                break;
            case '5':
                m_rank = 5;
                m_value = m_rank;
                break;
            case '6':
                m_rank = 6;
                m_value = m_rank;
                break;
            case '7':
                m_rank = 7;
                m_value = m_rank;
                break;
            case '8':
                m_rank = 8;
                m_value = m_rank;
                break;
            case '9':
                m_rank = 9;
                m_value = m_rank;
                break;
            case 'X':
                m_rank= 10;
                m_value = m_rank;
                break;
            case 'J':
                m_rank = 11;
                m_value = m_rank;
                break;
            case 'Q':
                m_rank = 12;
                m_value = m_rank;
                break;
            case 'K':
                m_rank = 13;
                m_value = m_rank;
                break;
        }

        // Need to handle Jokers seperately as their symbol appears differently.
        // Joker is represented as J1, J2, J3
        if (rank == 'J' && (suit == '1' || suit == '2' || suit == '3'))
        {
            m_suit = 6;
            m_value = 50;
            if (suit == '1')
            {
                m_rank = 1;
            }

            if (suit == '2')
            {
                m_rank = 2;
            }

            if (suit == '3')
            {
                m_rank = 3;
            }
        }

    }

    @Override
    public Card clone()
    {
        return new Card(this.m_suit, this.m_rank);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this.m_rank == ((Card)o).getRank() && this.m_suit == ((Card)o).getSuit())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

/**
 For each function, one-line description of the function
 @param // parameterName Describe the parameter, starting with its data type
 @return What the function returns - don't include if void. Also list special cases, such as what is returned if error.
 */

// *********************************************************
// ******************** Selectors **************************
// *********************************************************
    public int getRank()
    {
        return this.m_rank;
    }

    public int getSuit()
    {
        return this.m_suit;
    }

    boolean isWildcard()
    {
        return this.m_isWildcard;
    }

    public int getValue()
    {
        return this.m_value;
    }

    public boolean isJoker()
    {
        if (m_suit == 6)
            return true;
        else
            return false;
    }



// *********************************************************
// ******************** Mutators ***************************
// *********************************************************
    public void setRank(int rank)
    {
        m_rank = rank;
    }

    public void setSuit(int suit)
    {
        m_suit = suit;
    }

    public void setWildcard(boolean isWildcard)
    {
        m_isWildcard = isWildcard;
    }

    public void setValue(int value)
    {
        m_value = value;
    }

// *********************************************************
// ******************** Utility Methods ********************
// *********************************************************
// Converts the card to string representation
public String toString()
    {
        char suit = DEFAULT_SUIT;
        char rank = DEFAULT_RANK;

        switch (m_suit)
        {
            case 1:
                suit = 'S';
                break;
            case 2:
                suit = 'H';
                break;
            case 3:
                suit = 'C';
                break;
            case 4:
                suit = 'D';
                break;
            case 5:
                suit = 'T';
                break;
            case 6:
                suit = 'J';
                break;
            default:

        }

        // Note: the ranks 1,2 are needed for jokers only.
        switch (m_rank)
        {
            case 1:
                rank = '1';
                break;
            case 2:
                rank = '2';
                break;
            case 3:
                rank = '3';
                break;
            case 4:
                rank = '4';
                break;
            case 5:
                rank = '5';
                break;
            case 6:
                rank = '6';
                break;
            case 7:
                rank = '7';
                break;
            case 8:
                rank = '8';
                break;
            case 9:
                rank = '9';
                break;
            case 10:
                rank = 'X';
                break;
            case 11:
                rank = 'J';
                break;
            case 12:
                rank = 'Q';
                break;
            case 13:
                rank = 'K';
                break;
            default:
                // To Do: Print error and exit
        }
        String s_rank = Character.toString(rank);
        String s_suit = Character.toString(suit);

        // The string representation for Joker is opposite where suit comes first and rank comes second. Ex. J1, J2, J3
        if (!isJoker())
        {
            String symbol = s_rank + s_suit;
            return symbol;
        }
        else
        {
            String symbol = s_suit + s_rank;
            return symbol;
        }
    }

// *********************************************************
// ******************** Printing Methods *******************
// *********************************************************

// *********************************************************
// ******************** Debugging Methods ******************
// *********************************************************
public static void main(String [] args)
{
    Card temp = new Card(3,5);
    System.out.println(temp.toString());
}

// *********************************************************
// ******************** Trash Methods **********************
// *********************************************************
};