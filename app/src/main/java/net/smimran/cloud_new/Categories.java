package net.smimran.cloud_new;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import net.smimran.cloud_new.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Categories extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth auth;

    CategoriesAdapter categoryAdapter;

    RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        recyclerView = view.findViewById(R.id.recyclerID);
        setRecyclerView2();
        return view;
    }


    public void setRecyclerView2() {
        final FirebaseUser user = auth.getCurrentUser();
        db.collection(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            int dontAdd;
            ArrayList <String> categoriesList = new ArrayList <String>();
            int indexCounter;

            Map <String, Object> categoryDatatmp;

            ArrayList <Map <String, Object>> categoryData = new ArrayList <Map <String, Object>>();

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    categoryDatatmp = new HashMap <>();

                    Note note = documentSnapshot.toObject(Note.class);
                    dontAdd = 0;

                    for (int i = 0; i < categoriesList.size(); i++) {
                        if (categoriesList.get(i).toLowerCase().trim().equals(note.getCategory().toLowerCase().trim())) {
                            dontAdd = 1;
                            break;
                        }
                    }

                    if (dontAdd == 0) {
                        categoriesList.add(note.getCategory().trim());
                        categoryDatatmp.put("numberofnote", queryDocumentSnapshots.size());
                        categoryDatatmp.put("category", note.getCategory());
                        categoryData.add(categoryDatatmp);
                    }
                }

                categoryAdapter = new CategoriesAdapter(getActivity(), categoryData);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                recyclerView.setAdapter(categoryAdapter);
            }
        });
    }


//    public void setUpRecycleView() {
//        FirebaseUser user = auth.getCurrentUser();
//
//        for (int i = 0; i < MainActivity.categoriesList.size(); i++) {
//
//            categoryDatatmp = new HashMap <>();
//            categoryDatatmp.put("numberofnote", "5");
//            categoryDatatmp.put("category", MainActivity.categoriesList.get(i));
//            categoryData.add(categoryDatatmp);
//
//        }
//
//        categoryAdapter = new CategoriesAdapter(getActivity(), categoryData);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//        recyclerView.setAdapter(categoryAdapter);
//    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        db.collection(user.getUid()).addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), "Error while loading!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (queryDocumentSnapshots.isEmpty()) {

                    } else {
                        setRecyclerView2();
                    }
                }
            }
        });
    }
}
