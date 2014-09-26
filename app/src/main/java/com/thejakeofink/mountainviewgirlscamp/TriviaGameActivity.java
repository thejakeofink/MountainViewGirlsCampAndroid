package com.thejakeofink.mountainviewgirlscamp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class TriviaGameActivity extends Activity implements View.OnClickListener {

    ArrayList<TriviaQuestion> quizLoader;
    TriviaQuestion currentQuestion;
    int score;

    TextView questionView;
    TextView scoreView;
    Button answer1;
    Button answer2;
    Button answer3;
    Button answer4;

    Menu menu;
    ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quizLoader = new ArrayList<TriviaQuestion>();
        setContentView(R.layout.activity_trivia_game);

        questionView = (TextView)findViewById(R.id.txv_question);
        scoreView = (TextView)findViewById(R.id.txv_score);
        answer1 = (Button)findViewById(R.id.btn_answer_one);
        answer2 = (Button)findViewById(R.id.btn_answer_two);
        answer3 = (Button)findViewById(R.id.btn_answer_three);
        answer4 = (Button)findViewById(R.id.btn_answer_four);

        answer1.setOnClickListener(this);
        answer2.setOnClickListener(this);
        answer3.setOnClickListener(this);
        answer4.setOnClickListener(this);

        loadQuiz();
        loadFirstQuestion();
    }

    private void loadQuiz() {
        String[] fromResource = getResources().getStringArray(R.array.questions);

        for (int i = 0; i < fromResource.length; i += 5) {
            ArrayList<String> answers = new ArrayList<String>();
            answers.add(fromResource[i+1]);
            answers.add(fromResource[i+2]);
            answers.add(fromResource[i+3]);
            answers.add(fromResource[i+4]);
            long seed = System.nanoTime();
            Collections.shuffle(answers, new Random(seed));

            TriviaQuestion question = new TriviaQuestion(fromResource[i], fromResource[i+1], answers);
            quizLoader.add(question);
        }

        long seed = System.nanoTime();
        Collections.shuffle(quizLoader, new Random(seed));

        for (int i = quizLoader.size() - 1; i > 19; i--) {
            quizLoader.remove(i);
        }
    }

    private void loadFirstQuestion() {
        currentQuestion = quizLoader.get(0);
        populateQuestion();
    }

    private void populateQuestion() {
        questionView.setText(currentQuestion.question);
        answer1.setText(currentQuestion.answers.get(0));
        answer2.setText(currentQuestion.answers.get(1));
        answer3.setText(currentQuestion.answers.get(2));
        answer4.setText(currentQuestion.answers.get(3));
    }

    private void increaseScore() {
        score += 5;
        scoreView.setText("" + score);
    }

    private void loadNextQuestion() {
        if (quizLoader.get(quizLoader.size() - 1) != currentQuestion) {
            currentQuestion = quizLoader.get(quizLoader.indexOf(currentQuestion) + 1);
            populateQuestion();
        } else {
            getMenuInflater().inflate(R.menu.photo, menu);

            MenuItem item = menu.findItem(R.id.menu_item_share);

            mShareActionProvider = (ShareActionProvider) item.getActionProvider();

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "I just played the Mountain View Girls Camp Temple Trivia game and my score was " + score + "!!!");
            intent.setType("text/plain");
            setShareIntent(intent);
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof Button) {
            if (((Button) v).getText().equals(currentQuestion.correctAnswer)) {
                increaseScore();
                loadNextQuestion();
            } else {
                loadNextQuestion();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}
