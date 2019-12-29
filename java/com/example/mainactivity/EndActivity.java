package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {

    private int humanScore;
    private int compScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        humanScore = getIntent().getExtras().getInt("Human Score");
        compScore = getIntent().getExtras().getInt("Computer Score");
        updateDisplay();
    }


    public void updateDisplay()
    {
        TextView playerScoreView = findViewById(R.id.human_score);
        TextView compScoreView = findViewById(R.id.comp_score);
        TextView winnerView = findViewById(R.id.winner);
        playerScoreView.setText("Human Score: " + humanScore);
        compScoreView.setText("Computer Score: " + compScore);
        winnerView.setText(getWinner());
    }

    public String getWinner()
    {
        if (this.humanScore < this.compScore)
        {
            return "Human Wins!!!";
        }
        else if (this.compScore < this.humanScore)
        {
            return "Computer Wins!!!";
        }
        else
        {
            return "It's a tie !!!";
        }
    }
}
