package com.example.shop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookedCaseAdapter extends RecyclerView.Adapter<BookedCaseAdapter.ViewHolder> {
    private static final String LOG_TAG = BookedCaseAdapter.class.getName();
    private ArrayList<BookedCase> mBookedCaseData;
    private ArrayList<BookedCase> mBookedCaseDataAll;
    private Context mContext;
    private int lastPosition = -1;

    public BookedCaseAdapter(Context context, ArrayList<BookedCase> itemsData) {
        this.mBookedCaseData = itemsData;
        this.mBookedCaseDataAll = itemsData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.booked_case, parent, false));
    }

    @Override
    public void onBindViewHolder(BookedCaseAdapter.ViewHolder holder, int position) {
        BookedCase currentCase = mBookedCaseData.get(position);

        holder.bindTo(currentCase);

        if(holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mBookedCaseData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mDateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.mTitleText = itemView.findViewById(R.id.case_title);
            this.mDateText = itemView.findViewById(R.id.case_date);
        }

        public void bindTo(BookedCase currentCase) {
            mTitleText.setText(currentCase.getCase_name());
            mDateText.setText(currentCase.getSelectedDate());

            itemView.findViewById(R.id.delete).setOnClickListener(view -> {
                Log.d("Activity", "Delete button clicked!");
                ((BookedCaseActivity)mContext).deleteItem(currentCase);
            });
        }

    }
}


