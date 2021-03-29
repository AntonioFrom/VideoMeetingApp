package com.example.videomeetingapp.utilities;

import java.util.HashMap;

public class Constants {

    public static final String KEY_COLLECTIONS_USERS = "users";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_FCM_TOKEN = "fcm_token";

    public static final String KEY_PREFERENCE_NAME = "videoMeetingPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";

    public static final String REMOTE_MSG_AUTHORIZATION = "authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "content-type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meeting-type";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviter-token";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration-ids";

    public static HashMap<String, String> getRemoteMessageHeaders(){
        HashMap<String,String> headersMap = new HashMap<>();
        headersMap.put(Constants.REMOTE_MSG_AUTHORIZATION,
                "AAAAr1yURX8:APA91bEl5FrydMlCyoiedelCfvoCkbZsAomFDvtAzPlB1D9z8rIUmGzPBk_R7Jo9bw-K2DuV7rlHrDYTkUH_HuFE2EOboG6Y8kd92ZtPfYbpJATlw3_y5Oa1VWiOPmG9yRnIoFlKxQ1B");
        headersMap.put(Constants.REMOTE_MSG_CONTENT_TYPE, "application/json");
        return headersMap;
    }
}
