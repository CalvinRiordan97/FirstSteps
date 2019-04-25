package com.example.calvin.kidsfit;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    boolean trackingSteps = false;
    SensorManager sensorManager;
    Sensor countSteps;
    TextView stepsText;
    TextView uID;
    int stepsTaken = 0;
    User user;
    double latitude, longitude;

    DatabaseReference myRef;
    ValueEventListener dbListener;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser firebaseUser;

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
                }
            }
        };

        //Initialize References
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);
        myRef = FirebaseDatabase.getInstance().getReference();


        if (mAuth.getCurrentUser() == null)
            AuthenticateUser();
        else
            firebaseUser = mAuth.getCurrentUser();

        //Get Sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepsText = findViewById(R.id.steps);
        uID = findViewById(R.id.uID);
        uID.setText("User ID: "+firebaseUser.getUid());


        //Saves the logged in user as a User object
        user = new User();
        // user.getCurrentUser(firebaseUser.getUid());
        myRef.child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                stepsText.setText("Hello "+user.getName()+", You have taken: "+String.valueOf(user.getStepsToday())+" steps today!");
                //Toast.makeText(getApplicationContext(), "Welcome, "+user.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Get Floating Action Buttons
        FloatingActionButton mTrack = findViewById(R.id.trackButton);
        FloatingActionButton mGame = findViewById(R.id.gameButton);
        FloatingActionButton mShop = findViewById(R.id.shopButton);

        //TODO: stop tracking location when tracking footsteps stops
        mTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTrackingSteps(v);
            }
        });

        mGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureMatchGame(v);
            }
        });

        mShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shop(v);
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        //mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                else {
                    Location location = locationResult.getLastLocation();
                    myRef.child("Users").child(firebaseUser.getUid()).child("Location").child("lat").setValue(location.getLatitude());
                    myRef.child("Users").child(firebaseUser.getUid()).child("Location").child("lng").setValue(location.getLongitude());
                }
            }};

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        requestLocationPermission();

    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle("Require Permission")
                        .setMessage("Nedd location for GeoFence")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(HomeActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLocation = location;
                                mLocation.setLatitude(location.getLatitude());
                                mLocation.setLongitude(location.getLongitude());

                                myRef.child("Users").child(firebaseUser.getUid()).child("Location").child("lat").setValue(mLocation.getLatitude());
                                myRef.child("Users").child(firebaseUser.getUid()).child("Location").child("lng").setValue(mLocation.getLongitude());

                            }
                            else
                                Toast.makeText(getApplicationContext(),"nope",Toast.LENGTH_SHORT).show();
                        }
                    });
            fusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }
            else{

            }
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_progress) {
            Intent intent = new Intent(this, MyProgress.class);
            intent.putExtra("User", user);
            startActivity(intent);

        } else if (id == R.id.nav_friendslist) {
            Intent intent = new Intent(this, FriendsList.class);
            intent.putExtra("User", user);
            startActivity(intent);

        } else if (id == R.id.nav_friendrequests) {
            Intent intent = new Intent(this, FriendRequestList.class);
            intent.putExtra("User", user);
            startActivity(intent);

        } else if (id == R.id.nav_fence) {
            Intent intent = new Intent(this, GeoFence.class);
            startActivity(intent);

        } else if (id == R.id.nav_signout) {
            fusedLocationClient.removeLocationUpdates(mLocationCallback);
            mAuth.signOut();
        }
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Start tracking footsteps
    public void startTrackingSteps(View view){
        if(!trackingSteps)
            trackingSteps = true;
        else
            trackingSteps = false;

        if(trackingSteps){
            countSteps = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            if(countSteps != null){
                sensorManager.registerListener(this, countSteps, SensorManager.SENSOR_DELAY_UI);
                Toast.makeText(getApplicationContext(), "Tracking Started", Toast.LENGTH_SHORT).show();
                }
            else
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
        else{
            sensorManager.unregisterListener(this);
            trackingSteps = false;
            Toast.makeText(getApplicationContext(), "Tracking Stopped", Toast.LENGTH_SHORT).show();
        }

    }

    //Stop tracking footsteps
    public void stopTrackingSteps(View view){
        sensorManager.unregisterListener(this);
        trackingSteps = false;
        Toast.makeText(getApplicationContext(), "Tracking Stopped", Toast.LENGTH_SHORT).show();
    }

    public void shop(View view){
        Intent intent = new Intent(this, Shop.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(trackingSteps == true){
            user.setStepsToday(user.getStepsToday()+1);
            myRef.child("Users").child(user.getId()).child("stepsToday").setValue(user.getStepsToday());
            stepsText.setText("Hello "+user.getName()+", You have taken: "+String.valueOf(user.getStepsToday())+" Today");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public void pictureMatchGame(View view){
        Intent intent = new Intent(this, PictureMatch.class);
        startActivity(intent);
    }

    public void AuthenticateUser(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
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
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        myRef.child("Users").child(firebaseUser.getUid()).child("Location").child("lat").setValue(mLocation.getLatitude());
        myRef.child("Users").child(firebaseUser.getUid()).child("Location").child("lng").setValue(mLocation.getLongitude());

    }
}