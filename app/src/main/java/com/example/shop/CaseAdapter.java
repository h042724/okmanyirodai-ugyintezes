package com.example.shop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

public class CaseAdapter extends RecyclerView.Adapter<CaseAdapter.ViewHolder> implements Filterable {
    private static final String LOG_TAG = CaseAdapter.class.getName();
    private Context context;
    private ArrayList<Case> mCaseData;
    private ArrayList<Case> mCaseDataAll;
    private Context mContext;
    private int lastPosition = -1;
    Spinner spinner;

    public CaseAdapter(Context context, ArrayList<Case> itemsData) {
        this.mCaseData = itemsData;
        this.mCaseDataAll = itemsData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CaseAdapter.ViewHolder holder, int position) {
        Case currentCase = mCaseData.get(position);

        holder.bindTo(currentCase);

        if(holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mCaseData.size();
    }

    @Override
    public Filter getFilter() {
        return caseFilter;
    }

    private Filter caseFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Case> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                results.count = mCaseDataAll.size();
                results.values = mCaseDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(Case cases : mCaseDataAll) {
                    if(cases.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(cases);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mCaseData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {
        private TextView mTitleText;
        private TextView mInfoText;
        private Spinner mDateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.mTitleText = itemView.findViewById(R.id.case_title);
            this.mInfoText = itemView.findViewById(R.id.case_description);
            this.mDateText = itemView.findViewById(R.id.date_spinner);
        }

        public void bindTo(Case currentCase) {
            mTitleText.setText(currentCase.getName());
            mInfoText.setText(currentCase.getInfo());

            spinner = (Spinner)itemView.findViewById(R.id.date_spinner);
            spinner.setOnItemSelectedListener(this);
            /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                    R.array.case_dates, android.R.layout.simple_spinner_item);*/
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, currentCase.getDate());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            itemView.findViewById(R.id.book_appointment).setOnClickListener(view -> {
                Log.d("Activity", "Book appointment button clicked!");
                spinner = (Spinner)itemView.findViewById(R.id.date_spinner);
                String text = spinner.getSelectedItem().toString();
                ((CaseListActivity)mContext).updateAlertIcon(currentCase, text);
            });
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String selectedItem = adapterView.getItemAtPosition(i).toString();
            Log.i(LOG_TAG, selectedItem);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}


