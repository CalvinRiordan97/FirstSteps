package com.example.calvin.kidsfit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameOverDialog extends AppCompatDialogFragment {

    DatabaseReference myRef;
    ValueEventListener dbListener;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser firebaseUser;

    TextView scoreText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String score = getArguments().getString("score");

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("Users").child(firebaseUser.getUid()).child("HighScore").setValue(score);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.game_over_dialog, null);

        scoreText = view.findViewById(R.id.finalScore);
        scoreText.setText(score);

        builder.setView(view)
                .setTitle("Game Over")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }
}
