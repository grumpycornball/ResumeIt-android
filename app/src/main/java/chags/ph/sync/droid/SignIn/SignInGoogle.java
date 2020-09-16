package chags.ph.sync.droid.SignIn;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import chags.ph.sync.droid.Activity.MainActivity;
import chags.ph.sync.droid.R;

public class SignInGoogle extends MainActivity {
    public static final int RC_SIGN_IN = 9001;
    public final String default_web_client_id= getString(R.string.default_web_client_id);
    public GoogleSignInClient mGoogleSignInClient;
    private Context context;
    private FirebaseAuth mAuth;

    //Get user data
    public FirebaseUser getUser(FirebaseAuth mAuth)
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser;
    }

    //Initialize
    public void Initalize(Context context, FirebaseAuth mAuth)
    {
        this.context=context;
        this.mAuth=mAuth;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(default_web_client_id)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context.getApplicationContext(), gso);

    }




    public FirebaseUser firebaseAuthWithGoogle(String idToken) {

        FirebaseUser user;
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(context, "Authentication Successfull 2.", Toast.LENGTH_SHORT).show();
                            MainActivity mainActivity = ((MainActivity) context);
                            mainActivity.OnSignInComplete();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "Authentication Failed 2.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        user = mAuth.getCurrentUser();
        return null;
    }


}
