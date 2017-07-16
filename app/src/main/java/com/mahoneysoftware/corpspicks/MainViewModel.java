package com.mahoneysoftware.corpspicks;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;

import com.mahoneysoftware.corpspicks.Objects.Contest;

import java.util.ArrayList;

/**
 * Created by Dylan on 6/21/2017.
 */

public class MainViewModel extends ViewModel {
    private LiveData<ArrayList<Contest>> contests;

    public LiveData<ArrayList<Contest>> getContests() {
        if (contests == null) {
            contests = new LiveData<ArrayList<Contest>>() {
                @Override
                public void observe(LifecycleOwner owner, Observer<ArrayList<Contest>> observer) {
                    super.observe(owner, observer);
                }
            };
            loadContests();
        }

        return contests;
    }

    private void loadContests() {

    }
}
