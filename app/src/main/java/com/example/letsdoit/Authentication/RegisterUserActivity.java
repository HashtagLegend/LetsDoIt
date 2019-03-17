package com.example.letsdoit.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letsdoit.Fragments.VerifyUserDialogFragment;
import com.example.letsdoit.MainActivity;
import com.example.letsdoit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterUserActivity extends BaseActivity implements View.OnClickListener{

    public static final String MINE = "MINE";
    //Firebase
    private FirebaseAuth mAuth;


    Button btnCreateUserAC, btnLoginAC, btnSignOutAC, btnVerifyEmailAC;
    EditText registerEmail, registerPassword;
    TextView statusField, detailField, titleTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //Firebase
        mAuth = FirebaseAuth.getInstance();


        //EditT  TextFields
        registerEmail = (EditText) findViewById(R.id.registerEmail);
        registerPassword = (EditText) findViewById(R.id.registerPassword);

        //Textfields
        statusField = (TextView) findViewById(R.id.status);
        detailField = (TextView) findViewById(R.id.detail);

        //Buttons
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnCreateUser).setOnClickListener(this);
        findViewById(R.id.btnSignOut).setOnClickListener(this);
        findViewById(R.id.btnVerifyEmail).setOnClickListener(this);

        btnLoginAC = (Button) findViewById(R.id.btnLogin);
        btnCreateUserAC = (Button) findViewById(R.id.btnCreateUser);
        btnSignOutAC = (Button) findViewById(R.id.btnSignOut);
        btnVerifyEmailAC = (Button) findViewById(R.id.btnVerifyEmail);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }



    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.btnCreateUser) {
            createAccount(registerEmail.getText().toString(), registerPassword.getText().toString());
        } else if (i == R.id.btnLogin) {
            signIn(registerEmail.getText().toString(), registerPassword.getText().toString());
        } else if (i == R.id.btnSignOut) {
            signOut();
        } else if (i == R.id.btnVerifyEmail) {
            sendEmailVerification();
        }


    }





    private void createAccount(String email, String password) {
        Log.d(MINE, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(MINE, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            showVerificationDialog("Bekræft venligst email", "Vi har sendt dig en email, hvor du bedes bekræfte din email adresse!  Hvis du ikke modtager en email kan du sende den igen!", "Send igen", "Login");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(MINE, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterUserActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email =registerEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            registerEmail.setError("Required.");
            valid = false;
        } else {
            registerEmail.setError(null);
        }

        String password = registerPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            registerPassword.setError("Required.");
            valid = false;
        } else {
            registerPassword.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            statusField.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            detailField.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            titleTextView = findViewById(R.id.titleTextView);
            titleTextView.setText("Login");
            //TODO fix theese
            /*
            btnCreateUserAC.findViewById(R.id.btnCreateUser);
            btnCreateUserAC.setVisibility(View.GONE);
            */

            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);

            findViewById(R.id.btnVerifyEmail).setEnabled(!user.isEmailVerified());

        } else {
            statusField.setText(R.string.signed_out);
            detailField.setText(null);

            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.signedInButtons).setVisibility(View.GONE);

        }


    }

    private void signIn(String email, String password) {
        Log.d(MINE, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(MINE, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            navigateIfVerified(user);
                            //ToDo Shows up even right before navigation if user.isEmailVerified;
                            showVerificationDialog("Bekræft venligst email", "Vi har sendt dig en email, hvor du bedes bekræfte din email adresse!  Hvis du ikke modtager en email kan du sende den igen!", "Send igen", "Login");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(MINE, "signInWithEmail:failure", task.getException());
                            Toast.makeText(RegisterUserActivity.this, "Fejl! Kontrollér at brugernavn og password er korrekt!",
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            statusField.setText("Fejl! Kontrollér at brugernavn og password er korrekt!");
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]



    }

    private void signOut() {
        Log.d(MINE, "signOut: ");
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.btnVerifyEmail).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.btnVerifyEmail).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterUserActivity.this,
                                    "Email bekræftelse er sendt til " + user.getEmail(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Log.e(MINE, "sendEmailVerification", task.getException());
                            Toast.makeText(RegisterUserActivity.this,
                                    "Kunne ikke sende email! Kontakt venligst kundesupport på 30488592.",
                                    Toast.LENGTH_LONG).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    public void navigateIfVerified(FirebaseUser user){
        if(user.isEmailVerified()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {
            //TODO Create dialogbox to verify and sign in
            Toast.makeText(this, "Du skal verificere din mail", Toast.LENGTH_LONG).show();
        }
    }

    public void showVerificationDialog(String title, String message, String resendButtonText, String loginButtonText){
        FirebaseUser user = mAuth.getCurrentUser();

        if(user.isEmailVerified()){
            VerifyUserDialogFragment fragment = VerifyUserDialogFragment.newInstance(title, message, resendButtonText, loginButtonText);
            fragment.show(getSupportFragmentManager(), "dialog");
        }
        else{
            navigateIfVerified(user);
        }

    }

    public void resendVerificationEmail(){
        sendEmailVerification();
    }

    public void loginWhenVerified(){
        FirebaseUser user = mAuth.getCurrentUser();
        navigateIfVerified(user);
    }


}
