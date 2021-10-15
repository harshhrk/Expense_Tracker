package com.example.expensetracker;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BudgetActivity extends AppCompatActivity {

    private FloatingActionButton fap;

    private TextView totalAmount;
    private RecyclerView recyclerView;

    private DatabaseReference budgetRef;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
        progressDialog = new ProgressDialog(this);

        totalAmount = findViewById(R.id.totalBudget_budget);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmt = 0;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Data data = snap.getValue(Data.class);
                    assert data != null;
                    totalAmt += data.getAmount();
                    String sTotal = "Month budget: Rs " + totalAmt;
                    totalAmount.setText(sTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fap = findViewById(R.id.fap);

        fap.setOnClickListener(view -> addItem());
    }

    private void addItem() {
        AlertDialog.Builder myDialogue = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View myView = layoutInflater.inflate(R.layout.input_layout, null);
        myDialogue.setView(myView);

        final AlertDialog dialog = myDialogue.create();
        dialog.setCancelable(false);
        dialog.show();

        final Spinner spinner = findViewById(R.id.itemSpinner_inputlayout);
        final EditText amount = findViewById(R.id.amount_inputlayout);
        final EditText notes = findViewById(R.id.notes_inputlayout);
        final Button cancelBtn = findViewById(R.id.cancelBtn_inputLayout);
        final Button saveBtn = findViewById(R.id.saveBtn_inputLayout);

        saveBtn.setOnClickListener(view -> {
            String amt = amount.getText().toString();
            String item = spinner.getSelectedItem().toString();
            String nt = notes.getText().toString();

            if (TextUtils.isEmpty(amt)) {
                amount.setError("Amount is required!");
            }
            if (item.equals("Select Item")) {
                Toast.makeText(BudgetActivity.this, "Select a valid Item", Toast.LENGTH_SHORT).show();
            }
            else {
                progressDialog.setMessage("adding a budget item");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String id = budgetRef.push().getKey();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar calendar = Calendar.getInstance();
                String date = dateFormat.format(calendar.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime dateTime = new DateTime();
                Months months = Months.monthsBetween(epoch, dateTime);

                Data data = new Data(item, date, id, nt, Integer.parseInt(amt), months.getMonths());
                assert id != null;
                budgetRef.child(id).setValue(data).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(BudgetActivity.this, "Budget item added successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BudgetActivity.this, task.getException().toString() + "", Toast.LENGTH_SHORT).show();
                    }

                    progressDialog.dismiss();
                });
            }

            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(view -> dialog.dismiss());
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(budgetRef, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, ViewHolder> adapter = new FirebaseRecyclerAdapter<Data, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Data model) {

                holder.setItemAmount("Allocated Amount: Rs " + model.getAmount());
                holder.setDate("On: " + model.getDate());
                holder.setItemName("Budget item: " + model.getItem());
                holder.notes.setVisibility(View.GONE);

                switch (model.getItem()) {
                    case "Transport" :
                        holder.imageView.setImageResource(R.drawable.ic_transport);
                        break;

                    case "Food" :
                        holder.imageView.setImageResource(R.drawable.ic_food);
                        break;

                    case "House" :
                        holder.imageView.setImageResource(R.drawable.ic_house);
                        break;

                    case "Entertainment" :
                        holder.imageView.setImageResource(R.drawable.ic_entertainment);
                        break;

                    case "Education" :
                        holder.imageView.setImageResource(R.drawable.ic_education);
                        break;

                    case "Charity" :
                        holder.imageView.setImageResource(R.drawable.ic_consultancy);
                        break;

                    case "Apparel" :
                        holder.imageView.setImageResource(R.drawable.ic_shirt);
                        break;

                    case "Health" :
                        holder.imageView.setImageResource(R.drawable.ic_health);
                        break;

                    case "Personal" :
                        holder.imageView.setImageResource(R.drawable.ic_personalcare);
                        break;

                    case "Other" :
                        holder.imageView.setImageResource(R.drawable.ic_other);
                        break;
                }

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new ViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notify();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        public ImageView imageView;
        public  TextView notes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            imageView = itemView.findViewById(R.id.image_retrieveLayout);
            notes = itemView.findViewById(R.id.note_retrieveLayout);
        }

        public void setItemName (String itemName) {
            TextView item = view.findViewById(R.id.item_retrieveLayout);
            item.setText(itemName);
        }

        public void setItemAmount (String itemAmount) {
            TextView amount = view.findViewById(R.id.amount_retrieveLayout);
            amount.setText(itemAmount);
        }

        public void setDate (String itemDate) {
            TextView date = view.findViewById(R.id.date_retrieveLayout);
            date.setText(itemDate);
        }


    }
}