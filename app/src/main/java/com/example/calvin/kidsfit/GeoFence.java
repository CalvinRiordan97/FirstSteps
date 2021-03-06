package com.example.calvin.kidsfit;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class GeoFence extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, OnMapReadyCallback {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser firebaseUser;
    DatabaseReference myRef;
    ValueEventListener dbListener;

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient fusedLocationClient;
    LatLng userLL;
    double latitude;
    double longitude;
    String name;

    ArrayList<String> friends;
    ArrayList<User> users;
    HashMap<String, LatLng> friendsLocations;

    String friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_geo_fence);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(GeoFence.this, MainActivity.class));
                }
            }
        };

        //Initialize References
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);
        myRef = FirebaseDatabase.getInstance().getReference();


        firebaseUser = mAuth.getCurrentUser();

        //Initialize hash map
        friendsLocations = new HashMap<>();

        //Get list of the users friends
        friends = intent.getStringArrayListExtra("friends");

        for(String f : friends){
            friend = f;
            myRef.child("Users").child(f).child("Location").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    latitude = Double.parseDouble(dataSnapshot.child("lat").getValue().toString());
                    longitude = Double.parseDouble(dataSnapshot.child("lng").getValue().toString());
                    LatLng ll = new LatLng(latitude,longitude);
                    updateMap(ll, friend);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        name = intent.getStringExtra("name");
        myRef.child("Users").child(firebaseUser.getUid()).child("Location").addValueEventListener(dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                latitude = Double.parseDouble(dataSnapshot.child("lat").getValue().toString());
                longitude = Double.parseDouble(dataSnapshot.child("lng").getValue().toString());
                LatLng ll = new LatLng(latitude,longitude);
                userLL = ll;
                updateMap(ll, firebaseUser.getUid());

//                String latitudeS = dataSnapshot.child("lat").getValue().toString();
//                String longitudeS = dataSnapshot.child("lng").getValue().toString();
//                latitude = Double.parseDouble(latitudeS);
//                longitude = Double.parseDouble(longitudeS);
//
//                LatLng home = new LatLng(latitude,
//                        longitude);
//                mMap.clear();
//                mMap.addMarker(new MarkerOptions().position(home).title(name));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(home));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(18);
        mMap.setMinZoomPreference(18);
//        LatLng home = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(home).title("Ayyyyyy"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(home));
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng home = new LatLng(latitude,
                longitude);
        mMap.addMarker(new MarkerOptions().position(home).title("Ayyyyyy"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(home));
    }

    public void updateMap(LatLng ll, String f){
        friendsLocations.put(f, ll);
        mMap.clear();
        for(String key : friendsLocations.keySet()){
            mMap.addMarker(new MarkerOptions().position(friendsLocations.get(key)).title(key));
            if(userLL != null)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLL));
        }
    }
}
