package com.thejakeofink.mountainviewgirlscamp;

import java.util.ArrayList;

/**
 * Created by Jacob Stokes on 9/26/14.
 */
public class TriviaQuestion {
    public String question;
    public String correctAnswer;
    public ArrayList<String> answers;

    public TriviaQuestion (String question, String correctAnswer, ArrayList<String> answers) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.answers = answers;
    }

}
