package com.example.notebook;
// 10116337 - Gery Gunawan - AKB IF3
import static android.content.ContentValues.TAG;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addNotebtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    NoteAdapter noteAdapter;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

                addNotebtn = findViewById(R.id.add_note_btn);
        recyclerView = findViewById(R.id.recyler_view);
        menuBtn = findViewById(R.id.menu_btn1);

        addNotebtn.setOnClickListener((v)->startActivity(new Intent(MainActivity.this,NoteDetailActivity.class)));
        menuBtn.setOnClickListener((v)->showMenu());
        setupRecyclerView();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_bar);
        bottomNavigationView.setSelectedItemId(R.id.homes);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        System.out.println(token);
                        Toast.makeText(MainActivity.this, "Your device registration token" + token,
                                Toast.LENGTH_SHORT).show();

                                            }
                });


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);


        bottomNavigationView.setOnItemSelectedListener(item -> {
         switch (item.getItemId()){
             case R.id.homes:
                 return true;
             case R.id.profiles:
                 startActivity(new Intent(getApplicationContext(), Profile_activity.class));
                 overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                 finish();
                 return true;
             case R.id.infos:
                 startActivity(new Intent(getApplicationContext(), Information_Activity.class));
                 overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                 finish();
                 return true;
             case R.id.logouts:
                 signOut();
                 Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();
                 finish();
         }
         return false;
        });
    }


    void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(MainActivity.this, Login_activity.class));
            }
        });
    }

    void showMenu(){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this,menuBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this,Login_activity.class));
                    return true;
                }
                return false;
            }
        });
    }
    void setupRecyclerView(){
        Query query = Utility.getcollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note>options = new FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(query,Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options,this);
        recyclerView.setAdapter(noteAdapter);
    }
    protected void onStart(){
        super.onStart();
        noteAdapter.startListening();
    }
    protected void onStop(){
        super.onStop();
        noteAdapter.startListening();
    }
    protected void onResume(){
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}