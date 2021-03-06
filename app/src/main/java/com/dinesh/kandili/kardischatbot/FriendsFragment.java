package com.dinesh.kandili.kardischatbot;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase


        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, final Friends friends, int i) {

                friendsViewHolder.setDate(friends.getDate());

                String list_user_id = getRef(i).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();

                        if(dataSnapshot.hasChild("online"))
                        {
                            Boolean userOnline = (boolean) dataSnapshot.child("online").getValue();
                            friendsViewHolder.setUserOnline(userOnline);

                        }
                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setImage(image);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mFriendsList.setAdapter(friendsRecyclerViewAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder
    {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setDate(String date){

            TextView userStatusView = (TextView) mView.findViewById(R.id.users_single_status);
            userStatusView.setText(date);

        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.users_single_name);
            userNameView.setText(name);

        }
        public void setImage(final String image)
        {
            final CircleImageView circleImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

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

        public void setUserOnline(Boolean onlineStatus)
        {
            ImageView userOnline = (ImageView) mView.findViewById(R.id.user_online);
            if(onlineStatus.equals(true))
            {
                userOnline.setVisibility(View.VISIBLE);
            }
            else
            {
                userOnline.setVisibility(View.INVISIBLE);
            }
        }

    }

}