package apps.ahqmrf.onlychat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import apps.ahqmrf.onlychat.R;
import apps.ahqmrf.onlychat.model.User;
import apps.ahqmrf.onlychat.utils.Constants;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUsername;
    private EditText mPassword;
    private Button mSignInBtn;
    private Button mSignUpBtn;
    private ProgressDialog dialog;
    private Firebase mUserRef;
    private FirebaseAuth mFirebaseAuth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Firebase.goOnline();

        Log.d("Signup", "Refresh token: " + FirebaseInstanceId.getInstance().getToken());

        mSignUpBtn = (Button) findViewById(R.id.btn_signup);
        mSignUpBtn.setOnClickListener(this);

        mSignInBtn = (Button) findViewById(R.id.btn_signin);
        mSignInBtn.setOnClickListener(this);

        mUsername = (EditText) findViewById(R.id.et_email_or_username);
        mPassword = (EditText) findViewById(R.id.et_password);

        User user = getIntent().getParcelableExtra(Constants.PARAMS.USER);
        if (user != null) {
            mUsername.setText(user.getUsername());
        }

        dialog = new ProgressDialog(this);
        mUserRef = new Firebase(Constants.PARAMS.FIREBASE_URL).child(Constants.PARAMS.USER);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                signUp();
                break;
            case R.id.btn_signin:
                signIn();
                break;
        }
    }

    private void signIn() {
        dialog.setMessage("Retrieving email, please wait...");
        dialog.show();
        Firebase userEmailRef = mUserRef.child(mUsername.getText().toString());
        userEmailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                dialog.dismiss();
                if (snapshot != null && snapshot.getValue() != null) {
                    dialog.setMessage("Signing in...");
                    dialog.show();
                    String email = snapshot.child(Constants.PARAMS.EMAIL).getValue().toString();
                    user = new User(
                            mUsername.getText().toString(),
                            email,
                            snapshot.child(Constants.PARAMS.PROFILE_PICTURE_PATH).getValue().toString(),
                            snapshot.child(Constants.PARAMS.FULL_NAME).getValue().toString()
                            );
                    Toast.makeText(getApplicationContext(), user.getEmail(), Toast.LENGTH_SHORT).show();
                    signInWithEmail(email);
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }

    private void signInWithEmail(String email) {
        mFirebaseAuth.signInWithEmailAndPassword(email, mPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), SearchUserActivity.class).putExtra(Constants.PARAMS.USER, user));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signUp() {
        startActivity(new Intent(this, SignUpActivity.class));
    }

}
