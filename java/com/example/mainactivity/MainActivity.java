package com.example.mainactivity;

        import androidx.annotation.RequiresApi;
        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.DialogInterface;
        import android.content.Intent;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Environment;
        import android.util.Log;
        import android.view.View;

        import java.io.File;
        import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    // Start new round activity
    public void startNew(View view) {
        final AlertDialog coinToss = new AlertDialog.Builder(this)
                .setTitle("Coin Toss")
                .setMessage("Please choose heads or tails.")
                .setPositiveButton("Head", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        giveIntent(coinToss(1));
                    }
                })
                .setNegativeButton("Tail", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        giveIntent(coinToss(0));
                    }
                }).show();

    }

    // Function that gives intent to start new activity (Used in startNew)
    public void giveIntent(String result)
    {
        Intent intent = new Intent(this, RoundActivity.class);
        intent.putExtra("message", "new");
        intent.putExtra("nextPlayer", result);
        startActivity(intent);
    }


    public void loadGame(View view) {
        final String[] files = getFiles();
        AlertDialog.Builder filePicker = new AlertDialog.Builder(this);
        filePicker.setTitle("Pick a file to load");
        filePicker.setItems(files, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(DialogInterface dialog, int i) {
                String fileName = files[i];
                String filePath = getThisActivity().getFilesDir() + "/" + fileName;

                Intent intent = new Intent(getApplicationContext(),RoundActivity.class);
                intent.putExtra("message", "load");

                // Need to get filepath from AlertDialog
                intent.putExtra("filepath", filePath);
                startActivity(intent);
            }
        });
        filePicker.show();
    }

    public MainActivity getThisActivity(){return  this;}


    // Do the coin toss
    public String coinToss(int choice)
    {
        Random rand = new Random();
        int flip = rand.nextInt() % 2;
        if (choice == flip)
        {
            return "Human";
        }

        return "Computer";
    }

    // Get files from the current environment directory
    public String[] getFiles()
    {
        String[] fileList = null;
        File directory = this.getFilesDir();
        fileList = directory.list();
        return fileList;
    }
}
