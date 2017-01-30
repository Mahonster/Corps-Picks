package com.mahoneysoftware.corpspicks.Objects;

import java.util.HashMap;

/**
 * Created by Dylan on 12/23/2016.
 */

public class ContestPick {
    private String contestId = "";
    private String userId = "";
    private HashMap<Corps, Float> scores;

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public HashMap<Corps, Float> getScores() {
        return scores;
    }

    public void setScores(HashMap<Corps, Float> scores) {
        this.scores = scores;
    }
}
