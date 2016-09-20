package com.zaparound.AppUtils;


public class ConnectionsURL {
    public static String BASE_URL = "";
    // POSTDATA_KEY
    public static final String POSTDATA_KEY = "e10adc3949ba59abbe56e057f20f883e";

    // Email and Mobile no validation
    public static final String EMAIL_VALIDATION_URL = getBaseURL()
            + "emailvalidation";
    // Username validation
    public static final String USERNAME_VALIDATION_URL = getBaseURL()
            + "usernamevalidation";

    // UserRegistration  url
    public static final String USER_REGISTRATION_URL = getBaseURL()
            + "register";
    // UserLogin  url
    public static final String USER_LOGIN_URL = getBaseURL()
            + "login";
    // forgotpassword  url
    public static final String FORGOT_PASSWORD_URL = getBaseURL()
            + "forgotpassword";
    // OTP Verification  url
    public static final String OTP_VERIFICATIO_URL = getBaseURL()
            + "verifyotp";
    // Update password  url
    public static final String UPDATE_PASSWORD_URL = getBaseURL()
            + "updatepassword";

    // Update password  url
    public static final String UPDATE_PROFILE_URL = getBaseURL()
            + "updateprofile";
    // Checkin  url
    public static final String CHECKIN_SELFI_URL = getBaseURL()
            + "checkin";
    //Checkin pullto refresh  url
    public static final String CHECKIN_PULL_TO_REFRESH_URL = getBaseURL()
            + "checkin_pulldown";
    //Checkin Zap request url
    public static final String CHECKIN_ZAP_REQUEST_URL = getBaseURL()
            + "zap_status";
    //Checkin Zaps url
    public static final String ZAPS_URL = getBaseURL()
            + "zaps";
    //Checkin Zaps url
    public static final String ZAPUNREAD_URL = getBaseURL()
            + "zap_read";
    //Checkin Zaps url
    public static final String CHECKOUT_URL = getBaseURL()
            + "checkout";
    //Checkin Zaps TopLocations
    public static final String TOPLOCATIONS_URL = getBaseURL()
            + "top_locations";
    //Checkin Zaps TopLocations
    public static final String CONNECTIONS_URL = getBaseURL()
            + "connections1";
    //Checkin Zaps TopLocations
    public static final String LIVEUSERS_URL = getBaseURL()
            + "in_my_locations";

    //Checkin Locations
    public static final String CHECKINLOCATIONS_URL = getBaseURL()
            + "zap_places";
    //Checkin Locations
    public static final String CHECKINNEXTPAGE_URL = getBaseURL()
            + "zap_places_next";
    //Checkin Logout
    public static final String LOG_URL = getBaseURL()
            + "logout";
    //Checkin Support
    public static final String SUPPORT_URL = getBaseURL()
            + "support";
    //Checkin InviteFriends
    public static final String INVITEFRIENDS_URL = getBaseURL()
            + "invite";
    //Checkin InviteFriends
    public static final String INVITEFRIENDBYSMS_URL = getBaseURL()
            + "invite_by_sms";
    //Checkin Blockuser
    public static final String BLOCKUSER_URL = getBaseURL()
            + "block_user";

    //Cometchat Siteurl
    public static  String COMETCHAT_SITEURL ="";
    //Cometchat ApiKey
    public static  String COMETCHAT_APIKEY ="";

    /**
     * To get POST DATA KEY
     *
     * @return API key
     */
    public static String getPOSTDATAKey() {
        return POSTDATA_KEY;
    }

    /**
     * To get BASE_URL
     *
     * @return BASE_URL STRING
     */
    public static String getBaseURL() {
        if (AppUtils.environment.equalsIgnoreCase("DEVELOPMENT"))
            BASE_URL = "http://zaparound.com/dev/webserviceuser/";
            //BASE_URL = "http://192.168.1.33/zap1/webserviceuser/";
        else
            BASE_URL = "http://zaparound.com/webserviceuser/";
           //BASE_URL = "http://notionitsolutions.com/zaparound/admin/webserviceuser/";

        return BASE_URL;
    }

    /**
     * To get Cometchat Url
     *
     * @return getCometchatSiteurl STRING
     */
    public static String getCometchatSiteurl() {
        if (AppUtils.environment.equalsIgnoreCase("DEVELOPMENT"))
            COMETCHAT_SITEURL = "http://zaparound.com/dev/cometchat";
            //COMETCHAT_SITEURL = "http://192.168.1.33/zap1/cometchat";
        else
            COMETCHAT_SITEURL = "http://zaparound.com/cometchat/";
        return COMETCHAT_SITEURL;
    }

    /**
     * To get Cometchat Url
     *
     * @return getCometchatSiteurl STRING
     */
    public static String getCometchatApikey() {
        if (AppUtils.environment.equalsIgnoreCase("DEVELOPMENT"))
            COMETCHAT_APIKEY = "236264c232085b98d645f3641e296298";
       // COMETCHAT_APIKEY = "236264c232085b98d645f3641e296298";
       // COMETCHAT_APIKEY = "d0343c870c589953842b15e90eb12e42";
        else
            COMETCHAT_APIKEY = "21262cfbaa2523fdca91c51845b0d1c5";
        return COMETCHAT_APIKEY;
    }

    /**
     * To get EmailValidationUrl
     *
     * @return EmailValidationUrl
     */
    public static String getEmailValidationUrl() {
        return EMAIL_VALIDATION_URL;
    }

    /**
     * To get usernameValidationUrl
     *
     * @return usernameValidationUrl
     */
    public static String getUsernameValidationUrl() {
        return USERNAME_VALIDATION_URL;
    }

    /**
     * To get USER_REGISTRATION_URL
     *
     * @return USER_REGISTRATION_URL
     */
    public static String getUserRegistrationUrl() {
        return USER_REGISTRATION_URL;
    }

    /**
     * To get USER_LOGIN_URL
     *
     * @return USER_LOGIN_URL
     */
    public static String getUserLoginUrl() {
        return USER_LOGIN_URL;
    }

    /**
     * To get Forgot password
     *
     * @return Forgot password Url
     */
    public static String getForgotPasswordUrl() {
        return FORGOT_PASSWORD_URL;
    }

    /**
     * To get OTP Verification
     *
     * @return OTP Verification Url
     */
    public static String getOtpVerificatioUrl() {
        return OTP_VERIFICATIO_URL;
    }

    /**
     * To get Update Password
     *
     * @return Update Password Url
     */
    public static String getUpdatePasswordUrl() {
        return UPDATE_PASSWORD_URL;
    }

    /**
     * To get Update Profile
     *
     * @return Update Profile Url
     */
    public static String getUpdateProfileUrl() {
        return UPDATE_PROFILE_URL;
    }

    /**
     * To get getCheckinSelfiUrl
     *
     * @return getCheckinSelfiUrl Url
     */
    public static String getCheckinSelfiUrl() {
        return CHECKIN_SELFI_URL;
    }

    /**
     * To get getCheckinPulltorefreshUrl
     *
     * @return CheckinPulltorefresh Url
     */
    public static String getCheckinPullToRefreshUrl() {
        return CHECKIN_PULL_TO_REFRESH_URL;
    }

    /**
     * To get getCheckinZapRequestUrl
     *
     * @return CheckinZapRequest Url
     */
    public static String getCheckinZapRequestUrl() {
        return CHECKIN_ZAP_REQUEST_URL;
    }

    /**
     * To get ZapsUrl
     *
     * @return ZapsUrl Url
     */
    public static String getZapsUrl() {
        return ZAPS_URL;
    }
    /**
     * To get ZapsUnreadUrl
     *
     * @return ZapsUnreadUrl Url
     */
    public static String getZapunreadUrl() {
        return ZAPUNREAD_URL;
    }
    /**
     * To get CheckoutUrl
     *
     * @return Checkout Url
     */
    public static String getCheckoutUrl() {
        return CHECKOUT_URL;
    }
    /**
     * To get Toplocations URL
     *
     * @return Toplocations Url
     */
    public static String getToplocationsUrl() {
        return TOPLOCATIONS_URL;
    }
    /**
     * To get Connections URL
     *
     * @return Connections Url
     */

    public static String getConnectionsUrl() {
        return CONNECTIONS_URL;
    }
    /**
     * To get Liveusers URL
     *
     * @return Liveusers Url
     */
    public static String getLiveusersUrl() {
        return LIVEUSERS_URL;
    }
    /**
     * To get Checkinlocations URL
     *
     * @return Checkinlocations Url
     */
    public static String getCheckinlocationsUrl() {
        return CHECKINLOCATIONS_URL;
    }
    /**
     * To get Checkinlocationsnext page URL
     *
     * @return Checkinlocationsnext page Url
     */
    public static String getCheckinnextpageUrl() {
        return CHECKINNEXTPAGE_URL;
    }
    /**
     * To get Logout page URL
     *
     * @return Logout page Url
     */
    public static String getLogUrl() {
        return LOG_URL;
    }
    /**
     * To get Support page URL
     *
     * @return Support page Url
     */
    public static String getSupportUrl() {
        return SUPPORT_URL;
    }

    /**
     * To get invitefriends page URL
     *
     * @return Support page Url
     */
    public static String getInvitefriendsUrl() {
        return INVITEFRIENDS_URL;
    }
    /*
     * To get invitefriends SMS page URL
     *
     * @return Support page Url
     */

    public static String getInvitefriendbysmsUrl() {
        return INVITEFRIENDBYSMS_URL;
    }
     /*
     * To get Blockuser SMS page URL
     *
     * @return Support page Url
     */

    public static String getBlockuserUrl() {
        return BLOCKUSER_URL;
    }
}
