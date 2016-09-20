package com.zaparound.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zaparound.ModelVo.ChatUserListVO;
import com.zaparound.ModelVo.InterestVO;
import com.zaparound.ModelVo.UserProfileDetailVO;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    public static String DB_NAME = "ZAPAROUND";
    public static int DB_VERSION = 1;
    Context context;

    /**
     * Normal User tables
     */
    public static final String USER_PROFILE_DETAIL = "USER_PROFILE";
    //Interest Table
    public static final String INTEREST_DETAIL = "INTEREST";
    //Connections Table
    public static final String CONNECTIONS_DETAIL = "CONNECTION";

    public String createUserProfileDetails = "CREATE TABLE IF NOT EXISTS " + USER_PROFILE_DETAIL
            + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,USER_ID INTEGER NOT NULL,FIRST_NAME TEXT NOT NULL,"
            + "LAST_NAME TEXT NOT NULL,EMAIL TEXT NOT NULL,GENDER TEXT NOT NULL,"
            + "DOB TEXT NOT NULL,MOBILE TEXT NOT NULL,USER_NAME TEXT NOT NULL,"
            + "INTREST TEXT ALLOW NULL,COUNTRY_ID TEXT ALLOW NULL,NOTIFICATION TEXT NOT NULL,"
            + "SHOWAGE TEXT ALLOW NULL,ISONLINE INTEGER)";
    public String createInterestDetails = "CREATE TABLE IF NOT EXISTS " + INTEREST_DETAIL
            + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,INTEREST_ID INTEGER NOT NULL,INTEREST_NAME TEXT NOT NULL,"
            + "STATUS TEXT NOT NULL,CREATED_DATE TEXT NOT NULL)";

    public String createConnectionDetails = "CREATE TABLE IF NOT EXISTS " + CONNECTIONS_DETAIL
            + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,USER_ID TEXT NOT NULL,USER_NAME TEXT ALLOW NULL,"
            + "GENDER TEXT ALLOW NULL,AGE TEXT ALLOW NULL,PROFILE_URL TEXT ALLOW NULL,"
            + "USER_SELFI_THUMB TEXT ALLOW NULL,PLACE_NAME TEXT ALLOW NULL,TIME TEXT ALLOW NULL,"
            + "LAST_MESSAGE TEXT ALLOW NULL,STATUS_MESSAGE TEXT ALLOW NULL,STATUS TEXT ALLOW NULL,"
            + "CHANNEL TEXT ALLOW NULL,SIMILAR_INTREST_COUNT TEXT ALLOW NULL,SIMILAR_INTREST_NAME TEXT ALLOW NULL,"
            + "USER_OWN_SELFI TEXT ALLOW NULL,USER_OWN_SELFI_THUMB TEXT ALLOW NULL,"
            + "SHOWAGE TEXT ALLOW NULL,LAST_ID TEXT ALLOW NULL)";


    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(createUserProfileDetails);
            db.execSQL(createInterestDetails);
            db.execSQL(createConnectionDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        try {
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * To add createUserProfileDetails
     *
     * @throws Exception
     */

    public long addUserprofileDetails(UserProfileDetailVO vo) throws Exception {
        try {
            long count;
            ContentValues values = new ContentValues();
            // values.put("ID", vo.getId());
            values.put("USER_ID", vo.getUserid());
            values.put("FIRST_NAME", vo.getFirstname());
            values.put("LAST_NAME", vo.getLastname());
            values.put("EMAIL", vo.getEmail());
            values.put("GENDER", vo.getGender());
            values.put("DOB", vo.getDob());
            values.put("MOBILE", vo.getMobile());
            values.put("USER_NAME", vo.getUsername());
            values.put("INTREST", vo.getIntrest());
            values.put("COUNTRY_ID", vo.getCountryid());
            values.put("NOTIFICATION", vo.getUsername());
            values.put("SHOWAGE", vo.getIntrest());
            values.put("ISONLINE", vo.getCountryid());
            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            count = database.insert(USER_PROFILE_DETAIL, null, values);
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            return count;
        } catch (Exception e) {
            throw new Exception(e.getMessage());

        }
    }

    /**
     * To add createIntrestDetails
     *
     * @throws Exception
     */

    public long addInterestDetails(InterestVO vo) throws Exception {
        try {
            long count;
            ContentValues values = new ContentValues();
            // values.put("ID", vo.getId());
            values.put("INTEREST_ID", vo.getIntrestid());
            values.put("INTEREST_NAME", vo.getIntrest());
            values.put("STATUS", vo.getStatus());
            values.put("CREATED_DATE", vo.getCreate_date());
            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            count = database.insert(INTEREST_DETAIL, null, values);
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            return count;
        } catch (Exception e) {
            throw new Exception(e.getMessage());

        }
    }
    /**
     * To add addConnectionDetails
     *
     * @throws Exception
     */

    public long addConnectionDetails(ChatUserListVO vo) throws Exception {
        try {
            long count;
            ContentValues values = new ContentValues();
            // values.put("ID", vo.getId());
            values.put("USER_ID", vo.getUserid());
            values.put("USER_NAME", vo.getUsername());
            values.put("GENDER", vo.getGender());
            values.put("AGE", vo.getAge());
            values.put("PROFILE_URL", vo.getProfileurl());
            values.put("USER_SELFI_THUMB", vo.getUser_selfie_thumb());
            values.put("PLACE_NAME", vo.getPlace_name());
            values.put("TIME", vo.getTime());
            values.put("LAST_MESSAGE", vo.getLastmessage());
            values.put("STATUS_MESSAGE", vo.getStatusMessage());
            values.put("STATUS", vo.getStatus());
            values.put("CHANNEL", vo.getChannel());
            values.put("SIMILAR_INTREST_COUNT", vo.getSimilar_interest_count());
            values.put("SIMILAR_INTREST_NAME", vo.getSimilar_interest_name());
            values.put("USER_OWN_SELFI", vo.getUser_own_selfi());
            values.put("USER_OWN_SELFI_THUMB", vo.getUser_own_selfithumb());
            values.put("SHOWAGE", vo.getShow_age());
            values.put("LAST_ID", vo.getLast_id());

//            values.put("USER_ID", "sdsg");
//            values.put("USER_NAME", "sdsg");
//            values.put("GENDER", "sdsg");
//            values.put("AGE", "sdsg");
//            values.put("PROFILE_URL","sdsg");
//            values.put("USER_SELFI_THUMB", "sdsg");
//            values.put("PLACE_NAME", "sdsg");
//            values.put("TIME", "sdsg");
//            values.put("LAST_MESSAGE", "sdsg");
//            values.put("STATUS_MESSAGE","fgdfg");
//            values.put("STATUS", "dgdfg");
//            values.put("CHANNEL", "dfgffg");
//            values.put("SIMILAR_INTREST_COUNT", "sdsg");
//            values.put("SIMILAR_INTREST_NAME", "sdsg");
//            values.put("USER_OWN_SELFI", "sdsg");
//            values.put("USER_OWN_SELFI_THUMB", "sdsg");
//            values.put("SHOWAGE", "sdsg");
//            values.put("LAST_ID", "sdsg");

            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            count = database.insert(CONNECTIONS_DETAIL, null, values);
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            return count;
        } catch (Exception e) {
            throw new Exception(e.getMessage());

        }
    }
    /**
     * To update UserprofileDetails
     *
     * @return number of columns updated
     * @throws Exception
     */


    public long updateUserprofileDetails(UserProfileDetailVO vo) throws Exception {
        try {
            long count;
            ContentValues values = new ContentValues();
            values.put("FIRST_NAME", vo.getFirstname());
            values.put("LAST_NAME", vo.getLastname());
            values.put("EMAIL", vo.getEmail());
            values.put("GENDER", vo.getGender());
            values.put("DOB", vo.getDob());
            values.put("MOBILE", vo.getMobile());
            values.put("USER_NAME", vo.getUsername());
            values.put("INTREST", vo.getIntrest());
            values.put("COUNTRY_ID", vo.getCountryid());
            values.put("NOTIFICATION", vo.getUsername());
            values.put("SHOWAGE", vo.getIntrest());
            values.put("ISONLINE", vo.getCountryid());
            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            count = database.update(USER_PROFILE_DETAIL , values, "USER_ID=?",
                    new String[]{"" + vo.getUserid()});
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            return count;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    /**
     * To update IntrestDetails
     *
     * @return number of columns updated
     * @throws Exception
     */


    public long updateInterestDetails(InterestVO vo) throws Exception {
        try {
            long count;
            ContentValues values = new ContentValues();
            values.put("INTEREST_ID", vo.getIntrestid());
            values.put("INTEREST_NAME", vo.getIntrest());
            values.put("STATUS", vo.getStatus());
            values.put("CREATED_DATE", vo.getCreate_date());
            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            count = database.update(INTEREST_DETAIL , values, "INTEREST_ID=?",
                    new String[]{"" + vo.getIntrestid()});
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            return count;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    /**
     * To update updateConnectionDetails
     *
     * @return number of columns updated
     * @throws Exception
     */


    public long updateConnectionDetails(ChatUserListVO vo) throws Exception {
        try {
            long count;
            ContentValues values = new ContentValues();
            values.put("USER_NAME", vo.getUsername());
            values.put("GENDER", vo.getGender());
            values.put("AGE", vo.getAge());
            values.put("PROFILE_URL", vo.getProfileurl());
            values.put("USER_SELFI_THUMB", vo.getUser_selfie_thumb());
            values.put("PLACE_NAME", vo.getPlace_name());
            values.put("TIME", vo.getTime());
            values.put("LAST_MESSAGE", vo.getLastmessage());
            values.put("STATUS_MESSAGE", vo.getStatusMessage());
            values.put("STATUS", vo.getStatus());
            values.put("CHANNEL", vo.getChannel());
            values.put("SIMILAR_INTREST_COUNT", vo.getSimilar_interest_count());
            values.put("SIMILAR_INTREST_NAME", vo.getSimilar_interest_name());
            values.put("USER_OWN_SELFI", vo.getUser_own_selfi());
            values.put("USER_OWN_SELFI_THUMB", vo.getUser_own_selfithumb());
            values.put("SHOWAGE", vo.getShow_age());
            values.put("LAST_ID", vo.getLast_id());
            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            count = database.update(CONNECTIONS_DETAIL , values, "USER_ID=?",
                    new String[]{"" + vo.getUserid()});
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            return count;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    /**
     * Used to get all Userprofile details
     * @return Userprofile details arraylist
     * @throws Exception
     */
    public ArrayList<UserProfileDetailVO> getAllUserprofileDetails() throws Exception {
        try {
            ArrayList<UserProfileDetailVO> profilelist = new ArrayList<UserProfileDetailVO>();

            SQLiteDatabase database = getWritableDatabase();
            String query = "SELECT * FROM " + USER_PROFILE_DETAIL ;
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                profilelist.add((new UserProfileDetailVO(cursor.getInt(1),
                        cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5),
                        cursor.getString(6), cursor.getString(7),
                        cursor.getString(8), cursor.getString(9),
                        cursor.getString(10),cursor.getString(11),
                        cursor.getString(12), cursor.getInt(13))));
            }
            cursor.close();
            database.close();
            return profilelist;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Used to get all Interest details
     * @return Interest details arraylist
     * @throws Exception
     */
    public ArrayList<InterestVO> getAllInterestDetails() throws Exception {
        try {
            ArrayList<InterestVO> intersetlist = new ArrayList<>();

            SQLiteDatabase database = getWritableDatabase();
            String query = "SELECT * FROM " + INTEREST_DETAIL ;
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                intersetlist.add((new InterestVO(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3),false)));
            }
            cursor.close();
            database.close();
            return intersetlist;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    /**
     * Used to get all Connections
     * @return ChatUserListVO details arraylist
     * @throws Exception
     */
    public ArrayList<ChatUserListVO> getAllConnections() throws Exception {
        try {
            ArrayList<ChatUserListVO> connections = new ArrayList<>();

            SQLiteDatabase database = getWritableDatabase();
            String query = "SELECT * FROM " + CONNECTIONS_DETAIL ;
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                ChatUserListVO vo=new ChatUserListVO();
                vo.setUserid(cursor.getString(1));
                vo.setUsername(cursor.getString(2));
                vo.setGender(cursor.getString(3));
                vo.setAge(cursor.getString(4));
                vo.setProfileurl(cursor.getString(5));
                vo.setUser_selfie_thumb(cursor.getString(6));
                vo.setPlace_name(cursor.getString(7));
                vo.setTime(cursor.getString(8));
                vo.setLastmessage(cursor.getString(9));
                vo.setStatusMessage(cursor.getString(10));
                vo.setStatus(cursor.getString(11));
                vo.setChannel(cursor.getString(12));
                vo.setSimilar_interest_count(cursor.getString(13));
                vo.setSimilar_interest_name(cursor.getString(14));
                vo.setUser_own_selfi(cursor.getString(15));
                vo.setUser_own_selfithumb(cursor.getString(16));
                vo.setShow_age(cursor.getString(17));
                vo.setLast_id(cursor.getString(18));
                connections.add(vo);
            }
            cursor.close();
            database.close();
            return connections;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    /**
     * Used to get single profile details to edit
     *
     * @param userid fgh
     * @return User profile details
     * @throws Exception
     */
    public UserProfileDetailVO getUserprofileDetails(int userid) throws Exception {
        try {
            UserProfileDetailVO userprofile = new UserProfileDetailVO();
            SQLiteDatabase database = getWritableDatabase();
            String query = "SELECT * FROM " + USER_PROFILE_DETAIL  + " WHERE USER_ID=" + userid;
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                userprofile = ((new UserProfileDetailVO(cursor.getInt(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12), cursor.getInt(13))));
            }
            cursor.close();
            database.close();
            return userprofile;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    /**
     * Used to get single Interest by id
     *
     * @param interestid pass
     * @return User profile details
     * @throws Exception
     */
    public InterestVO getInterestDetails(int interestid) throws Exception {
        try {
            InterestVO userprofile=new InterestVO();
            SQLiteDatabase database = getWritableDatabase();
            String query = "SELECT * FROM " + INTEREST_DETAIL  + " WHERE INTEREST_ID=" + interestid;
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                userprofile = (new InterestVO(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3),false));
            }
            cursor.close();
            database.close();
            return userprofile;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    /**
     * Used to get single profile details to edit
     *
     * @param userid fgh
     * @return User Connection details
     * @throws Exception
     */
    public ChatUserListVO getConnectionDetails(int userid) throws Exception {
        try {
            ChatUserListVO vo = new ChatUserListVO();
            SQLiteDatabase database = getWritableDatabase();
            String query = "SELECT * FROM " + CONNECTIONS_DETAIL  + " WHERE USER_ID=" + userid;
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                vo.setUserid(cursor.getString(1));
                vo.setUsername(cursor.getString(2));
                vo.setGender(cursor.getString(3));
                vo.setAge(cursor.getString(4));
                vo.setProfileurl(cursor.getString(5));
                vo.setUser_selfie_thumb(cursor.getString(6));
                vo.setPlace_name(cursor.getString(7));
                vo.setTime(cursor.getString(8));
                vo.setLastmessage(cursor.getString(9));
                vo.setStatusMessage(cursor.getString(10));
                vo.setStatus(cursor.getString(11));
                vo.setChannel(cursor.getString(12));
                vo.setSimilar_interest_count(cursor.getString(13));
                vo.setSimilar_interest_name(cursor.getString(14));
                vo.setUser_own_selfi(cursor.getString(15));
                vo.setUser_own_selfithumb(cursor.getString(16));
                vo.setShow_age(cursor.getString(17));
                vo.setLast_id(cursor.getString(18));
            }
            cursor.close();
            database.close();
            return vo;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    /**
     * Used to get last id
     *
     * @param userid fgh
     * @return User string id
     * @throws Exception
     */
    public String getLastid(int userid) throws Exception {
        try {
            ChatUserListVO vo = new ChatUserListVO();
            SQLiteDatabase database = getWritableDatabase();
            String query = "SELECT * FROM " + CONNECTIONS_DETAIL  + " WHERE USER_ID=" + userid+"  ORDER BY LAST_ID DESC LIMIT 1;";
            Cursor cursor = database.rawQuery(query, null);
            try {
                if(cursor!=null)
                {
                    if(cursor.getCount() == 0)
                    {
                        return "0";
                    }
                    else
                        return cursor.getString(18);
                }
            }catch (Exception e){e.printStackTrace();}

            if(cursor!=null)
            cursor.close();
            database.close();
            return "0";
        } catch (Exception e) {

            throw new Exception(e.getMessage());
        }
    }
    /**
     * To delete User profile details as for user id
     *
     * @param userid pass
     * @return number of rows deleted
     * @throws Exception
     */
    public long deleteUserprofileDetailsbyID(int userid) throws Exception {
        try {
            long count;
            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            count = database.delete(USER_PROFILE_DETAIL , "USER_ID=?",
                    new String[]{String.valueOf(userid)});
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            return count;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * To delete interest details as for interest id
     *
     * @param interestid pass
     * @return number of rows deleted
     * @throws Exception
     */
    public long deleteInterestDetailsbyID(int interestid) throws Exception {
        try {
            long count;
            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            count = database.delete(INTEREST_DETAIL , "INTEREST_ID=?",
                    new String[]{String.valueOf(interestid)});
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            return count;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    /**
     * To delete User Connection as for user id
     *
     * @param userid pass
     * @return number of rows deleted
     * @throws Exception
     */
    public long deleteConnectionbyID(int userid) throws Exception {
        try {
            long count;
            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            count = database.delete(CONNECTIONS_DETAIL , "USER_ID=?",
                    new String[]{String.valueOf(userid)});
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            return count;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * To delete User profile details
     *
     * @return count of deleted columns
     * @throws Exception
     */
    public int deleteUserprofileDetails() throws Exception {
        try {
            int count;
            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            count = database.delete(USER_PROFILE_DETAIL , null, null);
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            return count;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    /**
     * To delete Interest details
     *
     * @return count of deleted columns
     * @throws Exception
     */
    public int deleteInterestDetails() throws Exception {
        try {
            int count;
            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            count = database.delete(INTEREST_DETAIL , null, null);
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            return count;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    /**
     * To delete Connection
     *
     * @return count of deleted columns
     * @throws Exception
     */
    public int deleteConnectionDetails() throws Exception {
        try {
            int count;
            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            count = database.delete(CONNECTIONS_DETAIL , null, null);
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            return count;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
