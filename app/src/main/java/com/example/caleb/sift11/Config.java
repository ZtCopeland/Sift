package com.example.caleb.sift11;


import static com.example.caleb.sift11.LoginActivity.getEmailAccount;
import static com.example.caleb.sift11.LoginActivity.getEmailPassword;

/**
 * Created by Caleb on 11/19/2016.
 */

public class Config {
    //REPLACE THIS w/t email credentials fetched from server associated account
    public static final String EMAIL = getEmailAccount();
    public static final String PASSWORD = getEmailPassword();
}
