package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private CardView budget;
    private CardView today;
    private CardView week;
    private CardView month;
    private CardView analytics;
    private CardView history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        budget = findViewById(R.id.budgetCardView);
        today = findViewById(R.id.todayCardView);
        week = findViewById(R.id.weekCardView);
        month = findViewById(R.id.monthCardView);
        analytics = findViewById(R.id.analyticsCardView);
        history = findViewById(R.id.historyCardView);

        budget.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
            startActivity(intent);
        });

        today.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TodayActivity.class);
            startActivity(intent);
        });

        week.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WeekActivity.class);
            startActivity(intent);
        });

        month.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MonthActivity.class);
            startActivity(intent);
        });

        analytics.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });

        history.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }
}