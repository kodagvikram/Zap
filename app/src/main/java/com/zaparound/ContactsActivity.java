package com.zaparound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.zaparound.Adapters.BigramHeaderAdapter;
import com.zaparound.Adapters.ContactStickyAdapter;
import com.zaparound.Adapters.ContactsLiveAdapter;
import com.zaparound.Adapters.PersonAdapter;
import com.zaparound.AppUtils.AppUtils;
import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.IntentServices.FrindListIntentService;
import com.zaparound.ModelVo.ChatUserListVO;
import com.zaparound.ModelVo.ContactStickyVO;
import com.zaparound.ModelVo.ContactsLivelistVO;
import com.zaparound.Singleton.Appsingleton;
import com.zaparound.data.PersonDataProvider;
import com.zaparound.stickyheaders.OnHeaderClickListener;
import com.zaparound.stickyheaders.StickyHeadersBuilder;
import com.zaparound.stickyheaders.StickyHeadersItemDecoration;
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

public class ContactsActivity extends AppCompatActivity implements OnHeaderClickListener {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    Toolbar toolbar;
    RelativeLayout rl_setting,rl_nocontacts;
    public TextView tv_toolbartitle,tv_toplocation,tv_allcontacts,tv_liveusercount,
            tv_allusercount,closebracket1,closebracket2,tv_nocontacts1,tv_nocontacts2,tv_nocontacts3;

    public RecyclerView recyclerView,liveuserrecyclerView;
    public StickyHeadersItemDecoration top;
    public PersonDataProvider personDataProvider;
    public PersonAdapter personAdapter;
    public ArrayList<ChatUserListVO> contactlist = new ArrayList<>();
    public ArrayList<ContactsLivelistVO> Liveuserlist = new ArrayList<>();
    public SwipeRefreshLayout mSwipeRefreshLayout,emptySwipeRefreshLayout;
    public ContactStickyAdapter mAdapter;
    public ContactsLiveAdapter liveadapter;
    RelativeLayout rl_liveusertitleLayout,rl_liveuserLayout;
    public InmyLocationReceiver receiver;
    @Override
    protected void onResume() {
        super.onResume();
        try {
            appsingleton=Appsingleton.getinstance(this);
            appsingleton.finishAllSession();
        }catch (Exception e){e.printStackTrace();}
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            appsingleton = Appsingleton.getinstance(this);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_contacts);
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
            tv_toolbartitle = (TextView) toolbar.findViewById(R.id.tv_title);
            tv_toolbartitle.setText(getResources().getString(R.string.st_contacts));
            //initialize recyclerView
            recyclerView = (RecyclerView) findViewById(R.id.stickylist);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setSmoothScrollbarEnabled(true);
            recyclerView.setScrollbarFadingEnabled(true);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            liveuserrecyclerView = (RecyclerView) findViewById(R.id.liveuserlist);
            LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(this);
            mLayoutManager2.setSmoothScrollbarEnabled(true);
            liveuserrecyclerView.setScrollbarFadingEnabled(true);
            liveuserrecyclerView.setLayoutManager(mLayoutManager2);
            liveuserrecyclerView.setHasFixedSize(true);
            liveuserrecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            //initialize Views
            tv_toplocation = (TextView) findViewById(R.id.tv_toptitltext);
            tv_allcontacts = (TextView) findViewById(R.id.tv_contactstitle);
            tv_liveusercount = (TextView) findViewById(R.id.tv_liveusercount);
            tv_allusercount = (TextView) findViewById(R.id.tv_allusercount);
            closebracket1 = (TextView) findViewById(R.id.tv_closebracket);
            closebracket2 = (TextView) findViewById(R.id.tv_alluserclosebracket);
            tv_nocontacts1 = (TextView) findViewById(R.id.tv_logintitle);
            tv_nocontacts2 = (TextView) findViewById(R.id.tv_validation);
            tv_nocontacts3 = (TextView) findViewById(R.id.tv_validation);
            rl_nocontacts = (RelativeLayout) findViewById(R.id.rl_nolocationfoundlayout);
            rl_liveusertitleLayout = (RelativeLayout) findViewById(R.id.rl_mylocationlayout);
            rl_liveuserLayout = (RelativeLayout) findViewById(R.id.rl_HorizantalLayout);
            mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
            emptySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_emptyView);
               /*set custom fonts*/
            tv_toplocation.setTypeface(appsingleton.boldtype);
            tv_allcontacts.setTypeface(appsingleton.boldtype);
            tv_liveusercount.setTypeface(appsingleton.boldtype);
            tv_allusercount.setTypeface(appsingleton.boldtype);
            closebracket1.setTypeface(appsingleton.boldtype);
            closebracket2.setTypeface(appsingleton.boldtype);
            tv_nocontacts1.setTypeface(appsingleton.regulartype);
            tv_nocontacts2.setTypeface(appsingleton.regulartype);
            tv_nocontacts3.setTypeface(appsingleton.regulartype);

//            Collections.sort(contactlist, new Comparator<ContactStickyVO>() {
//                public int compare(ContactStickyVO s1, ContactStickyVO s2) {
//                    return s1.getUsername().compareToIgnoreCase(s2.getUsername());
//                }
//            });

            if(!appsingleton.getCheckinsession())
            {
               rl_liveuserLayout.setVisibility(View.GONE);
               rl_liveusertitleLayout.setVisibility(View.GONE);

            }
            reloadLIveuserListData(Liveuserlist);
            reloadListData(contactlist);

            try{
                //Check internet permission
                if (AppUtils.isNetworkAvailable(this)) {
                    //new ConnectionsPulltorefresh().execute();

                    if(appsingleton.getCheckinsession())
                    {
                        new LiveuserPulltorefresh().execute();
                    }
                }//end of network if
                else
                {
                    appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",tv_toplocation);
                }

                getDatabaseList();
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }



            rl_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        if(appsingleton.getCheckinsession())
                            appsingleton.getZaptabhostcontext().SettingClick();
                        else
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
            emptySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Refresh items
                    refreshItems();
                }
            });
            IntentFilter filter = new IntentFilter(InmyLocationReceiver.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            receiver = new InmyLocationReceiver();
            registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end pf oncreate
    void refreshItems() {
        // Load items
        // ...
        // Load complete
        try{
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onItemsLoadComplete();
                }
            }, 1000);
            //Check internet permission
            if (AppUtils.isNetworkAvailable(this)) {
                new ConnectionsPulltorefresh().execute();

                if(appsingleton.getCheckinsession())
                {
                    new LiveuserPulltorefresh().execute();
                }
            }//end of network if
            else
            {
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",tv_toplocation);
            }
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
        // Stop refresh animation
        try{
            if(mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
        try{
            if(emptySwipeRefreshLayout.isRefreshing())
                emptySwipeRefreshLayout.setRefreshing(false);
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }
    /*
        *Get data from database
        */
    public void getDatabaseList()
    {
        try{
            ArrayList<ChatUserListVO> list=appsingleton.mDatabase.getAllConnections();
            reloadListData(list);
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }
    /*
      * Refresh list without changing current position
      */
    public void reloadListData(ArrayList<ChatUserListVO> list) {
        try {
            contactlist = list;
            tv_allusercount.setText(""+contactlist.size());
            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                rl_nocontacts.setVisibility(View.VISIBLE);
                emptySwipeRefreshLayout.setVisibility(View.VISIBLE);
                //mSwipeRefreshLayout.setVisibility(View.GONE);

            } else {
                emptySwipeRefreshLayout.setVisibility(View.GONE);
                //mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                // intrestgridView.setVisibility(ListView.VISIBLE);
                if (mAdapter != null) {
                    recyclerView.setVisibility(View.VISIBLE);
                    rl_nocontacts.setVisibility(View.GONE);
                    mAdapter.reload(list);
                } else {
                    bindList(list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /**
     * Bind all orders to list view
     */
    public void bindList(ArrayList<ChatUserListVO> list) {
        try {
               contactlist = list;
            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                rl_nocontacts.setVisibility(View.VISIBLE);
                emptySwipeRefreshLayout.setVisibility(View.VISIBLE);
               // mSwipeRefreshLayout.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                rl_nocontacts.setVisibility(View.GONE);
                emptySwipeRefreshLayout.setVisibility(View.GONE);
                //mSwipeRefreshLayout.setVisibility(View.VISIBLE);

                mAdapter = new ContactStickyAdapter(this, contactlist);
                personDataProvider = new PersonDataProvider();
                personAdapter = new PersonAdapter(this, personDataProvider);

                top = new StickyHeadersBuilder()
                        .setAdapter(personAdapter)
                        .setRecyclerView(recyclerView)
                        .setStickyHeadersAdapter(new BigramHeaderAdapter(contactlist))
                        .setOnHeaderClickListener(ContactsActivity.this)
                        .build();
                recyclerView.setAdapter(mAdapter);
                YoYo.with(Techniques.FadeInUp).duration(appsingleton.animationduration).interpolate(new AccelerateDecelerateInterpolator()).playOn(recyclerView);
                recyclerView.addItemDecoration(top);
            }//end of else
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }


    /*
     * Refresh list without changing current position
     */
    public void reloadLIveuserListData(ArrayList<ContactsLivelistVO> list) {
        try {
            Liveuserlist = list;
            tv_liveusercount.setText(""+Liveuserlist.size());
            if (list.isEmpty()) {
                    rl_liveuserLayout.setVisibility(View.GONE);
                    rl_liveusertitleLayout.setVisibility(View.GONE);
            } else {
                    rl_liveuserLayout.setVisibility(View.VISIBLE);
                    rl_liveusertitleLayout.setVisibility(View.VISIBLE);
                if (liveadapter != null) {
                    liveadapter.reload(list);
                } else {
                    bindLIveuserList(list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /**
     * Bind all orders to list view
     */
    public void bindLIveuserList(ArrayList<ContactsLivelistVO> list) {
        try {
            Liveuserlist = list;
            if (list.isEmpty()) {

            } else {
                liveadapter = new ContactsLiveAdapter(this, Liveuserlist);
                liveuserrecyclerView.setAdapter(liveadapter);
                YoYo.with(Techniques.FadeInUp).duration(appsingleton.animationduration).interpolate(new AccelerateDecelerateInterpolator()).playOn(liveuserrecyclerView);
            }//end of else
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }


    @Override
    public void onHeaderClick(View header, long headerId) {
        TextView text = (TextView) header.findViewById(R.id.title);
        //Toast.makeText(getApplicationContext(), "Click on " + text.getText(), Toast.LENGTH_SHORT).show();
    }


    /**
     * Connections Pullto Refresh web service
     *
     * @author user
     */
    public class ConnectionsPulltorefresh extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getConnectionsUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();

        public ConnectionsPulltorefresh() {
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(ContactsActivity.this);
                if(!mSwipeRefreshLayout.isRefreshing()&&!emptySwipeRefreshLayout.isRefreshing()) {
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
                        .key("user_id").value(appsingleton.getUserid())
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

                                    getDatabaseList();
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
                appsingleton.dismissDialog();
            } catch (Exception e) {

                e.printStackTrace();
                appsingleton.dismissDialog();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }
    }//end of Checkin Pullto refresh Asynctask

    /**
     * Connections Pullto Refresh web service
     *
     * @author user
     */
    public class LiveuserPulltorefresh extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getLiveusersUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();

        @Override
        public void onPreExecute() {
            try {
  //             appsingleton.initDialog(ContactsActivity.this);
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
                    int code = Integer.parseInt(resultCode);
                    switch (code) {
                        case 101:
                            try {
                                try {
                                    ArrayList<ContactsLivelistVO> templiveusers = new ArrayList<ContactsLivelistVO>();
                                    templiveusers.clear();
                                    String resultArray = object.getString("result");
                                    JSONArray jsonArray = new JSONArray(resultArray);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject arrayObject = jsonArray.getJSONObject(i);
                                        ContactsLivelistVO vo = new ContactsLivelistVO();
                                        vo.setUsername(arrayObject.getString("user_name"));
                                        vo.setGender(arrayObject.getString("gender"));
                                        vo.setAge(arrayObject.getString("age"));
                                        vo.setShow_age(arrayObject.getString("show_age"));
                                        vo.setUserid(arrayObject.getString("user_id"));
                                        vo.setProfileurl(arrayObject.getString("user_selfie"));
                                        vo.setUser_selfie_thumb(arrayObject.getString("user_selfie_thumb"));
                                        vo.setSimilar_interest_count(arrayObject.getString("similar_interest_count"));
                                        vo.setSimilar_interest_name(arrayObject.getString("similar_interest_name"));
                                        templiveusers.add(vo);
                                    }//end of for

                                    reloadLIveuserListData(templiveusers);
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
                appsingleton.dismissDialog();
            } catch (Exception e) {

                e.printStackTrace();
                appsingleton.dismissDialog();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }
    }//end of Checkin Pullto refresh Asynctask


    /*
    *function to block user
    */
    public void Blockuser(String touserid)
    {
        try{
            //Check internet permission
            if (AppUtils.isNetworkAvailable(this)) {
                new Blockuser(touserid).execute();
            }//end of network if
            else
            {
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",rl_liveuserLayout);
            }


        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }
      /*
       * Blockuser read web service
       *
       * @author user
       */
    public class Blockuser extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getBlockuserUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
          String touserid;
        public Blockuser(String touserid) {
            this.touserid=touserid;
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(ContactsActivity.this);
                appsingleton.showDialog();
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
                        .key("user_id_from").value(appsingleton.getUserid())
                        .key("user_id_to").value(touserid)
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
                    int code = Integer.parseInt(resultCode);
                    switch (code) {
                        case 101:
                            try {
                                ArrayList<ChatUserListVO> tempcontactlist = new ArrayList<>();
                                tempcontactlist.clear();
                                for (ChatUserListVO vo:contactlist) {
                                    if(!touserid.equals(vo.getUserid()))
                                    {
                                       tempcontactlist.add(vo);
                                    }
                                }//end of for
                                reloadListData(tempcontactlist);
                                try{
                                    //sendNotificationInmylocation("",);
                                    Intent msgIntent = new Intent(ContactsActivity.this, FrindListIntentService.class);
                                    msgIntent.putExtra(FrindListIntentService.USER_ID, "" + appsingleton.getUserid());
                                    msgIntent.putExtra(FrindListIntentService.LAST_ID, "" + "0");
                                    startService(msgIntent);
                                }catch(Exception e){
                                    e.printStackTrace();
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
                        case 111:
                            appsingleton.UserToastMessage(getResources().getString(R.string.st_failstatus));
                            break;
                        default:

                            break;
                    }
                }//end of if
                appsingleton.dismissDialog();
            } catch (Exception e) {

                e.printStackTrace();
                appsingleton.dismissDialog();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }
    }//end of Logout refresh Asynctask
    //----------------------------------------------
    public class InmyLocationReceiver extends BroadcastReceiver {

        public static final String PROCESS_RESPONSE = "com.zaparound.intent.action.INMYLOCATION";

        @Override
        public void onReceive(Context context, Intent intent) {
//             String responseString = intent.getStringExtra(ZapWhosIntentService.RESPONSE_STRING);
//             String reponseMessage = intent.getStringExtra(ZapWhosIntentService.RESPONSE_MESSAGE);
            try{
                ArrayList<ContactsLivelistVO> Liveuserlist1 = new ArrayList<>();
                Liveuserlist1.clear();
                Liveuserlist1.addAll(appsingleton.tempLiveuserlist);
               reloadLIveuserListData(Liveuserlist1);
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }

        }


    }


    //-----------------------------------------------
}
