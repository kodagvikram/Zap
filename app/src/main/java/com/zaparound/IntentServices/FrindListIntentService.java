package com.zaparound.IntentServices;


import android.app.IntentService;
import android.content.Intent;

import com.zaparound.AndroidMultiPartEntity;
import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.ContactsActivity;
import com.zaparound.ModelVo.ChatUserListVO;
import com.zaparound.ModelVo.ContactStickyVO;
import com.zaparound.ModelVo.ContactsLivelistVO;
import com.zaparound.R;
import com.zaparound.Singleton.Appsingleton;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

public class FrindListIntentService  extends IntentService {

    public static final String USER_ID = "USERID";
    public static final String LAST_ID = "LASTID";

    public Appsingleton appsingleton;
    public String result ="";
    public FrindListIntentService() {
        super("FrindListIntentService");
        appsingleton=Appsingleton.getinstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userid = intent.getStringExtra(USER_ID);
        String Lastid = intent.getStringExtra(LAST_ID);
        try {
            if(userid==null) userid=""+appsingleton.getUserid();
            if(Lastid==null) Lastid="0";
        }catch (Exception e){e.printStackTrace();}
        String URL = ConnectionsURL.getConnectionsUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        try {
            HttpPost request = new HttpPost(URL);
            // request.setHeader("Accept", "application/json");
            // request.setHeader("content-type", "application/json");
            HttpClient client;

            HttpParams parameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(parameters, 10000);
            HttpConnectionParams.setSoTimeout(parameters, 20000);
            client = new DefaultHttpClient(parameters);

            JSONStringer item = new JSONStringer()
                    .object()
                    .key("user_id").value(userid)
                    //.key("place_id").value(appsingleton.getCheckinPlaceid())
                    .endObject();
                /*
                 * Create object of multi part class to upload status and text
                 * data
                 */
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                        }
                    });
            try {
                // Extra parameters if you want to pass to server
                entity.addPart("key", new StringBody(postdataKey));
                entity.addPart("json_data", new StringBody(item.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }

            request.setEntity(entity);
            HttpResponse response = client.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                result = EntityUtils.toString(response.getEntity());
            }
            appsingleton.ToastMessage("" + statusCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            ParseData(result);
        }catch(Exception e){
            e.printStackTrace();
           // appsingleton.ToastMessage("" + e.getMessage());
        }
    }//end of on handel intent

    /*
    *function to Parse data
    */
    public void ParseData(String result)
    {
        try {
          //  appsingleton.ToastMessage(result);
            if (result != null) {
                JSONObject object = new JSONObject(result);
                String resultCode = object.getString("response");
                int code = Integer.parseInt(resultCode);
                switch (code) {
                    case 101:
                        try {
                            try {
                                appsingleton.mDatabase.deleteConnectionDetails();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            try {
                                ArrayList<ChatUserListVO> tempcontactlist = new ArrayList<>();
                                tempcontactlist.clear();
                                String resultArray = object.getString("result");
                                JSONArray jsonArray = new JSONArray(resultArray);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject arrayObject = jsonArray.getJSONObject(i);
                                    ChatUserListVO vo = new ChatUserListVO();
                                    vo.setUsername(arrayObject.getString("user_name"));
                                    vo.setGender(arrayObject.getString("gender"));
                                    vo.setAge(arrayObject.getString("age"));
                                    vo.setShow_age(arrayObject.getString("show_age"));
                                    vo.setUserid(arrayObject.getString("user_id"));
                                    vo.setProfileurl(arrayObject.getString("user_selfie"));
                                    vo.setUser_selfie_thumb(arrayObject.getString("user_selfie_thumb"));
                                    vo.setSimilar_interest_count(arrayObject.getString("similar_interest_count"));
                                    vo.setSimilar_interest_name(arrayObject.getString("similar_interest_name"));
                                    vo.setLast_id(arrayObject.getString("id"));
                                    vo.setUser_own_selfi(arrayObject.getString("user_own_selfie"));
                                    vo.setUser_own_selfithumb(arrayObject.getString("user_own_selfie_thumb"));
                                    vo.setPlace_name(arrayObject.getString("place_name"));
                                    vo.setTime(arrayObject.getString("create_date"));
                                    try {
                                     appsingleton.mDatabase.addConnectionDetails(vo);
                                    }catch (Exception e){e.printStackTrace();}

                                    tempcontactlist.add(vo);
                                }//end of for
//                                appsingleton.tempLiveuserlist.clear();
//                                appsingleton.tempLiveuserlist.addAll(templiveusers);
//                                Intent broadcastIntent = new Intent();
//                                broadcastIntent.setAction(ContactsActivity.InmyLocationReceiver.PROCESS_RESPONSE);
//                                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
//                                sendBroadcast(broadcastIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                appsingleton.ToastMessage("" + e.getMessage());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            appsingleton.ToastMessage("" + e.getMessage());
                        }

                        break;
                    case 102:
                        appsingleton.ToastMessage(getResources().getString(R.string.st_parameter_missing));
                        break;
                    case 103:
                        appsingleton.ToastMessage(getResources().getString(R.string.st_invalid_privatekey));
                        break;
                    case 104:
                        appsingleton.ToastMessage(getResources().getString(R.string.st_invaliduser));
                        break;
                    case 105:
                        appsingleton.ToastMessage(getResources().getString(R.string.st_invaliduser));
                        break;
                    case 106:
                        appsingleton.ToastMessage(getResources().getString(R.string.st_pagenotfound));
                        break;
                    case 107:
                        break;
                    case 108:
                        break;
                    case 109:
                        appsingleton.ToastMessage(getResources().getString(R.string.st_invalid_verificationcode));
                        break;
                    case 113:
                        appsingleton.UserToastMessage(getResources().getString(R.string.st_failtoupload));
                        break;
                    default:

                        break;
                }
            }//end of if
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }//end of parsedata

}//end of intent service