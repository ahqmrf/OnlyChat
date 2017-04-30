package apps.ahqmrf.onlychat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lenovo on 3/3/2017.
 */

public class User implements Parcelable {
    private String username;
    private String email;
    private String profilePicturePath;
    private String fullName;

    public User(String username, String email,  String profilePicturePath, String fullName) {
        this.username = username;
        this.email = email;
        this.profilePicturePath = profilePicturePath;
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }


    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public String getFullName() {
        return fullName;
    }

    protected User(Parcel in) {
        username = in.readString();
        email = in.readString();
        profilePicturePath = in.readString();
        fullName = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(profilePicturePath);
        dest.writeString(fullName);
    }
}
