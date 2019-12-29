package com.example.mainactivity;

import java.lang.String;
import java.util.*;


public class Human extends Player {
    public Human()
    {
        super();
        this.m_isHuman = true;
    }

    public Human(String name, int playerNum)
    {
        super(name, playerNum);
        this.m_hand = new Hand();
        this.m_isHuman = true;
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
     Print the menu before human's turn
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void printMenu()
    {
        System.out.println("Options: ");
        System.out.println("\t 1. Save the game");
        System.out.println("\t 2. Make a move");
        System.out.println("\t 3. Quit the game");
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
           System.out.print("Please enter your option: ");
           Scanner input = new Scanner(System.in);
           option = input.nextInt();
        } while (!(option >= 1 && option <= 3));

        // Quit game if option is 3.
        if (option == 3)
        {
            System.out.println("Exiting Program! Byee!!");
            System.exit(0);
        }
        return option;
    }

    /*****************************************************************************************************************
     /* Function Name: play
     /* Purpose: To initiate the human player's turn
     /* Parameters:
     Vector<Card> drawPile and Vector<Card> discardPile - both passed by reference.
     This is because we need to update drawPile and discardPile when the player picks and
     drops cards.
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm:
     Display player's hand. Enables player to pick a card either from draw or discard pile, drop a card from
     hand and go out when possible. Help can also be provided by the computer in each stage of the game. (i.e.
     pick, drop, arrange card and go out)
     /* Assistance Received: None
     ******************************************************************************************************************/
    public void play(Vector<Card> drawPile, Vector<Card> discardPile)
    {
        System.out.println();
        System.out.println( getName() + ":");
        m_hand.displayHand();
        pickCard(drawPile, discardPile);
        m_hand.displayHand();
        dropCard(discardPile,"ToBeRemoved"); // Need to pass the cardString of the card.
        System.out.println();

        // Ask if player needs help arranging cards or wants to go out or continue play
        int choice;
        do
        {
            System.out.println( "What would you like to do: ");
            System.out.println( "================================");
            System.out.println( "\t 1. Continue Play");
            System.out.println( "\t 2. Go out");
            System.out.println("\t 3. Ask help to arrange cards");

            System.out.print( "Enter your choice: ");
            Scanner input = new Scanner(System.in);
            choice = input.nextInt();

            if (choice == 3)
            {
                helpArrange();
            }

        } while (!(choice >= 1 && choice <= 2));

        if (choice == 1)
        {
            System.out.println( "Continuing Play...");
            return;
        }
        else
        {
            goOut();
        }

        return;
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
        char input;
        do
        {
            System.out.print(this.getName() + ", Please call heads(H) or Tails(T): " );
            Scanner inp = new Scanner(System.in);
            input = inp.next().charAt(0);
            input = Character.toLowerCase(input);
        } while (!(input == 'h' || input == 't'));

        return input;
    }

    /*****************************************************************************************************************
     /* Function Name: help pick
     /* Purpose: Provide recommendations to the human player to pick a card using AI strategy (HandAnaluyzer)
     /* Parameters:
     Vector<Card> discardPile - Need to look at the card on top of the discard pile in order to give help.
     /* Return Value: None
     /* Local Variables:
     Hand playerHand - copy of human's hand
     temp - temporary copy of human's hand to analyze it seond time
     HandAnalyzer analyzer1 - Used to analyze players hand and get the best run/books and remaining cards
     HandAnalyzer analyzer2 - Used to analyzer player hand after adding card from discard pile
     /* Algorithm:
     We use handAnalyzer object to determine our current number of remaining cards. We then add the card from
     discard pile and analyze our hand again. If the new number of remaining cards is equal or less than the previous
     remaining cards, recommend to pick from discard pile. Else, recommend to pick from draw pile.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public String helpPick(Vector<Card> discardPile)
    {

        Vector<Card> tempDiscard = new Vector<Card>();
        Hand.cardListDeepCopy(discardPile, tempDiscard);

        // Analyze the copy of current hand and get current remaining.
        Hand temp = this.m_hand.clone();

        // We need new analyzer for every check that we do
        // New analyzer
        HandAnalyzer analyzer1 = new HandAnalyzer();
        analyzer1.analyzeHand(this.getHand());
        int currentRemaining = analyzer1.getRemainingCards().size();

        Card topDiscard = discardPile.elementAt(0);
        temp.addCardToHand(topDiscard);

        //New analyzer to check if discard pile will help
        HandAnalyzer analyzer2 = new HandAnalyzer();
        analyzer2.analyzeHand(temp);
        int newRemaining = analyzer2.getRemainingCards().size();

        // If adding the top card from draw pile results in less remaining cards, choose discard else choose draw.
        // Note: <= because when we pick a card from discard, our hand has one extra card. So, even if we get the
        // same number of remaining cards, we are in a better position.
        if (newRemaining <= currentRemaining)
        {
           return "I recommend you to pick from discard because you use the top card to make a run/book.";
        }

        return "I recommend you to pick from draw pile because the top card in discard pile is not useful.";


    }

    /*****************************************************************************************************************
     /* Function Name: helpDrop
     /* Purpose: To help the human to drop a card using AI strategy
     /* Parameters: None
     /* Return Value: Nones
     /* Local Variables:
     Card toDrop - the variable that holds the recommended card to drop.
     Hand playerhand - copy of player's hand
     HandAnalyzer analyzer - Used to analyzer player's hand for runs/book  and remaining cards.
     Vector<Card> remainingCardStack - Vector to hold the remaining cards after setting aside runs/books
     Hand remaining - A new hand to store the remaining cards as a hand and analyze them.
     HandAnalyzer potentialAnalyzer - Another HandAnalyzer object to analyze the hand of remaining cards to identify
     potential runs/books.
     Vector<Combination> potentials - Vector of Combination pointers that stores all the potential runs/books.
     /* Algorithm:
     Analyze player's hand - get remaining cards after extracting runs/books.
     Then, we create a hand of remaining cards and further analyze them to identify potential books/runs. We
     don't want the player to discard useful cards.
     We remove the cards that are can form potential books/runs from the hand.
     We find the greatest card among the remaining of the cards and recommend to discard the greatest card.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public String helpDrop()
    {
        Card toDrop;
        Hand playerHand = getHand();
        // New Analyzer to check which card to drop
        HandAnalyzer analyzer = new HandAnalyzer();
        analyzer.analyzeHand(playerHand);
        Vector<Card> remainingCardStack = analyzer.getRemainingCards();

        // We need to consider potential books/runs that could be in remaining cards
        // so we create a temporary hand object to extract those potential cards
        Hand remaining = new Hand();
        if (remainingCardStack.size() > 0)
        {
            remaining.setHand(remainingCardStack);
            // New analyzer to check the potential runs and books in the remaining cards
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
                    toDrop = Computer.findGreatestCard(remaining.getHand());
                }
                // If remaining is empty, we will have to abandon the potential run/book and drop a card
                else
                {
                    toDrop = Computer.findGreatestCard(remainingCardStack);
                }

            }
            else
            {
                toDrop = Computer.findGreatestCard(remainingCardStack);
            }
        }
        else
        {
            return "All cards are in combinations. You could drop any card. ";

        }

        return "I recommend you to drop " + toDrop.toString() + " because it is the greatest unused card in your hand.";

    }

    /*****************************************************************************************************************
     /* Function Name: helpArrange
     /* Purpose: To help the human player to arrange their cards in runs/books using AI strategy
     /* Parameters: None
     /* Return Value: None
     /* Local Variables:
     Hand playerHand - copy of human's hand
     HandAnalyzer analyzer - Analyzer object to analyze current hand and get best combinations.
     Vector<Combination> bestCombination - holds the best combination for player's hand.
     /* Algorithm:
     We use the HandAnalyzer object to first find the best combinations in player's hand and we print the best
     combinations to the screen.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void helpArrange()
    {
        Hand playerHand = getHand();
        HandAnalyzer analyzer = new HandAnalyzer();
        analyzer.analyzeHand(playerHand);
        Vector<Combination> bestCombinations = analyzer.getCombination();

        if (!bestCombinations.isEmpty())
        {
            for (Combination comb : bestCombinations)
            {
                System.out.println( "I recommend you to ");
                comb.printPile();
                System.out.println();
            }
        }
        else
        {
            System.out.println("You don't have any runs and books at this stage.");
        }
        return;
    }



    /*****************************************************************************************************************
     /* Function Name: pickCard
     /* Purpose: To allow human to pick from either draw or discard pile
     /* Parameters:
     Vector<Card> drawPile - passed by reference because we need to update it after picking.
     Vector<Card> discardPile - also passed by reference because we need to update it after picking.
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm:
     Display user's option. Prompt the user for input. Get the input from the user and based on the input, use the
     pickFromDraw / pickFromDiscard functions to pick a card.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void pickCard(Vector<Card> drawPile, Vector<Card> discardPile)
    {
        int choice;
        // Print options to pick. Ask if player wants to pick from draw pile, discard pile or wants to ask computer for help.
        // Continue only if choice is 1 or 2, which means player wants to pick either from drop or discard.
        do
        {
            System.out.println( "Would you like to pick a card from draw pile or discard pile?");
            System.out.println( "\t 1. Draw Pile");
            System.out.println( "\t 2. Discard Pile");
            System.out.println( "\t 3. Ask for help");
            Scanner input = new Scanner(System.in);
            choice = input.nextInt();

            if (choice == 3)
            {
                System.out.print( "Asking Computer for Help: ");
                System.out.print( "=====================================");
                helpPick(discardPile);
                System.out.println();
            }

        } while (!(choice >= 1 && choice <= 2));

        if (choice == 1)
        {
            this.pickFromDraw(drawPile);
        }
        else
        {
            this.pickFromDiscard(discardPile);
        }

        return;
    }



    /*****************************************************************************************************************
     /* Function Name: pickFromDraw
     /* Purpose: Add a card from drawPile to hand
     /* Parameters:
     Vector<Card> drawPile - passed by reference to get the card and remove it from top of the drawPile
     /* Return Value: None
     /* Local Variables: Card topCard - to store the top card in the drawPile and add it later to hand
     /* Algorithm:
     We store the top card in the drawPile in a temporary variable. Remove the card from the drawPile and
     add the card to human's hand.
     /* Assistance Received: None


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
        System.out.print( getName() + ":" );
        m_hand.displayHand();
        pickCard(drawPile, discardPile);
        m_hand.displayHand();
        dropCard(discardPile, "ToBeRemoved"); // Needs to be fixed.
        System.out.println();
        m_hand.displayHand();
        evaluateHand();
    }


}
