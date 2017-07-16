package com.mahoneysoftware.corpspicks;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Dylan on 6/22/2017.
 */

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    private String[] corps;
    private String[] scores;
    private String[] predictedTexts;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView corpsLabel;
        TextView pickedLabel;
        TextView scoreLabel;
        TextView placementLabel;

        ViewHolder(View v) {
            super(v);
            corpsLabel = (TextView) v.findViewById(R.id.result_list_corps_label);
            pickedLabel = (TextView) v.findViewById(R.id.result_list_picked_label);
            scoreLabel = (TextView) v.findViewById(R.id.result_list_score_label);
            placementLabel = (TextView) v.findViewById(R.id.result_list_placement_label);
        }
    }

    ResultAdapter(String[] corps, String[] scores, String[] predictedTexts) {
        this.corps = corps;
        this.scores = scores;
        this.predictedTexts = predictedTexts;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.corpsLabel.setText(corps[position]);
        holder.placementLabel.setText("" + (position + 1));
        holder.scoreLabel.setText(scores[position]);
        holder.pickedLabel.setText(predictedTexts[position]);
    }

    @Override
    public int getItemCount() {
        return corps.length;
    }

}
