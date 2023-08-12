package com.example.notebook;
//10116337 - Gery Gunawan AKB IF-3
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splashscreen_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currenUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currenUser==null){
                    startActivity(new Intent(Splashscreen_activity.this,Create_account_activity.class));
                }else{
                    startActivity(new Intent(Splashscreen_activity.this,Login_activity.class));
                }
                finish();
            }
        },1000);
    }
}