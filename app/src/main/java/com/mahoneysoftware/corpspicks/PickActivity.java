package com.mahoneysoftware.corpspicks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PickActivity extends AppCompatActivity implements PickListAdapter.OnStartDragListener {
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private ItemTouchHelper touchHelper;
    private int contestId;
    private String[] dataSet;
    private String[] scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        contestId = extras.getInt("contest_id");
        setTitle(extras.getString("contest_title"));

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("2017").child("v1").child("lineups").child("" + contestId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                attachAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }

    private void attachAdapter() {
        adapter = new PickListAdapter(dataSet, scores, this);
        recyclerView.setAdapter(adapter);
    }
}
