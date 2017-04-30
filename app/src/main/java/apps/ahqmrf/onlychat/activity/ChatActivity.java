package apps.ahqmrf.onlychat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Field;
import java.util.ArrayList;

import apps.ahqmrf.onlychat.R;
import apps.ahqmrf.onlychat.adapter.MessageAdapter;
import apps.ahqmrf.onlychat.database.DatabaseHelper;
import apps.ahqmrf.onlychat.model.Message;
import apps.ahqmrf.onlychat.model.User;
import apps.ahqmrf.onlychat.utils.Constants;

public class ChatActivity extends AppCompatActivity implements ChildEventListener {

    private RecyclerView mChatList;
    private MessageAdapter mAdapter;
    private Button btnSend;
    private EditText msgText;
    private String mId;
    private ArrayList<Message> msgList;
    private Firebase mFirebaseRef;
    private FirebaseAuth mFirebaseAuth;
    private String sentTo;
    private TextView notification;
    private int counter = -1;
    private User user;
    private Firebase mUserRef;
    private StorageReference mStorage;
    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Firebase.goOnline();
        helper = new DatabaseHelper(this, null, null, 1);
        counter = -1;

        user = getIntent().getParcelableExtra(Constants.PARAMS.USER);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mId = user.getUsername();

        sentTo = getIntent().getStringExtra(Constants.Utils.CHAT_USER);


        setTitle(sentTo);
        mUserRef = new Firebase(Constants.PARAMS.FIREBASE_URL).child(Constants.PARAMS.USER).child(sentTo);
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null && snapshot.getValue() != null) {
                    String name = snapshot.child(Constants.PARAMS.FULL_NAME).getValue().toString();
                    String email = snapshot.child(Constants.PARAMS.EMAIL).getValue().toString();
                    String path = snapshot.child(Constants.PARAMS.PROFILE_PICTURE_PATH).getValue().toString();
                    if(getSupportActionBar() != null) getSupportActionBar().setTitle(name);
                    getAvatar(email, path);
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });


        mChatList = (RecyclerView) findViewById(R.id.rv_msg_list);
        btnSend = (Button) findViewById(R.id.btn_send);
        msgText = (EditText) findViewById(R.id.et_msg);
        msgList = new ArrayList<>();
        mChatList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MessageAdapter(this, mId, msgList, helper.getChatColor(sentTo));
        mChatList.setAdapter(mAdapter);

        notification = (TextView) findViewById(R.id.tv_notification);

        getOverflowMenu();

        mFirebaseRef = new Firebase(Constants.PARAMS.FIREBASE_URL).child("messages");


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msgText.getText().toString();

                if (!message.isEmpty()) {
                    /**
                     * Firebase - Send message
                     */
                    mFirebaseRef.push().setValue(new Message(mId, message, sentTo, "1", "0", -1));
                }

                msgText.setText("");
            }
        });

        mFirebaseRef.addChildEventListener(this);

        msgText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mStorage = FirebaseStorage.getInstance().getReference();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, helper.getChatColor(sentTo))));
        }
    }

    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                Firebase.goOffline();
                mFirebaseRef.removeEventListener(this);
                finish();
                return true;

            case R.id.change_color:
                changeChatColor();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeChatColor() {
        Intent intent = new Intent(this, ChangeColorActivity.class);
        startActivityForResult(intent, Constants.RequestCode.CHANGE_COLOR_REQ_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
            try {
                Message model = dataSnapshot.getValue(Message.class);
                if (model.getSentTo().equals(sentTo) && model.getId().equals(mId)) {
                    if (model.getSerialId() == -1) {
                        model.setSerialId(msgList.size());
                        dataSnapshot.getRef().setValue(model);
                    }
                    msgList.add(model);
                    mChatList.scrollToPosition(msgList.size() - 1);
                    mAdapter.notifyItemInserted(msgList.size() - 1);
                } else if (model.getSentTo().equals(mId) && model.getId().equals(sentTo)) {
                    if (model.getSerialId() == -1) {
                        model.setSerialId(msgList.size());
                    }
                    model.setReadBySentTo("1");
                    dataSnapshot.getRef().setValue(model);
                    msgList.add(model);
                    mChatList.scrollToPosition(msgList.size() - 1);
                    mAdapter.notifyItemInserted(msgList.size() - 1);
                    Vibrator vibrator;
                    vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(500);
                } else if (model.getSentTo().equals(mId) && model.getReadBySentTo().equals("0")) {
                    String str[] = notification.getText().toString().split(" ");
                    int count = Integer.parseInt(str[0]);
                    count++;
                    if (count > 0) {
                        String updateNotific = "" + count + " new messages";
                        notification.setText(updateNotific);
                        notification.setVisibility(View.VISIBLE);
                    } else {
                        notification.setVisibility(View.GONE);
                    }
                }
            } catch (Exception ex) {
                //Log.e("Chat activity", ex.getMessage());
            }
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
            try {
                Message model = dataSnapshot.getValue(Message.class);
                if (model.getSentTo().equals(sentTo) && model.getId().equals(mId) && model.getReadBySentTo().equals("1")) {
                    msgList.set(model.getSerialId(), model);
                    mAdapter.notifyItemChanged(model.getSerialId());
                }
            } catch (Exception ex) {
                //Log.e("Chat activity", ex.getMessage());
            }
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    private void getAvatar(String email, String path) {
        StorageReference mPhotoRef = mStorage.child(email).child(Constants.PARAMS.PHOTO).child(path);

        final long ONE_MEGABYTE = 1024 * 1024;
        mPhotoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Toast.makeText(getApplicationContext(), "Found avatar", Toast.LENGTH_SHORT).show();
                mAdapter.setAvatar(bitmap);
                mAdapter.notifyDataSetChanged();
                mChatList.scrollToPosition(msgList.size() - 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.RequestCode.CHANGE_COLOR_REQ_CODE && resultCode == RESULT_OK) {
            int color = data.getIntExtra(Constants.Utils.SELECTED_COLOR, ContextCompat.getColor(this, R.color.colorPrimary));
            mAdapter.setChatColor(data.getIntExtra(Constants.Utils.SELECTED_COLOR, ContextCompat.getColor(this, R.color.colorPrimary)));
            if(getSupportActionBar() != null) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, color)));
            }
            mAdapter.notifyDataSetChanged();
            helper.changeChatColor(sentTo, color);
        }
    }
}
