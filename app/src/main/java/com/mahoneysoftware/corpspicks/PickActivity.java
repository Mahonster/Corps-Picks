package com.mahoneysoftware.corpspicks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.style.TtsSpan;
import android.util.SparseArray;
import android.view.View;

import com.mahoneysoftware.corpspicks.Objects.Contest;
import com.mahoneysoftware.corpspicks.Objects.ContestPick;
import com.mahoneysoftware.corpspicks.Objects.Corps;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class PickActivity extends AppCompatActivity implements PickListAdapter.OnStartDragListener {
    private RecyclerView.Adapter adapter;
    private ItemTouchHelper touchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("World Championship Finals");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.pick_list_view);
        recyclerView.setHasFixedSize(true); //for performance: if content size doesn't change.
        recyclerView.setItemViewCacheSize(20); //more performance statements.
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final String[] dataSet = getResources().getStringArray(R.array.test_finalists);
        final String[] scores = getResources().getStringArray(R.array.test_finalists_scores);
        ContestPick contestPick = new ContestPick();
        Contest contest = getContestFromArray(dataSet);


        adapter = new PickListAdapter(dataSet, scores, this);
        adapter.setHasStableIds(false); //For performance - If items are maintained with unique IDs.
        recyclerView.setAdapter(adapter);

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

    private Contest getContestFromArray(String[] dataSet)
    {
        SparseArray<Corps> corpsLineup = new SparseArray<>();
        for(int i = 0; i < dataSet.length; i++) {
            Corps corps = new Corps();
            corps.setId("cp" + i);
            corps.setName(dataSet[i]);
            corpsLineup.put(i, corps);
        }
        Contest contest = new Contest();
        contest.setId("Test Contest");
        contest.setName("World Championship Finals");
        contest.setLineup(corpsLineup);
        return contest;
    }
}
