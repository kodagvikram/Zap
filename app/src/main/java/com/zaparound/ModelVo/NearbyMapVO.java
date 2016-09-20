package com.zaparound.ModelVo;


public class NearbyMapVO {
    public String placename, miles, profileurl;
    public boolean isselected;
    public double latitude;
    public double longitude;
    public String place_description, placeid, iconurl, total_checkin;

    public NearbyMapVO(String placename, String miles, String profileurl, String iconurl, String place_description, String placeid, double latitude, double longitude, String total_checkin, boolean isselected) {
        this.placename = placename;
        this.miles = miles;
        this.profileurl = profileurl;
        this.isselected = isselected;
        this.place_description = place_description;
        this.placeid = placeid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.iconurl = iconurl;
        this.total_checkin = total_checkin;
    }

    public NearbyMapVO() {
        this.placename = "";
        this.miles = "";
        this.profileurl = "";
        this.isselected = false;
        this.place_description = "";
        this.placeid = "";
        this.latitude = 0;
        this.longitude = 0;
        this.iconurl = "";
        this.total_checkin = "";
    }


    public void setMiles(String miles) {
        this.miles = miles;
    }

    public String getMiles() {
        return miles;
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getPlace_description() {
        return place_description;
    }

    public String getPlacename() {
        return placename;
    }

    public String getTotal_checkin() {
        return total_checkin;
    }

    public void setPlace_description(String place_description) {
        this.place_description = place_description;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }

    public void setTotal_checkin(String total_checkin) {
        this.total_checkin = total_checkin;
    }

}
