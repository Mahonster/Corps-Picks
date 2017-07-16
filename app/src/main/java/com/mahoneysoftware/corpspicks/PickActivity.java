package com.mahoneysoftware.corpspicks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickActivity extends AppCompatActivity implements PickListAdapter.OnStartDragListener {
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private ItemTouchHelper touchHelper;
    private int contestId;
    private String[] dataSet;
    private String[] scores;
    DatabaseReference mainReference;
    private FirebaseUser user;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);
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

        checkForExistingPicks();

        recyclerView = (RecyclerView) findViewById(R.id.pick_list_view);
        recyclerView.setHasFixedSize(true); //for performance: if content size doesn't change.
        recyclerView.setItemViewCacheSize(20); //more performance statements.
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.Callback touchCallback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN | ItemTouchHelper.UP);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                String s1 = dataSet[viewHolder.getAdapterPosition()];
                dataSet[viewHolder.getAdapterPosition()] = dataSet[target.getAdapterPosition()];
                dataSet[target.getAdapterPosition()] = s1;

                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                adapter.onBindViewHolder(viewHolder, viewHolder.getAdapterPosition());
                adapter.onBindViewHolder(target, target.getAdapterPosition());
                return true;
            }

            @Override
            public int interpolateOutOfBoundsScroll(RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
                return 8 * super.interpolateOutOfBoundsScroll(recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };

        touchHelper = new ItemTouchHelper(touchCallback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    public void submitPick() {

        if (user != null) {
            List<String> names = new ArrayList<>(Arrays.asList(dataSet));
            List<String> scoresList = new ArrayList<>(Arrays.asList(scores));

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/picks/" + contestId + "/" + userId + "/name", names);
            childUpdates.put("/picks/" + contestId + "/" + userId + "/score", scoresList);
            childUpdates.put("picks/" + contestId + "/" + userId + "/metadata/placementOnly", true);
            childUpdates.put("/users/" + userId + "/predicted/" + contestId, true);

            mainReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                }
            });
        } else {
            Toast.makeText(this, "Authentication Error.", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(PickActivity.this, "Prediction Submitted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }

    private void attachAdapter() {
        adapter = new PickListAdapter(dataSet, scores, this);
        recyclerView.setAdapter(adapter);
    }

    private void checkForExistingPicks() {
        DatabaseReference pickReference = mainReference.child("picks").child("" + contestId).child(userId);

        pickReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getChildren().iterator().hasNext()) {
                    setupData(false);
                } else { // Load data from previous prediction if it exists
                    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
                    };
                    List<String> corps = dataSnapshot.child("name").getValue(t);
                    dataSet = new String[corps.size()];
                    scores = new String[corps.size()];
                    for (int i = 0; i < corps.size(); i++) {
                        dataSet[i] = corps.get(i);
                        scores[i] = "99.65";
                    }
                    setupData(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //TODO needs to be optimized
    public void setupData(final Boolean predictionMade) {

        if (!predictionMade) {
            DatabaseReference reference = mainReference.child("lineups").child("" + contestId);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!predictionMade) {
                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
                        };
                        List<String> list = dataSnapshot.getValue(t);
                        Log.d("LOOK MOM", "" + dataSnapshot.getChildrenCount());
                        dataSet = new String[list.size()];
                        scores = new String[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            dataSet[i] = list.get(i);
                            scores[i] = "99.65";
                        }
                    } else {
                        //Probably check for validity of previous prediction here
                    }

                    attachAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            attachAdapter(); //If a prediction was made, it's loaded, attach adapter.
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.pick_menu_done) {
            submitPick();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pick_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
