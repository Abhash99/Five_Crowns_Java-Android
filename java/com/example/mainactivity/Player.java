package com.example.mainactivity;

import java.lang.String;
import java.util.*;

public class Player {

    protected String m_name;
    protected int m_playerNum;
    protected int m_score;
    protected Hand m_hand;
    protected boolean m_isHuman;
    protected boolean m_goOutFlag;


    // Constructor for player class
    public Player()
    {
        this.m_goOutFlag = false;
        this.m_hand = new Hand();
        this.m_isHuman = false;
        this.m_playerNum = 0;
        this.m_score = 0;
    };

    public Player(String name, int playerNum)
    {
        this.m_hand = new Hand();
        this.m_name = name;
        this.m_playerNum = playerNum;
        this.m_score = 0;
        this.m_isHuman = false;
        this.m_goOutFlag = false;
    }

    // Accessors
    public String getName()
    {
        return this.m_name;
    }

    public int getPlayerNum()
    {
        return this.m_playerNum;
    }

    public int getScore()
    {
        return this.m_score;
    }


    public Hand getHand()
    {
        return this.m_hand;
    }

    public boolean isHuman()
    {
        return this.m_isHuman;
    }

    public boolean wentOut()
    {
        return m_goOutFlag;
    }

    //Mutators
    public void setName(String name)
    {
        this.m_name = name;
    }

    public void setPlayerNum(int number)
    {
        this.m_playerNum = number;
    }

    public void setScore(int score)
    {
        this.m_score = score;
    }

    public void setHand(Hand hand)
    {
        this.m_hand = hand;
    }

    public void setIsHuman(boolean isHuman)
    {
        this.m_isHuman = isHuman;
    }

    public void setGoOutFlag(boolean goOut)
    {
        this.m_goOutFlag = goOut;
    }

   // Utility Functions

    /*****************************************************************************************************************
     /* Function Name: askForChoice
     /* Purpose: To ask call for Coin-Toss (Polymorphic)
     /* Parameters: None
     /* Return Value: By default return character 'h' for heads.
     /* Local Variables: None
     /* Algorithm:
     /* Assistance Received: None
     ******************************************************************************************************************/
    public char askForCall(){return 'h';}


    /*****************************************************************************************************************
     /* Function Name: play	
     /* Purpose: To initiate playing turn of each player
     /* Parameters: Vector<Card> drawPile and discardPile - passed by reference to be updated by the player's turn
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm: It is a virtual function in the base class. 
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void play(Vector<Card> drawPile, Vector<Card> discardPile)
    {
    }

    /*****************************************************************************************************************
     /* Function Name: addCard
     /* Purpose: To add card to player's hands
     /* Parameters: Card c
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm:
     Simply call the addCardToHand function of Hand Class.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void addCard(Card c)
    {
        this.m_hand.addCardToHand(c);
    }

    /*****************************************************************************************************************
     /* Function Name: clearHand
     /* Purpose: To clear player's hand
     /* Parameters: None
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm: Simply calls the clear hand function of Hand class.
     /* Assistance Received: None

     ******************************************************************************************************************/
    void clearHand()
    {
        this.m_hand.clearHand();
    }


    /*****************************************************************************************************************
     /* Function Name: goOut
     /* Purpose: To make the player go out
     /* Parameters: None
     /* Return Value: None
     /* Local Variables:
     HandAnalyzer analyzer - Used to analyze player's hand to determine if the player can go out.
     Vector<Card> remainingCards - holds the remaining cards in player's hand after setting aside
     runs/books.
     /* Algorithm:
     We analyze the player's hand and obtain the remaining cards that are in player's hand. If there are no 
     remaining cards, we can declare that player went out. Then set the goOutFlag and evalute player's hand.
     If the remaining cards are all jokers, we can go out, otherwise we cannot go out if we have any other
     remaining cards.

     /* Assistance Received: None

     ******************************************************************************************************************/
    public void goOut()
    {
        // Verify if the player can go out
        HandAnalyzer analyzer = new HandAnalyzer();
        analyzer.analyzeHand(getHand());
        Vector<Card> remainingCards = analyzer.getRemainingCards();
        int remCards = remainingCards.size();
        if (remCards == 0)
        {
            System.out.println(getName() + " went out. ");
            setGoOutFlag(true);
            evaluateHand();
            return;
        }
        else
        {
            for (int i = 0; i < remCards; i++)
            {
                while (remainingCards.elementAt(i).isJoker() || remainingCards.elementAt(i).isWildcard())
                {
                    i++;
                }

                if (!(remainingCards.elementAt(i).isJoker() || remainingCards.elementAt(i).isWildcard()))
                {
                    System.out.println("You cannot go out at this point. Your cards cannot be arranged into books/runs.");
                    return;
                }
            }
            System.out.println(getName() + " went out. ");
            System.out.println("================================================");
            setGoOutFlag(true);
            evaluateHand();
            return;
        }

    }

    /*****************************************************************************************************************
     /* Function Name: evaluateHand
     /* Purpose: To evaluate the runs/books and remaining cards at the end of the round when a player went out.
     /* Parameters: None
     /* Return Value: None
     /* Local Variables:
     Hand Analyzer analyzer - To analyze player's hand for runs and books.
     Vector<Combination*> bestCombination - Best possible combination of runs and books in player's hand. 
     Vector<Card> remainingCard - Remaining cards in player's hand after setting runs/books aside. 
     Vector
     /* Algorithm:
     We analyze player's hand using a HandAnalyzer object and obtain the bestCombination and remaining cards. 
     Print all the runs/books the player has. If there are any potential combinations (i.e. books/runs of two cards)
     We add their rank value to the total score. In the remaining cards, we add the rank value of all the cards to the 
     player's score. If the card is a joker, it has 50 point, if is it a wildcard, it gets 20 points.
     At the end, print the player's total score for the round.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void evaluateHand() {
        int scoreCounter = getRoundScore();
        setScore(getScore() + scoreCounter);
    }

    // Get current round score
    public int getRoundScore()
    {
        int scoreCounter = 0;
        HandAnalyzer analyzer = new HandAnalyzer();
        analyzer.analyzeHand(getHand());
        Vector<Combination> bestCombination = analyzer.getCombination();
        Vector<Card> remainingCard = analyzer.getRemainingCards();

        // Print all the combinations that the player has
        if (!bestCombination.isEmpty()) {
            for (Combination comb : bestCombination) {
                System.out.print(getName() + " can ");
                comb.printPile();
                System.out.println();
            }
            System.out.println();
        } else {
            System.out.println(getName() + " doesn't have any combinations in hand.");
        }

        // Need to consider potential runs/books (which are of size 2)
        for (Combination comb : bestCombination) {
            if (comb.getPile().size() == 2) {
                for (Card card : comb.getPile()) {
                    card.setValue(card.getRank());
                    scoreCounter = scoreCounter + card.getValue();
                }
            }
        }

        if (remainingCard.size() > 0) {
            System.out.println(getName() + "'s remaining cards are: ");
            for (Card card : remainingCard) {
                if (card.isJoker()) {
                    card.setValue(50);
                } else if (card.isWildcard()) {
                    card.setValue(20);
                } else {
                    card.setValue(card.getRank());
                }
                scoreCounter = scoreCounter + card.getValue();
                System.out.println(card.toString() + " ");
            }
            System.out.println();
        } else {
            System.out.println();
            System.out.println(getName() + " doesn't have any remaining cards.");
            System.out.println();
        }

        System.out.println(getName() + "'s score for this round is: " + scoreCounter + ".");

        return scoreCounter;
    }

    public void lastTurn(Vector<Card> drawPile, Vector<Card> discardPile){}


    void pickFromDraw(Vector<Card> drawPile)
    {
        Card topCard = drawPile.elementAt(0);
        drawPile.remove(0);
        addCard(topCard);
    }

    /*****************************************************************************************************************
     /* Function Name: pickFromDiscard
     /* Purpose: Add a card from discardPile to hand
     /* Parameters:
     Vector<Card> discardPile - passed by reference to get the card and remove it from top of the discardPile
     /* Return Value: None
     /* Local Variables:
     Card topCard - to store the top card in the discardPile and add it later to hand.
     /* Algorithm:
     We store the top card in the discardPile in a temporary variable. Remove the card from the discardPile and
     add the card to human's hand.
     /* Assistance Received: None
     ******************************************************************************************************************/
    void pickFromDiscard(Vector<Card> discardPile)
    {
        Card topCard = discardPile.elementAt(0);
        discardPile.remove(0);
        addCard(topCard);
    }

    /*****************************************************************************************************************
     /* Function Name: dropCard
     /* Purpose: To allow human player to drop a card
     /* Parameters: Vector<Card> discardPile - passed by reference because we will need to drop the card to discard pile.
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm:
     We simply display human player's options. Prompt them for input and based on their input, drop a card to
     discard pile. Help can also be provided to drop a card.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void dropCard(Vector<Card> discardPile, String cardString)
    {
        Card toBeRemoved;

        // Remove the card from player's hand.
        toBeRemoved = this.m_hand.removeCard(cardString);
        discardPile.insertElementAt(toBeRemoved,0);
        goOut();
    }


    public String helpPick(Vector<Card> discardPile) {
        return "";
    }


    public String helpDrop()
    {
        return "";
    }


}
