package apps.ahqmrf.onlychat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import apps.ahqmrf.onlychat.R;
import apps.ahqmrf.onlychat.model.User;
import apps.ahqmrf.onlychat.utils.Constants;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserActivity extends AppCompatActivity implements View.OnClickListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private StorageReference mStorage;
    private User user;
    private CircleImageView profilePic;
    private NavigationView navigationView;
    private TextView navEmail;
    private TextView navFullName;
    private EditText mSearchUser;
    private Button mStartChattingBtn;
    private Firebase searchUserRef;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        dialog = new ProgressDialog(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        user = getIntent().getParcelableExtra(Constants.PARAMS.USER);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        View hView = navigationView.getHeaderView(0);
        profilePic = (CircleImageView) hView.findViewById(R.id.iv_pp);
        navEmail = (TextView) hView.findViewById(R.id.nav_tv_email);
        navFullName = (TextView) hView.findViewById(R.id.nav_tv_full_name);
        navEmail.setText(user.getEmail());
        navFullName.setText(user.getFullName());

        mSearchUser = (EditText) findViewById(R.id.et_username);
        mStartChattingBtn = (Button) findViewById(R.id.btn_startChatting);
        mStartChattingBtn.setOnClickListener(this);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference mPhotoRef = mStorage.child(user.getEmail()).child(Constants.PARAMS.PHOTO).child(user.getProfilePicturePath());

        final long ONE_MEGABYTE = 1024 * 1024;
        mPhotoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePic.setImageBitmap(bitmap);
            }
        });

        searchUserRef = new Firebase(Constants.PARAMS.FIREBASE_URL).child(Constants.PARAMS.USER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToMyProfile(MenuItem item) {
        mDrawerLayout.closeDrawers();
        Intent intent = new Intent(this, ViewProfileActivity.class);
        intent.putExtra(Constants.PARAMS.USERNAME, user.getUsername());
        startActivity(intent);
    }

    public void signOut(MenuItem item) {
        mDrawerLayout.closeDrawers();
        FirebaseAuth.getInstance().signOut();
        Firebase.goOffline();

        startActivity(new Intent(this, SignInActivity.class));
        finishAffinity();
    }

    public void goToSettings(MenuItem item) {
        mDrawerLayout.closeDrawers();
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    public void onBackPressed() {
        Firebase.goOffline();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_startChatting:
                startChatting();
                break;
        }
    }

    private void startChatting() {
        final String username = mSearchUser.getText().toString();
        Firebase lookFor = searchUserRef.child(username);
        dialog.setMessage("Searching user...");
        dialog.show();
        lookFor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                dialog.dismiss();
                if (snapshot != null && snapshot.getValue() != null) {
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra(Constants.Utils.CHAT_USER, username);
                    intent.putExtra(Constants.PARAMS.USER, user);
                    mSearchUser.setText("");
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "No such user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }
}
