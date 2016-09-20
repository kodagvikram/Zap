package com.zaparound.IntentServices;


import android.app.IntentService;
import android.content.Intent;

import com.zaparound.AndroidMultiPartEntity;
import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.CheckinZaparoundFragment;
import com.zaparound.LandingActivity;
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

public class CheckoutIntentService extends IntentService {

    public static final String REQUEST_STRING = "USERID";

    public Appsingleton appsingleton;
    public String result ="";
    public CheckoutIntentService() {
        super("CheckoutIntentService");
        appsingleton=Appsingleton.getinstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userid = intent.getStringExtra(REQUEST_STRING);
        try {
            if(userid==null) userid=""+appsingleton.getUserid();
        }catch (Exception e){e.printStackTrace();}
        String URL = ConnectionsURL.getCheckoutUrl();
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
                            appsingleton.setCheckinsession(false);
                            appsingleton.ToastMessage("Check out successfully..");
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
                    case 111:
                        appsingleton.ToastMessage(getResources().getString(R.string.st_failstatus));
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