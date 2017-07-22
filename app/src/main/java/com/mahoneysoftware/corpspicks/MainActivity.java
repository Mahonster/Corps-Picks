package com.mahoneysoftware.corpspicks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mahoneysoftware.corpspicks.Objects.Contest;

import java.lang.reflect.Field;
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

    private String userId;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mainReference;

    private RecyclerView recyclerView;
    private MainRecyclerAdapter adapter;

    private SwipeRefreshLayout refreshLayout;

    private ArrayList<Contest> contests;

    private Random rand;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        contests = new ArrayList<>();
        rand = new Random();

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mainReference = mDatabase.getReference("2017").child("v1");
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            userId = user.getUid();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_activity_swipe_layout);
        setRefreshListener();

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MainRecyclerAdapter(contests, getApplicationContext(), this);
        recyclerView.setAdapter(adapter);

        queryEventsList();
    }

    private void queryEventsList() {
        refreshLayout.setRefreshing(true);
        updateEventsList();
    }

    void updateEventsList() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 6, 2);

        DatabaseReference eventsRef = mainReference.child("events");
        eventsRef.keepSynced(true);

        final Query query = mainReference.child("events").orderByChild("date");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DateFormat df = new SimpleDateFormat("yyMMdd", Locale.US);

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Contest contest = child.getValue(Contest.class);
                    int id = Integer.parseInt(child.getKey());
                    contest.setId(id);
                    //contest.setImageResourceId(getContestImage(contest));

                    String isLocked = (String) child.child("isLocked").getValue();
                    contest.setLocked(isLocked);

                    String isComplete = (String) child.child("isComplete").getValue();
                    if (isComplete != null) {
                        contest.setComplete(isComplete);
                    }
                    try {
                        contest.setDateObject(df.parse(contest.getDate()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        contest.setDateObject(calendar.getTime());
                    }
                    contests.add(contest);
                }
                updateEventsPredicted();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateEventsPredicted() {
        Query predictedQuery = mainReference.child("users").child(userId).child("predicted");

        predictedQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    int id = Integer.parseInt(child.getKey());

                    for (Contest contest : contests) {
                        if (contest.getId() == id) {
                            contest.setContestPredicted(true);
                        }
                    }
                }
                onItemsLoadComplete();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int getContestImage(Contest contest) {
        if (contest.getImage().equals("")) {
            int randomNumber = rand.nextInt((38 - 14) + 1) + 14;
            return getResId("event" + randomNumber, R.drawable.class);
        } else {
            return getResId("indianapolis", R.drawable.class);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateEventsPredicted();
    }

    private void setRefreshListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
    }

    private void refreshItems() {
        contests.clear();
        adapter.notifyDataSetChanged();
        updateEventsList();
    }

    private void onItemsLoadComplete() {
        refreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }

    //Very rudimentary method to sign out
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
    }

    public void startContestPick(Contest contest) {
        Intent intent = new Intent(this, PickActivity.class);
        intent.putExtra("contest_title", contest.getName());
        intent.putExtra("contest_id", contest.getId());
        startActivity(intent);
    }

    public void startContestLineup(Contest contest) {
        Toast.makeText(this, "This event is currently in progress or scores have not been updated yet.", Toast.LENGTH_LONG).show();
    }

    public void startContestResults(Contest contest) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("contest_title", contest.getName());
        intent.putExtra("contest_id", contest.getId());
        startActivity(intent);
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.main_menu_settings)

        if (id == R.id.main_menu_sign_out)
            signOut();
//        } else if (id == R.id.main_menu_bottom) {
//            Intent intent = new Intent(this, BottomNavActivity.class);
//            startActivity(intent);
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
