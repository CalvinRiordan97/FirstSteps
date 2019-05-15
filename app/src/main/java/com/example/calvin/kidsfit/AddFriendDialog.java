package com.example.calvin.kidsfit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddFriendDialog extends AppCompatDialogFragment {

    EditText editTextEmail;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    ArrayList<String> users;
    String friendToAdd;
    int pos;
    //ArrayList<User> friends;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        users = new ArrayList<>();

        //friends = getArguments().getParcelableArrayList("friends");
        pos = getArguments().getInt("position");
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("Friends").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    users.add(ds.getKey().toString());
                }
                friendToAdd = users.get(pos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_friend_dialog, null);
        builder.setView(view)
                .setTitle("Friend Request")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myRef.child("Friends").child(friendToAdd).child(firebaseUser.getUid()).setValue(true);
                        myRef.child("Friends").child(firebaseUser.getUid()).child(friendToAdd).setValue(true);
                    }
                });

        editTextEmail = view.findViewById(R.id.userEmailText);
        return builder.create();

    }
}
