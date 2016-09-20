package com.zaparound.IntentServices;


import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;

import com.zaparound.AndroidMultiPartEntity;
import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.CheckinZaparoundFragment;
import com.zaparound.ModelVo.CheckinZaparoundVO;
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

public class ZapWhosIntentService extends IntentService {

    public static final String REQUEST_STRING = "myRequest";
    public static final String RESPONSE_STRING = "myResponse";
    public static final String RESPONSE_MESSAGE = "myResponseMessage";

    private String URL = null;
    private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    public Appsingleton  appsingleton;
    public String result ="";
    public ZapWhosIntentService() {
        super("ZapWhosIntentService");
        appsingleton=Appsingleton.getinstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String URL = ConnectionsURL.getCheckinPullToRefreshUrl();
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
                    .key("user_id").value(appsingleton.getUserid())
                    .key("place_id").value(appsingleton.getCheckinPlaceid())
                    .endObject();
            /**
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
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }//end of on handel intent

    /*
    *function to Parse data
    */
    public void ParseData(String result)
    {
        try {
            appsingleton.ToastMessage(result);
            if (result != null) {
                JSONObject object = new JSONObject(result);
                String resultCode = object.getString("response");
                int code = Integer.parseInt(resultCode);
                switch (code) {
                    case 101:
                        try {
                            try {
                                ArrayList<CheckinZaparoundVO> tempzaparoundlist = new ArrayList<CheckinZaparoundVO>();
                                tempzaparoundlist.clear();

                                String resultArray = object.getString("result");
                                JSONArray jsonArray = new JSONArray(resultArray);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject arrayObject = jsonArray.getJSONObject(i);
                                    CheckinZaparoundVO vo = new CheckinZaparoundVO();
                                    vo.setUsername(arrayObject.getString("user_name"));
                                    vo.setGender(arrayObject.getString("gender"));
                                    vo.setAge(arrayObject.getString("age"));
                                    vo.setShow_age(arrayObject.getString("show_age"));
                                    vo.setUserid(arrayObject.getString("user_id"));
                                    vo.setPlace_name(arrayObject.getString("place_name"));
                                    vo.setProfileurl(arrayObject.getString("user_selfie"));
                                    vo.setUser_selfie_thumb(arrayObject.getString("user_selfie_thumb"));
                                    vo.setSimilar_interest_count(arrayObject.getString("similar_interest_count"));
                                    vo.setSimilar_interest_name(arrayObject.getString("similar_interest_name"));
                                    tempzaparoundlist.add(vo);
                                }//end of for

                                //if(tempzaparoundlist.size()>0) {
                                    appsingleton.tempzaparoundlist.clear();
                                    appsingleton.tempzaparoundlist.addAll(tempzaparoundlist);
                                    Intent broadcastIntent = new Intent();
                                    broadcastIntent.setAction(CheckinZaparoundFragment.MyZapCheckinRequestReceiver.PROCESS_RESPONSE);
                                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                    //broadcastIntent.putExtra(RESPONSE_STRING, responseString);
                                    //broadcastIntent.putExtra(RESPONSE_MESSAGE, responseMessage);
                                    sendBroadcast(broadcastIntent);
                                //}

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