package com.example.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaseListActivity extends AppCompatActivity {
    private static final String LOG_TAG = CaseListActivity.class.getName();
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private CaseAdapter mCaseItemAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mCases;
    private CollectionReference mBookedCases;
    private ArrayList<Case> mItemList;
    private NotificationHandler mNotificationHandler;

    private int gridNumber = 1;
    private boolean viewRow;
    private FrameLayout redCircle;
    private TextView contentTextView;
    private int bookedAppointments = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_list);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG,"Authenticated user!");
        } else {
            Log.d(LOG_TAG,"Unauthenticated user!");
            finish();
        }

        mFirestore = FirebaseFirestore.getInstance();
        mCases = mFirestore.collection("Cases");
        mBookedCases = mFirestore.collection("BookedCases");

        queryData();

        mNotificationHandler = new NotificationHandler(this);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void queryData() {
        //mItemList.clear();
        mItemList = new ArrayList<>();

        mCases.orderBy("name", Query.Direction.ASCENDING).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Case currentCase = document.toObject((Case.class));
                currentCase.setId(document.getId());
                mItemList.add(currentCase);
            }

            if(mItemList.size() == 0) {
                initializeData();
                queryData();
            }

            //Log.d(LOG_TAG, String.valueOf(mItemList.get(0).getDate()));

            mRecyclerView = findViewById(R.id.recyclerView);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
            mCaseItemAdapter = new CaseAdapter(this, mItemList);
            mRecyclerView.setAdapter(mCaseItemAdapter);
            mCaseItemAdapter.notifyDataSetChanged();
        });
    }

    private void initializeData() {
        String[] itemsList = getResources().getStringArray(R.array.case_item_names);
        String[] itemsInfo = getResources().getStringArray(R.array.case_item_desc);
        List<String> itemsDates = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.case_dates)));

        for (int i = 0; i < itemsList.length; i++) {
            mCases.add(new Case(itemsList[i], itemsInfo[i], itemsDates));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.case_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mCaseItemAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_button:
                Log.d(LOG_TAG, "Log out clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
            case R.id.settings_button:
                Log.d(LOG_TAG, "Settings clicked!");
                return true;
            case R.id.appointment:
                Log.d(LOG_TAG, "Booked appointments button clicked!");
                startBookedCaseActivity();
                return true;
            case R.id.view_selector:
                Log.d(LOG_TAG, "View selector clicked!");
                if(viewRow) {
                    changeSpanCount(item, R.drawable.ic_grid_view, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_grid_view, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startBookedCaseActivity() {
        Intent intent = new Intent(this,BookedCaseActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else {
            startActivity(intent);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.appointment);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(view -> onOptionsItemSelected(alertMenuItem));

        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(Case currentCase, String text) {
        bookedAppointments = (bookedAppointments + 1);
        if(0 < bookedAppointments) {
            contentTextView.setText(String.valueOf(bookedAppointments));
        } else {
            contentTextView.setText("");
        }

        redCircle.setVisibility((bookedAppointments > 0) ? View.VISIBLE : View.GONE);
        Toast.makeText(this, "Időpont a(z) " + currentCase.getName() + " ügyre sikeresen foglalva.", Toast.LENGTH_SHORT).show();
        mBookedCases.add(new BookedCase(FirebaseAuth.getInstance().getCurrentUser().getUid(), currentCase.getName(), text));

        currentCase.getDate().remove(text);
        Log.d(LOG_TAG, String.valueOf(currentCase.getDate()));
        mCases.document(currentCase._getId()).update("date", currentCase.getDate())
            .addOnFailureListener(fail -> Toast.makeText(this, "Item " + currentCase.getName() + " cannot be changed.", Toast.LENGTH_LONG).show());

        mNotificationHandler.send(currentCase.getName());
        queryData();
    }
}