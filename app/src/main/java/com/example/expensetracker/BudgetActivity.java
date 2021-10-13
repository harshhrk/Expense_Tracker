package com.example.expensetracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BudgetActivity extends AppCompatActivity {

    private FloatingActionButton fap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

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
    }
}