package apps.ahqmrf.onlychat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import apps.ahqmrf.onlychat.R;
import apps.ahqmrf.onlychat.model.User;
import apps.ahqmrf.onlychat.utils.Constants;

public class SignUpActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mFullName;
    private Button mChooseBtn;
    private Button mUploadBtn;
    private Button mSignUpBtn;
    private CheckBox mCheckbox;
    private ProgressDialog dialog;

    private StorageReference mStorage;
    private Uri uri;
    private FirebaseAuth auth;
    private Firebase mUserRef;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Firebase.goOnline();

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mEmail = (EditText) findViewById(R.id.et_email);
        mEmail.setOnFocusChangeListener(this);
        mUsername =  (EditText) findViewById(R.id.et_username);
        mUsername.setOnFocusChangeListener(this);
        mPassword =  (EditText) findViewById(R.id.et_password);
        mPassword.setOnFocusChangeListener(this);
        mConfirmPassword =  (EditText) findViewById(R.id.et_confirm_password);
        mConfirmPassword.setOnFocusChangeListener(this);
        mFullName =  (EditText) findViewById(R.id.et_full_name);
        mFullName.setOnFocusChangeListener(this);

        mChooseBtn = (Button) findViewById(R.id.btn_choose);
        mChooseBtn.setOnClickListener(this);

        mUploadBtn = (Button) findViewById(R.id.btn_upload);
        mUploadBtn.setOnClickListener(this);

        mSignUpBtn = (Button) findViewById(R.id.btn_signup);
        mSignUpBtn.setOnClickListener(this);

        mStorage = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        mUserRef = new Firebase(Constants.PARAMS.FIREBASE_URL).child(Constants.PARAMS.USER);
        dialog = new ProgressDialog(this);

        mCheckbox = (CheckBox) findViewById(R.id.checkbox);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.et_confirm_password ||
                v.getId() == R.id.et_email ||
                v.getId() == R.id.et_password ||
                v.getId() == R.id.et_username ||
                v.getId() == R.id.et_full_name) {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose:
                chooseImage();
                break;
            case R.id.btn_upload:
                uploadImage();
                break;
            case R.id.btn_signup:
                signUp();
                break;
        }
    }

    private void signUp() {
        if(checkValidation()) {
            if(mCheckbox.isChecked()) {
                user = new User(
                        mUsername.getText().toString(),
                        mEmail.getText().toString(),
                        uri.getLastPathSegment(),
                        mFullName.getText().toString()

                );
                dialog.setMessage("Signing up, please wait...");
                dialog.show();
                createUser();
            }
            else {
                Toast.makeText(getApplicationContext(), "You must agree to the terms of Only Chat", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createUser() {
        auth.createUserWithEmailAndPassword(user.getEmail(), mPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                dialog.dismiss();
                if(task.isSuccessful()) {
                    Firebase userRef = mUserRef.child(user.getUsername());
                    userRef.setValue(user);
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    intent.putExtra(Constants.PARAMS.USER, user);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected error, please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private boolean checkValidation() {
        if(mEmail.getText().toString().equals("") || mUsername.getText().toString().equals("") || mPassword.getText().toString().equals("") ||
                mConfirmPassword.getText().toString().equals("") || mFullName.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please fill up all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!mPassword.getText().toString().equals(mConfirmPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadImage() {
        if(!checkValidation()) return;
        if (uri == null) {
            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog.setMessage("Uploading image...");
        dialog.show();
        StorageReference photoStorage = mStorage.child(mEmail.getText().toString()).child(Constants.PARAMS.PHOTO).child(uri.getLastPathSegment());
        photoStorage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Constants.RequestCode.GALLERY_BROWSE_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.RequestCode.GALLERY_BROWSE_REQ_CODE && resultCode == RESULT_OK) {
            uri = data.getData();
        }
    }
}
