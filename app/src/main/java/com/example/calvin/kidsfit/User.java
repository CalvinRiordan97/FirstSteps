package com.example.calvin.kidsfit;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class User implements Parcelable {

    String id;
    String name;
    int age;
    HashMap<String, Boolean> friends;
    HashMap<String, Boolean> friendRequests;
    String email;
    int stepsToday;
    int stepsTotal;
    int highScore;
    HashMap<String, Integer> steps;

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    User user;
    ArrayList<User> users;

    public User(){

    }

    public User(String id, String name, int age, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.friends = new HashMap<>();
        this.friendRequests = new HashMap<>();
        this.stepsToday = 0;
        this.stepsTotal = 0;
        this.highScore = 0;
        this.steps = new HashMap<>();
    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        age = in.readInt();
        email = in.readString();
        friends = in.readHashMap(null);
        friendRequests = in.readHashMap(null);
        stepsToday = in.readInt();
        stepsTotal = in.readInt();
        highScore = in.readInt();
        steps = in.readHashMap(null);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public int getStepsToday() {
        return stepsToday;
    }

    public void setStepsToday(int stepsToday) {
        this.stepsToday = stepsToday;
    }

    public int getStepsTotal() {
        return stepsTotal;
    }

    public void setStepsTotal(int stepsTotal) {
        this.stepsTotal = stepsTotal;
    }

    public HashMap<String, Boolean>  getFriends() {
        return this.friends;
    }

    public void setFriends(HashMap<String, Boolean>  friends) {
        this.friends = friends;
    }

    public void setFriendRequests(HashMap<String, Boolean> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public void addFriend (String uid){
        this.friends.put(uid, true);
    }

    public void addFriendRequest (String uid){
        this.friendRequests.put(uid, false);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<String, Boolean>  getFriendRequests() {
        return friendRequests;
    }

    public int getWalletBalance(){
        return 0;
    }
    @Override
    public int describeContents() {
        return 0;
    }


    public HashMap<String, Integer> getSteps() {
        return steps;
    }

    public void setSteps(HashMap<String, Integer> steps) {
        this.steps = steps;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.name);
        parcel.writeInt(this.age);
        parcel.writeString(this.email);
        parcel.writeMap(this.friends);
        parcel.writeMap(this.friendRequests);
        parcel.writeInt(this.stepsToday);
        parcel.writeInt(this.stepsTotal);
        parcel.writeInt(this.highScore);
        parcel.writeMap(this.steps);
    }


}
