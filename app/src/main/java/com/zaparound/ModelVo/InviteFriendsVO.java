package com.zaparound.ModelVo;


public class InviteFriendsVO {
    public String username,contact,gender,profileurl;
    public boolean isselected;

    public InviteFriendsVO(String username, String contact, String gender, String profileurl, boolean isselected)
    {
        this.username = username;
        this.contact = contact;
        this.gender=gender;
        this.profileurl = profileurl;
        this.isselected = isselected;
    }

    public InviteFriendsVO()
    {
        this.username = "";
        this.contact = "";
        this.profileurl = "";
        this.gender="";
        isselected=false;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }

    public String getProfileurl() {
        return profileurl;
    }

    public void setIsselected(boolean isselected) {
        this.isselected = isselected;
    }

    public boolean getisselected() {
        return isselected;
    }

}

