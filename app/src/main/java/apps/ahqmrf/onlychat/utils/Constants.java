package apps.ahqmrf.onlychat.utils;

/**
 * Created by Lenovo on 3/3/2017.
 */

public final class Constants {
    public static class Utils {
        public static final int SPLASH_DURATION = 4000;
        public static final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        public static final String CHAT_USER = "chat_user";
        public static final String SELECTED_COLOR = "selected_color";
        public static  final String TOKEN = "eu7lnmBgJ80:APA91bFfak2OXk3Aghyj4V9mG2NgyR22b-eNfjzafKWS-xuMpP04TVQbxRwtpdYp5O2tJ35JL84mcUmPvJnwwAcOpWnCs9Nq2muPrO1BrrK084kfuhmyVCJ6PaouknMSIkVbZOc1OYUk";
    }

    public static class RequestCode {
        public static final int GALLERY_BROWSE_REQ_CODE = 1;
        public static final int CHANGE_COLOR_REQ_CODE = 2;
    }

    public static class PARAMS {
        public static final String USER = "user";
        public static final String FRIEND = "friend";
        public static final String ONLINE = "online";
        public static final String ALL = "all";
        public static final String EMAIL = "email";
        public static final String PHOTO = "photo";
        public static final String FULL_NAME = "fullName";
        public static final String USERNAME = "username";
        public static final String PROFILE_PICTURE_PATH = "profilePicturePath";
        public static final String FIREBASE_URL = "https://only-chat.firebaseio.com/";
    }

    public static final class DB {
        public static final int VERSION = 1;
        public static final String DATABASE_NAME = "OnlyChat.db";
        public static final String TABLE_CHAT_COLOR = "chat_color";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_ID = "id";
    }
}
