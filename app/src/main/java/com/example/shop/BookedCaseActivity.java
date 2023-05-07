package com.example.shop;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class BookedCaseActivity extends AppCompatActivity {
    private static final String LOG_TAG = BookedCaseActivity.class.getName();
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ArrayList<BookedCase> mItemList;
    private BookedCaseAdapter mBookedCaseAdapter;
    private FirebaseFirestore mFirestore;
    private CollectionReference mBookedCases;
    private int gridNumber = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_case);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG,"Authenticated user!");
        } else {
            Log.d(LOG_TAG,"Unauthenticated user!");
            finish();
        }
        mFirestore = FirebaseFirestore.getInstance();
        mBookedCases = mFirestore.collection("BookedCases");

        queryData();
    }

    private void queryData() {
        mItemList = new ArrayList<>();

        mBookedCases.whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                BookedCase currentCase = document.toObject(BookedCase.class);
                currentCase.setId(document.getId());
                mItemList.add(currentCase);
            }

            mRecyclerView = findViewById(R.id.recyclerViewBookedCase);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
            mBookedCaseAdapter = new BookedCaseAdapter(this, mItemList);
            mRecyclerView.setAdapter(mBookedCaseAdapter);
        });
    }

    public void deleteItem(BookedCase currentCase) {
        DocumentReference ref = mBookedCases.document(currentCase._getId());

        ref.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Lefoglalt időpont sikeresen törölve: " + currentCase.getCase_name());
        })
        .addOnFailureListener(failure -> {
            Toast.makeText(this, "Case " + currentCase._getId() + " cannot be deleted.", Toast.LENGTH_SHORT).show();
        });

        queryData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }
}
