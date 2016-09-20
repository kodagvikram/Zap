package com.zaparound;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.zaparound.Adapters.CheckinLocationListAdapter;
import com.zaparound.AppUtils.AppUtils;
import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.ModelVo.CheckinLocationListVO;
import com.zaparound.ModelVo.InterestVO;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class CheckinListActivity extends AppCompatActivity implements LocationListener {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    public ImageView iv_logoimage;
    public LinearLayout ll_bottombtnlayout;
    public TextView tv_logotitle, tv_createaccount, tv_validationtext;
    public Button bt_enablegps;
    RelativeLayout rl_setting, rl_recyclelayout, rl_locationlayout;
    public RelativeLayout rl_ckechinlayout;
    public Button bt_checkin;
    public CheckinLocationListVO vo;
    Toolbar toolbar;
    GPSTracker gps;
    RecyclerView recyclerView;
    public ArrayList<CheckinLocationListVO> chechinlist;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public CheckinLocationListAdapter adapter;
    private static final int LOCATION_SETTINGS = 4;
    LinearLayoutManager mLayoutManager;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    public RelativeLayout rl_bottom;
    public TextView tv_tollbartitle;
    @Override
    protected void onResume() {
        super.onResume();
        try {
            appsingleton=Appsingleton.getinstance(this);
            appsingleton.finishAllSession();
        }catch (Exception e){e.printStackTrace();}
        try {
            if(appsingleton.needPermissionCheck()) {
               if(appsingleton.locationPermission(this)) {
                   gps = new GPSTracker(this);
                   if (gps.canGetLocation()) {
                       bt_enablegps.setText(getResources().getString(R.string.st_refresh));
                       tv_createaccount.setVisibility(View.GONE);
                   } else {
                       bt_enablegps.setText(getResources().getString(R.string.st_enablebtn));
                       tv_createaccount.setVisibility(View.VISIBLE);
                   }
               }
            }
            else
            {
                gps = new GPSTracker(this);
                if (gps.canGetLocation()) {
                    bt_enablegps.setText(getResources().getString(R.string.st_refresh));
                    tv_createaccount.setVisibility(View.GONE);
                } else {
                    bt_enablegps.setText(getResources().getString(R.string.st_enablebtn));
                    tv_createaccount.setVisibility(View.VISIBLE);
                }
            }

        } catch (Exception e) {
            appsingleton.ToastMessage("" + e.getMessage());
        }

        try {
            callLocationListner();
        }catch (Exception e){e.printStackTrace();}
    }//end of resume
    @Override
    protected void onPause() {
        super.onPause();
        try{
            appsingleton.locationManager.removeUpdates(appsingleton.networkLocationListener);
            appsingleton. locationManager.removeUpdates(appsingleton.gpsLocationListener);
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            appsingleton = Appsingleton.getinstance(this);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_checkin_list);

            //appsingleton instance
            //get height width
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            appsingleton.deviceheight = metrics.heightPixels;
            appsingleton.devicewidth = metrics.widthPixels;
            appsingleton.activityArrayList.add(this);



            toolbar = (Toolbar) findViewById(R.id.tool_bar1);
            setSupportActionBar(toolbar);
            rl_setting = (RelativeLayout) toolbar.findViewById(R.id.iv_checksetting);
            tv_tollbartitle = (TextView) toolbar.findViewById(R.id.tv_title);

            // add back arrow to toolbar
            chechinlist = new ArrayList<>();

            //initialize views
            try{
                if(appsingleton.needPermissionCheck()) {
                    if (appsingleton.locationPermission(this))
                        gps = new GPSTracker(this);

                }
                else
                    gps = new GPSTracker(this);
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }


            iv_logoimage = (ImageView) findViewById(R.id.iv_LOGOIMAGE);
            tv_logotitle = (TextView) findViewById(R.id.tv_logintitle);
            ll_bottombtnlayout = (LinearLayout) findViewById(R.id.ll_Buttonlayout);
            rl_recyclelayout = (RelativeLayout) findViewById(R.id.rl_Checkinlistlayout);
            rl_locationlayout = (RelativeLayout) findViewById(R.id.rl_nolocationfoundlayout);
            bt_enablegps = (Button) findViewById(R.id.bt_enablegps);
            tv_validationtext = (TextView) findViewById(R.id.tv_title);
            tv_createaccount = (TextView) findViewById(R.id.tv_gpsmess);
            rl_ckechinlayout = (RelativeLayout) findViewById(R.id.rl_checkinlayout);
            bt_checkin = (Button) findViewById(R.id.bt_checkinbtn);
            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
            rl_bottom = (RelativeLayout) findViewById(R.id.bottom);
            //initialize recyclerView
            recyclerView = (RecyclerView) findViewById(R.id.rc_checkin_recyclerview);
            mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mLayoutManager.setSmoothScrollbarEnabled(true);
            recyclerView.setScrollbarFadingEnabled(true);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);

            vo = new CheckinLocationListVO();
            /*set custom fonts*/
            tv_logotitle.setTypeface(appsingleton.regulartype);
            tv_createaccount.setTypeface(appsingleton.lighttype);
            bt_enablegps.setTypeface(appsingleton.regulartype);
            tv_validationtext.setTypeface(appsingleton.lighttype);
            tv_tollbartitle.setTypeface(appsingleton.regulartype);
             /*setheight width Programatically*/
            SetHeightWidth(iv_logoimage, 0.50, 0.30);

            //Set margins programatically
            setMargins(bt_checkin, 0.07, 0, 0.07, 0);

            callWebservice();
            /*Set Margins*/
            setMargins(ll_bottombtnlayout, 0.07, 0, 0.07, 0);

            try{
                appsingleton.CometChatSubscribe();
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }



            rl_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                            appsingleton.getTabhostcontext().SettingClick();
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Refresh items
                    refreshItems();
                }
            });

            /*
            *load more location data
            */
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    try {
                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                        if (loading) {
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                loading = false;
                                rl_bottom.setVisibility(View.VISIBLE);
                                NextpageLocationList();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of oncreate

    /*
    *function to call webservice
    */
    public void callWebservice()
    {
        try{
        // check if GPS enabled
        if (AppUtils.isNetworkAvailable(this)) {
            if(appsingleton.needPermissionCheck()) {
                if(appsingleton.locationPermission(this))
                {
                    if (gps.canGetLocation()) {
                        try {
                            try {
                                callLocationListner();
                            }catch (Exception e){e.printStackTrace();}
                            if (appsingleton.currentLatitude == 0) {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        new CheckinPulltorefresh(appsingleton.currentLatitude,appsingleton.currentLongitude).execute();
                                        //refreshLocationList();
                                    }
                                }, 500);
                            } else {
//                                appsingleton.currentLatitude=gps.getLatitude();
//                                appsingleton.currentLongitude=gps.getLongitude();
                                new CheckinPulltorefresh(appsingleton.currentLatitude, appsingleton.currentLongitude).execute();
                                // refreshLocationList();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            appsingleton.ToastMessage("" + e.getMessage());
                        }
                    } else {
                        rl_locationlayout.setVisibility(View.VISIBLE);
                        rl_recyclelayout.setVisibility(View.GONE);
                    }
                }//end of permission request
                else
                {
                    rl_locationlayout.setVisibility(View.VISIBLE);
                }
            }//end if
            else
            {
                if (gps.canGetLocation()) {
                    try {
                        try {
                            callLocationListner();
                        }catch (Exception e){e.printStackTrace();}
                        if (appsingleton.currentLatitude == 0) {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                    appsingleton.currentLatitude=gps.getLatitude();
//                                    appsingleton.currentLongitude=gps.getLongitude();
                                   new CheckinPulltorefresh(appsingleton.currentLatitude,appsingleton.currentLongitude).execute();
                                }
                            }, 500);
                        } else {
//                            appsingleton.currentLatitude=gps.getLatitude();
//                            appsingleton.currentLongitude=gps.getLongitude();
                            new CheckinPulltorefresh(appsingleton.currentLatitude, appsingleton.currentLongitude).execute();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                } else {
                    rl_locationlayout.setVisibility(View.VISIBLE);
                    rl_recyclelayout.setVisibility(View.GONE);
                }
            }

        }//end of network if
        else
        {
            rl_locationlayout.setVisibility(View.VISIBLE);
            rl_recyclelayout.setVisibility(View.GONE);
            appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",rl_locationlayout);
        }
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    void refreshItems() {
        try {
            //Check internet permission
            if (AppUtils.isNetworkAvailable(this)) {
                try {
                    callLocationListner();
                }catch (Exception e){e.printStackTrace();}
                new CheckinPulltorefresh(appsingleton.currentLatitude,appsingleton.currentLongitude).execute();
               // refreshLocationList();
            }//end of network if
            else
            {
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",rl_locationlayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onItemsLoadComplete();
            }
        }, 1000);
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }
    /*
   *function to get nextpage locations list
   */
    public void NextpageLocationList() {
        try {
            try {
                callLocationListner();
            }catch (Exception e){e.printStackTrace();}
            if (AppUtils.isNetworkAvailable(this)) {
//                appsingleton.currentLatitude=gps.getLatitude();
//                appsingleton.currentLongitude=gps.getLongitude();
                new Checkinnextpage(appsingleton.currentLatitude,appsingleton.currentLongitude).execute();
                // refreshLocationList();
            }//end of network if
            else
            {
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",rl_locationlayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /*
     * Refresh list without changing current position
     */
    public void reloadListData(ArrayList<CheckinLocationListVO> list) {
        try {
            chechinlist = list;
            rl_ckechinlayout.setVisibility(View.GONE);
            if (list.isEmpty()) {
                rl_locationlayout.setVisibility(View.VISIBLE);
                rl_recyclelayout.setVisibility(View.GONE);
            } else {
//                try{
//                    Collections.sort(chechinlist, new Comparator<CheckinLocationListVO>() {
//                        public int compare(CheckinLocationListVO s1, CheckinLocationListVO s2) {
//                            return Double.compare(Double.valueOf(s1.getMiles()),Double.valueOf(s2.getMiles()));
//                        }
//                    });
//                }catch(Exception e){
//                    e.printStackTrace();
//                    appsingleton.ToastMessage("" + e.getMessage());
//                }
                // intrestgridView.setVisibility(ListView.VISIBLE);
                if (adapter != null) {
                    rl_locationlayout.setVisibility(View.GONE);
                    rl_recyclelayout.setVisibility(View.VISIBLE);
                    adapter.reload(list);
                    loading = true;
                } else {
                    bindList(list);
                }
            }
            rl_bottom.setVisibility(View.GONE);
            appsingleton.dismissDialog();
        } catch (Exception e) {
            rl_bottom.setVisibility(View.GONE);
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /**
     * Bind all orders to list view
     */
    public void bindList(ArrayList<CheckinLocationListVO> list) {
        try {
            chechinlist = list;
            if (list.isEmpty()) {
                rl_locationlayout.setVisibility(View.VISIBLE);
                rl_recyclelayout.setVisibility(View.GONE);
            } else {
                rl_locationlayout.setVisibility(View.GONE);
                rl_recyclelayout.setVisibility(View.VISIBLE);
                adapter = new CheckinLocationListAdapter(this, list);
                recyclerView.setAdapter(adapter);
                YoYo.with(Techniques.FadeInUp).duration(appsingleton.animationduration).interpolate(new AccelerateDecelerateInterpolator()).playOn(recyclerView);
            }
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }


//    /*
//    *function load data
//    */
//    public void loadData() {
//        chechinlist.clear();
//        rl_locationlayout.setVisibility(View.GONE);
//        rl_recyclelayout.setVisibility(View.VISIBLE);
//        for (int i = 0; i < 5; i++) {
////            CheckinLocationListVO vo = new CheckinLocationListVO("De Nearby Hotel", "1 mile", "", false);
////            CheckinLocationListVO vo1 = new CheckinLocationListVO("Pizza Hut", "10 mile", "", false);
////            CheckinLocationListVO vo2 = new CheckinLocationListVO("Usbank", "100 mile", "", false);
////            chechinlist.add(vo);
////            chechinlist.add(vo1);
////            chechinlist.add(vo2);
//        }
//        adapter = new CheckinLocationListAdapter(this, chechinlist);
//        recyclerView.setAdapter(adapter);
//    }//end of load data

    /*
       *function to hidecheckinview
       */
    public void HideShowCheckinview(CheckinLocationListVO vo) {
        try {
            //Set margins programatically
            setMargins(bt_checkin, 0.07, 0, 0.07, 0);
            this.vo = vo;
            if (vo.getisselected()) {
                rl_ckechinlayout.setVisibility(View.VISIBLE);
            } else {
                rl_ckechinlayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }

    /*
    * function to checkin click
    */
    public void Checkin(View view) {
        try {
            Intent intent = new Intent(this, SelfiCameraActivity.class);
            intent.putExtra("PLACE_ID",vo.getPlaceid());
            intent.putExtra("PLACE_NAME",vo.getItemtitle());
            intent.putExtra("PLACE_PHOTOREFERENCE",vo.getProfileurl());
            intent.putExtra("PLACE_ADDRESS",vo.getVicinity());
            intent.putExtra("PLACE_LAT",""+vo.getLatitude());
            intent.putExtra("PLACE_LANG",""+vo.getLongitude());
            intent.putExtra("PLACE_DESCRIPTION",""+vo.getDescription());
            startActivity(intent);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        } catch (Exception e) {
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }//end of check in

    /* Function to enable GPS
   * */
    public void EnableGPS(View view) {
        try {
            if(appsingleton.needPermissionCheck()) {
                if(appsingleton.locationPermission(this))
                {
                    try {
                        if (!gps.canGetLocation()) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, LOCATION_SETTINGS);
                            return;
                        }
                        if (bt_enablegps.getText().toString().equals("Refresh")) {
                            callWebservice();
                        }
                    } catch (Exception e) {
                        appsingleton.ToastMessage(e.getMessage());
                    }
                }//end of permission request
                else
                {

                }
            }//end of permission check
            else
            {
                try {
                    if (!gps.canGetLocation()) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, LOCATION_SETTINGS);
                        return;
                    }
                    if (bt_enablegps.getText().toString().equals("Refresh")) {
                        callWebservice();
                    }
                } catch (Exception e) {
                    appsingleton.ToastMessage(e.getMessage());
                }
            }
            //appsingleton.SnackbarMessage("Chat","This is sample message",view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
* To set View height width programatically
* */
    public void SetHeightWidth(View view, double width, double height) {
        try {
            if (width == 0) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = (int) ((appsingleton.deviceheight) * height);
            } else if (height == 0) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = (int) ((appsingleton.devicewidth) * height);
            } else {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = (int) ((appsingleton.devicewidth) * width);
                params.height = (int) ((appsingleton.deviceheight) * height);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of heightwidth

    /*
       * To set View margins programatically
       * */
    public void setMargins(View v, double l, double t, double r, double b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v
                    .getLayoutParams();
            int lpx = (int) ((appsingleton.devicewidth) * l);
            int tpx = (int) ((appsingleton.deviceheight) * t);
            int rpx = (int) ((appsingleton.devicewidth) * r);
            int bpx = (int) ((appsingleton.deviceheight) * b);
            p.setMargins(lpx, tpx, rpx, bpx);
            v.requestLayout();
        }
    }//end of margins

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
        }
    }//end of on back pressed

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {

                case LOCATION_SETTINGS:
                    // check if GPS enabled
                    if (gps.canGetLocation()) {
                        bt_enablegps.setText(getResources().getString(R.string.st_refresh));
                        tv_createaccount.setVisibility(View.GONE);
                    } else {
                        bt_enablegps.setText(getResources().getString(R.string.st_enablebtn));
                        tv_createaccount.setVisibility(View.VISIBLE);
                    }

                    break;

            }
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
//        double latitude = 18.5087086;
//        double longitude = 73.8124914;
//        int PROXIMITY_RADIUS = 5000;
//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//        LatLng latLng = new LatLng(latitude, longitude);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
    /**
     * Connections CheckinPulltorefresh  web service
     *
     * @author user
     */
    public class CheckinPulltorefresh extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getCheckinlocationsUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        double lat,long_;
        public CheckinPulltorefresh( double lat,double long_) {
            this.lat=lat;
            this.long_=long_;
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(CheckinListActivity.this);
                if(!mSwipeRefreshLayout.isRefreshing()) {
                    appsingleton.showDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public String doInBackground(String... params) {
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
                        .key("lat").value(lat)
                        .key("long").value(long_)
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
                publishProgress("Connected...");

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    publishProgress("Loading...");
                    String result = EntityUtils.toString(response.getEntity());
                    return result;
                }
                appsingleton.ToastMessage("" + statusCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(String... values) {
        }

        @Override
        public void onPostExecute(String result) {
            try {
                appsingleton.ToastMessage(result);
                if (result != null) {
                    JSONObject object = new JSONObject(result);
                    String resultCode = object.getString("response");
                    AppUtils.CHECKIN_NEXTURL_TOKEN=object.getString("next_token");
                    int code = Integer.parseInt(resultCode);
                    switch (code) {
                        case 101:
                            try {
                                try {
                                    ArrayList<CheckinLocationListVO> tempcontactlist = new ArrayList<>();
                                    tempcontactlist.clear();
                                    String resultArray = object.getString("result");
                                    JSONArray jsonArray = new JSONArray(resultArray);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject arrayObject = jsonArray.getJSONObject(i);
                                        CheckinLocationListVO vo = new CheckinLocationListVO();
                                        vo.setPlaceid(arrayObject.getString("place_id"));
                                        vo.setMiles(arrayObject.getString("distance"));
                                        vo.setProfileurl(arrayObject.getString("image_url"));
                                        vo.setLatitude(arrayObject.getDouble("lat"));
                                        vo.setLongitude(arrayObject.getDouble("long"));
                                        vo.setVicinity(arrayObject.getString("vicinity"));
                                        vo.setItemtitle(arrayObject.getString("place_name"));
                                        vo.setDescription(arrayObject.getString("description"));
                                        tempcontactlist.add(vo);
                                    }//end of for

                                    reloadListData(tempcontactlist);
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
                else
                {
                    reloadListData(new ArrayList<CheckinLocationListVO>());
                }
                appsingleton.dismissDialog();
                try{
//                    appsingleton.locationManager.removeUpdates(appsingleton.networkLocationListener);
//                    appsingleton. locationManager.removeUpdates(appsingleton.gpsLocationListener);
                }catch(Exception e){
                    e.printStackTrace();
                    appsingleton.ToastMessage("" + e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                reloadListData(new ArrayList<CheckinLocationListVO>());
                appsingleton.dismissDialog();
                appsingleton.ToastMessage("" + e.getMessage());
                try{
//                    appsingleton.locationManager.removeUpdates(appsingleton.networkLocationListener);
//                    appsingleton. locationManager.removeUpdates(appsingleton.gpsLocationListener);
                }catch(Exception e1){
                    e1.printStackTrace();
                    appsingleton.ToastMessage("" + e.getMessage());
                }
            }
        }
    }//end of Checkin Pullto refresh Asynctask
    /**
     * Connections Checkinnextpage  web service
     *
     * @author user
     */
    public class Checkinnextpage extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getCheckinnextpageUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        double lat,long_;
        public Checkinnextpage( double lat,double long_) {
            this.lat=lat;
            this.long_=long_;
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(CheckinListActivity.this);
//                if(!mSwipeRefreshLayout.isRefreshing()) {
//                    appsingleton.showDialog();
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public String doInBackground(String... params) {
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
                        .key("lat").value(lat)
                        .key("long").value(long_)
                        .key("next_page").value(AppUtils.CHECKIN_NEXTURL_TOKEN)
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
                publishProgress("Connected...");

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    publishProgress("Loading...");
                    String result = EntityUtils.toString(response.getEntity());
                    return result;
                }
                appsingleton.ToastMessage("" + statusCode);
            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        public void onProgressUpdate(String... values) {
        }

        @Override
        public void onPostExecute(String result) {
            try {
                appsingleton.ToastMessage(result);
                if (result != null) {
                    JSONObject object = new JSONObject(result);
                    String resultCode = object.getString("response");
                    AppUtils.CHECKIN_NEXTURL_TOKEN=object.getString("next_token");
                    int code = Integer.parseInt(resultCode);
                    switch (code) {
                        case 101:
                            try {
                                try {
                                    ArrayList<CheckinLocationListVO> tempcontactlist = new ArrayList<>();
                                    tempcontactlist.clear();
                                    String resultArray = object.getString("result");
                                    JSONArray jsonArray = new JSONArray(resultArray);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject arrayObject = jsonArray.getJSONObject(i);
                                        CheckinLocationListVO vo = new CheckinLocationListVO();
                                        vo.setPlaceid(arrayObject.getString("place_id"));
                                        vo.setMiles(arrayObject.getString("distance"));
                                        vo.setProfileurl(arrayObject.getString("image_url"));
                                        vo.setLatitude(arrayObject.getDouble("lat"));
                                        vo.setLongitude(arrayObject.getDouble("long"));
                                        vo.setVicinity(arrayObject.getString("vicinity"));
                                        vo.setItemtitle(arrayObject.getString("place_name"));
                                        vo.setDescription(arrayObject.getString("description"));
                                        tempcontactlist.add(vo);
                                    }//end of for
                                    chechinlist.addAll(tempcontactlist);
                                    tempcontactlist.clear();
                                    tempcontactlist.addAll(chechinlist);
                                    reloadListData(tempcontactlist);
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
                rl_bottom.setVisibility(View.GONE);
                appsingleton.dismissDialog();
                try{
//                    appsingleton.locationManager.removeUpdates(appsingleton.networkLocationListener);
//                    appsingleton. locationManager.removeUpdates(appsingleton.gpsLocationListener);
                }catch(Exception e){
                    e.printStackTrace();
                    appsingleton.ToastMessage("" + e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                rl_bottom.setVisibility(View.GONE);
                appsingleton.dismissDialog();
                appsingleton.ToastMessage("" + e.getMessage());
                try{
//                    appsingleton.locationManager.removeUpdates(appsingleton.networkLocationListener);
//                    appsingleton. locationManager.removeUpdates(appsingleton.gpsLocationListener);
                }catch(Exception e1){
                    e1.printStackTrace();
                    appsingleton.ToastMessage("" + e1.getMessage());
                }
            }
        }
    }//end of Checkin Pullto refresh Asynctask

    public void callLocationListner()
    {
//        try{
//            appsingleton.locationManager.removeUpdates(appsingleton.networkLocationListener);
//            appsingleton. locationManager.removeUpdates(appsingleton.gpsLocationListener);
//        }catch(Exception e){
//            e.printStackTrace();
//            appsingleton.ToastMessage("" + e.getMessage());
//        }
        try {
            appsingleton.locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1200000, 0,
                    appsingleton.networkLocationListener);

            appsingleton. locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5000, 0, appsingleton.gpsLocationListener);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}//end of Activity


