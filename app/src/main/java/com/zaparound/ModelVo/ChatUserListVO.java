package com.zaparound.ModelVo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ChatUserListVO implements Serializable {
    public String username, lastmessage, time, profileurl;
    public String statusMessage, status, channel,lastmessage_time;
    public String gender, age, show_age, userid, user_selfie_thumb,
            place_name, similar_interest_count, similar_interest_name,user_own_selfi,user_own_selfithumb,last_id;
    public long id;
    public boolean isselected, islive;
    public ChatUserListVO(long id, String statusmessage, String status, String channel, String username, String lastmessage, String time, String gender, String age
            , String show_age, String userid, String profileurl, String user_selfie_thumb
            , String place_name, String similar_interest_count, String similar_interest_name,
            String user_own_selfi,String user_own_selfithumb,
            String last_id,String lastmessage_time,boolean isselected, boolean islive) {
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
        this.user_own_selfi = user_own_selfi;
        this.user_own_selfithumb = user_own_selfithumb;
        this.last_id = last_id;
        this.lastmessage_time = lastmessage_time;
    }

    public ChatUserListVO() {
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
        this.show_age = "0";
        this.userid = "";
        this.user_selfie_thumb = "";
        this.place_name = " ";
        this.similar_interest_count = "";
        this.similar_interest_name = "";
        this.gender = "";
        this.user_own_selfi = "";
        this.user_own_selfithumb = "";
        this.last_id = "0";
        this.last_id = "0";
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

    public String getUser_own_selfi() {
        return user_own_selfi;
    }

    public String getUser_own_selfithumb() {
        return user_own_selfithumb;
    }

    public void setUser_own_selfi(String user_own_selfi) {
        this.user_own_selfi = user_own_selfi;
    }

    public void setUser_own_selfithumb(String user_own_selfithumb) {
        this.user_own_selfithumb = user_own_selfithumb;
    }

    public String getLast_id() {
        return last_id;
    }

    public void setLast_id(String last_id) {
        this.last_id = last_id;
    }

    public String getLastmessage_time() {
        return lastmessage_time;
    }

    public void setLastmessage_time(String lastmessage_time) {
        this.lastmessage_time = lastmessage_time;
    }
}
