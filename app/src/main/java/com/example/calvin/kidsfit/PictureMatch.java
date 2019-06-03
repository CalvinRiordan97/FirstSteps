package com.example.calvin.kidsfit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PictureMatch extends AppCompatActivity {

    DatabaseReference myRef;
    ValueEventListener dbListener;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser firebaseUser;

    ImageView imgView = null;
    int countPair = 0;
    int maxPairs;

    int level = 0;
    int lives = 2;
    int score;

    TextView scoreDisplay;
    TextView levelDisplay;
    TextView livesDisplay;

    GridView gridView;

    int[] drawable = new int[]{R.drawable.blue_arrow, R.drawable.orange_cross, R.drawable.pink_diamond,
            R.drawable.purple_circle, R.drawable.star};


    int[] pos = {0,1,2,3,4,0,1,2,3,4};
    int currentPos = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_match);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(PictureMatch.this, MainActivity.class));
                }
            }
        };

        //Initialize References
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);
        myRef = FirebaseDatabase.getInstance().getReference();
        firebaseUser = mAuth.getCurrentUser();

        levelDisplay = findViewById(R.id.levelText);

        setUpGame();
        maxPairs = 5;

        livesDisplay = findViewById(R.id.livesText);
        livesDisplay.setText("Lives: "+String.valueOf(lives));

        score = 0;
        scoreDisplay = findViewById(R.id.playerScore);
        scoreDisplay.setText("Score: "+String.valueOf(score));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentPos < 0)
                {
                    currentPos = position;
                    imgView = (ImageView) view;
                    ((ImageView)view).setImageResource(drawable[pos[position]]);
                }
                else {
                    if(currentPos == position)
                    {
                        ((ImageView)view).setImageResource(R.drawable.avatar);
                    }
                    else if(pos[currentPos] != pos[position]){
                        imgView.setImageResource(R.drawable.avatar);
                        lives--;
                        if(lives == -1)
                            gameOver();
                        else
                            livesDisplay.setText("Lives: "+String.valueOf(lives));
                            Toast.makeText(getApplicationContext(),"No Match!",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        ((ImageView)view).setImageResource(drawable[pos[position]]);
                         countPair++;
                         score += 5;
                         scoreDisplay.setText("Score: "+String.valueOf(score));
                         Toast.makeText(getApplicationContext(),"Match!",Toast.LENGTH_SHORT).show();
                         if(countPair == maxPairs){
                             setUpGame();
                         }
                    }
                    currentPos = -1;
                }
            }
        });
    }

    public void setUpGame(){
        level++;
        levelDisplay.setText("Level: "+String.valueOf(level));
        gridView = findViewById(R.id.gridView);
        ImageAdapter imageAdapter = new ImageAdapter(this);
        gridView.setAdapter(imageAdapter);
        countPair = 0;
    }

    public  void gameOver(){
        GameOverDialog over = new GameOverDialog();
        Bundle args = new Bundle();
        args.putString("score", String.valueOf(score));
        over.setArguments(args);
        over.show(getSupportFragmentManager(), "Game Over Dialog");

        level = 1;
        levelDisplay.setText("Level: "+String.valueOf(level));

        score = 0;
        scoreDisplay.setText("Score: "+String.valueOf(score));

        lives = 2;
        livesDisplay.setText("Lives: "+String.valueOf(lives));

        gridView = findViewById(R.id.gridView);
        ImageAdapter imageAdapter = new ImageAdapter(this);
        gridView.setAdapter(imageAdapter);
        countPair = 0;
    }

    public void skipLevel(View view){
        myRef.child("Users").child(firebaseUser.getUid()).child("powers").child("skip").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = Integer.parseInt(dataSnapshot.getValue().toString());
                if(count > 0){
                    myRef.child("Users").child(firebaseUser.getUid()).child("powers").child("skip").setValue(count-1);
                    setUpGame();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void extraLife(View view){
        myRef.child("Users").child(firebaseUser.getUid()).child("powers").child("extraLives").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = Integer.parseInt(dataSnapshot.getValue().toString());
                if(count > 0){
                    myRef.child("Users").child(firebaseUser.getUid()).child("powers").child("extraLives").setValue(count-1);
                    lives++;
                    livesDisplay.setText("Lives: "+String.valueOf(lives));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
