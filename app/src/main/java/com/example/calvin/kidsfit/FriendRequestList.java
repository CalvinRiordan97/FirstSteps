package com.example.calvin.kidsfit;

import android.content.Intent;
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


        myRef.child("Users").addListenerForSingleValueEvent(dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User fUser = ds.getValue(User.class);
                    if(user.getFriendRequests().contains(fUser.getId()))
                        friends.add(fUser.getName());
                }
                configAdapter(friends);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void configAdapter(ArrayList<String> friends){
        list = findViewById(R.id.fList);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                friends);
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
