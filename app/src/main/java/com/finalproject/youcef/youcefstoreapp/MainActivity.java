package com.finalproject.youcef.youcefstoreapp;

/*
*
* @uthor: Youcef O'Connor
* Date: 02/12/16
*/

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1; //RC stands for Request Code

    private String pUsername;

    ////////using classes from the FirebaseDatabase API////////////////////////////////////
    ///////These are Firebase instance variables /////////////////////////////////////////

    //entry point for the app to access the database
    private FirebaseDatabase pFirebaseDatabase;

    //DatabaseReference object is a class that references a spacific part of the database
    private DatabaseReference pMessagesDatabaseReference; //referencing the messaging part of the database

    //read from the messages node on the database
    private ChildEventListener pChildEventListener;

    //Authentication Instance variables
    //This is where user authentication details are stored in the Firebase database
    private FirebaseAuth pFirebaseAuth;

    //This variable checks for updates in FirebaseAuth database
    private FirebaseAuth.AuthStateListener pAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Anonymous user is used when authentication is deactivated
        pUsername = ANONYMOUS;

        pFirebaseDatabase = FirebaseDatabase.getInstance();
        pFirebaseAuth = FirebaseAuth.getInstance();

        Button next = (Button) findViewById(R.id.add_prodBtn);
        next.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), AddProductImage.class);
                startActivityForResult(myIntent, 0);
            }

        });

        //this will let system know if the user authentic or not right away
        pAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    //user is signed in
                    Toast.makeText(MainActivity.this, "Signed in successful!", Toast.LENGTH_SHORT).show();
                    onSignedIInitialize(user.getDisplayName());
                } else {

                    // /user is not signed in
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);
                }

            }
        };
    }

    //Toast used to inform user of login status
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if (requestCode == RESULT_OK){
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            }else if (requestCode == RESULT_CANCELED){
                Toast.makeText(this, "Sign in Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Displaying the main menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    //Activating Sign out option in main menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sing_out_menu:
                //Sign out option
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //this pauses the login procedure at the launch (if user is already logged in)
    @Override
    protected void onPause() {
        super.onPause();
        if (pAuthStateListener != null) {
            pFirebaseAuth.removeAuthStateListener(pAuthStateListener);
        }
        detachDatabaseReadListener();
    }

    //this resumes the login procedure at the launch (if the user is not logged on)
    @Override
    protected void onResume() {
        super.onResume();
        pFirebaseAuth.addAuthStateListener(pAuthStateListener);
    }

    //Retrieving the username
    private void onSignedIInitialize(String username) {
        pUsername = username;
        attachDatabaseReadListener(); // calls the attachDatabaseReadListener method
    }

    private void onSignedOutCleanup() {
        pUsername = ANONYMOUS;
    }

    private void attachDatabaseReadListener() {
        if (pChildEventListener == null) {
        }

        }


    private void detachDatabaseReadListener() {
        if (pChildEventListener != null) {
            pMessagesDatabaseReference.removeEventListener(pChildEventListener);
            pChildEventListener = null;
        }

    }
}
