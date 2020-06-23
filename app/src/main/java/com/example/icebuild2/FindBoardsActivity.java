package com.example.icebuild2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindBoardsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView findBoardsRecyleList;
    private DatabaseReference boardsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_boards);

        boardsRef= FirebaseDatabase.getInstance().getReference().child("Boards");

        findBoardsRecyleList=(RecyclerView)findViewById(R.id.find_boards_recycle_list);
        findBoardsRecyleList.setLayoutManager(new LinearLayoutManager(this));
        mToolbar=(Toolbar)findViewById(R.id.find_boards_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Boards");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Boards> options=new FirebaseRecyclerOptions.Builder<Boards>()
                .setQuery(boardsRef, Boards.class)
                .build();

        FirebaseRecyclerAdapter<Boards,FindBoardsViewHolder> adapter=
                new FirebaseRecyclerAdapter<Boards, FindBoardsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindBoardsViewHolder holder, final int position, @NonNull Boards model) {
                holder.boardsName.setText(model.getName());
                holder.creatorName.setText(model.getCreator());
                holder.boardsClass.setText(model.getBoardClass());
                Picasso.with(FindBoardsActivity.this).load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_board_id=getRef(position).getKey();
                        Intent boardViewIntent=new Intent (FindBoardsActivity.this, BoardViewActivity.class);
                        boardViewIntent.putExtra("visit_board_id",visit_board_id);
                        startActivity(boardViewIntent);
                    }
                });
            }

            @NonNull
            @Override
            public FindBoardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.boards_display_layout,parent,false);
                FindBoardsViewHolder viewHolder=new FindBoardsViewHolder(view);
                return viewHolder;
            }
        };
        findBoardsRecyleList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindBoardsViewHolder extends RecyclerView.ViewHolder{
        TextView boardsName,creatorName,boardsClass;
        CircleImageView profileImage;
        public FindBoardsViewHolder(@NonNull View itemView) {
            super(itemView);
            boardsName=itemView.findViewById(R.id.board_profile_name);
            creatorName=itemView.findViewById(R.id.board_creator_name);
            profileImage=itemView.findViewById(R.id.board_creator_profile_image);
            boardsClass=itemView.findViewById(R.id.board_class_name);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(FindBoardsActivity.this,StudentActivity.class);
        this.startActivity(intent);
        finish();
    }
}
