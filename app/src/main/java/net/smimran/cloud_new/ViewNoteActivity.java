package net.smimran.cloud_new;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class ViewNoteActivity extends AppCompatActivity {

    String noteid, category;

    Boolean isFABOpen = false;

    TextView categorytxtview, datetxtview, detailsnotetxtview;

    LinearLayout linearLayout;

    TextToSpeech mTTs;

    private FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FloatingActionButton fab, playfab, stopfab, settingfab;

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

        fab = findViewById(R.id.fab);
        playfab = findViewById(R.id.playfab);
        stopfab = findViewById(R.id.stopfab);
        settingfab = findViewById(R.id.settingfab);

        setLayout();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFABOpen) {
                    closefab();
                } else {
                    openfab();
                }
            }
        });


        mTTs = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result = mTTs.setLanguage(new Locale("bn_IN"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "Language is not supported", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });


        playfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
        });

        stopfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stoptalk();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setLayout();
    }

    public void setLayout() {
        FirebaseUser user = auth.getCurrentUser();
        db.document(user.getUid() + "/" + noteid).
                get().addOnSuccessListener(new OnSuccessListener <DocumentSnapshot>() {
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

    public void openfab() {
        isFABOpen = true;

        playfab.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        stopfab.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        settingfab.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }

    public void closefab() {
        isFABOpen = false;

        playfab.animate().translationY(0);
        settingfab.animate().translationY(0);
        stopfab.animate().translationY(0);
    }

    public void settingaudio(View view) {

        closefab();

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupview = layoutInflater.inflate(R.layout.audio_settings_popup, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean foucsable = true;
        final PopupWindow popupWindow = new PopupWindow(popupview, 1000, 1000, foucsable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        Button button_save = popupview.findViewById(R.id.button_save);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                closefab();
                popupWindow.dismiss();

                mTTs.stop();

                SeekBar mSeekBarPitch = popupview.findViewById(R.id.seek_bar_pitch);
                SeekBar mSeekBarSpeed = popupview.findViewById(R.id.seek_bar_speed);
                float pitch = (float) mSeekBarPitch.getProgress() / 50;
                if (pitch < 0.1) pitch = 0.1f;
                float speed = (float) mSeekBarSpeed.getProgress() / 50;
                if (speed < 0.1) speed = 0.1f;

                mTTs.setPitch(pitch);
                mTTs.setSpeechRate(speed);

                speak();



            }
        });


        Button closepopup = popupview.findViewById(R.id.closepopup);
        closepopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }


    public void speak() {

        closefab();

        mTTs.speak(detailsnotetxtview.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    }

    public void stoptalk() {
        closefab();
        if (mTTs != null) {
            mTTs.stop();
        }
    }

    @Override
    protected void onDestroy() {

        if (mTTs != null) {
            mTTs.stop();
            mTTs.shutdown();
        }

        super.onDestroy();
    }


}
