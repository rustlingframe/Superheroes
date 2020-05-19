package edu.miracosta.cs134.aespinoza.superheroes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.miracosta.cs134.aespinoza.superheroes.model.JSONLoader;
import edu.miracosta.cs134.aespinoza.superheroes.model.Superhero;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Superheros Quiz";
    public static final int SUPERHEROES_IN_QUIZ = 10;
    public static final String PREF_QUIZ_TYPE ="prefQuizType";

    //keeps track of the defaults for nurrenumber of choices and quiz type
    private int mChoices =4;
   private String mQuizType = "Superhero Name";

    //Create List of SuperHero objects
    private Button[] mButtons = new Button[4];//Button that keeps track of number of answer nuttons on the screen
    private List<Superhero> mAllSuperheroesList; //all the superheros loaded from the JSON
    private List<Superhero> mQuizSuperheroesList;
    private Superhero mCorrectSuperhero;
    private String mCorrectSuperHeroAnswer ="";
    private int mTotalGuesses;   //number of total guesses made
    private int mCorrectGuesses; //number of correct guesses made

    private SecureRandom rng;      //used for quiz randomization
    private Handler handler;         //used to delay loading hero

    private TextView mQuestionNumberTextView;
    private ImageView mSuperHeroImageView;
    private TextView mAnswerTextView;
    private TextView mGuessSuperHeroTextView;

     // Create MediaPlayer objects to handle the correct and incorrect answer sounds
        private MediaPlayer mediaPlayerCorrectAnswer;
        private MediaPlayer mediaPlayerIncorrectAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mQuizSuperheroesList = new ArrayList<>(SUPERHEROES_IN_QUIZ);
        rng = new SecureRandom();
        handler = new Handler();

        //link model to view
        mQuestionNumberTextView = findViewById(R.id.questionNumberTextView);
        mSuperHeroImageView = findViewById(R.id.superheroImageView);
        mAnswerTextView = findViewById(R.id.answerTextView);
        mGuessSuperHeroTextView = findViewById(R.id.guessSuperHeroTextView);

        mButtons[0] = findViewById(R.id.button);
        mButtons[1] = findViewById(R.id.button2);
        mButtons[2] = findViewById(R.id.button3);
        mButtons[3] = findViewById(R.id.button4);

        //Set mQuestionNumberTextView to string derived from string xml values
        mQuestionNumberTextView.setText(getString(R.string.question,1,SUPERHEROES_IN_QUIZ));

        //load all superheros from the JSON file using the JSONLoader
        try{
              mAllSuperheroesList = JSONLoader.loadJSONFromAsset(this);
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error loading from JSON", e);
        }

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);

        //get raw asset file set up and link up with correct and incorrect answer mp3 sounds

        //reset quiz so that it resets each time onCreate method is rrun
        resetQuiz();
    }

     /**
     * Sets up and starts a new quiz.
     */
     private void resetQuiz()
     {
         //reset the correct number of guesses variables and total number of guesses
         //resets the mQuizSuperheroesList
         mCorrectGuesses = 0;
         mTotalGuesses = 0;
         mQuizSuperheroesList.clear();

         //randomly adds the random Superhero to the mQuizSuperheroeslist by checking if the random hero is already in the list
         Superhero random;
         while (mQuizSuperheroesList.size() < SUPERHEROES_IN_QUIZ)
         {
             random = mAllSuperheroesList.get(rng.nextInt(mAllSuperheroesList.size()));  //creates random superhero by grabbing from mAllSuperhero list 
             if(!mQuizSuperheroesList.contains(random))
             {
                 mQuizSuperheroesList.add(random);
             }
         }

         switch(mQuizType)
         {
             case "Superhero Name":
                 mGuessSuperHeroTextView.setText("Guess the Superhero");
                 break;
             case "Superpower":
                 mGuessSuperHeroTextView.setText("Guess the Superpower");
                 break;
             case "One Thing":
                 mGuessSuperHeroTextView.setText("Guess the One Thing");
                 break;
         }
         //loads the next superhero of the quiz and start the quiz by starting the load for the next quiz
         loadNextSuperhero();
     }

     private void loadNextSuperhero() {
         //load the correct Superhero into this question by getting it from position
         // 0 in the mQuizSuperheroesList
         mCorrectSuperhero = mQuizSuperheroesList.remove(0);

         switch (mQuizType) {
             case "Superhero Name":
                 mCorrectSuperHeroAnswer = mCorrectSuperhero.getmName();
                 break;
             case "Superpower":
                 mCorrectSuperHeroAnswer = mCorrectSuperhero.getmSuperpower();
                 break;
             case "One Thing":
                 mCorrectSuperHeroAnswer = mCorrectSuperhero.getmOneThing();
                 break;
         }

         //set the mAnswerText view to "" so it does not retain the information from last question
         mAnswerTextView.setText("");
         mQuestionNumberTextView.setText(getString(R.string.question,SUPERHEROES_IN_QUIZ - mQuizSuperheroesList.size(), SUPERHEROES_IN_QUIZ));
         AssetManager am = getAssets();

         try {
             InputStream stream = am.open(mCorrectSuperhero.getmFileName());
             Drawable image = Drawable.createFromStream(stream,mCorrectSuperhero.getmName());
             mSuperHeroImageView.setImageDrawable(image);
         }
         catch(IOException e)
         {
             Log.e(TAG,"ERROR LOADING IMAGE FROM FILE:"+mCorrectSuperhero.getmFileName(),e);

         }

         Collections.shuffle(mAllSuperheroesList);
         updateBasedOnQuizType();

     }

    public void makeGuess(View v) {

        // Downcast the View v into a Button (since it's one of the buttons)
        Button clickedButton = (Button) v;

        // Get the superhero's name from the text of the button
        String guess = clickedButton.getText().toString();

        // increment the total number of guesses
        mTotalGuesses++;

        // If the guess matches the correct superhero's name, increment the number of correct
        // guesses, then display correct answer in green text. Also, disable all buttons (can't keep
        // guessing once it's correct)
        if (guess.equals(mCorrectSuperHeroAnswer)) {
            // Increment the number of correct guesses
            mCorrectGuesses++;

            // Disable all buttons (can't keep guessing once it's correct)
            for (int i = 0; i < mChoices; i++) {
                mButtons[i].setEnabled(false);
            }

            // Display correct answer in green text
            mAnswerTextView.setTextColor(getResources().getColor(R.color.correct_answer));
            mAnswerTextView.setText(mCorrectSuperhero.getName());

            // Play the mediaPlayerCorrectAnswer sound

            if (mCorrectGuesses < SUPERHEROES_IN_QUIZ) {
                // Code a delay (2000ms = 2 seconds) using a handler to load the next flag
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextSuperhero();
                    }
                }, 2000);
            }

            // Nested in this decision, if the user has completed all 10 questions, show an
            // AlertDialog with the statistics and an option to Reset Quiz
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(
                        R.string.results,
                        mTotalGuesses,
                        (double) mCorrectGuesses / mTotalGuesses * 100));

                // Set positive button of the dialog
                // positive button = reset quiz
                builder.setPositiveButton(
                        getString(R.string.reset_quiz),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                resetQuiz();
                            }
                        });

                // To prevent user from getting stuck!
                builder.setCancelable(false);
                builder.create();
                builder.show();
            }
        }

        // Else, the answer is incorrect, so display "Incorrect Guess!" in red and disable just the
        // incorrect button.
        else
        {
            // Set the clicked button to disabled
            clickedButton.setEnabled(false);

            // Set the incorrect text and text color in the TextView
            mAnswerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer));
            mAnswerTextView.setText(getString(R.string.incorrect_answer));

            // Play the mediaPlayerIncorrectAnswer sound
        }
    }

    /**
     * Changes the button text between Name, Superpower, and One Thing based on the quiz type
     * selected.
     */
    private void updateBasedOnQuizType() {
        // Loop through all buttons, enable them all and set them to the first mChoices superheroes
        // in the mAllSuperheroesList
        for (int i = 0; i < mChoices; i++) {
            mButtons[i].setEnabled(true);

            // Make sure not to pull mCorrectSuperhero info
            Superhero randomSuperhero = mAllSuperheroesList.get(i);
            if (mAllSuperheroesList.get(i).equals(mCorrectSuperhero)) {
                randomSuperhero = mAllSuperheroesList.get(i + 1);
            }

            // Get random superhero's info based on the quiz type
            switch(mQuizType) {
                case "Superhero Name":
                    mButtons[i].setText(randomSuperhero.getName());
                    break;

                case "Superpower":
                    mButtons[i].setText(randomSuperhero.getmSuperpower());
                    break;

                case "One Thing":
                    mButtons[i].setText(randomSuperhero
                            .getmOneThing()
                            .replaceAll("_", " "));
                    break;
            }
        }

        // After the loop, randomly replace one of the buttons with the name of the correct
        // superhero
        mButtons[rng.nextInt(mChoices)].setText(mCorrectSuperHeroAnswer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_settings,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull  MenuItem item){

        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
        return super.onOptionsItemSelected(item);


    }
    SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    switch (key)
                    {
                        case PREF_QUIZ_TYPE:
                            mQuizType = sharedPreferences.getString(PREF_QUIZ_TYPE,mQuizType);
                            updateBasedOnQuizType();
                            break;
                    }
                    resetQuiz();
                    Toast.makeText(MainActivity.this,"QUIZ will restart with your new settings",Toast.LENGTH_SHORT).show();
                }
            };


}
