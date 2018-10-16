package net.smimran.cloud_new;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity {
    AutoCompleteTextView category;
    EditText description;

    LinearLayout linearLayout;

    private FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Note");

        auth = FirebaseAuth.getInstance();

        category = findViewById(R.id.category);
        description = findViewById(R.id.description);

        linearLayout = (LinearLayout) findViewById(R.id.add_note_layout);


        String[] categoryStr = MainActivity.categoriesList.toArray(new String[MainActivity.categoriesList.size()]);
        ArrayAdapter <String> adapter = new ArrayAdapter <String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, categoryStr);
        category.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveNoteID:
                saveNote();
                return true;
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNote() {
        String categoryStr = category.getText().toString();
        String descriptionStr = description.getText().toString();
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        if (categoryStr.trim().isEmpty() || descriptionStr.trim().isEmpty()) {
            Snackbar.make(linearLayout, "Please insert a category and note", Snackbar.LENGTH_LONG).show();
            return;
        }

        FirebaseUser user = auth.getCurrentUser();

        CollectionReference notebookRef = FirebaseFirestore.getInstance().collection(user.getUid());
        notebookRef.add(new Note(categoryStr, descriptionStr, date, null));
        Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
        finish();
    }
}
