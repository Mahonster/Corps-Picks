package com.mahoneysoftware.corpspicks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

/**
 * Created by Dylan on 1/15/2017.
 */

public class MainActivity extends AppCompatActivity implements MainRecyclerAdapter.ContestsAdapterInterface {

    private String userId;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mainReference;

    private RecyclerView recyclerView;
    private MainRecyclerAdapter adapter;

    private ArrayList<Contest> contests;
    private SparseArray<Contest> contestMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contests = new ArrayList<>();
        contestMap = new SparseArray<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            userId = user.getUid();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
        mainReference = mDatabase.getReference("2017").child("v1");

        queryEventsList();

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void queryEventsPredicted() {
        Query predictedQuery = mainReference.child("users").child(userId).child("predicted");

        predictedQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    int id = Integer.parseInt(child.getKey());
                    int index = contests.indexOf(contestMap.get(id));
                    Contest contest = contests.get(index);
                    contest.setContestPredicted(true);
                    contests.set(index, contest);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void queryEventsList() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 6, 2);

        Query query = mainReference.child("events").orderByChild("date");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DateFormat df = new SimpleDateFormat("yyMMdd", Locale.US);

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Contest contest = child.getValue(Contest.class);
                    int id = Integer.parseInt(child.getKey());
                    contest.setId(id);

                    if (child.child("isComplete").getValue() != null)
                        contest.setComplete((String) child.child("isComplete").getValue());
                    try {
                        contest.setDateObject(df.parse(contest.getDate()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        contest.setDateObject(calendar.getTime());
                    }
                    contests.add(contest);
                    contestMap.append(id, contest);
                }
                queryEventsPredicted();
                attachAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
