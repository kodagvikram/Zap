package com.zaparound.ModelVo;


import java.io.Serializable;

public class ContactsLivelistVO implements Serializable {
    public String username, lastmessage, time, profileurl;
    public boolean isselected, islive;
    public String statusMessage, status, channel;
    public long id;
    public String gender, age, show_age, userid, user_selfie_thumb, place_name, similar_interest_count, similar_interest_name;

    public ContactsLivelistVO(long id, String statusmessage, String status, String channel, String username, String lastmessage, String time, String gender, String age
            , String show_age, String userid, String profileurl, String user_selfie_thumb
            , String place_name, String similar_interest_count, String similar_interest_name, boolean isselected, boolean islive) {
        this.username = username;
        this.lastmessage = lastmessage;
        this.time = time;
        this.profileurl = profileurl;
        this.isselected = isselected;
        this.islive = islive;
        this.id = id;
        this.statusMessage = statusmessage;
        this.status = status;
        this.channel = channel;
        this.age = age;
        this.show_age = show_age;
        this.userid = userid;
        this.user_selfie_thumb = user_selfie_thumb;
        this.place_name = place_name;
        this.similar_interest_count = similar_interest_count;
        this.similar_interest_name = similar_interest_name;
        this.gender = gender;
    }

    public ContactsLivelistVO() {
        this.username = "";
        this.lastmessage = "";
        this.profileurl = "";
        this.time = "";
        isselected = false;
        islive = false;
        this.id = 0;
        this.statusMessage = "";
        this.status = "";
        this.channel = "";
        this.age = "";
        this.show_age = "";
        this.userid = "";
        this.user_selfie_thumb = "";
        this.place_name = "";
        this.similar_interest_count = "";
        this.similar_interest_name = "";
        this.gender = "";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setIslive(boolean islive) {
        this.islive = islive;
    }

    public boolean islive() {
        return islive;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
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


}
