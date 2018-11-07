package net.smimran.cloud_new;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdateNoteActivity extends AppCompatActivity {

    String noteid, createdate;

    AutoCompleteTextView category;
    EditText description;

    LinearLayout linearLayout;

    private FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);

        noteid = getIntent().getStringExtra("NOTEID");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Update Note");

        auth = FirebaseAuth.getInstance();

        category = findViewById(R.id.category);
        description = findViewById(R.id.description);

        setLayout();

        linearLayout = (LinearLayout) findViewById(R.id.add_note_layout);

    }


    public void setLayout() {
        FirebaseUser user = auth.getCurrentUser();
        db.document(user.getUid() + "/" + noteid).
                get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Note note = documentSnapshot.toObject(Note.class);

                category.setText(note.getCategory());
                description.setText(note.getDescription());
                createdate = note.getCreated_at();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveNoteID:
                updateNote();
                return true;
            case android.R.id.home:
                finish();
                break;
            case R.id.shareNoteID:
                Intent shareintent = new Intent();
                shareintent.setAction(Intent.ACTION_SEND);
                shareintent.putExtra(Intent.EXTRA_TEXT, category.getText().toString());
                shareintent.setType("text/plan");
                startActivity(shareintent);
                break;
            case R.id.deleteNoteID:
                deletenote();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


    public void updateNote() {
        String descriptionStr = description.getText().toString();
        String categoryStr = category.getText().toString().trim();
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        if (categoryStr.trim().isEmpty() || descriptionStr.trim().isEmpty()) {
            Snackbar.make(linearLayout, "Please insert a category and note", Snackbar.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> note = new HashMap<>();
        note.put("category", categoryStr);
        note.put("description", descriptionStr);
        note.put("update_at", date);


        FirebaseUser user = auth.getCurrentUser();
        db.document(user.getUid() + "/" + noteid).update(note);

        Toast.makeText(this, "Note updted", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void deletenote() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Delete Note ");
        builder1.setMessage("Are you sure ? ");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            FirebaseUser user = auth.getCurrentUser();

            public void onClick(DialogInterface dialog, int id) {
                db.document(user.getUid() + "/" + noteid).delete().addOnSuccessListener(new OnSuccessListener <Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Note deleted ", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
