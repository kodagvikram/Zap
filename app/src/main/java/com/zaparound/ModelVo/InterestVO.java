package com.zaparound.ModelVo;


public class InterestVO {
    public String intrest;
    public boolean isselected;
    public int intrestid;
    public String status;
    public String create_date;

    public InterestVO(int intrestid, String intrest, String status, String create_date, boolean isselected) {
        this.intrest = intrest;
        this.isselected = isselected;
        this.intrestid = intrestid;
        this.status = status;
        this.create_date = create_date;
    }

    public InterestVO() {
        intrest = "";
        isselected = false;
        this.intrestid = -1;
        this.status = "";
        this.create_date = "";
    }

    public void setIntrest(String intrest) {
        this.intrest = intrest;
    }

    public String getIntrest() {
        return intrest;
    }

    public void setIsselected(boolean isselected) {
        this.isselected = isselected;
    }

    public boolean getisselected() {
        return isselected;
    }

    public void setIntrestid(int intrestid) {
        this.intrestid = intrestid;
    }

    public int getIntrestid() {
        return intrestid;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getCreate_date() {
        return create_date;
    }
}
