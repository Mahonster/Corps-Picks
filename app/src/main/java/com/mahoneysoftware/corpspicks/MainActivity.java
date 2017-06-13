package com.mahoneysoftware.corpspicks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mahoneysoftware.corpspicks.Objects.Contest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Dylan on 1/15/2017.
 */

public class MainActivity extends AppCompatActivity implements MainRecyclerAdapter.ContestsAdapterInterface {

    private FirebaseDatabase mDatabase;
    private DatabaseReference eventsDatabase;

    private RecyclerView recyclerView;
    private MainRecyclerAdapter adapter;

    private ArrayList<Contest> contests;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contests = new ArrayList<>();

        final Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 6, 2);
        final Random rand = new Random();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
        eventsDatabase = mDatabase.getReference("2017").child("v1").child("events");

        Query query = eventsDatabase.orderByChild("date");


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DateFormat df = new SimpleDateFormat("yyMMdd", Locale.US);

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Contest contest = child.getValue(Contest.class);
                    contest.setId(Integer.parseInt(child.getKey()));
                    try {
                        contest.setDateObject(df.parse(contest.getDate()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        contest.setDateObject(calendar.getTime());
                    }
                    contests.add(contest);
                }
                attachAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        Contest[] contests = new Contest[6];
//
//
//
//        contests[0] = new Contest("DCI West", calendar.getTime(), "Stanford, California");
//
//        calendar.clear();
//        calendar.set(2017, 6, 5);
//
//        contests[1] = new Contest("DCI Less West", calendar.getTime(), "Stillwater, Oklahoma");
//        contests[2] = new Contest("DCI South", calendar.getTime(), "Austin, Texas");
//        calendar.set(2017, 6, 8);
//        contests[3] = new Contest("DCI Faker", calendar.getTime(), "Nowhere");
//        calendar.set(2017, 6, 9);
//        contests[4] = new Contest("DCI Npe", calendar.getTime(), "Somewhere");
//        calendar.set(2017, 7, 10);
//        contests[5] = new Contest("DCI Finals", calendar.getTime(), "Indianapolis");

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }

    private void attachAdapter() {
        adapter = new MainRecyclerAdapter(contests, this, this);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void startContestPick(Contest contest) {
        Intent intent = new Intent(this, PickActivity.class);
        intent.putExtra("contest_title", contest.getName());
        intent.putExtra("contest_id", contest.getId());
        startActivity(intent);
    }
}
