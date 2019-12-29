package com.example.mainactivity;

import java.lang.String;
import java.util.*;

public class Computer extends Player {
    public Computer()
    {
        super();
        this.m_isHuman = false;
        this.m_goOutFlag = false;

    }

    public Computer(String name, int playerNum)
    {
        super(name,playerNum);
        this.m_hand = new Hand();
        this.m_isHuman = false;
        this.m_score = 0;
        this.m_goOutFlag = false;
    }


    /*****************************************************************************************************************
     /* Function Name: printMenu
     /* Purpose: To print the menu before each turn
     /* Parameters: None
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm:
     Print the menu before computer's turn
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void printMenu()
    {
        System.out.println( "Options: ");
        System.out.println( "\t 1. Save the game");
        System.out.println( "\t 2. Make a move");
        System.out.println( "\t 3. Quit the game");
    }

    /*****************************************************************************************************************
     /* Function Name: getMenuInput
     /* Purpose: To get the human input for the menu above
     /* Parameters: None
     /* Return Value: int integer that represents the user's choice
     /* Local Variables: None
     /* Algorithm:
     Prompt the user for input. Only accept input between 1 and 3
     /* Assistance Received: None

     ******************************************************************************************************************/
    public int getMenuInput()
    {
        int option;
        do
        {
            System.out.print( "Please enter your option: ");
            Scanner input = new Scanner(System.in);
            option = input.nextInt();
        } while (!(option >= 1 && option <= 3));

        // Quit game if option is 3.
        if (option == 3)
        {
            System.out.println( "Exiting Program! Byee!!");
            System.exit(0);
        }
        return option;
    }

    /*****************************************************************************************************************
     /* Function Name: play
     /* Purpose: To initiate the computer player's turn
     /* Parameters:
     Vector<Card> drawPile and Vector<Card> discardPile - both passed by reference.
     This is because we need to update drawPile and discardPile when the player picks and
     drops cards.
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm:
     Display player's hand. Computer player uses AI strategy to pick the card, drop the card and go out if
     it can.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void play(Vector<Card> drawPile, Vector<Card> discardPile)
    {
        // Print player's name and hand
        System.out.println();
        System.out.println(getName() + ":");
        m_hand.displayHand();
        System.out.println();

        pickCard(drawPile, discardPile);

        m_hand.displayHand();

        dropCard(discardPile);

        if (canGoOut())
        {
            goOut();
        }
        System.out.println();
    }

    /*****************************************************************************************************************
     /* Function Name: askForCall
     /* Purpose: To ask for player's input for coin toss
     /* Parameters:
     A character choice that is passed by reference
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm:
     This is a virtual function in the base class and doesn't have any functionality for the computer's
     class.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public char askForCall()
    {
        return 'h';
    }

    /*****************************************************************************************************************
     /* Function Name: pickCard
     /* Purpose: To pick a card from either draw or discard pile using AI strategy
     /* Parameters: Vector<Card> drawPile and discardPile - Both are passed by reference in order to update those piles
     after picking a card.
     /* Return Value: None
     /* Local Variables:
     Hand temp - temporary copy of the player's hand that will be analyzed
     HandAnalyzer analyzer - HandAnalyzer object to analyze player's hand
     Card topDiscard - that gets the copy of the top of discardPile
     HandAnalyzer analyzer2 - HandAnalyzer object to analyze hand after adding card from discard pile.
     /* Algorithm:
     Analyze the cards in hand using the handAnalyzer and obtain the number of remaining cards.
     Add the card in discard pile to hand, analyze the hand again and get new number of remaining
     cards.
     If the new remaining cards is less than or equals the first remaining card, pick the card from the
     discard pile.
     Else pick the card from draw pile.

     /* Assistance Received: None

     ******************************************************************************************************************/
    public void pickCard(Vector<Card> drawPile, Vector<Card> discardPile)
    {
        // Copy Variables
        Vector<Card> tempDraw= new Vector<Card>();
        Hand.cardListDeepCopy(drawPile, tempDraw);

        Vector<Card> tempDiscard = new Vector<Card>();
        Hand.cardListDeepCopy(discardPile, tempDiscard);

        // Analyze the copy of current hand and get current remaining.
        Hand temp = this.m_hand.clone();

        HandAnalyzer analyzer = new HandAnalyzer();
        analyzer.analyzeHand(temp);
        int currentRemaining = analyzer.getRemainingCards().size();

        // Add card from the top of discard pile, analyze hand and get the new remaining cards
        Card topDiscard = tempDiscard.elementAt(0);
        temp.addCardToHand(topDiscard);
        HandAnalyzer analyzer2 = new HandAnalyzer();
        analyzer2.analyzeHand(temp);
        int newRemaining = analyzer2.getRemainingCards().size();


        // If there are less remaining cards after adding the top of discard pile, pick from discard pile.
        // Else pick from draw pile
        if (newRemaining <= currentRemaining)
        {
            pickFromDiscard(discardPile);
            System.out.println( "Computer picked from discard pile because it can make a run/book. ");
            System.out.println();
        }
        else
        {
            pickFromDraw(drawPile);
            System.out.println( "Computer picked from draw pile because top card in discard pile isn't helpful.");
        }
        return;
    }

    /*****************************************************************************************************************
     /* Function Name: dropCard
     /* Purpose: To drop a card from the computer's hand
     /* Parameters:
     Vector<Card> discardPile - passed by reference to update the discard pile when the player drops a
     card.
     /* Return Value: None
     /* Local Variables:
     Card toBeRemoved - A card object that holds the card returned by cardToDrop function.
     /* Algorithm:
     First, obtain the card that needs to be obtained from cardToDrop function.
     Then, obtain the String representation of the card
     Remove the card from the hand. Print the rationale and adds the card to discard pile.
     /* Assistance Received:

     ******************************************************************************************************************/
    public void dropCard(Vector<Card> discardPile)
    {
        Card toBeRemoved = cardToDrop();
        String cardString = toBeRemoved.toString();
        toBeRemoved = this.m_hand.removeCard(cardString);
        System.out.println( "Computer removed " + cardString + " from hand because it was the greatest card in hand.");
        discardPile.insertElementAt(toBeRemoved, 0);
    }

    /*****************************************************************************************************************
     /* Function Name: cardToDrop
     /* Purpose: To return the optimal card that can be dropped from the hand
     /* Parameters: None
     /* Return Value:
     Card toDrop - One of the card in the hand that is unused and can be dropped.
     /* Local Variables:
     Hand temp - A copy of the player's hand
     HandAnalyzer analyzer - Handanalyzer object to analyze the hand.
     Vector<Card> remainingCardStack - stores the copy of the possible remaining cards after analyzing the
     hand.
     Hand remaining - A new hand to store the remaining cards as a hand and analyze them.
     HandAnalyzer potentialAnalyzer - Another HandAnalyzer object to analyze the hand of remaining cards to identify
     potential runs/books.
     Vector<Combination> potentials - Vector of Combination pointers that stores all the potential runs/books.
     /* Algorithm:
     Analyze player's hand - get remaining cards after extracting runs/books.
     Then, we create a hand of remaining cards and further analyze them to identify potential books/runs. We
     don't want the computer to discard useful cards.
     We remove the cards that are can form potential books/runs from the hand.
     We find the greatest card among the remaining of the cards and discard the greatest card.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public Card cardToDrop()
    {
        Card toDrop = new Card();
        Hand temp = this.getHand().clone();

        HandAnalyzer analyzer = new HandAnalyzer();
        analyzer.analyzeHand(temp);
        Vector<Card> remainingCardStack = analyzer.getRemainingCards();

        // We need to consider potential books/runs that could be in remaining cards
        // so we create a temporary hand object to extract those potential cards
        Hand remaining = new Hand();
        if (remainingCardStack.size() > 0)
        {
            remaining.setHand(remainingCardStack);
            HandAnalyzer potentialAnalyzer = new HandAnalyzer();
            Vector<Combination> potentials = potentialAnalyzer.considerPotentials(remaining);
            if (potentials.size() > 0)
            {
                for (int i = 0; i < potentials.size(); i++)
                {
                    for (Card card : potentials.elementAt(i).getPile())
                    {
                        remaining.removeCard(card.toString());
                    }
                }

                // Remaining now has all the cards except for the potential runs or books
                // If remaining has card in it, find the greatest card and return it
                if (remaining.getHand().size() > 0)
                {
                    toDrop = findGreatestCard(remaining.getHand());
                }
                // If remaining is empty, we will have to abandon the potential run/book and drop a card
                else
                {
                    toDrop = findGreatestCard(remainingCardStack);
                }

            }
            else
            {
                toDrop = findGreatestCard(remainingCardStack);
            }
        }
        else
        {
           System.out.println("Some error occured. No cards to remove. ");
           System.exit(1);
        }

        return toDrop;
    }

    /*****************************************************************************************************************
     /* Function Name: pickfromDraw
     /* Purpose: To add top card from drawPile to hand
     /* Parameters: Vector<Card> drawPile - passed by reference in order to update the drawPile after picking from it.
     /* Return Value: None
     /* Local Variables:
     Card topCard - stores the top card in the drawPile.
     /* Algorithm:
     We obtain the top card from the draw pile and store it in the variable.
     We remove the card from drawPile and add the card into player's hand.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void pickFromDraw(Vector<Card> drawPile)
    {
        Card topCard = drawPile.elementAt(0);
        drawPile.remove(0);
        addCard(topCard);
        return;
    }

    /*****************************************************************************************************************
     /* Function Name: pickFromDiscard
     /* Purpose: To pick the card from discard pile and add it into player's hand
     /* Parameters: Vector<Card> discardPile - passed by reference to update the pile after picking from it.
     /* Return Value: None
     /* Local Variables: Card topCard - stores the top card of the discardPile.
     /* Algorithm:
     Obtain the top card of the discardPile.
     Remove the card from the discard pile and add the card into player's hand.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void pickFromDiscard(Vector<Card> discardPile)
    {
        Card topCard = discardPile.elementAt(0);
        discardPile.remove(0);
        addCard(topCard);
    }

    /*****************************************************************************************************************
     /* Function Name: canGoOut
     /* Purpose: To determine if a player can go out with current hand
     /* Parameters: None
     /* Return Value: booleanean - true if player can go out, false if player can't go out.
     /* Local Variables:
     HandAnalyzer analyzer - to analyze player's hand.
     Vector<Card> remainingCards - to store remaining cards after extracting combination.
     /* Algorithm:
     Analyze player's hand using handanalyzer and get the remaining cards after extracting all combinations.
     If the size of remainingCard Vector is 0, this means all cards can be arranged in runs/books.
     So, the player can go out and then return true.
     Else, if remainingCard Vector has cards that are not jokers, then return false.

     /* Assistance Received: None

     ******************************************************************************************************************/
    public boolean canGoOut()
    {
        HandAnalyzer analyzer = new HandAnalyzer();
        analyzer.analyzeHand(getHand());
        Vector<Card> remainingCards = analyzer.getRemainingCards();
        int remCards = remainingCards.size();
        if (remCards == 0)
        {
            return true;
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
                    return false;
                }
            }
            return true;
        }

    }

    /*****************************************************************************************************************
     /* Function Name: lastTurn
     /* Purpose: To play the last turn after one of the player goes out.
     /* Parameters:
     Vector<Card> drawPile and discardPile - passed by reference in order to update the piles after the player
     pick or drops card.
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm:
     Display Player's Hand, Pick a card (either from draw or discard), drop a card from hand and finally,
     evaluate hand (because it is the last round).
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void lastTurn(Vector<Card> drawPile, Vector<Card> discardPile)
    {
        System.out.println();
        System.out.println(getName() + ":");
        m_hand.displayHand();
        System.out.println();
        pickCard(drawPile, discardPile);
        m_hand.displayHand();
        dropCard(discardPile);
        m_hand.displayHand();
        evaluateHand();
    }

    /*****************************************************************************************************************
     /* Function Name: findGreatestCards
     /* Purpose: To return the greatest card in a Vector of cards
     /* Parameters: Vector<Card> cardStack - the Vector that contains the cards
     /* Return Value: Card greatest - which is the card with the highest rank in the Vector
     /* Local Variables:
     /* Algorithm:
     Set max = 0.
     Iteratively, go through all the cards in the Vector, comparing its rank to the max value.
     If card's rank is greater than max, set max to the card's rank, and set that card as the greatest.
     At the end of the iteration, we should have the greatest card.
     /* Assistance Received: None

     ******************************************************************************************************************/
    static public Card findGreatestCard(Vector<Card> cardStack)
    {
        Card greatest = new Card();
        int max = 0;
        for (Card card : cardStack)
        {
            if (card.getRank() > max)
            {
                max = card.getRank();
                greatest = card;
            }
        }

        return greatest;
    }

}
