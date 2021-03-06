package com.example.calvin.kidsfit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Shop extends AppCompatActivity {

    User user;

    TextView wallet;
    Button extraLives;
    Button skipLevel;
    Button convertSteps;

    int stepsToday, stepsTotal, balance;
    int skips, extraL;

    DatabaseReference myRef;
    ValueEventListener dbListener;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("User");
        wallet = findViewById(R.id.walletBalance);

        //Get the shop items
        extraLives = findViewById(R.id.extraLives);
        skipLevel = findViewById(R.id.skipLevel);
        convertSteps = findViewById(R.id.convert);

        //Initialize References
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(Shop.this, MainActivity.class));
                }
            }
        };
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);
        myRef = FirebaseDatabase.getInstance().getReference();
        firebaseUser = mAuth.getCurrentUser();

        myRef.child("Users").child(firebaseUser.getUid()).child("steps").addValueEventListener(dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                balance = Integer.parseInt(dataSnapshot.child("wallet").getValue().toString());
                stepsToday = Integer.parseInt(dataSnapshot.child("stepsToday").getValue().toString());
                stepsTotal = Integer.parseInt(dataSnapshot.child("stepsTotal").getValue().toString());
                wallet.setText(String.valueOf(balance));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.child("Users").child(firebaseUser.getUid()).child("powers").addValueEventListener(dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
;               skips = Integer.parseInt(dataSnapshot.child("skip").getValue().toString());
                extraL = Integer.parseInt(dataSnapshot.child("extraLives").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        extraLives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PowerUpDialog pud = new PowerUpDialog();
                Bundle args = new Bundle();
                args.putString("name", "Extra Lives");
                args.putString("path", "extraLives");
                args.putString("desc", "Increases the amount of lives you have by 2.");
                args.putString("cost", "150");
                args.putInt("wallet", balance);
                args.putInt("powerCount", extraL);
                pud.setArguments(args);
                pud.show(getSupportFragmentManager(), "Power Up Dialog");
                //myRef.child("Users").child(firebaseUser.getUid()).child("wallet").setValue(balance-20);
            }
        });

        skipLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PowerUpDialog pud = new PowerUpDialog();
                Bundle args = new Bundle();
                args.putString("name", "Skip Level");
                args.putString("path", "skip");
                args.putString("desc", "Skip the level you are on and go to the next one.");
                args.putString("cost", "100");
                args.putInt("wallet", balance);
                args.putInt("powerCount", skips);
                pud.setArguments(args);
                pud.show(getSupportFragmentManager(), "Power Up Dialog");
                //Toast.makeText(getApplicationContext(), "Skip Level", Toast.LENGTH_SHORT).show();
            }
        });

        convertSteps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myRef.child("Users").child(firebaseUser.getUid()).child("steps").child("wallet").setValue(balance+stepsToday);
            }
        });
    }
}
