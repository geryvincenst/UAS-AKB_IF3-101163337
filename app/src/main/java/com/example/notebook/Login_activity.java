package com.example.notebook;
//10116337 - Gery Gunawan AKB IF-3
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login_activity extends AppCompatActivity {

    EditText emailEditText1, passwordEditText1;
    Button loginBtn;
    ProgressBar progressBar;
    TextView create_account_btn;
    ImageView googleBtn;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText1 = findViewById(R.id.email_edit_text1);
        passwordEditText1 = findViewById(R.id.password_edit_text1);
        loginBtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progress_bar);
        create_account_btn = findViewById(R.id.create_account_text_btn);
        googleBtn = findViewById(R.id.signingoogle);

        loginBtn.setOnClickListener((view) -> loginUser());
        create_account_btn.setOnClickListener((v)->startActivity(new Intent(Login_activity.this,Create_account_activity.class)));


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,1000);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e){
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void navigateToSecondActivity(){
        finish();
        Intent intent = new Intent(Login_activity.this,MainActivity.class);
        startActivity(intent);
    }

    void loginUser(){
        String email = emailEditText1.getText().toString();
        String password = passwordEditText1.getText().toString();

        boolean isValidated = validateData(email,password);
        if(!isValidated){
            return;
        }

        loginAccountInFirebase(email,password);
    }

    void loginAccountInFirebase(String email, String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    //login is success
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        //goto mainActivity
                        startActivity(new Intent(Login_activity.this,MainActivity.class));
                    }else{
                        Utility.showToast(Login_activity.this,"Email not verified, Please verify your email");
                    }
                } else{
                  //login failed
                  Utility.showToast(Login_activity.this,task.getException().getLocalizedMessage());
                }
            }
        });
    }


    //validate the data that are input by user
    boolean validateData(String email,String password){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText1.setError("Email is invalid!");
            return false;
        }
        if(password.length()<6){
            passwordEditText1.setError("Password length is invalid");
            return false;
        }
        return true;
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }
}