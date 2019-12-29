package com.example.mainactivity;

import java.lang.String;
import java.util.*;
import java.io.*;

public class Serializer {

    private Vector<String> m_labels;
    private Vector<String> m_statements;

    

    /*****************************************************************************************************************
     /* Function Name: Serializer
     /* Purpose: Constructor of serializer class
     /* Parameters: None
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm: Initialize the member variables.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public Serializer()
    {
        this.m_labels = new Vector<String>();
        this.m_statements = new Vector<String>();
    }


    /*****************************************************************************************************************
     /* Function Name: saveRound
     /* Purpose: To save the round state into a text file
     /* Parameters:
     Vector<Player> playerList - holds the players playing the game
     int roundNum - holds the current round number
     Vector<Card> drawPile - hold the draw pile
     Vector<Card> discardPile - holds discard pile
     Player nextPlayer - holds next player
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm:
     Ask user for the file name to save in. Create the new save file.
     Using filestream, write all the key round data to the text file.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void saveRound(String filename, Vector<Player> playerList, int roundNum, Vector<Card> drawPile, Vector<Card> discardPile, Player nextPlayer) throws IOException {
        
        FileWriter fw = null;
        try
        {
            fw = new FileWriter(filename);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.out.println("Couldn't read file.");
        }

        assert fw != null;
        fw.write("Round:" + roundNum + "\n");
        for (Player player : playerList)
        {
            fw.write("\n");
            if (player.isHuman())
            {
                fw.write("Human: \n");
            }
            else
            {
                fw.write("Computer: \n");
            }
            fw.write("\tScore: " + player.getScore() + "\n");
            fw.write("\tHand: ");
            for (Card card : player.getHand().getHand())
            {
                fw.write(card.toString() + " ");
            }
            fw.write("\n");
    }

        fw.write("Draw Pile: ");
        for (Card card : drawPile)
        {
            fw.write(card.toString() + " ");
        }
        fw.write("\n");

        fw.write("Discard Pile: ");
        for (Card card : discardPile)
        {
            fw.write(card.toString() + " ");
        }
        fw.write("\n");

        fw.write("Next Player: ");
        if (nextPlayer.isHuman())
        {
            fw.write("Human");
        }
        else
        {
            fw.write("Computer");
        }
        fw.write("\n");

        fw.close();

    }


    /*****************************************************************************************************************
     /* Function Name: recordStatements
     /* Purpose: To record statements in the save file as a Vector
     /* Parameters: None
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm: Use filestream object to record the statements.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public void recordStatements(String filename){
        FileReader fr = null;
        try
        {
            fr = new FileReader(filename);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.out.println("Cannot open file for reading.");
            System.exit(1);
        }

        assert fr != null;
        BufferedReader br = new BufferedReader(fr);
        // Variable Declaration
        String line = null;


        // Read the file, one line at a time
        while (true)
        {
            try {
                if ((line = br.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error reading the lines of the file. Exiting.");
                System.exit(1);
            }
            // Remove leading and trailing whitespaces
            if (line != null) {
                line = line.trim();
            }
            // If the line isn't empty
            assert line != null;
            if (!line.equalsIgnoreCase(""))
            {
                String delim = "[:]";

                String[] tokens = line.split(delim);

                String label = tokens[0];
                String stat = "";
                if (tokens.length > 1) {
                    stat = tokens[1];
                }
                else
                {
                    stat = "";
                }
                label = label.trim();
                stat = stat.trim();

                m_labels.add(label);
                m_statements.add(stat);
            }
        }

        return;
    }

    /*****************************************************************************************************************
     /* Function Name: getRoundNum
     /* Purpose: To obtain the round number from the save file
     /* Parameters: None
     /* Return Value: returns the round number
     /* Local Variables: None
     /* Algorithm:
     Iterate throught the statements Vector and get the corresponding value next to the "Round" String
     Return the value.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public int getRoundNum()
    {
        int roundNum = 0;
        if (this.m_labels.elementAt(0).equalsIgnoreCase("Round"))
        {
            int index = 0;
            String sNum = m_statements.elementAt(index);
            int number = 0;
            number = Integer.parseInt(sNum);
            roundNum = (int)number;
        }
        else
        {
            System.out.println("Invalid format of Load File. Exiting Program!");
            System.exit(1);
        }

        return roundNum;
    }

/*****************************************************************************************************************
 /* Function Name: getPlayerList
 /* Purpose: To obtain playerList from the save file
 /* Parameters: None
 /* Return Value: None
 /* Local Variables:
 Vector<Player> playerList -the Vector that hols the players playing the game
 /* Algorithm:
 We iterate throught the statement Vector to get the element that ends with a ':'. That marks a new player so
 create a new player based on their name and add them to our playerList.
 /* Assistance Received: None

 ******************************************************************************************************************/
    public Vector<Player> getPlayerList()
    {
        Vector<Player> playerList = new Vector<Player>();
        int index = 0;
        int playerNum = 0;

        for(int i = 0; i < m_labels.size(); i++)
        {
            Player player = null;
            if (m_labels.elementAt(i).equalsIgnoreCase("Draw Pile"))
            {
                break;
            }

            if (i != 0)
            {
                index = i;

                if (m_statements.elementAt(index).equalsIgnoreCase(""))
                {
                    int score = 0;
                    String playerName = m_labels.elementAt(index);
                    Hand playerHand = new Hand();
                    playerName = convertToLower(playerName);
                    index++;

                    if (m_labels.elementAt(index).equalsIgnoreCase("Score"))
                    {
                        int scoreVal = Integer.parseInt(m_statements.elementAt(index));
                        score = (int)scoreVal;
                        index++;

                        if (m_labels.elementAt(index).equalsIgnoreCase("Hand"))
                        {
                            String str = m_statements.elementAt(index);
                            Vector<Card> pile = convertToCard(str);
                            playerHand.setHand(pile);
                            playerHand.setHandSize(pile.size());
                        }
                        else
                        {
                            System.out.println("Invalid format of Load File. Exiting Program!");
                            System.exit(1);
                        }
                    }
                    else
                    {
                        System.out.println("Invalid format of Load file. Exiting Program!");
                        System.exit(1);
                    }

                    if (playerName.equalsIgnoreCase("human"))
                    {
                        player = new Human(playerName, playerNum);
                    }
                    else if (playerName.equalsIgnoreCase("computer"))
                    {
                        player = new Computer(playerName, playerNum);
                    }

                    playerNum++;

                    player.setHand(playerHand);
                    player.setScore(score);
                    player.setGoOutFlag(false);
                    playerList.add(player);
                }
            }

        }

        if (playerList.elementAt(0).getName().equalsIgnoreCase("computer"))
        {
            Collections.swap(playerList,0,1);
        }
        return playerList;

    }

    /*****************************************************************************************************************
     /* Function Name: getDrawPile
     /* Purpose: To read the drawpile from the save file
     /* Parameters: None
     /* Return Value:
     Vector<Card> copy - reversed copy of the card pile that is read.
     /* Local Variables:
     Vector<Card> cardPile - that holds the card pile read from the stirng
     /* Algorithm:
     We iterate to the statement with "draw pile:" and read the rest of the String delimeted by space one at
     a time.

     /* Assistance Received: None

     ******************************************************************************************************************/
    public Vector<Card> getDrawPile()
    {
        Vector<Card> cardPile = null;

        for (int i = 0; i < m_labels.size(); i++)
        {
            if (m_labels.elementAt(i).equalsIgnoreCase("Draw Pile"))
            {
                cardPile = convertToCard(m_statements.elementAt(i));
                break;
            }
        }

        return cardPile;
    }

    /*****************************************************************************************************************
     /* Function Name: getDiscardPile
     /* Purpose: To read the discardpile from the save file
     /* Parameters: None
     /* Return Value:
     Vector<Card> copy - reversed copy of the card pile that is read.
     /* Local Variables:
     Vector<Card> cardPile - that holds the card pile read from the stirng
     /* Algorithm:
     We iterate to the statement with "discard pile:" and read the rest of the String delimeted by space one at
     a time.

     /* Assistance Received: None

     ******************************************************************************************************************/
    public Vector<Card> getDiscardPile()
    {
        Vector<Card> cardPile = null;
        for (int i = 0; i < m_labels.size(); i++)
        {
            if (m_labels.elementAt(i).equalsIgnoreCase("Discard Pile"))
            {
                cardPile = convertToCard(m_statements.elementAt(i));
                break;
            }
        }

        return cardPile;
    }

/*****************************************************************************************************************
 /* Function Name: getNextPlayer
 /* Purpose: To read the next player who will play the turn from the save file
 /* Parameters: Vector<Player> playerList
 /* Return Value:
 Player nextPlayer - the player who will play the next turn
 /* Local Variables:
 Player nextPlayer - will point to the next player
 /* Algorithm: Read the name next to the "Next Player:" label in the save file and set that player as the next player.
 /* Assistance Received: None

 ******************************************************************************************************************/
    public Player getNextPlayer(Vector<Player> playerList)
    {
        Player nextPlayer = null;

        for (int i = 0; i < m_labels.size(); i++)
        {
            if (m_labels.elementAt(i).equalsIgnoreCase("Next Player"))
            {
                String playerName = m_statements.elementAt(i);
                playerName = playerName.trim();
                playerName = convertToLower(playerName);

                for (int j = 0; j < playerList.size(); j++)
                {
                    if (playerList.elementAt(j).getName().equalsIgnoreCase(playerName))
                    {
                        nextPlayer = playerList.elementAt(j);
                        break;
                    }
                }
                break;
            }
        }

        return nextPlayer;
    }


    /*****************************************************************************************************************
     /* Function Name: convertToCard
     /* Purpose: To convert a cardString to card
     /* Parameters: String pileString - the String representation of the pile
     /* Return Value: Vector<Card> cardPile - a Vector of cards created based on the String
     /* Local Variables:
     Vector<Card> cardPile - that holds the cards read from the String
     /* Algorithm:
     We use the convertToCardString function to get the Vector of String representing each card and then
     create a card object from each element in the Vector.

     /* Assistance Received: None

     ******************************************************************************************************************/
    public Vector<Card> convertToCard(String pileString)
    {
        Vector<String> cardString = convertToCardString(pileString);
        return getCardVector(cardString);
    }

    /*****************************************************************************************************************
     /* Function Name: getCardVector
     /* Purpose: To get the Vector of cards from a Vector of card Strings.
     /* Parameters: Vector<String> cardStringPile - holds all the cardStrings of cards in a pile
     /* Return Value:
     Vector<Card> cardPile - the card pile that is created from respective cardString in the Vector of
     Strings.
     /* Local Variables: Vector<Card> cardPile - holds the cards created from cardStrings
     /* Algorithm: Simply create a new card from each cardString in the Vector of Strings and push it into the Vector of
     cards.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public Vector<Card> getCardVector(Vector<String> cardStringPile)
    {
        Vector<Card> cardPile = new Vector<Card>();
        for (int i = 0; i < cardStringPile.size(); i++)
        {
            String temp = cardStringPile.elementAt(i).trim();
            Card newCard = new Card(temp);
            cardPile.add(newCard);
        }
        return cardPile;
    }

    /*****************************************************************************************************************
     /* Function Name: convertToCardString
     /* Purpose: To convert a String of multiple cardStrings to multiple Strings representing each card.
     /* Parameters: String pileString - the String of multiple cardStrings
     /* Return Value: Vector<String> cardString - a Vector that contains String representation of all the cards in the
     pile.
     /* Local Variables: None
     /* Algorithm: We delimit the cardString with the delimiter of space character and store each of them into a Vector.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public Vector<String> convertToCardString(String pileString)
    {
        Vector<String> cardString = new Vector<String>();
        pileString = pileString.trim();
        String delim = "[ ]";
        String [] tokens = pileString.split(delim);
        for (int i = 0; i < tokens.length; i++)
        {
            cardString.add(tokens[i]);
        }

        return cardString;
    }

    /*****************************************************************************************************************
     /* Function Name: convertToLower
     /* Purpose: To convert String to lowercase
     /* Parameters:
     String str - the String to convert
     /* Return Value: None
     /* Local Variables: None
     /* Algorithm: Uses the transform function to convert each character in the String to lower case.
     /* Assistance Received: None

     ******************************************************************************************************************/
    public String convertToLower(String str)
    {

        return str.toLowerCase();
    }

}
