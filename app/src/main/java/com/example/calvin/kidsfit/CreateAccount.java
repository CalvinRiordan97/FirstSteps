package com.example.calvin.kidsfit;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccount extends AppCompatActivity {

    EditText firstName;
    EditText secondName;
    EditText email;
    EditText birthDate;
    EditText password;
    DatabaseReference myRef;
    Button createAccountBtn;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    String newFirstName;
    String newSecondName;
    String newEmail;
    String newBirthDay;
    String newPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //Get EditText Fields
        firstName = (EditText) findViewById(R.id.userFirstName);
        secondName = (EditText) findViewById(R.id.userSecondName);
        email = (EditText) findViewById(R.id.eMail);
        birthDate = (EditText) findViewById(R.id.dateBirth);
        password = (EditText) findViewById(R.id.newPassword);

        //Get Create Account Button
        createAccountBtn = (Button) findViewById(R.id.createNewAccount);

        //Init Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

    }

    //Create Account Function
    public void createNewAccount(View view){
        newFirstName = firstName.getText().toString();
        newSecondName = secondName.getText().toString();
        newEmail = email.getText().toString();
        newBirthDay = birthDate.getText().toString();
        newPass = password.getText().toString();

        mAuth.createUserWithEmailAndPassword(newEmail, newPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful())
                    Toast.makeText(getApplicationContext(), "Failed to create account", Toast.LENGTH_SHORT).show();
                else{
                    mAuth.signInWithEmailAndPassword(newEmail, newPass);
                    firebaseUser = mAuth.getCurrentUser();
                    User newUser = new User(firebaseUser.getUid(), newFirstName+newSecondName, Integer.parseInt(newBirthDay), newEmail);
                    newUser.addFriend("test");
                    newUser.addFriendRequest("test");
                    myRef.child("Users").child(firebaseUser.getUid()).setValue(newUser);
                }

            }
        });
    }
}
