package com.example.letsdoit.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.letsdoit.MainActivity;
import com.example.letsdoit.Model.User;
import com.example.letsdoit.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    //Firebase
    FirebaseDatabase database;
    DatabaseReference users;
    private FirebaseAuth mAuth;

    Button btnLogin, btnGoToRegisterUser;
    EditText sndUsername, sndPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Firebase authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_login);

        btnGoToRegisterUser = (Button) findViewById(R.id.btnGoToRegisterUser);

        btnGoToRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterUserActivity.class);
                startActivity(intent);
            }
        });

        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        sndUsername = (EditText) findViewById(R.id.sendUserName);
        sndPassword = (EditText) findViewById(R.id.sendPassword);

        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(sndUsername.getText().toString(), sndPassword.getText().toString());
                Log.d("MINE", "onClick: Clicked");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn(final String username, final String password) {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MINE", "onDataChange: got in");
                if(dataSnapshot.child(username).exists()){
                    if(!username.isEmpty()){
                        User login = dataSnapshot.child(username).getValue(User.class);
                        if(login.getPassword().equals(password)){
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                            navigateOnLogin();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Password is incorrect!", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "There is no user registered with that name!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

            public void navigateOnLogin(){
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void updateUI(FirebaseUser user){
        sndUsername = (EditText) findViewById(R.id.sendUserName);
        sndUsername.setText(user.toString());
    }

}
