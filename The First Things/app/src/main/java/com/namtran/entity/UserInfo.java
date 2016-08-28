package com.namtran.entity;

/**
 * Created by namtr on 15/08/2016.
 */
public class UserInfo {
    public static final String PREF_NAME = "user_info";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_UID = "uid";
    public static final String KEY_AVATAR = "avatar";
    private String name;
    private String email;
    private String uid;

    public UserInfo(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }
}
