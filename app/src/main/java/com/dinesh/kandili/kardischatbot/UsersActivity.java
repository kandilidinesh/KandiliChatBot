package com.dinesh.kandili.kardischatbot;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mRecyclerView = (RecyclerView) findViewById(R.id.users_list);

        mToolbar = (Toolbar) findViewById(R.id.users_appbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(model.getImage());

                final String userId = getRef(position).getKey();

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("userId",userId);
                        startActivity(profileIntent);
                    }
                });
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    public static class UsersViewHolder extends  RecyclerView.ViewHolder
    {
        View mview;
        public UsersViewHolder(View itemView)
        {

            super(itemView);
            mview =itemView;
        }

        public void setName(String name)
        {
            TextView userNameView = (TextView) mview.findViewById(R.id.users_single_name);
            userNameView.setText(name);

        }

        public void setStatus(String status)
        {
            TextView statusView = (TextView) mview.findViewById(R.id.users_single_status);
            statusView.setText(status);
        }

        public void setImage(final String image)
        {
            final CircleImageView circleImageView = (CircleImageView) mview.findViewById(R.id.user_single_image);

            if(!image.equals("default"))
            {
//                Picasso.get().load(image).placeholder(R.drawable.unknown).into(circleImageView);
                Picasso.get().load(image)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.unknown).into(circleImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(image).placeholder(R.drawable.unknown).into(circleImageView);
                    }
                });
            }
        }
    }


}
