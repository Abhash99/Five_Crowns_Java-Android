package com.example.mainactivity;

import java.lang.String;
import java.util.*;

public class HandAnalyzer {

    private int[][] m_table = new int [Card.M_NUM_SUITS][Card.M_NUM_RANKS - 2];
    private Vector<Combination> m_bestCombination = null;
    private Vector<Card> m_remainingCards = null;
    private Vector<Card> m_wildCardStack = null;
    
    // Constructor and Destructor
    public HandAnalyzer()
    {
        this.m_bestCombination = new Vector<Combination>();
        this.m_remainingCards = new Vector<Card>();
        this.m_wildCardStack = new Vector<Card>();
    }

    // Accessors

    public Vector<Combination> getCombination()
    {
        return this.m_bestCombination;
    }

    public Vector<Card> getRemainingCards()
    {
        return this.m_remainingCards;
    }

    public Vector<Card> getWildCardStack()
    {
        return this.m_wildCardStack;
    }

    // Mutators
    public void setCombination(Vector<Combination> cardObjects)
    {
        this.m_bestCombination = cardObjects;
    }

    public void setRemainingCards(Vector<Card> remainingCards)
    {
        this.m_remainingCards = remainingCards;
    }

    public void setWildCardStack(Vector<Card> wildCardStack)
    {
        this.m_wildCardStack = wildCardStack;
    }



    // Utility Functions
    /*****************************************************************************************************************
     /* Function Name: loadHandstoTable
     /* Purpose: To plot the table based on the cards in hand.
     /* Parameters:
     Hand playerHand - the hand that is to be analyzed
     /* Return Value: None
     /* Local Variables:
     Vector<Card> cardsInHand - Vector of all cards in hand
     /* Algorithm:
     For all cards in hand, if it is not joker or a wildcard, go to the appropriate position in the table
     and increment the value by 1 (E.g. for 5H - We go to row position = 2(suit value for heart) - 1 = 1
     and column position = 5 (rank of the card) - 3 = 2. So, we increment index [1][2] by 1).
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void loadHandsToTable(Hand playerHand)
    {
        Vector<Card> cardsInHand = playerHand.getHand();
        for (int i = 0; i < cardsInHand.size(); i++)
        {
            if (!(cardsInHand.elementAt(i).isJoker() || cardsInHand.elementAt(i).isWildcard()))
            {
                m_table[(cardsInHand.elementAt(i).getSuit()) - 1][(cardsInHand.elementAt(i).getRank()) - 3]++;
            }
        }
        return;
    }


    /*****************************************************************************************************************
     /* Function Name: loadWildCards
     /* Purpose: To load the wildcard and jokers in hand to the wildcardstack if present.
     /* Parameters:
     Hand playerHand - the player hand to be analyzed
     /* Return Value: None
     /* Local Variables:
     Vector<Card> cardsInHand - temporary Vector of cards to hold the cards in hand.
     /* Algorithm:
     Make a copy of the Vector of cards. For each card in the Vector of cards, if card is a joker or
     card is a wildcard, push it into the wildcard stack.
     /* Assistance Received: None

     ******************************************************************************************************************/
// Loads the wildcards and jokers in the wildcardstack if they are present.
    public void loadWildCards(Hand playerHand)
    {
        Vector<Card> cardsInHand = playerHand.getHand();
        for (Card card : cardsInHand)
        {
            if (card.isJoker() || card.isWildcard())
            {
                m_wildCardStack.add(card);
            }
        }
        return;
    }

    /*****************************************************************************************************************
     /* Function Name: analyzeHand
     /* Purpose: Serves as interface function that takes player's hand, does all the analysis and updates the best
     combinations and remaining cards based on the analysis.
     /* Parameters:
     Hand playerHand - The hand that is to be analyzed.
     /* Return Value: None
     /* Local Variables:
     int table - a copy of the member variable m_table
     Vector<Combination> allCombiantion - holds all the possible combinations of runs/books and half-runs/half-books
     Vector<Card> wildCardStack - copy of the member variable m_wildCardStack
     Vector<Combiantion*> helper - A helper function that will be used to store possible combinations.
     Vector<Vector<Combination>> combinationCollection - A combination of combinations of runs/books.
     Vector<Combination> bestCombination - Holds the best possible combination that can be obtained from
     current hand.
     Vector<Card> remCards - Holds the remaining cards after the best combinations are removed from the current
     hand.
     /* Algorithm:
     First it loads all the cards in the hand to the frequency table. It also loads the wildcards in the hand
     to the wildcard stack.
     We obtain all the possible combinations as a Vector of Combination.
     Then, we pass on the Vector of all possible combinations to a recursive function that gives us a list of
     possible independent combinations.
     From there, we take the Vector of combinations that yields us the lowest remaining cards, which is out best
     combination. We also extract the remaining cards and update the values of member variables bestCombination
     and remainingCards.

     /* Assistance Received: None

     ******************************************************************************************************************/
// Interface Function
    public void analyzeHand(Hand playerHand)
    {
        // Load cards to table and wildcard stack
        loadHandsToTable(playerHand);
        loadWildCards(playerHand);

        // Make a copy of the frequency table
        int[][] table= new int[Card.M_NUM_SUITS][Card.M_NUM_RANKS - 2];
        copyTable(m_table, table);


        // Get all Combinations Initially
        Vector<Combination> allCombinations = extractCardObjects(table, m_wildCardStack);
        Vector<Card> wildCardStack = m_wildCardStack;

        Vector<Combination> helper = new Vector<Combination>();
        Vector<Vector<Combination>> combinationCollection = new Vector<Vector<Combination>>();

        getAllCombinations(playerHand, allCombinations, table, wildCardStack, helper, combinationCollection);
        Vector<Combination> bestCombination = getBestCombination(playerHand, combinationCollection);

        int bestRemainingCards = getRemainingCardsNum(playerHand, bestCombination);
        Vector<Card> remCards = getRemainingVector(playerHand, bestCombination);

        // Set Best Combination and Remaining Cards
        setCombination(bestCombination);
        setRemainingCards(remCards);
    }


    /*****************************************************************************************************************
     /* Function Name: getRemainingVector
     /* Purpose: To obtain a list of remaining cards after extracting best combination from the hand
     /* Parameters:
     Hand playerHand - the player's hand that is being analyzed.
     Vector<Combination> bestCombination - the best combination of cards (runs/books) that is possible
     from the player's current hand.
     /* Return Value:
     Vector<Card> remCards - A Vector that contains the remaining cards in hand after setting aside the
     runs/books.
     /* Local Variables:
     Vector<Card> remCards - temporary Vector of cards to hold remaining cards
     Vector<Card> usedCards - temporary Vector of cards to hold cards that have been used in a
     combination.
     /* Algorithm:
     We loop throught the combinations and push all the cards in the combinations to usedCards Vector.
     Then in player's hand, if the card is not in the used card stack, we push it into the remCards Vector.
     At the end, the remCards Vector will contain all the cards that are not in a run/book.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public Vector<Card> getRemainingVector(Hand playerHand, Vector<Combination> bestCombination)
    {
        Vector<Card> remCards = new Vector<Card>();
        Vector<Card> usedCards = new Vector<Card>();
        usedCards.clear();
        // Push all the cards in the combination to a used card stack.
        for (int i = 0; i < bestCombination.size(); i++)
        {
            for (Card card : bestCombination.elementAt(i).getPile())
            {
                usedCards.add(card);
            }
        }

        // For each card in player's hand, if the card is not present in the used card
        // stack, push it into the remaining card stack.
        for (Card card : playerHand.getHand())
        {
            if (!usedCards.contains(card))
            {
                // The card is not present. Push it into the remCards Vector.
                remCards.add(card);
            }
        }

        return remCards;

    }

/*****************************************************************************************************************
 /* Function Name: getBestCombination
 /* Purpose: To generate best combination from a list of combinations
 /* Parameters:
 Hand playerhand - Player's hand that is to be analyzed.
 Vector<Vector<Combination>> combinationCollection - Collection of all the independent possible combinations.
 /* Return Value:
 Vector<Combination> bestCombination - returns the best combination (the combination that will yield in the
 lease number of remaining cards.
 /* Local Variables:
 Vector<Combination< bestCombination - temporary Vector to hold the best combination.
 /* Algorithm:
 We simply iterate through the list of all possible independent combinations, find the one that yields the least
 number of remaining cards and set that as the best combination.
 /* Assistance Received: None

 ******************************************************************************************************************/
    public Vector<Combination> getBestCombination(Hand playerHand, Vector<Vector<Combination>> combinationCollection)
    {
        Vector<Combination> bestCombination = new Vector<Combination>();
        int bestRemainingCards = playerHand.getHand().size();

        for (int i = 0; i < combinationCollection.size(); i++)
        {
            int remCards = getRemainingCardsNum(playerHand, combinationCollection.elementAt(i));
            if (remCards < bestRemainingCards)
            {
                bestRemainingCards = remCards;
                bestCombination = combinationCollection.elementAt(i);
            }
        }

        return bestCombination;
    }


    /*****************************************************************************************************************
     /* Function Name: getRemainingCardsNum
     /* Purpose: To get the number of cards remaining after a particular combination has been set aside from player's hand.
     /* Parameters:
     Hand playerHand - The player's hand that is to be analyzed.
     Vector<Combination> newCombination - The combination that is to be set aside from the player's hand.
     /* Return Value:
     Returns the number of cards remaining in the hand after setting aside runs/books.
     /* Local Variables: None
     /* Algorithm:
     We simply get the number of cards in the combinations and the number of cards in player's hand.
     We take the difference of the two and return it as the number of remaining cards.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public int getRemainingCardsNum(Hand playerHand, Vector<Combination> newCombination)
    {
        int counter = 0;
        if (!newCombination.isEmpty())
        {
            for (Combination combination : newCombination)
            {
                int numCards = combination.getPile().size();
                counter = counter + numCards;
            }
        }
        int cardsInHand = playerHand.getHand().size();

        return cardsInHand - counter;
    }


    /*****************************************************************************************************************
     /* Function Name: getAllCombinations
     /* Purpose: To generate all the possible independent combinations of run/books from a hand.
     /* Parameters:
     Hand playerHand - the player's hand which is to be analyzed.
     Vector<Combination> allCombinations - the list of all possible combinations of runs/books. (not independent)
     table[][] - the frequency table representing the cards in player's hand.
     wildCardStack - the list of wildcards in player's hand.
     Vector<Combination> helper - A helper Vector that stores the sequences of combination from the root to a particular leaf.
     Vector<Vector<Combination>> combinationCollection - A list containing all the possible independent combination of runs/books.
     /* Return Value: None
     /* Local Variables:
     copy_table[][] - Copy of the frequency table
     Vector<Card> copyWildCardStack - Copy of the wildcard stack.

     /* Algorithm:
     From the list of all combination possible, we take each combination and recursively search for all the possible combinations that can
     go with it (no overlaps). When we reach from the root to a leaf, we save the combination to combinationCollection and reset the helper
     function for new sequence of combinations. We do this recursively until all the possible combination are exhausted.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void getAllCombinations(Hand playerHand, Vector<Combination> allCombinations,int table[][], Vector<Card> wildCardStack, Vector<Combination> helper, Vector<Vector<Combination>> combinationCollection)
    {
        // Base case. Return when the combination Vector is empty
        if (allCombinations.isEmpty())
        {
            Vector<Combination> copyHelper = new Vector<Combination>();

            deepCopy(helper,copyHelper);
            combinationCollection.add(copyHelper);
            return;
        }
        else
        {
            for (int i = 0; i < allCombinations.size(); i++)
            {
                int [][] copy_table = new int[Card.M_NUM_SUITS][Card.M_NUM_RANKS - 2];
                copyTable(table, copy_table);
                Vector<Card> copyWildCardStack = wildCardStack;

                // If it is an incomplete run/book (half-run or book)
                // Erase from table and update wildcardState if necessary
                if (allCombinations.elementAt(i).isIncomplete())
                {
                    for (Card card : allCombinations.elementAt(i).getPile())
                    {
                        if (card.isJoker() || card.isWildcard())
                        {
                            if (copyWildCardStack.size() > 0)
                            {
                                copyWildCardStack.remove(copyWildCardStack.size() - 1);
                            }
                        }
                    }
                }
                // Erase the current combination from the table
                eraseFromTable(allCombinations.elementAt(i), copy_table);

                // Save the combination to the helperPile;
                helper.add(allCombinations.elementAt(i));
                Vector<Combination> newCombination = new Vector<Combination>();
                newCombination = extractCardObjects(copy_table, copyWildCardStack);
                getAllCombinations(playerHand, newCombination, copy_table, copyWildCardStack, helper, combinationCollection);
                if (!helper.isEmpty())
                {
                    helper.remove(helper.size() - 1);
                }
            }
        }
    }


    /*****************************************************************************************************************
     /* Function Name: eraseFromTable
     /* Purpose: To remove the card frequency from the table
     /* Parameters:
     Combination combination - the combination of cards (either runs/books) that needs to be erased from
     the table.
     table[][] - The frequency table from where the combination is to be removed.
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm:
     We go to the appropriate index for each card in the combination and decrement it by 1.
     Note: We decrement it by 1 because there is a possibility of having two cards of the same rank and suit.
     /* Assistance Received: Noned

     ******************************************************************************************************************/
    public void eraseFromTable(Combination combination, int[][] table)
    {
        for (Card card : combination.getPile())
        {
            if (!(card.isWildcard() || card.isJoker()))
            {
                int suit = card.getSuit();
                int rank = card.getRank();
                if (table[suit - 1][rank - 3] != 0)
                {
                    table[suit - 1][rank - 3]--;
                }
            }
        }

    }

/*****************************************************************************************************************
 /* Function Name: extractCardObjects
 /* Purpose: To obtain potential runs/books or half-run/half-books from the current hand
 /* Parameters:
 table[][] - The frequency table from where we can extract the combinations.
 wildCardStack - The list of wildcards/jokers that could be used to make a run/book

 /* Return Value:
 Vector<Combination> allCombinations - Returns a Vector containing all the potential runs/books in
 a player's hand.
 /* Local Variables:
 Vector<Combination> runList - A list of all the runs
 Vector<Combination> bookList - A list of all the books
 Vector<Combination> halfRuns - A list of all the half-runs (uses jokers)
 Vector<Combination> halfBooks - A list of all the half-books (uses jokers)
 Vector<Combination> combinations - A composite list containing all of the above
 /* Algorithm:
 We obtain Vectors containing runs, books, half-runs and half-books.
 We concatenate the all these Vectors to one Vector and return that Vector.
 /* Assistance Received: None

 ******************************************************************************************************************/
// Reads runs and books from the table (permits overlaps) and returns a Vector of CardObjects
    public Vector<Combination> extractCardObjects(int[][] table, Vector<Card> wildCardStack)
    {
        Vector<Combination> combinations = new Vector<Combination>();

        // Extract Combinations
        Vector<Combination> runList = new Vector<Combination>();
        runList = extractRuns(table, wildCardStack);
        Vector<Combination> bookList = new Vector<Combination>();
        bookList = extractBooks(table, wildCardStack);
        Vector<Combination> halfRuns = new Vector<Combination>();
        halfRuns = extractHalfRuns(table, wildCardStack);
        Vector<Combination> halfBooks = new Vector<Combination>();
        halfBooks = extractHalfBooks(table, wildCardStack);

        // Concatenate all these combinations into one particular Vector
        combinations.addAll(runList);
        combinations.addAll(bookList);
        combinations.addAll(halfRuns);
        combinations.addAll(halfBooks);

        return combinations;
    }

/*****************************************************************************************************************
 /* Function Name: considerPotentials
 /* Purpose: To obtain the potential runs/books - cards that are not complete runs/books but have the potential to
 be a complete book with one additional card.
 /* Parameters:
 Hand remainingCards - a hand of remainingCards that is to be analyzed for potential runs/books.
 /* Return Value:
 Vector<Combination> potentialCombs - a Vector of potential combination
 /* Local Variables: None
 /* Algorithm:
 We simply load the hand to table, and extract potential combinations using the funtion extractPotentials().
 /* Assistance Received: None

 ******************************************************************************************************************/
   public Vector<Combination> considerPotentials(Hand remainingCards)
    {
        loadHandsToTable(remainingCards);
       Vector<Combination> potentialCombs = new Vector<Combination>();
       potentialCombs = extractPotentials(this.m_table);
        return potentialCombs;
    }

/*****************************************************************************************************************
 /* Function Name: extractPotentials
 /* Purpose:	To return the possible combination that could be a run/book with additional card that matches.
 /* Parameters:
 table[][] - the frequency table representing the cards that are to be considered. s
 /* Return Value: Vector<Combination> potentialCombs - a Vector of potential combination
 /* Local Variables:
 Vector<Combination> potentialRuns - A list of all the potential runs
 Vector<Combination> potentialBooks - A list of all the potential books
 Vector<Combination> potentialCombinations - A combination of both Vectors listed above.
 /* Algorithm:
 We simply extract potential runs and potential books one at a time and concatenate these Vectors into
 a new Vector.
 /* Assistance Received: None

 ******************************************************************************************************************/
    public Vector<Combination> extractPotentials(int[][] table)
    {
        Vector<Combination> potentialCombinations = new Vector<Combination>();
        Vector<Combination> potentialRuns = new Vector<Combination>();
        potentialRuns = extractPotentialRuns(table);
        Vector<Combination> potentialBooks = new Vector<Combination>();
        potentialBooks = extractPotentialBooks(table);

        return potentialCombinations;
    }

/*****************************************************************************************************************
 /* Function Name: extractRuns
 /* Purpose: To obtain runs from a hand of cards
 /* Parameters:
 table[][] - The table from which the runs will be extracted.
 wildCardStack - wildcard stack contining the wildcards in hand.
 /* Return Value:
 Vector<Combination> runList - returns a Vector containing all the possible runs.
 /* Local Variables:
 Vector<Card> tempVector - Temporary Vector to hold cards in potential run.
 Card newCard - A card object that holds the current card.
 Combination newBook - Hold a run that is created and pushed into the runList.
 /* Algorithm:
 We iterate through each suit looking for runs. If there are more than three cards in a sequence in
 a suit, we can call it a run.
 /* Assistance Received: None

 ******************************************************************************************************************/
// Extracts runs from the table
    public Vector<Combination> extractRuns(int[][] table, Vector<Card> wildCardStack)
    {
        Vector<Combination> runList = new Vector<Combination>();
        for (int i = 0; i < Card.M_NUM_SUITS; i++)
        {
            for (int j = 0; j < Card.M_NUM_RANKS - 2; j++)
            {
                int start_index = 0;
                int end_index = 0;

                while (j < Card.M_NUM_RANKS - 2 && table[i][j] == 0 )
                {
                    start_index++;
                    end_index++;
                    j++;
                }

                if (j < Card.M_NUM_RANKS - 2 && m_table[i][j] >= 1)
                {
                    start_index = j;
                    end_index = j;

                    while ((j < (Card.M_NUM_RANKS - 3)) && (table[i][j] >= 1))
                    {
                        end_index++;
                        j++;
                    }

                    if (end_index - start_index >= 3)
                    {
                        Vector<Card> tempVector = new Vector<Card>();
                        int k = start_index + 3;
                        while (k < end_index + 3)
                        {
                            Card newCard = new Card(i + 1, k);
                            k++;
                            tempVector.add(newCard);
                        }
                        Combination newRun = new Run(tempVector);
                        runList.add(newRun);
                    }
                }

            }
        }
        return runList;
    }


/*****************************************************************************************************************
 /* Function Name: extractBooks
 /* Purpose: To obtain books from a hand of cards
 /* Parameters:
 table[][] - The table from which the books will be extracted.
 wildCardStack - wildcard stack containing the wildcards in hand.
 /* Return Value:
 Vector<Combination> bookList - returns a Vector containing all the possible books.
 /* Local Variables:
 Vector<Card> tempVector - Temporary Vector to hold cards in potential book.
 Card newCard - A card object that holds the current card.
 Combination newBook - Hold a book that is created and pushed into the bookList.
 /* Algorithm:
 We iterate through each rank looking for books. If the number of cards in each rank is more than
 three, we can consider it as a book.
 /* Assistance Received: None

 ******************************************************************************************************************/
// Extract books from the table
    public Vector<Combination> extractBooks(int[][] table, Vector<Card> wildCardStack)
    {
        Vector<Combination> bookList = new Vector<Combination>();

        // Iterate through each rank
        for (int i = 0; i < Card.M_NUM_RANKS - 2; i++)
        {
            Vector<Card> tempVector = new Vector<Card>();
            int counter = 0;

            for (int j = 0; j < Card.M_NUM_SUITS; j++)
            {

                // Continue until a card is found
                while (j < Card.M_NUM_SUITS && table[j][i] == 0)
                {
                    j++;
                }

                // If a card is found, check for a double card.
                // Increase the counter
                if (j < Card.M_NUM_SUITS && table[j][i] >= 1)
                {
                    Card newCard = new Card(j + 1, i + 3);
                    if (table[j][i] == 2)
                    {
                        Card doubleCard = new Card(j + 1, i + 3);
                        tempVector.add(doubleCard);
                        counter++;
                    }
                    tempVector.add(newCard);
                    counter++;
                }

            }

            // If there are 3 or more cards of the same rank, we push them as a book.
            if (counter >= 3)
            {
                Combination newBook = new Book(tempVector);
                bookList.add(newBook);
            }
        }

        return bookList;
    }

/*****************************************************************************************************************
 /* Function Name: extractHalfRuns
 /* Purpose: To extract half runs (incomplete runs that can be completed with the addition of joker)
 /* Parameters:
 table[] - frequency table from which the runs are to be extracted.
 Vector<Card> wildCardStack - the stack containing wildcards/jokers.
 /* Return Value:
 Vector<Combination> halfRuns - returns a Vector of combination containing half runs.
 /* Local Variables:
 Vector<Combination> halfRuns - to store the combinations of half runs.
 Vector<Card> copyWildCards - a copy of the wildCardStack.
 Vector<Card> tempVector - to store temporarily store half runs.
 Card newCard - a card that is created.
 Card tempWildCard - a card to hold a wildcard from the wildcard stack.
 Combination newRun - a combination object that stores the half runs.
 /* Algorithm:
 We iterate through each suit looking for half runs. (cards that are incomplete runs but can be made into runs
 using wildcards).
 As long as we have three or more cards, we can include them as halfRuns.

 /* Assistance Received: None

 ******************************************************************************************************************/
    public Vector<Combination> extractHalfRuns(int[][] table, Vector<Card> wildCardStack)
    {
        Vector<Combination> halfRuns = new Vector<Combination>();
        if (!wildCardStack.isEmpty())
        {
            // Iterate through each suit
            for (int i = 0; i < Card.M_NUM_SUITS; i++)
            {
                for (int j = 0; j < Card.M_NUM_RANKS - 2; j++)
                {
                    Vector<Card> copyWildCards = new Vector<Card>();
                    copyWildCards = (Vector<Card>)wildCardStack.clone();
                    int counter = 0;
                    Vector<Integer> indexHolder = new Vector<Integer>();
                    Vector<Card> tempVector = new Vector<Card>();
                    int wildCardCount = copyWildCards.size();

                    // Continue until a card is found

                    while (j < Card.M_NUM_RANKS - 2 && table[i][j] == 0)
                    {
                        j++;
                    }

                    // If a card is found, check if it's adjacent cards are also present
                    while (j < Card.M_NUM_RANKS - 2 && table[i][j] >= 1)
                    {
                        Card newCard = new Card(i + 1, j + 3);
                        tempVector.add(newCard);
                        counter++;
                        j++;


                        // Ignore the 0's and continue until we have wildcards
                        while ((!copyWildCards.isEmpty() && j < Card.M_NUM_RANKS - 2) && table[i][j] == 0 )
                        {
                            Card tempWildCard = copyWildCards.elementAt(copyWildCards.size()-1);
                            wildCardCount--;
                            copyWildCards.remove(copyWildCards.size() - 1);
                            tempVector.add(tempWildCard);
                            counter++;
                            j++;
                        }


                        // If we only have the card in the last rank for a specific suit,
                        // and we have two extra jokers, we need to be able to consider that as
                        // a half run also. So, handle that case.
                        while (j < Card.M_NUM_RANKS - 2 && table[i][j] >= 1 && copyWildCards.size() > 0)
                        {
                            Card tempWildCard = copyWildCards.elementAt(copyWildCards.size() - 1);
                            wildCardCount--;
                            copyWildCards.remove(copyWildCards.size() - 1);
                            tempVector.add(tempWildCard);
                            counter++;
                        }
                    }

                    // If there are more than three cards, create a new half run and push it into halfRuns.
                    if (counter >= 3)
                    {
                        Combination newRun = new HalfRun(tempVector);
                        halfRuns.add(newRun);
                    }

                }
            }
        }

        return halfRuns;

    }

/*****************************************************************************************************************
 /* Function Name: extractHalfBooks
 /* Purpose: To extract half books (incomplete books that can be completed with the addition of joker)
 /* Parameters:
 table[] - frequency table from which the books are to be extracted.
 Vector<Card> wildCardStack - the stack containing wildcards/jokers.
 /* Return Value:
 Vector<Combination> halfBooks - returns a Vector of combination containing half books.
 /* Local Variables:
 Vector<Combination> halfBooks - to store the combinations of half books.
 Vector<Card> copyWildCard - a copy of the wildCardStack.
 Vector<Card> tempVector - to store temporarily store potential half books.
 Card newCard - a card that is created.
 Card doubleCard - a card to store the second card of the same rank and suit.
 Card tempWildCard - a card to hold a wildcard from the wildcard stack.
 Combination newBook - a combination object that stores the half book.
 /* Algorithm:
 We iterate through each rank looking for half books. (cards that are incomplete books but can be made into books
 using wildcards).
 As long as we have three or more cards, we can include them as halfRuns.

 /* Assistance Received: None

 ******************************************************************************************************************/
    public Vector<Combination> extractHalfBooks(int[][] table, Vector<Card> wildCardStack)
    {
        Vector<Combination> halfBooks = new Vector<Combination>();
        if (!wildCardStack.isEmpty())
        {
            // Iterate through each rank
            for (int i = 0; i < Card.M_NUM_RANKS - 2; i++)
            {
                Vector<Card> tempVector = new Vector<Card>();
                Vector<Card> copyWildCard = new Vector<Card>();
                copyWildCard = (Vector<Card>)wildCardStack.clone();
                int counter = 0;
                for (int j = 0; j < Card.M_NUM_SUITS; j++)
                {
                    // Continue until a card is found
                    while (j < Card.M_NUM_SUITS && table[j][i] == 0)
                    {
                        j++;
                    }

                    // If there is a card, check if there is a double.
                    // Continue searching further in the column
                    if (j < Card.M_NUM_SUITS && table[j][i] >= 1)
                    {
                        Card newCard = new Card(j + 1, i + 3);
                        if (table[j][i] == 2)
                        {
                            Card doubleCard = new Card(j + 1, i + 3);
                            tempVector.add(doubleCard);
                            counter++;
                        }
                        tempVector.add(newCard);
                        counter++;
                    }

                }

                // If there are two or more cards of the same rank,
                // We insert the wildcard and set it as a half book
                if (counter >= 2 && (!copyWildCard.isEmpty()))
                {
                    Card tempWildCard = copyWildCard.elementAt(copyWildCard.size() - 1);
                    copyWildCard.remove(copyWildCard.size() - 1);
                    tempVector.add(tempWildCard);
                    Combination newBook = new HalfBook(tempVector);
                    halfBooks.add(newBook);
                }
            }

        }

        return halfBooks;
    }

/*****************************************************************************************************************
 /* Function Name: extractPotentialBooks
 /* Purpose: To extract potential books (a combination of two cards that could be a book)
 /* Parameters: table[] - frequency table from which the books are to be extracted.
 /* Return Value:
 Vector<Combination> halfBooks - returns a Vector of combination containing potential books.
 /* Local Variables:
 Vector<Combination> halfBooks - to store the combinations of potential books.
 Vector<Card> tempVector - to store temporarily store potential potential books.
 Card newCard - a card that is created.
 Card doubleCard - a card to store the second card of the same suit and rank.
 Combination newBook - a combination object that stores the potential books.
 /* Algorithm:
 We iterate through each rank looking for potential books (two cards).
 We just need two cards to make a potential book.
 /* Assistance Received: None
 ******************************************************************************************************************/
    public Vector<Combination> extractPotentialBooks(int[][] table)
    {
        Vector<Combination> halfBooks = new Vector<Combination>();

        // Iterate through each rank
        for (int i = 0; i < Card.M_NUM_RANKS - 2; i++)
        {
            Vector<Card> tempVector = new Vector<Card>();
            int counter = 0;
            for (int j = 0; j < Card.M_NUM_SUITS; j++)
            {
                // Continue until we find a card (i.e. value is 1)
                while (j < Card.M_NUM_SUITS-2 && table[j][i] == 0)
                {
                    j++;
                }

                // If the value is greater than 1, see if there is a double card.
                if (j < Card.M_NUM_SUITS-2 && table[j][i] >= 1)
                {
                    Card newCard = new Card(j + 1, i + 3);
                    if (table[j][i] == 2)
                    {
                        Card doubleCard = new Card(j + 1, i + 3);
                        tempVector.add(doubleCard);
                        counter++;
                    }
                    tempVector.add(newCard);
                    counter++;
                }

            }

            // If there two or more cards, create a new potential book. (two should be fine)
            if (counter >= 2)
            {
                Combination newBook = new HalfBook(tempVector);
                halfBooks.add(newBook);
            }
        }

        return halfBooks;
    }

/*****************************************************************************************************************
 /* Function Name: extractPotentialRuns
 /* Purpose: To extract potential runs (a combination of two cards that could be a run)
 /* Parameters: table[] - frequency table from which the runs are to be extracted.
 /* Return Value:
 Vector<Combination> halfRuns - returns a Vector of combination containing potential runs.
 /* Local Variables:
 Vector<Combination> halfRuns - to store the combinations of potential runs.
 Vector<Card> tempVector - to store temporarily store potential potential runs.
 Card newCard - a card that is created.
 Combination newRun - a combination object that stores the potential runs.
 /* Algorithm:
 We iterate through each suit, trying to find possible runs that have at least two cards in it.
 We consider two cases - 1) two cards in a sequence or 2) two cards with a space in between which be completed
 to make a run.

 /* Assistance Received: None

 ******************************************************************************************************************/
    Vector<Combination> extractPotentialRuns(int[][] table)
    {
        Vector<Combination> halfRuns = new Vector<Combination>();

        // We look through the ranks for each suit, trying to find possible runs that have atleast two cards in it.
        for (int i = 0; i < Card.M_NUM_SUITS; i++)
        {
            for (int j = 0; j < Card.M_NUM_RANKS - 2; j++)
            {
                int counter = 0;
                Vector<Integer> indexHolder = new Vector<Integer>();
                Vector<Card> tempVector = new Vector<Card>();
                int zeroCounter = 1;

                // If the value in the position is 0, we go to the adjacent position.
                while (j < Card.M_NUM_RANKS - 2 && table[i][j] == 0)
                {
                    j++;
                }

                // If the value is 1 (i.e. we have a card), see if the next value is 0 and the value after that is 1.
                // Only if this is true, we consider it a potential run.
                while (j < Card.M_NUM_RANKS - 2 && table[i][j] >= 1)
                {
                    Card newCard = new Card (i + 1, j + 3);
                    tempVector.add(newCard);
                    counter++;
                    j++;

                    // Check the next index
                    if (j < Card.M_NUM_RANKS - 2 && table[i][j] == 0 && zeroCounter != 0)
                    {
                        counter++;
                        zeroCounter--;
                        j++;
                    }
                }

                // If the number of cards is two or greater, consider that as a potential run.
                if (counter >= 2)
                {
                    Combination newRun = new HalfRun(tempVector);
                    halfRuns.add(newRun);
                }

            }
        }

        return halfRuns;

    }

    /*****************************************************************************************************************
     /* Function Name: copyTable
     /* Purpose: To make a copy of the table
     /* Parameters:
     source[][] - the table which is to be copied.
     destination[][] - the table that is being copied into.
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm: Simply iterate through every element in the table and copy it one at a time.
     /* Assistance Received: None

     ******************************************************************************************************************/
// Copy 2D array
    public void copyTable(int[][] source, int[][] destination)
    {
        for (int i = 0; i < Card.M_NUM_SUITS; i++)
        {
            for (int j = 0; j < Card.M_NUM_RANKS - 2; j++)
            {
                destination[i][j] = source[i][j];
            }
        }
        return;
    }

    public static void deepCopy(Vector<Combination> source, Vector<Combination> destination)
    {
        for (int i = 0; i < source.size(); i++)
        {
            Combination copy = (Combination) source.elementAt(i).clone();
            destination.add(copy);
        }
    }


    public static void main(String [] args)
    {
        Card c1 = new Card(1,3);
        Card c2 = new Card (1,4);
        Card c3 = new Card(1,5);
        Card c4 = new Card(6,1);
        Card c5 = new Card(6,2);
        Card c6 = new Card(1,13);
        Card c7 = new Card(1,12);

        Hand newHand = new Hand();
        newHand.addCardToHand(c1);
        newHand.addCardToHand(c2);
        newHand.addCardToHand(c3);
        newHand.addCardToHand(c4);
        newHand.addCardToHand(c5);
        newHand.addCardToHand(c6);
        newHand.addCardToHand(c7);


        HandAnalyzer analyzer = new HandAnalyzer();
        analyzer.analyzeHand(newHand);
        Vector<Combination> best= new Vector<Combination>();
        best = analyzer.getCombination();

        Vector<Card> remaining = new Vector<Card>();
        remaining = analyzer.getRemainingCards();
        for (Combination comb : best)
        {
            comb.printPile();
            System.out.println();
        }
        System.out.println("Remaining Cards: ");
        for (Card card : remaining)
        {
            System.out.print(card.toString());
        }

    }


}
