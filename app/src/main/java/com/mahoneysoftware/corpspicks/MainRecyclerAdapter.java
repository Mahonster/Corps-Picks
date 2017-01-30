package com.mahoneysoftware.corpspicks;

import android.app.ActionBar;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mahoneysoftware.corpspicks.Objects.Contest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dylan on 1/15/2017.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {
    private Context context;
    private Contest[] contests;
    private ArrayList<ArrayList<Contest>> groupedContests;
    public final ContestsAdapterInterface contestsAdapterInterface;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        HorizontalScrollView scrollView;
        LinearLayout scrollViewContent;

        ArrayList<CardViewHolder> cardViews;
        ContestsAdapterInterface adapterInterface;

        ViewHolder(View v, ContestsAdapterInterface adapterInterface) {
            super(v);
            dateTextView = (TextView) v.findViewById(R.id.contest_list_item_date);
            scrollView = (HorizontalScrollView) v.findViewById(R.id.contest_list_item_scroll_view);
            scrollViewContent = (LinearLayout) v.findViewById(R.id.contest_list_item_scroll_container);
            this.adapterInterface = adapterInterface;
        }

        void bindCard(final ArrayList<Contest> itemContests) {
            if(scrollViewContent.getChildCount() != itemContests.size()) {
                cardViews = new ArrayList<>();

                for (Contest contest : itemContests) {
                    final CardViewHolder holder = new CardViewHolder(scrollViewContent, cardViews.size());

                    holder.compTitle.setText(contest.getName());
                    holder.location.setText(contest.getLocation());
                    holder.action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("STAGGG", "Position clicked: " + getAdapterPosition());
                            Log.d("STAGGG", "Card Clicked: " + holder.position);
                            adapterInterface.startContestPick(itemContests.get(holder.position));

                        }
                    });
                    cardViews.add(holder);
                    scrollViewContent.addView(holder.cardView);
                }
            } else {
                for (int i = 0; i < itemContests.size(); i++) {
                    cardViews.get(i).compTitle.setText(itemContests.get(i).getName());
                    cardViews.get(i).location.setText(itemContests.get(i).getLocation());
                }
            }
        }

        static class CardViewHolder {
            CardView cardView;
            TextView compTitle;
            TextView location;
            Button action;
            int position;

            CardViewHolder(ViewGroup parent, int position) {
                cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_card_view, parent, false);
                compTitle = (TextView) cardView.findViewById(R.id.contest_card_title);
                location = (TextView) cardView.findViewById(R.id.contest_card_location);
                action = (Button) cardView.findViewById(R.id.contest_card_button);
                this.position = position;
            }
        }
    }

    MainRecyclerAdapter(Contest[] contests, Context context, ContestsAdapterInterface contestsAdapterInterface) {
        this.contests = contests;
        this.context = context;
        groupedContests = getGroupedContestsByDate();
        this.contestsAdapterInterface = (ContestsAdapterInterface) context;
    }

    @Override
    public MainRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_list_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v, contestsAdapterInterface);

        Log.d("STAG", "COUNT: " + getItemCount());
        Log.d("STAG", "VIEWHOLDER POSITION: " + viewHolder.getAdapterPosition());

        //set listeners and such here.
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MainRecyclerAdapter.ViewHolder holder, int position) {
        ArrayList<Contest> itemContests = groupedContests.get(position);

        String dateText;
        dateText = (String) android.text.format.DateFormat.format("EEEE, MMMM d, yyyy", itemContests.get(0).getDate());
        holder.dateTextView.setText(dateText);

        holder.bindCard(itemContests);

    }

    @Override
    public int getItemCount() {
        return groupedContests.size();
    }

    private ArrayList<ArrayList<Contest>> getGroupedContestsByDate() {
        ArrayList<ArrayList<Contest>> contestsGrouped = new ArrayList<>();
        Date date = new Date();

        for (Contest contest : contests) {
            Log.d("STAG", "ACTIVATED");
            Log.d("STAG", "" + contest.getDate().toString());

            ArrayList<Contest> itemContests = new ArrayList<>();

            //If contest is on the same date as previous contest
            if (contest.getDate().equals(date)) {
                itemContests = contestsGrouped.get(contestsGrouped.size() - 1);
                itemContests.add(contest);
                contestsGrouped.set(contestsGrouped.size() - 1, itemContests);
            } else {
                itemContests.add(contest);
                contestsGrouped.add(itemContests);
            }
            date = contest.getDate();
        }

        return contestsGrouped;
    }

    interface ContestsAdapterInterface {
        void startContestPick(Contest contest);
    }
}
