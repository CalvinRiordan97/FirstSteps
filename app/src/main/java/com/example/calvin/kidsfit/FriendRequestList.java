package com.example.calvin.kidsfit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendRequestList extends AppCompatActivity {
    DatabaseReference myRef;
    ValueEventListener dbListener;
    User user;
    ArrayList<User> users;
    ArrayList<String> friends;
    ArrayAdapter<String> adapter;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request_list);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("User");

        myRef = FirebaseDatabase.getInstance().getReference();

        users = new ArrayList<>();
        friends = new ArrayList<>();


        myRef.child("Friends").child(user.getId()).addValueEventListener(dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friends.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(!(Boolean) ds.getValue())
                        friends.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        myRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                    if(friends.contains(ds.getValue(User.class).getId()))
                        users.add(ds.getValue(User.class));
                configAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void configAdapter(){
        list = findViewById(R.id.fList);

        ArrayList<String> fUID = new ArrayList<>();

        for(User u : users)
            fUID.add(u.getName());

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                fUID);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddFriendDialog afd = new AddFriendDialog();
                Bundle args = new Bundle();
                args.putInt("position", position);
                afd.setArguments(args);
                afd.show(getSupportFragmentManager(), "Add Friend Dialog");
            }
        });

    }


}
