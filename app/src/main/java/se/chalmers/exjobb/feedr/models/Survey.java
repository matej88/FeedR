package se.chalmers.exjobb.feedr.models;

import java.util.Map;

/**
 * Created by matej on 2017-01-05.
 */

public class Survey {

    private String refCode;
    private String surveyName;
    private Map<String,Question> questions;
    private Map<String,Answer> answers;

    public Survey() {
    }

    public Survey(String refCode, String surveyName) {
        this.refCode = refCode;
        this.surveyName = surveyName;
    }

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setName(String surveyName) {
        this.surveyName = surveyName;
    }

    public Map<String, Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Map<String, Question> questions) {
        this.questions = questions;
    }

    public Map<String, Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, Answer> answers) {
        this.answers = answers;
    }
}
