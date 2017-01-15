package se.chalmers.exjobb.feedr.models;

/**
 * Created by matej on 2017-01-06.
 */

public class Question {

    private String question;
    private String answerRef;

    public Question() {
    }

    public Question(String question, String answerRef) {
        this.question = question;
        this.answerRef = answerRef;
    }

    public Question(String question){
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswerRef() {
        return answerRef;
    }

    public void setAnswerRef(String answerRef) {
        this.answerRef = answerRef;
    }
}
