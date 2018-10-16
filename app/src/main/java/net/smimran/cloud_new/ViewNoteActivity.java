package net.smimran.cloud_new;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewNoteActivity extends AppCompatActivity {

    String noteid, category;

    TextView categorytxtview,datetxtview,detailsnotetxtview;

    LinearLayout linearLayout;

    private FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);


        noteid = getIntent().getStringExtra("NOTEID");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Note");

        auth = FirebaseAuth.getInstance();

        categorytxtview = findViewById(R.id.showcategoryid);
        datetxtview = findViewById(R.id.create_dateID);
        detailsnotetxtview = findViewById(R.id.notedetailsid);

        setLayout();
    }


    public void setLayout() {
        FirebaseUser user = auth.getCurrentUser();
        db.document(user.getUid() + "/" + noteid).
                get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Note note = documentSnapshot.toObject(Note.class);

                categorytxtview.setText(note.getCategory());
                detailsnotetxtview.setText(note.getDescription());
                datetxtview.setText(note.getCreated_at());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editNoteID:
                Intent intent = new Intent(this, UpdateNoteActivity.class);
                intent.putExtra("NOTEID", noteid);
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                break;
            case R.id.shareNoteID:
                Intent shareintent = new Intent();
                shareintent.setAction(Intent.ACTION_SEND);
                shareintent.putExtra(Intent.EXTRA_TEXT, detailsnotetxtview.getText().toString());
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
