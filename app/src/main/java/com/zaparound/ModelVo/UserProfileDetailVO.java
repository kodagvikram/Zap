package com.zaparound.ModelVo;


public class UserProfileDetailVO {

    public int userid,isonline;
    public String firstname,lastname,email,gender,dob,mobile,
            username,intrest,countryid,notification,show_age;

    public  UserProfileDetailVO(int userid,String firstname,String lastname,String email,
                                String gender,String dob,String mobile,String username,
                                String intrest,String countryid,String notification,String show_age,int isonline)
    {
        this.userid=userid;
        this.firstname=firstname;
        this.lastname=lastname;
        this.email=email;
        this.gender=gender;
        this.dob=dob;
        this.mobile=mobile;
        this.username=username;
        this.intrest=intrest;
        this.countryid=countryid;
        this.notification=notification;
        this.show_age=show_age;
        this.isonline=isonline;
    }
    public  UserProfileDetailVO()
    {
        this.userid=-1;
        this.firstname="";
        this.lastname="";
        this.email="";
        this.gender="";
        this.dob="";
        this.mobile="";
        this.username="";
        this.intrest="";
        this.countryid="";
        this.notification="";
        this.show_age="";
        this.show_age="";
        this.isonline=0;
    }

    public int getUserid() {
        return userid;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getDob() {
        return dob;
    }

    public String getMobile() {
        return mobile;
    }

    public String getUsername() {
        return username;
    }

    public String getIntrest() {
        return intrest;
    }

    public String getCountryid() {
        return countryid;
    }

    public String getNotification() {
        return notification;
    }

    public String getShow_age() {
        return show_age;
    }

    public int getIsonline() {
        return isonline;
    }

    public void setIsonline(int isonline) {
        this.isonline = isonline;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public void setShow_age(String show_age) {
        this.show_age = show_age;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setIntrest(String intrest) {
        this.intrest = intrest;
    }

    public void setCountryid(String countryid) {
        this.countryid = countryid;
    }

}//end of class
