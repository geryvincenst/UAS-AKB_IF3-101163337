package com.example.notebook;
//10116337 - Gery Gunawan AKB IF-3
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    ImageButton saveBtn;
    TextView pagettitleTextView, deleteNoteTextViewbtn;
    String title,content,docId;
    boolean isEditMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        titleEditText = findViewById(R.id.note_title_text);
        contentEditText = findViewById(R.id.note_content_text);
        saveBtn = findViewById(R.id.save_note_btn);
        pagettitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewbtn = findViewById(R.id.delete_note);

        //receive data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");


       if(docId!=null && !docId.isEmpty()){
           isEditMode = true;
       }

        titleEditText.setText(title);
        contentEditText.setText(content);
        if(isEditMode){
            pagettitleTextView.setText("Edit your note");
            deleteNoteTextViewbtn.setVisibility(View.VISIBLE);
        }

        saveBtn.setOnClickListener(view -> saveNote());
        deleteNoteTextViewbtn.setOnClickListener(view -> deleteNoteFromFirebase());
    }

    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        if(noteTitle==null || noteTitle.isEmpty()){
            titleEditText.setError("Title is required");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }
    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            //update the note
            documentReference = Utility.getcollectionReferenceForNotes().document(docId);
        }else{
            //create new note
            documentReference = Utility.getcollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is added
                    Utility.showToast(NoteDetailActivity.this,"Note Added Successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailActivity.this,"Failed while adding note");
                }
            }
        });
    }
    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
            documentReference = Utility.getcollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is deleted
                    Utility.showToast(NoteDetailActivity.this,"Note deleted Successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailActivity.this,"Failed while deleting note");
                }
            }
        });

    }
}