package net.smimran.cloud_note;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CategoryBaseNote extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth auth;

    NoteAdapter noteAdapter;

    RecyclerView recyclerView;

    String category;



    @Override
    public void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        category = getArguments().getString("CATEGORYNAME");

        auth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.fragment_all_note,container,false);

        recyclerView = view.findViewById(R.id.recyclerID);
        setUpRecycleView();

        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingactionbutton);

        return view;
    }

    public void setUpRecycleView() {
        FirebaseUser user = auth.getCurrentUser();

        Query query = db.collection(user.getUid()).whereEqualTo("category",category)
                .orderBy("created_at",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class).build();

        noteAdapter = new NoteAdapter(options, getActivity());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(noteAdapter);
    }

    public void callAddActivity(View view) {
        FirebaseUser user = auth.getCurrentUser();
        Intent intent = new Intent(getActivity(), AddNoteActivity.class);
        intent.putExtra("USERID", user.getUid());
        startActivity(intent);
    }
}
