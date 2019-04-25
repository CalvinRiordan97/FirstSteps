package com.example.calvin.kidsfit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class MyProgress extends AppCompatActivity {

    TextView userName;
    TextView age;
    TextView todaysSteps;
    TextView totalSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_progress);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        User user = intent.getParcelableExtra("User");

        userName = findViewById(R.id.username);
        age = findViewById(R.id.age);
        todaysSteps = findViewById(R.id.stepstoday);
        totalSteps = findViewById(R.id.stepstotal);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        displayProgress(user);
    }

    public void displayProgress(User user){
        userName.setText(user.getName());
        age.setText(Integer.toString(user.getAge()));
        todaysSteps.setText(Integer.toString(user.getStepsToday()) );
        totalSteps.setText(Integer.toString(user.getStepsTotal()) );
    }

}
