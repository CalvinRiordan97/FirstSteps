package com.example.calvin.kidsfit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendsList extends AppCompatActivity {

    DatabaseReference myRef;
    ValueEventListener dbListener;
    User user;
    ArrayList<User> users;
    ArrayList<String> friends;
    ArrayAdapter<String> adapter;
    ListView list;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("User");


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        myRef = FirebaseDatabase.getInstance().getReference();

        users = new ArrayList<>();
        friends = new ArrayList<>();

        myRef.child("Friends").child(user.getId()).addListenerForSingleValueEvent(dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friends.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if((Boolean) ds.getValue())
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
                configAdapter(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        myRef.child("Users").addValueEventListener(dbListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                friends.clear();
//                user = dataSnapshot.child(user.getId()).getValue(User.class);
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    User fUser = ds.getValue(User.class);
//
//                    if(user.getId().equals(fUser.getId())){
//                        if(user.getFriends() != null)
//                            users = fUser.getFriends();
//                        else
//                            break;
//
//                        for (DataSnapshot ds1 : dataSnapshot.getChildren()){
//                            User fUser1 = ds1.getValue(User.class);
//                            if(users.containsKey(fUser1.getId()))
//                                friends.add(fUser1);
//                        }
//                    }
//
//                }
//                configAdapter(friends);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    public void configAdapter(final ArrayList<User> friends){
        intent = new Intent(this, MyProgress.class);
        list = findViewById(R.id.fList);
        ArrayList<String> fUID = new ArrayList<>();

        for(User u : friends)
            fUID.add(u.getName());

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                fUID);

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra("User", friends.get(position));
                startActivity(intent);
            }
        });
    }

    private void openDialog() {
        FriendRequestDialog frd = new FriendRequestDialog();
        Bundle args = new Bundle();
        frd.setArguments(args);
        frd.show(getSupportFragmentManager(), "Friend Request Dialog ");
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
