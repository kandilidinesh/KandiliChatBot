package com.dinesh.kandili.kardischatbot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class StatusActivity extends AppCompatActivity {


    private Toolbar mToolBar;
    private TextInputLayout textInputLayout;
    private Button button;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private ProgressDialog regProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mAuth = FirebaseAuth.getInstance();
        mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textInputLayout = (TextInputLayout) findViewById(R.id.status_input);
        button = (Button) findViewById(R.id.update_status_btn);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                regProgress= new ProgressDialog(StatusActivity.this);
                String status = textInputLayout.getEditText().getText().toString();
                if(!TextUtils.isEmpty(status)) {
                    regProgress.setTitle("Updating Status");
                    regProgress.setMessage("Please wait while we update your new status");
                    regProgress.setCanceledOnTouchOutside(false);
                    regProgress.show();
                }

                database = FirebaseDatabase.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String uid = user.getUid();
                myRef = database.getReference().child("Users").child(uid);

                myRef.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            regProgress.dismiss();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"There was some error saving your status",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
