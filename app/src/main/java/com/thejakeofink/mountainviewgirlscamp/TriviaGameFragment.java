package com.thejakeofink.mountainviewgirlscamp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class TriviaGameFragment extends Fragment implements View.OnClickListener {

    ArrayList<TriviaQuestion> quizLoader;
    TriviaQuestion currentQuestion;
    int score;
    boolean scoreAdded = false;
    boolean questionQueued = false;
    boolean gameOver = false;

    TextView questionView;
    TextView scoreView;
    Button answer1;
    Button answer2;
    Button answer3;
    Button answer4;

    Menu menu;
    MenuInflater menuInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quizLoader = new ArrayList<>();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_trivia_game, container, false);

        questionView = (TextView) rootView.findViewById(R.id.txv_question);
        scoreView = (TextView) rootView.findViewById(R.id.txv_score);
        answer1 = (Button) rootView.findViewById(R.id.btn_answer_one);
        answer2 = (Button) rootView.findViewById(R.id.btn_answer_two);
        answer3 = (Button) rootView.findViewById(R.id.btn_answer_three);
        answer4 = (Button) rootView.findViewById(R.id.btn_answer_four);

        answer1.setOnClickListener(this);
        answer2.setOnClickListener(this);
        answer3.setOnClickListener(this);
        answer4.setOnClickListener(this);

        loadQuiz();
        loadFirstQuestion();

        return rootView;
    }

    private void loadQuiz() {
        String[] fromResource = getResources().getStringArray(R.array.questions);

        for (int i = 0; i < fromResource.length; i += 5) {
            ArrayList<String> answers = new ArrayList<String>();
            answers.add(fromResource[i + 1]);
            answers.add(fromResource[i + 2]);
            answers.add(fromResource[i + 3]);
            answers.add(fromResource[i + 4]);
            long seed = System.nanoTime();
            Collections.shuffle(answers, new Random(seed));

            TriviaQuestion question = new TriviaQuestion(fromResource[i], fromResource[i + 1], answers);
            quizLoader.add(question);
        }

        long seed = System.nanoTime();
        Collections.shuffle(quizLoader, new Random(seed));

        for (int i = quizLoader.size() - 1; i > 19; i--) {
            quizLoader.remove(i);
        }

        scoreView.setText("" + score + " pts");
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


        answer1.setBackgroundResource(R.drawable.button_background);
        answer2.setBackgroundResource(R.drawable.button_background);
        answer3.setBackgroundResource(R.drawable.button_background);
        answer4.setBackgroundResource(R.drawable.button_background);
    }

    private void increaseScore() {
        if (!scoreAdded) {
            score += 5;
            scoreView.setText("" + score + " pts");
            scoreAdded = true;
        }
    }

    private void loadNextQuestion() {
        if (quizLoader.get(quizLoader.size() - 1) != currentQuestion) {
            currentQuestion = quizLoader.get(quizLoader.indexOf(currentQuestion) + 1);
            populateQuestion();
        } else {
            gameOver = true;
            new AlertDialog.Builder(getActivity())
                    .setTitle("You Finished!")
                    .setMessage("Way to go! You got a score of: " + score + " pts")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            gameOver = false;
                            score = 0;
                            loadQuiz();
                            loadFirstQuestion();
                        }
                    })
                    .setNeutralButton(R.string.action_share, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, "I just played the Mountain View Girls Camp Temple Trivia game and my score was " + score + "!!!");
                            intent.setType("text/plain");
                            getActivity().startActivity(Intent.createChooser(intent, "Pick an app to share!"));
                            gameOver = false;
                            score = 0;
                            loadQuiz();
                            loadFirstQuestion();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
        scoreAdded = false;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof Button) {
            Button b = (Button) v;
            if (b.getText().equals(currentQuestion.correctAnswer)) {
                if (!gameOver) {
                    increaseScore();
                }
                b.setBackgroundResource(R.drawable.button_correct);

                if (!questionQueued) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadNextQuestion();
                            questionQueued = false;
                        }
                    }, 1000);
                    questionQueued = true;
                }
            } else {
                b.setBackgroundResource(R.drawable.button_wrong);
                scoreAdded = true;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        this.menuInflater = inflater;
        super.onCreateOptionsMenu(menu, inflater);
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

}
