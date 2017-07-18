package com.mahoneysoftware.corpspicks;


import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

class PickListAdapter extends RecyclerView.Adapter<PickListAdapter.ViewHolder> {
    private String[] dataSet;
    private String[] scores;
    private final OnStartDragListener onStartDragListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView corpsLabel;
        TextView placeLabel;
        //TextView scoreLabel;
        ImageView handleView;
        ViewHolder(View v) {
            super(v);
            corpsLabel = (TextView) v.findViewById(R.id.pick_list_corps_label);
            placeLabel = (TextView) v.findViewById(R.id.pick_list_place_label);
            //scoreLabel = (TextView) v.findViewById(R.id.pick_list_score_label);
            handleView = (ImageView) v.findViewById(R.id.pick_list_handle);
        }
    }

    PickListAdapter(String[] dataSet, String[] scores, OnStartDragListener dragListener) {
        this.dataSet = dataSet;
        this.scores = scores;
        this.onStartDragListener = dragListener;
    }

    @Override
    public PickListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pick_list_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);

        viewHolder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    onStartDragListener.onStartDrag(viewHolder);
                }
                return true;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.corpsLabel.setText(dataSet[position]);
        holder.placeLabel.setText("" + (position + 1));
        //holder.scoreLabel.setText(scores[position]);
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }

    interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

}
