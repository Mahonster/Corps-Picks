package com.mahoneysoftware.corpspicks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahoneysoftware.corpspicks.Objects.Contest;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Dylan on 1/15/2017.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Contest> contests;
    public final ContestsAdapterInterface contestsAdapterInterface;
    private Random rand;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView title;
        TextView date;
        TextView location;
        TextView complete;
        TextView predicted;
        //ImageView background;

        ViewHolder(View v) {
            super(v);
            view = v;
            title = (TextView) v.findViewById(R.id.contest_item_title);
            date = (TextView) v.findViewById(R.id.contest_item_date);
            location = (TextView) v.findViewById(R.id.contest_item_location);
            complete = (TextView) v.findViewById(R.id.contest_item_complete);
            predicted = (TextView) v.findViewById(R.id.contest_item_predicted);
            //background = (ImageView) v.findViewById(R.id.contest_item_picture);
        }
    }

    MainRecyclerAdapter(ArrayList<Contest> contests, Context context, ContestsAdapterInterface contestsAdapterInterface) {
        this.contests = contests;
        this.context = context;
//        groupedContests = getGroupedContestsByDate();
        this.contestsAdapterInterface = contestsAdapterInterface;
        rand = new Random();
    }

    @Override
    public MainRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_list_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When clicked, check if contest is complete or not, if so, launch results activity.
                Contest contest = contests.get(viewHolder.getAdapterPosition());

                if (contest.isComplete().equals("true")) {
                    contestsAdapterInterface.startContestResults(contest);
                } else {
                    if (contest.isLocked()) {
                        contestsAdapterInterface.startContestLineup(contest);
                    } else {
                        contestsAdapterInterface.startContestPick(contest);
                    }
                }
            }
        });

        //set listeners and such here.
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MainRecyclerAdapter.ViewHolder holder, int position) {
        Contest contest = contests.get(position);
        String dateText;
        dateText = (String) android.text.format.DateFormat.format("EEEE, MMMM d", contests.get(position).getDateObject());
        //holder.background.setImageResource(contest.getImageResourceId());
        holder.title.setText(contest.getName());
        holder.date.setText(dateText + " - " + contest.getTime());
        holder.location.setText(contest.getLocation());
        String isComplete = contest.isComplete();
        if (isComplete.equals("true"))
            holder.complete.setText("Contest Finished! Click to view results.");
        else
            holder.complete.setText("");
        if (contest.isContestPredicted())
            holder.predicted.setText("Prediction Made");
        else
            holder.predicted.setText("");
    }


    @Override
    public int getItemCount() {
        return contests.size();
    }

//    private ArrayList<ArrayList<Contest>> getGroupedContestsByDate() {
//        ArrayList<ArrayList<Contest>> contestsGrouped = new ArrayList<>();
//        Date date = new Date();
//
//        for (Contest contest : contests) {
//            Log.d("STAG", "ACTIVATED");
//            Log.d("STAG", "" + contest.getDateObject().toString());
//
//            ArrayList<Contest> itemContests = new ArrayList<>();
//
//            //If contest is on the same date as previous contest
//            if (contest.getDateObject().equals(date)) {
//                itemContests = contestsGrouped.get(contestsGrouped.size() - 1);
//                itemContests.add(contest);
//                contestsGrouped.set(contestsGrouped.size() - 1, itemContests);
//            } else {
//                itemContests.add(contest);
//                contestsGrouped.add(itemContests);
//            }
//            date = contest.getDateObject();
//        }
//
//        return contestsGrouped;
//    }

    interface ContestsAdapterInterface {
        void startContestPick(Contest contest);
        void startContestResults(Contest contest);

        void startContestLineup(Contest contest);
    }
}
