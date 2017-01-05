package se.chalmers.exjobb.feedr.models;

/**
 * Created by matej on 2017-01-05.
 */

public class Survey {

    private String refCode;
    private String surveyName;

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
}
