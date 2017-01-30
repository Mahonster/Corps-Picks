package com.mahoneysoftware.corpspicks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.mahoneysoftware.corpspicks.Objects.Contest;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dylan on 1/15/2017.
 */

public class MainActivity extends AppCompatActivity implements MainRecyclerAdapter.ContestsAdapterInterface {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Contest[] contests = new Contest[6];

        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 6, 2);

        contests[0] = new Contest("DCI West", calendar.getTime(), "Stanford, California");

        calendar.clear();
        calendar.set(2017, 6, 5);

        contests[1] = new Contest("DCI Less West", calendar.getTime(), "Stillwater, Oklahoma");
        contests[2] = new Contest("DCI South", calendar.getTime(), "Austin, Texas");
        calendar.set(2017, 6, 8);
        contests[3] = new Contest("DCI Faker", calendar.getTime(), "Nowhere");
        calendar.set(2017, 6, 9);
        contests[4] = new Contest("DCI Npe", calendar.getTime(), "Somewhere");
        calendar.set(2017, 7, 10);
        contests[5] = new Contest("DCI Finals", calendar.getTime(), "Indianapolis");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        MainRecyclerAdapter adapter = new MainRecyclerAdapter(contests, this, this);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void startContestPick(Contest contest) {
        startActivity(new Intent(this, PickActivity.class));
    }
}
