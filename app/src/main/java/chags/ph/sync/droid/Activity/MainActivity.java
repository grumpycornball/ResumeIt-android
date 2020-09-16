package chags.ph.sync.droid.Activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import chags.ph.sync.droid.R;
import chags.ph.sync.droid.SignIn.SignInGoogle;
import chags.ph.sync.droid.pojo.UserData;
import chags.ph.sync.droid.pojo.UserOtp;
import chags.ph.sync.droid.service.SharedPrefManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    protected Button button_signin,button_subscribe,button_permission;
    protected FirebaseAuth mAuth;
    protected SignInGoogle signInGoogle;
    private FirebaseDatabase mDatabase;
    private FirebaseApp firebaseApp;
    private String passwd=null;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //call setEnvVariable
        setEnvVariables();


    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            DatabaseReference databaseReference1 = mDatabase.getReference().child("authenticate").child(passwd);
            databaseReference1.removeValue();
            TextView t = (TextView) findViewById(R.id.otptext);
            t.setText("NULL");
            passwd=null;
        }catch(Exception e){

        }

    }

    //To bind xml attributes to MainActivity
    protected void setEnvVariables()
    {
        setButton_signin();
        setSubscribe_signin();
        setButton_permission();
        setProgressBar();

        mAuth = FirebaseAuth.getInstance();
        firebaseApp = FirebaseApp.getInstance();
        mDatabase = FirebaseDatabase.getInstance(firebaseApp);
        signInGoogle= new SignInGoogle();
        signInGoogle.Initalize(MainActivity.this,mAuth);
        addOnClickSignIn();
        addOnSubscribe();
        addOnPermission();
        SharedPrefManager.getSharedPrefManager(this);


    }

    protected void getPermissions()
    {

        // Check if Android M or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this.checkCallingOrSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {

            // Launch the settings activity if the user prefers
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(myIntent);
        }
    }


    //Assign progress bar variable


    public void setProgressBar() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    //set permission button variable


    public void setButton_permission() {
        this.button_permission=(Button) findViewById(R.id.button_permission);
    }

    //Assign SignIn Button variable
    protected void setSubscribe_signin(){
        this.button_subscribe=(Button) findViewById(R.id.button_subscribe);
    }

    //add on click for permission

    protected void addOnPermission()
    {
        button_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissions();
            }
        });
    }

    //Add on click listener to subscribe button
    protected void addOnSubscribe()
    {

        button_subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwd==null){
                    try{
                        progressBar.setVisibility(View.VISIBLE);
                        passwd=generateOtp();

                        DatabaseReference databaseReference = mDatabase.getReference().child("authenticate").child(passwd);
                        ValueEventListener messageListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                TextView t = (TextView) findViewById(R.id.otptext);
                                //t.setText("starting process");
                                if (dataSnapshot.exists()) {
                                    addOnSubscribe();
                                }else{
                                    DatabaseReference databaseReference1 = mDatabase.getReference().child("authenticate").child(passwd);
                                    HashMap<String,String> hashMap = new HashMap<>();
                                    try{
                                        UserOtp otp = new UserOtp(SharedPrefManager.getToken("userData1").toString().split("\\|")[0]);
                                        databaseReference1.setValue(otp);
                                        t.setText(passwd);
                                    }catch(Exception e){
                                        Toast.makeText(MainActivity.this, "Please sign in first", Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                TextView t = (TextView) findViewById(R.id.Error_text);
                                t.setText("oncacelled"+databaseError);

                            }
                        };
                        databaseReference.addListenerForSingleValueEvent(messageListener);


                    }catch(Exception e) {
                        TextView t = (TextView) findViewById(R.id.Error_text);
                        t.setText(e.getMessage());

                    }
                }else
                {
                    TextView t = (TextView) findViewById(R.id.Error_text);
                    t.setText("Otp is already generated please use that");
                }


            }
        });
    }

    protected String generateOtp()
    {
        String passwd="";
        Random randomMethod = new Random();
        String alphabets="asdfghjklpoiuytrewqzxcvbnm";
        String number="0246219875";
        for (int i=0;i<6;i++)
        {
            if(randomMethod.nextInt(10)%2==0)
            {
                passwd=passwd+number.charAt((randomMethod.nextInt(5)));
            }else{
                passwd=passwd+alphabets.charAt((randomMethod.nextInt(5)));
            }
        }
        return passwd;
    }

    //Assign SignIn Button variable
    protected void setButton_signin(){
        this.button_signin=(Button) findViewById(R.id.button_signin);
    }

    //Add on click listener to signIn button
    protected void addOnClickSignIn()
    {
        button_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Intent signInIntent = signInGoogle.mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, signInGoogle.RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == signInGoogle.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(this, "Authentication Successfull 1.", Toast.LENGTH_SHORT).show();
                signInGoogle.firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Authentication Failed 1."+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }


    public void OnSignInComplete()
    {
        TextView t = (TextView) findViewById(R.id.Error_text);
        UserData userData1=null;
        try {

             userData1=  new UserData(signInGoogle.getUser(mAuth).getUid(),signInGoogle.getUser(mAuth).getEmail(),"","");
            userData1.setAndroidToken(SharedPrefManager.getToken("token"));
            SharedPrefManager.setToken(userData1.getPipeDelimited(),"userData1");
            //t.setText(userData1.getAndroidToken());
        }catch (Exception e)
        {
            t.setText(e.getMessage());
        }

        try {
            final DatabaseReference databaseReference = mDatabase.getReference().child("users").child(userData1.getUUID().toString());
            ValueEventListener messageListener = new ValueEventListener() {
                TextView t = (TextView) findViewById(R.id.Error_text);
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {
                        t.setText("data exists");
                        try{
                            UserData userData2 = dataSnapshot.getValue(chags.ph.sync.droid.pojo.UserData.class);
                            String[] string = SharedPrefManager.getToken("userData1").split("\\|");
                            UserData userData1 = new UserData(string[0],string[1],string[2],string[3]);
                            //userData1 data from android
                            //userData2 data from cloud
                            UserData userData3=null;
                            userData3=userData1;
                            //Syncing data between android and cloud. condition android token will be valid of android device and web token will be valid of web token
                            if (userData1.getAndroidToken() != userData2.getAndroidToken() )
                            {
                                userData3.setAndroidToken((userData1.getAndroidToken()));
                            }
                            if (userData1.getWebToken() != userData2.getWebToken())
                            {
                                userData3.setWebToken(userData2.getWebToken());
                            }
                            DatabaseReference mref= mDatabase.getReference().child("users").child(string[0]);
                            setDataInDatabase(mref,userData3);

                            progressBar.setVisibility(View.INVISIBLE);
                            t.setText("fetched/created user credentials successfully in cloud");
                        }catch (Exception e){
                            t.setText(e.getMessage());
                        }



                    }
                    else
                    {

                       String[] string = SharedPrefManager.getToken("userData1").toString().split("\\|");

                       try {

                           DatabaseReference mref= mDatabase.getReference().child("users").child(string[0]);
                           UserData userData2 = new UserData(string[0],string[1],string[2],"");
                           setDataInDatabase(mref,userData2);
                       }catch (Exception i )
                       {
                           t.setText("1:"+i.getMessage().toString());

                       }


                    }
                }

                public void setDataInDatabase(DatabaseReference mref, final UserData userData)
                {
                    try {
                        mref.setValue(userData);
                        SharedPrefManager.setToken(userData.getPipeDelimited(),"userData1");
                    }catch(Exception e){

                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Failed to read value
                    TextView t = (TextView) findViewById(R.id.Error_text);
                    t.setText(databaseError.getMessage());
                }
            };
            databaseReference.addListenerForSingleValueEvent(messageListener);
        }catch (Exception e)
        {
            t.setText(e.getMessage());
        }


    }



}