package com.apps.vicky.addressmanager;

/**
 * Created by Vicky cj on 26-11-2017.
 */

public class UserData {

    public String userName;
    public String addressName;
    public String addressValue;
    public String addedDate;
    public String emailId;
    public String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddressValue() {
        return addressValue;
    }

    public void setAddressValue(String addressValue) {
        this.addressValue = addressValue;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public  UserData(){

    }


    public UserData(String userName, String addressName, String addressValue, String addedDate, String emailId){
        this.userName=userName;
        this.addressName=addressName;
        this.addressValue=addressValue;
        this.addedDate=addedDate;
        this.emailId=emailId;

    }
}
