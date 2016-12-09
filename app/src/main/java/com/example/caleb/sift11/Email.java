package com.example.caleb.sift11;


import java.text.*;
import java.util.*;
/**
 * Created by Caleb on 11/22/2016.
 */

public class Email {

    private boolean expanded;
    private String dRecipient;
    private String dSubject;
    private String dMessage;
    private String dDate;
    private String dSender;
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    Date date = new Date();

    public Email(String dMessage)
    {
        this.dRecipient = "NO RECIPIENT";
        this.dSubject = "EMPTY SUBJECT";
        this.dMessage = dMessage;
        expanded = false;

    }

    public Email(){
        this.dRecipient = "NO RECIPIENT";
        this.dSubject = "EMPTY SUBJECT";
        this.dMessage = "BLANK MESSAGE";
        expanded = false;
    }

    public Email(String dRecipient, String dSubject, String dMessage)
    {
        this.dRecipient = dRecipient;
        this.dSubject = dSubject;
        this.dMessage = dMessage;
        dDate = df.format(date);
        expanded = false;
    }

    public boolean getExpanded(){
        return expanded;
    }

    public void setExpanded(boolean b){
        expanded = b;
    }
    public String getdRecipient() {
        return dRecipient;
    }

    public void setdRecipient(String dRecipient) {
        this.dRecipient = dRecipient;
    }

    public String getdSubject() {
        return dSubject;
    }

    public void setdSubject(String dSubject) {
        this.dSubject = dSubject;
    }

    public String getdMessage() {
        return dMessage;
    }

    public void setdMessage(String dMessage) {
        this.dMessage = dMessage;
    }

    public String getdDate() {
        return dDate;
    }

    public void setdDate(String dDate) {
        this.dDate = dDate;
    }

    public void setdSender(String dSender) { this.dSender = dSender; }

    public String getdSender () { return dSender; }

    public String toString()
    {
        return "Date/Time: "+dDate+"\nRecipient(s): "+dRecipient+"\nSubject: "+dSubject+"\nMessage:\n"+dMessage;
    }


}
