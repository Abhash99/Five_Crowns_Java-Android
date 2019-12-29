package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Random;
import java.util.Vector;

import static android.icu.lang.UCharacter.toLowerCase;

public class RoundActivity extends AppCompatActivity {

    private Round round = null;
    private boolean pickedState  = false;
    private int currentRoundNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);

        // Loads a NEW GAME state: Round = 1
        if (getIntent().getExtras().getString("message").equals("new"))
        {
            // Create a new round object
            round = new Round();

            // Round number is 1 because it's the first round
            round.setRoundNum(1);

            // Get next player based on results from coin toss
            if (getIntent().getExtras().getString("nextPlayer").equals("Human"))
            {
                round.setNextPlayer(round.getPlayerList().elementAt(0));
            }
            else
            {
                round.setNextPlayer(round.getPlayerList().elementAt(1));
            }
            // Initialize Components of the Round
            round.initializeRound();

        }
        // Loads LOAD GAME state (Using serializer)
        else
        {
            String filename = getIntent().getExtras().getString("filepath");
            Serializer loadClient = new Serializer();
            loadClient.recordStatements(filename);
            Vector<Player> playerList = loadClient.getPlayerList();
            int round_num = loadClient.getRoundNum();
            int numPlayers = playerList.size();
            Vector<Card> drawPile = loadClient.getDrawPile();
            Vector<Card> discardPile = loadClient.getDiscardPile();
            Player nextPlayer = loadClient.getNextPlayer(playerList);
            round = new Round(playerList,round_num,numPlayers,drawPile,discardPile,nextPlayer);
        }
        currentRoundNum = round.getRoundNum();
        updateDisplay();
    }


    // Update Display
    public void updateDisplay()
    {
        if (round.getRoundNum() > Round.MAX_ROUND_NUM)
        {
            endGame();
            return;
        }
       // if (currentRoundNum != round.getRoundNum()) {
            // AlertDialog dialog = new AlertDialog.Builder(this).create();
            // dialog.setTitle("Round Ended");
            // dialog.setMessage("Human Score: " + round.getPlayerList().elementAt(0).getRoundScore() + "\n Computer Score: " + round.getPlayerList().elementAt(1).getRoundScore());
            // dialog.show();
        // }

        updateDrawPile();
        updateDiscardPile();
        updateComputerHand();
        updateHumanHand();
        updateRoundDetails();
        updatePlayerScore();
    }

    // Update Player Score
    private void updatePlayerScore()
    {
        TextView compScore = findViewById(R.id.comp_score);
        TextView playerScore = findViewById(R.id.player_score);
        compScore.setText("Score: " + round.getPlayerList().elementAt(1).getScore());
        playerScore.setText("Score: " + round.getPlayerList().elementAt(0).getScore());
    }

    // Update Draw Pile
    public void updateDrawPile()
    {
        Vector<Card> drawPile = round.getDrawPile();
        LinearLayout layout = findViewById(R.id.draw_cards);
        updateCollection(drawPile, layout);
    }

    // Update Discard Pile
    public void updateDiscardPile()
    {
        Vector<Card> discardPile = round.getDiscardPile();
        LinearLayout layout = findViewById(R.id.discard_cards);
        updateCollection(discardPile, layout);

    }
    // Update Player Hands
    public void updateHumanHand()
    {
        Vector<Card> collection = round.getPlayerList().elementAt(0).getHand().getHand();
        LinearLayout layout = findViewById(R.id.player_cards);
        updateCollection(collection, layout);
    }

    // Update Computer Hands
    public void updateComputerHand()
    {
        Vector<Card> collection = round.getPlayerList().elementAt(1).getHand().getHand();
        LinearLayout layout = findViewById(R.id.comp_cards);
        updateCollection(collection, layout);
    }

    public void updateRoundDetails()
    {
        TextView roundNum = findViewById(R.id.round_num);
        TextView nextPlayer = findViewById(R.id.next_player);
        TextView nextMove = findViewById(R.id.next_move);
        roundNum.setText("Round Number: " + round.getRoundNum());

        if (round.getPlayerList().elementAt(1).wentOut() == true) {
            nextPlayer.setText("Computer has gone out.");
            Toast.makeText(getBaseContext(),"Computer Went Out. Please make your last moves for this round.", Toast.LENGTH_LONG).show();
        }
        else
        {
            nextPlayer.setText("No player has gone out.");
        }

        if (pickedState == false)
        {
            nextMove.setText("Next Move: Pick a card (Draw / Discard).");
        }
        else
        {
            nextMove.setText("Next Move: Drop a card from hand.");
        }
    }

    // Update Card Collection
    public void updateCollection(Vector<Card> collection, LinearLayout layout)
    {
        // Clear the layout functions.
        layout.removeAllViews();


        // Obtain the corresponding image for each card in collection.
        for (Card card : collection)
        {

            int id = getApplicationContext().getResources().getIdentifier(getCardPath(card), "drawable", getApplicationContext().getPackageName());

            int height = 250;
            int width = 150;
            Bitmap bmp;
            bmp = BitmapFactory.decodeResource(getResources(), id);
            bmp = Bitmap.createScaledBitmap(bmp, width, height, true);
            ImageButton imageButton = new ImageButton(this);
            imageButton.setImageBitmap(bmp);
            imageButton.setBackgroundColor(Color.TRANSPARENT);
            imageButton.setPadding(30,0,30,0);
            imageButton.setTag(card.toString());
            if (layout.getId() == R.id.discard_cards || layout.getId() == R.id.draw_cards)
            {
                if (collection.firstElement().equals(card))
                {
                    attachListener(imageButton);
                }
            }
            else
            {
                attachListener(imageButton);
            }
            //ImageButton button = new ImageButton(this);
            //button.setImageResource(id);
            //button.setLayoutParams(new LinearLayout.LayoutParams(250,250));
            layout.addView(imageButton);
        }
    }


    // Get card path
    public String getCardPath(Card card)
    {
        if (card.getSuit() == 6)
        {
            return toLowerCase(card.toString());
        }

        String cardString = card.toString();
        StringBuilder builder = new StringBuilder();

        builder.append(cardString);
        builder = builder.reverse();

        return toLowerCase(builder.toString());
    }

    public RoundActivity getThisActivity(){return  this;}

    // Save Game
    public void saveButton(View view)
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Please enter the name for your save file: ");

        final EditText filename = new EditText(this);
        builder.setView(filename);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener(){


            @Override
            public void onClick(DialogInterface dialog, int which) {
                String file = filename.getText().toString() + ".txt";
                String path = getThisActivity().getFilesDir()+ File.separator + file;

                // Pass the location(path) for the save file
                round.saveGame(path);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void endGame()
    {
        Intent intent = new Intent(this, EndActivity.class);
        intent.putExtra("Human Score", round.getPlayerList().elementAt(0).getScore());
        intent.putExtra("Computer Score", round.getPlayerList().elementAt(1).getScore());

        startActivity(intent);
    }

    private void attachListener(ImageButton imageButton)
    {
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                LinearLayout layout = (LinearLayout) v.getParent();
                int id = layout.getId();
                if (id == R.id.draw_cards)
                {
                    if (!pickedState)
                    {
                        round.getPlayerList().elementAt(0).pickFromDraw(round.getDrawPile());
                        pickedState = true;
                        updateDisplay();
                    }
                }

                if (id == R.id.discard_cards)
                {
                    if (!pickedState)
                    {
                        round.humanPickFromDiscard();
                        pickedState = true;
                        updateDisplay();

                    }
                }
                if (id == R.id.player_cards)
                {
                    if (pickedState)
                    {
                        String tag = (String) v.getTag();
                        round.humanDropCard(tag);
                        pickedState = false;
                        updateDisplay();
                    }
                }
            }
        });
    }


    public void helpButton(View view)
    {
        if (!pickedState)
        {
            Toast.makeText(getBaseContext(), round.getPlayerList().elementAt(0).helpPick(round.getDiscardPile()), Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getBaseContext(), round.getPlayerList().elementAt(0).helpDrop(), Toast.LENGTH_LONG).show();
        }
    }

}
