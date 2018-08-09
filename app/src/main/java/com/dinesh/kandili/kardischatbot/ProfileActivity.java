package com.dinesh.kandili.kardischatbot;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {


    private ImageView mProfileImageView;
    private TextView mProfileName;
    private TextView mProfileStatus;
    private TextView mProfileFriends;
    private Button mProfileSendReqBtn;
    private ProgressDialog regProgress;
    private Button mProfileDecReqBtn;

    private String display_name=null,status=null,image;

    private String mCurrentState;

    private DatabaseReference usersDatabase;

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;

    private FirebaseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mCurrentState="not_friends";
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        final String user_Id= getIntent().getStringExtra("userId");
       /* final String user_id;
        String data = getIntent().getStringExtra("user_id");
        if (data == null) {
            user_id = getIntent().getStringExtra("from_user_id");
        } else {
            user_id = getIntent().getStringExtra("user_id");
        }*/


        regProgress= new ProgressDialog(this);
        regProgress.setTitle("Loading...");
        regProgress.setMessage("Please wait while we load the user data.");
        regProgress.setCanceledOnTouchOutside(false);
        regProgress.show();

        mProfileImageView = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_display_name);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriends = (TextView) findViewById(R.id.profile_total_frnds);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_send_request_btn2);
        mProfileDecReqBtn = (Button) findViewById(R.id.profile_dec_request_btn);
        mProfileDecReqBtn.setVisibility(View.INVISIBLE);
        mProfileDecReqBtn.setEnabled(false);


        mRootRef = FirebaseDatabase.getInstance().getReference();
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_Id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");

        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                display_name= dataSnapshot.child("name").getValue().toString();
               status = dataSnapshot.child("status").getValue().toString();
                image= dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
//                Picasso.get().load(image)
//                        .placeholder(R.drawable.unknown)
//                        .into(mProfileImageView);
                Picasso.get().load(image)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.unknown).into(mProfileImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(image).placeholder(R.drawable.unknown).into(mProfileImageView);
                    }
                });

                //----------------------Friend List Request Feature--------------------------

                mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_Id))
                        {
                            String reqType = dataSnapshot.child(user_Id).child("request_type").getValue().toString();
                            if(reqType.equals("recieved"))
                            {
//                                Toast.makeText(ProfileActivity.this, "Request Sent",Toast.LENGTH_LONG).show();
                                mProfileSendReqBtn.setEnabled(true);
                                mCurrentState="req_recieved";
                                mProfileSendReqBtn.setText("Accept Request");
                                mProfileDecReqBtn.setVisibility(View.VISIBLE);
                                mProfileDecReqBtn.setEnabled(true);
                            }
                            else if(reqType.equals("sent"))
                            {
                                mProfileSendReqBtn.setEnabled(true);
                                mCurrentState="req_sent";
                                mProfileSendReqBtn.setText("Cancel Request");
                                mProfileDecReqBtn.setVisibility(View.INVISIBLE);
                                mProfileDecReqBtn.setEnabled(false);

                            }
                            regProgress.dismiss();
                        }
                        else
                        {
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_Id))
                                    {
                                        mCurrentState="friends";
                                        mProfileSendReqBtn.setText("Unfriend "+display_name);
                                    }
                                    regProgress.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    regProgress.dismiss();
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProfileSendReqBtn.setEnabled(false);
                if(mCurrentState.equals("not_friends"))
                {
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_Id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                mFriendReqDatabase.child(user_Id).child(mCurrentUser.getUid()).child("request_type").setValue("recieved").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        HashMap<String,String> notificationData = new HashMap<>();
                                        notificationData.put("from",mCurrentUser.getUid());
                                        notificationData.put("type","request");

                                        mNotificationDatabase.child(user_Id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ProfileActivity.this, "Request Sent",Toast.LENGTH_LONG).show();
                                                mProfileSendReqBtn.setEnabled(true);
                                                mCurrentState="req_sent";
                                                mProfileSendReqBtn.setText("Cancel Request");
                                                mProfileDecReqBtn.setVisibility(View.INVISIBLE);
                                                mProfileDecReqBtn.setEnabled(false);
                                            }
                                        });


                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(ProfileActivity.this, "Failed Sending Request",Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }

                if(mCurrentState.equals("req_sent"))
                {
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_Id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqDatabase.child(user_Id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileSendReqBtn.setEnabled(true);
                                    Toast.makeText(ProfileActivity.this, "Request Cancelled",Toast.LENGTH_LONG).show();
                                    mProfileSendReqBtn.setText("Send Request");
                                    mCurrentState="not_friends";
                                    mProfileDecReqBtn.setVisibility(View.INVISIBLE);
                                    mProfileDecReqBtn.setEnabled(false);

                                }
                            });
                        }
                    });
                }

                if(mCurrentState.equals("req_recieved"))
                {
                    final String date = DateFormat.getDateInstance().format(new Date());
                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_Id).child("date").setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendDatabase.child(user_Id).child(mCurrentUser.getUid()).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_Id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mFriendReqDatabase.child(user_Id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mProfileSendReqBtn.setEnabled(true);
                                                    mCurrentState="friends";
                                                    mProfileSendReqBtn.setText("Unfriend "+display_name);
                                                    mProfileDecReqBtn.setVisibility(View.INVISIBLE);
                                                    mProfileDecReqBtn.setEnabled(false);
                                                }
                                            });
                                        }
                                    });

                                    mFriendReqDatabase.child(user_Id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                                    }
                            });
                        }
                    });
                }

                if(mCurrentState.equals("friends"))
                {
                    mFriendDatabase.child(user_Id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendDatabase.child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ProfileActivity.this, "Unfriend Successful",Toast.LENGTH_LONG).show();
                                    mCurrentState="not_friends";
                                    mProfileSendReqBtn.setText("Send Request");
                                    mProfileDecReqBtn.setVisibility(View.INVISIBLE);
                                    mProfileDecReqBtn.setEnabled(false);
                                }
                            });

                        }
                    });
                }
            }
        });



    }
}
