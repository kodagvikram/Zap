package com.zaparound.ModelVo;


public class CheckinZapmeVO {
    public String username, gender, profileurl,
            age, show_age, userid, user_selfie_thumb, place_name, similar_interest_count, similar_interest_name,requestid;
    public boolean isselected;
    public int isread;

    public CheckinZapmeVO(String username, String gender, String age
            , String show_age, String userid, String profileurl, String user_selfie_thumb
            , String place_name, String similar_interest_count, String similar_interest_name, int isread,String requestid,boolean isselected) {
        this.username = username;
        this.age = age;
        this.show_age = show_age;
        this.userid = userid;
        this.user_selfie_thumb = user_selfie_thumb;
        this.place_name = place_name;
        this.similar_interest_count = similar_interest_count;
        this.similar_interest_name = similar_interest_name;
        this.gender = gender;
        this.profileurl = profileurl;
        this.requestid = requestid;
        this.isread = isread;
        this.isselected = isselected;
    }

    public CheckinZapmeVO() {
        this.username = "";
        this.age = "";
        this.show_age = "";
        this.userid = "";
        this.user_selfie_thumb = "";
        this.place_name = "";
        this.similar_interest_count = "";
        this.similar_interest_name = "";
        this.profileurl = "";
        this.requestid = "";
        this.gender = "";
        this.isread = 1;
        isselected = false;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
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

    public String getAge() {
        return age;
    }

    public String getPlace_name() {
        return place_name;
    }

    public String getShow_age() {
        return show_age;
    }

    public String getSimilar_interest_count() {
        return similar_interest_count;
    }

    public String getSimilar_interest_name() {
        return similar_interest_name;
    }

    public String getUser_selfie_thumb() {
        return user_selfie_thumb;
    }

    public String getUserid() {
        return userid;
    }

    public boolean isselected() {
        return isselected;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public void setShow_age(String show_age) {
        this.show_age = show_age;
    }

    public void setSimilar_interest_count(String similar_interest_count) {
        this.similar_interest_count = similar_interest_count;
    }

    public void setSimilar_interest_name(String similar_interest_name) {
        this.similar_interest_name = similar_interest_name;
    }

    public void setUser_selfie_thumb(String user_selfie_thumb) {
        this.user_selfie_thumb = user_selfie_thumb;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getIsread() {
        return isread;
    }

    public void setIsread(int isread) {
        this.isread = isread;
    }

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }
}
