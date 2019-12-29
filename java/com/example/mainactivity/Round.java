package com.example.mainactivity;

import java.io.IOException;
import java.lang.String;
import java.util.*;

//===================================================
// To do: Handle save game/Round loop
//====================================================
public class Round {

    // Symbolic Constants
    static final int MAX_ROUND_NUM = 11;

    private Vector<Player> m_playerList;
    private int m_round_num;
    private Vector<Card> m_drawPile;
    private Vector<Card> m_discardPile;
    private Player m_nextPlayer;
    private int m_numPlayers;



    // Default Constructor
    Round()
    {
        this.m_playerList = new Vector<Player>();
        // Load default players
        Player human = new Human("Human",0);
        this.m_playerList.add(human);
        Player computer = new Computer ("Computer", 1);
        this.m_playerList.add(computer);
        this.m_round_num = 0;
        this.m_drawPile = new Vector<Card>();
        this.m_discardPile = new Vector<Card>();
        this.m_nextPlayer = null;
        this.m_numPlayers = 2;
    }


    // Parametrized Constructors
    Round(Vector<Player> playerList, int round_num, int numPlayers)
    {
        this.m_playerList = playerList;
        this.m_round_num = round_num;
        this.m_numPlayers = numPlayers;
        this.m_drawPile = new Vector<Card>();
        this.m_discardPile = new Vector<Card>();
        this.m_nextPlayer = playerList.elementAt(0);
        this.initializeRound();
    }

    Round(Vector<Player> playerList, int round_num, int numPlayers, Vector<Card> drawPile, Vector<Card> discardPile, Player nextPlayer)
    {
        this.m_playerList = playerList;
        this.m_round_num = round_num;
        this.m_numPlayers = numPlayers;
        this.m_drawPile = drawPile;
        this.m_discardPile = discardPile;
        this.m_nextPlayer = nextPlayer;
    }



    //=================================================================================================================
    // Accessors
    public Vector<Player> getPlayerList()
    {
        return m_playerList;
    }

    public int getRoundNum()
    {
        return m_round_num;
    }

    public Vector<Card> getDrawPile()
    {
        return m_drawPile;
    }

    public Vector<Card> getDiscardPile()
    {
        return m_discardPile;
    }

    public Player getNextPlayer()
    {
        return m_nextPlayer;
    }

    public int getNumPlayers()
    {
        return m_numPlayers;
    }

    //================================================================================
    // Mutators
    public void setPlayerList(Vector<Player> playerList)
    {
        m_playerList = playerList;
    }

    public void setRoundNum(int round_num)
    {
        m_round_num = round_num;
    }

    public void setDrawPile(Vector<Card> drawPile)
    {
        m_drawPile = drawPile;
    }

    public void setDiscardPile(Vector<Card> discardPile)
    {
        m_discardPile = discardPile;
    }

    public void setNextPlayer(Player nextPlayer)
    {
        m_nextPlayer = nextPlayer;
    }

    public void setNumPlayers(int numPlayers)
    {
        m_numPlayers = numPlayers;
    }




    //InitializeRound
    public void initializeRound()
    {
        initializeDrawPile();
        clearPlayerHands();
        dealCards();
        addTodiscardPile(pickFromDrawPile());
    }

    // Load Game State
    public void startLoad(String filename)
    {

        Serializer loadClient = new Serializer();
        loadClient.recordStatements(filename);
        this.m_playerList = loadClient.getPlayerList();
        this.m_round_num= loadClient.getRoundNum();
        this.m_drawPile = loadClient.getDrawPile();
        this.m_discardPile = loadClient.getDiscardPile();
        this.m_nextPlayer= loadClient.getNextPlayer(this.m_playerList);
        this.m_numPlayers = this.m_playerList.size();
    }


    public void humanPickFromDiscard()
    {
        getPlayerList().elementAt(0).pickFromDiscard(getDiscardPile());
    }

    public void humanDropCard(String cardString)
    {
      getPlayerList().elementAt(0).dropCard(getDiscardPile(),cardString);
      if (getPlayerList().elementAt(1).wentOut())
      {
          endRound();
          return;
      }

      if (getPlayerList().elementAt(0).wentOut())
      {
          computerLastTurn();
          endRound();
      }
      else
      {
          computerPlay();
      }
    }

    public void computerPlay()
    {
        getPlayerList().elementAt(1).play(getDrawPile(), getDiscardPile());
    }

    public void computerLastTurn()
    {
        getPlayerList().elementAt(1).lastTurn(getDrawPile(), getDiscardPile());
    }



    /*****************************************************************************************************************
     /* Function Name: endRound
     /* Purpose: To end the round
     /* Parameters: None
     /* Return Value: None
     /* Local Variables:
     /* Algorithm:
     We increment the round number after each round ends.
     We clear the draw and discard piles. 
     We clear player's hands. 
     We initialize drawPile and deal cards. 
     Finally we add the top of draw pile to discard pile. 
     Then the round is started again. 
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void endRound()
    {
        // Increase the round number
        this.m_round_num++;

        // Clear drawpile and discard pile
        m_drawPile.clear();
        m_discardPile.clear();

        // Clear playerHands
        clearPlayerHands();

        // Initialize drawPile
        initializeDrawPile();

        // Deal Cards
        dealCards();

        // Set one card for discardPile
        addTodiscardPile(pickFromDrawPile());

    }

    /*****************************************************************************************************************
     /* Function Name: initializeDrawPile
     /* Purpose: To initialize the draw pile after each round.
     /* Parameters: None
     /* Return Value: None
     /* Local Variables:
     Deck d1, d2 - Two deck objects that are created and mixed to form the drawpile. 
     Vector<Card> temp1 - stores cards of deck1
     Vector<Card> temp2 - stores cards of deck2
     Vector<Card> newPile - a temporary Vector of cards that holds both the decks, essentially the drawPile
     /* Algorithm:
     We create two deck objects and initialize them. We shuffle both the decks and concatenate both of them into
     a Vector of cards, which is our draw pile. 
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void initializeDrawPile()
    {
        Deck d1 = new Deck();
        Deck d2 = new Deck();
        d1.initializeDeck();
        d2.initializeDeck();
        //Make wildcard here
        d1.shuffle();
        d2.shuffle();
        Vector<Card> temp1 = d1.getDeck();
        Vector<Card> temp2 = d2.getDeck();
        Vector<Card> newPile = new Vector<Card>();
        newPile.addAll(temp1);
        newPile.addAll(temp2);
        m_drawPile = newPile;
        makeWildcard();
    }

    /*****************************************************************************************************************
     /* Function Name: saveGame
     /* Purpose: To save the round state
     /* Parameters: None
     /* Return Value: None
     /* Local Variables: 
     Serializer saveClient - a serializer object that saves the round information into a text file. 
     /* Algorithm: 
     We use the serialzier object to save the round state into a text file. 
     /* Assistance Received: None

     ******************************************************************************************************************/
    void saveGame(String filename)
    {
        Serializer saveClient = new Serializer();

        try {
            saveClient.saveRound(filename, m_playerList, m_round_num, m_drawPile, m_discardPile, m_nextPlayer);
        } catch (IOException e) {
            e.printStackTrace();
            //System.out.println("Cannot write to save file. Exiting Program!");
            //System.exit(1);
        }
        //System.exit(0);

    }

    /*****************************************************************************************************************
     /* Function Name: makeWildcard
     /* Purpose: To set the wildcards based on the round number
     /* Parameters: None
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm:
     For each card in the drawPile, if the card's rank equals the current round number + 2, set the card 
     as wildcard. 
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void makeWildcard()
    {
        for (int i = 0; i < m_drawPile.size(); i++)
        {
            // Need to exclude jokers from it. 
            if ((m_drawPile.elementAt(i).getRank() == (m_round_num + 2)) && !m_drawPile.elementAt(i).isJoker())
            {
                m_drawPile.elementAt(i).setWildcard(true);
                m_drawPile.elementAt(i).setValue(20);
            }
        }
    }

    /*****************************************************************************************************************
     /* Function Name: clearPlayerHands
     /* Purpose: To clear hands of the all players playing the game
     /* Parameters: None
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm: Simply clear the hands of each player iteratively.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void clearPlayerHands()
    {
        for (Player player : m_playerList)
        {
            player.clearHand();
        }
    }


    /*****************************************************************************************************************
     /* Function Name: dealCards
     /* Purpose: To deal cards to each player based on the round number
     /* Parameters: None
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm:
     For each player in playerList, add card to player's hand based on the round number. 
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void dealCards()
    {
        for (int i = 0; i < m_round_num + 2; i++)
        {
            for (Player player : m_playerList)
            {
                player.addCard(pickFromDrawPile());
            }
        }
    }


    /*****************************************************************************************************************
     /* Function Name: addTodiscardPile
     /* Purpose: To add a particular card to discard pile
     /* Parameters: 
     Card c - The card that is to be added to the discard pile
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm: Simply add the card c to the discard pile using push_back function of Vector. 
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void addTodiscardPile(Card c)
    {
        m_discardPile.insertElementAt(c,0);
    }

    /*****************************************************************************************************************
     /* Function Name: pickFromDrawPile
     /* Purpose: To pick the top card from draw pile
     /* Parameters: None
     /* Return Value: Card topCard - the top card of draw pile
     /* Local Variables:
     Card topCard - this card will hold the copy of the top card of the draw pile.
     /* Algorithm: 
     Simply save the copy of the top of draw pile in a card variable, pop back the top of the pile and
     return the saved topCard.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public Card pickFromDrawPile()
    {
        Card topCard = m_drawPile.elementAt(0);
        m_drawPile.remove(0);
        return topCard;
    }

}
