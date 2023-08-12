package com.example.notebook;
//10116337 - Gery Gunawan AKB IF-3
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Create_account_activity extends AppCompatActivity {

    EditText emailEdittext,passwordEdittext,confirmPasswordEdittext;
    Button createAccountbtn;
    ProgressBar progressBar;
    TextView loginBtnTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEdittext = findViewById(R.id.email_edit_text);
        passwordEdittext = findViewById(R.id.password_edit_text);
        confirmPasswordEdittext = findViewById(R.id.confirm_password_edit_text);
        createAccountbtn = findViewById(R.id.create_account_btn);
        progressBar = findViewById(R.id.progress_bar);
        loginBtnTextview = findViewById(R.id.login_text_btn);

        createAccountbtn.setOnClickListener(v -> createAccount());
        loginBtnTextview.setOnClickListener((v)->startActivity(new Intent(Create_account_activity.this,Login_activity.class)));
    }

    void createAccount(){
        String email = emailEdittext.getText().toString();
        String password = passwordEdittext.getText().toString();
        String confirmPassword = confirmPasswordEdittext.getText().toString();

        boolean isValidated = validateData(email,password,confirmPassword);
        if(!isValidated){
            return;
        }
        createAccountInFirebase(email,password);
    }

    void createAccountInFirebase(String email, String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Create_account_activity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //creating account is done
                            Utility.showToast(Create_account_activity.this, "Succesfully create account, Check email to verify");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }
                        else{
                            //failure
                            Utility.showToast(Create_account_activity.this,task.getException().getLocalizedMessage());
                        }

                    }
                }
        );
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountbtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            createAccountbtn.setVisibility(View.VISIBLE);
        }
    }

    //validate the data that are input by user
    boolean validateData(String email,String password, String confirmPassword){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdittext.setError("Email is invalid!");
            return false;
        }
        if(password.length()<6){
            passwordEdittext.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confirmPassword)){
            confirmPasswordEdittext.setError("Password not matched");
            return false;
        }
        return true;
    }
}