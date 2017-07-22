package com.mahoneysoftware.corpspicks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Dylan on 6/22/2017.
 */

public class ResultActivity extends AppCompatActivity {
    private int contestId;
    private String userId;
    private String[] dataSet;
    private String[] scores;
    private String[] predictedCorps;

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;

    private DatabaseReference mainReference;
    private FirebaseUser user;

    TextView scoreLabel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle extras = getIntent().getExtras();
        contestId = extras.getInt("contest_id");
        setTitle(extras.getString("contest_title"));

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mainReference = database.getReference("2017").child("v1");

        recyclerView = (RecyclerView) findViewById(R.id.result_list_view);
        recyclerView.setHasFixedSize(true); //for performance: if content size doesn't change.
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        scoreLabel = (TextView) findViewById(R.id.activity_main_score_label);

        getResultsData();
    }

    private void getResultsData() {
        DatabaseReference resultsReference = mainReference.child("results").child("" + contestId);

        resultsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
                };
                List<String> corps = dataSnapshot.child("name").getValue(t);
                List<String> scoresList = dataSnapshot.child("score").getValue(t);
                dataSet = new String[corps.size()];
                scores = new String[corps.size()];
                for (int i = 0; i < corps.size(); i++) {
                    dataSet[i] = corps.get(i);
                    scores[i] = scoresList.get(i);
                }
                getPredicted();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPredicted() {
        DatabaseReference pickReference = mainReference.child("picks").child("" + contestId).child(userId);

        pickReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getChildren().iterator().hasNext()) {
                    predictedCorps = new String[dataSet.length];
                    for (int i = 0; i < predictedCorps.length; i++) {
                        predictedCorps[i] = "No Pick";
                    }
                    scoreLabel.setText("No prediction made.");
                } else { // Load data from previous prediction if it exists
                    updateScore();
                    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
                    };
                    List<String> corps = dataSnapshot.child("name").getValue(t);
                    predictedCorps = new String[corps.size()];
                    for (int i = 0; i < corps.size(); i++) {
                        predictedCorps[i] = "Picked: " + corps.get(i);
                        //scores[i] = "99.65"; Add in when scores are implemented...
                    }
                }
                setupData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void updateScore() {
        DatabaseReference scoreReference = mainReference.child("leaderboard").child("" + contestId).child("placement").child(userId).child("score");

        scoreReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String score = (String) dataSnapshot.getValue();
                scoreLabel.setText("Your Score: " + score);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupData() {
        attachAdapter();
    }

    private void attachAdapter() {
        adapter = new ResultAdapter(dataSet, scores, predictedCorps);
        recyclerView.setAdapter(adapter);
    }
}
