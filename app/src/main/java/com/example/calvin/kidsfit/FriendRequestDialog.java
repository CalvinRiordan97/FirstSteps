package com.example.calvin.kidsfit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendRequestDialog extends AppCompatDialogFragment {

    EditText editTextEmail;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    ArrayList<User> users;
    //ArrayList<User> friends;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //friends = getArguments().getParcelableArrayList("friends");
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    users.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.friend_request_dialog, null);
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
                        String userEmail = editTextEmail.getText().toString();
                        for(User user : users){
                            if(user.getEmail().equalsIgnoreCase(userEmail)){
                                myRef.child("Friends").child(user.getId()).child(firebaseUser.getUid()).setValue(false);
//                                friends.add(user.getId());
//                                myRef.child("Users").child(firebaseUser.getUid()).child("friends").setValue(friends);
                            }
                        }
                    }
                });

        editTextEmail = view.findViewById(R.id.userEmailText);
        return builder.create();
    }
}
