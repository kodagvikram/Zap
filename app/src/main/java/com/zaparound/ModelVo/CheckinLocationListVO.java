package com.zaparound.ModelVo;

public class CheckinLocationListVO {
    public String itemtitle,miles,profileurl;
    public boolean isselected;
    public  double latitude ;
    public double longitude ;
    public String vicinity,placeid,iconurl,description;
    public  CheckinLocationListVO(String itemtitle,String miles,String profileurl,String iconurl,String vicinity,String placeid,String description,double latitude,double longitude,boolean isselected)
    {
        this.itemtitle = itemtitle;
        this.miles = miles;
        this.profileurl = profileurl;
        this.isselected = isselected;
        this.vicinity = vicinity;
        this.placeid = placeid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.iconurl = iconurl;
        this.description = description;
    }

    public  CheckinLocationListVO()
    {
        this.itemtitle = "";
        this.miles = "";
        this.profileurl = "";
        isselected=false;
        this.vicinity = "";
        this.placeid = "";
        this.latitude = 0;
        this.longitude = 0;
        this.iconurl = "";
        this.description = "";
    }

    public void setItemtitle(String itemtitle) {
        this.itemtitle = itemtitle;
    }

    public String getItemtitle() {
        return itemtitle;
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

    public String getVicinity() {
        return vicinity;
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

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
