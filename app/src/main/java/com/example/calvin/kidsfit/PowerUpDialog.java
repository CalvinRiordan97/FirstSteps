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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PowerUpDialog  extends AppCompatDialogFragment {

    TextView description;
    TextView price;
    int balance;
    String cost;

    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String powerUpName = getArguments().getString("name");
        String desc = getArguments().getString("desc");
        cost = getArguments().getString("cost");

        balance = getArguments().getInt("wallet");

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.power_up_dialog, null);

        description = view.findViewById(R.id.description);
        price = view.findViewById(R.id.price);

        description.setText(desc);
        price.setText("Price: "+cost+"p");

        builder.setView(view)
                .setTitle(powerUpName)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int newBal = balance - Integer.parseInt(cost);
                        myRef.child("Users").child(firebaseUser.getUid()).child("wallet").setValue(newBal);
                    }
                });

        return builder.create();
    }
}
