package net.smimran.cloud_note;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteHolder> {


    Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull final NoteHolder holder, final int position, @NonNull Note model) {
        holder.category.setText(model.getCategory());
        holder.description.setText(model.getDescription());
        holder.createdate.setText(model.getCreated_at());

        holder.deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Delete Note ");
                builder1.setMessage("Are you sure ? ");
                builder1.setCancelable(true);

                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getSnapshots().getSnapshot(position).getReference().delete();
                        Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show();
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
        });

        holder.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = holder.category.getText().toString();
                ((MainActivity) context).callCategoryFragment(data);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteid = getSnapshots().getSnapshot(position).getReference().getId();
                Intent intent = new Intent(context, UpdateNoteActivity.class);
                intent.putExtra("NOTEID", noteid);
                context.startActivity(intent);
            }
        });

        holder.sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareintent = new Intent();
                shareintent.setAction(Intent.ACTION_SEND);
                shareintent.putExtra(Intent.EXTRA_TEXT, holder.description.getText().toString());
                shareintent.setType("text/plan");
                context.startActivity(shareintent);
            }
        });
    }


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_note, viewGroup, false);
        return new NoteHolder(v);
    }
    class NoteHolder extends RecyclerView.ViewHolder {

        TextView createdate, description, category;
        ImageButton deletebtn, sharebtn;

        public NoteHolder(@NonNull final View itemView) {
            super(itemView);
            createdate = itemView.findViewById(R.id.create_dateID);
            description = itemView.findViewById(R.id.detailsID);
            category = itemView.findViewById(R.id.categoryID);

            deletebtn = itemView.findViewById(R.id.deletebtn);
            sharebtn = itemView.findViewById(R.id.sharebtnid);
        }
    }
}