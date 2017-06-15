package com.mahoneysoftware.corpspicks.Objects;

import android.util.SparseArray;

import java.util.Date;

/**
 * Information for each contest.
 * Created by Dylan on 12/23/2016.
 */

public class Contest {
    private int id = -1;
    private String name = "";
    private String location = "";
    private Date dateObject;
    private String dateLabel = "";
    private String date = "";
    private String time = "";
    private SparseArray<Corps> lineup;
    private String complete = "false";

    public String isComplete() {
        return complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }



    public Contest() {

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Contest(String name, Date date, String location) {
        this.name = name;
        this.dateObject = date;
        this.location = location;
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public void setDateLabel(String dateLabel) {
        this.dateLabel = dateLabel;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getDateObject() {
        return dateObject;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }

    public SparseArray<Corps> getLineup() {
        return lineup;
    }

    public void setLineup(SparseArray<Corps> lineup) {
        this.lineup = lineup;
    }
}
