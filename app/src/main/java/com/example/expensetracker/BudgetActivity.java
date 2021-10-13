package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        fap = findViewById(R.id.fap);

        fap.setOnClickListener(view -> {
            addItem();
        });
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

        cancelBtn.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(budgetRef, Data.class)
                .build();
    }


}