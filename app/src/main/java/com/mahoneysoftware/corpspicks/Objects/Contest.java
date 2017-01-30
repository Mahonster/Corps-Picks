package com.mahoneysoftware.corpspicks.Objects;

import android.util.SparseArray;

import java.util.Calendar;
import java.util.Date;

/**
 * Information for each contest.
 * Created by Dylan on 12/23/2016.
 */

public class Contest {
    private String id = "";
    private String name = "";
    private String location = "";
    private Date date;
    private SparseArray<Corps> lineup;

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
        this.date = date;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public SparseArray<Corps> getLineup() {
        return lineup;
    }

    public void setLineup(SparseArray<Corps> lineup) {
        this.lineup = lineup;
    }
}
