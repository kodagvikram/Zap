package com.zaparound;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.zaparound.Adapters.InviteFriendHeaderAdapter;
import com.zaparound.Adapters.InviteFriendsAdapter;
import com.zaparound.Adapters.PersonAdapter;
import com.zaparound.AppUtils.AppUtils;
import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.ModelVo.InviteFriendsVO;
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
import java.util.Collections;
import java.util.Comparator;

public class InviteFriendsActivity extends AppCompatActivity implements OnHeaderClickListener {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    Toolbar toolbar;
    public TextView tv_toolbartitle;
    public RecyclerView recyclerView;
    public StickyHeadersItemDecoration top;
    public PersonDataProvider personDataProvider;
    public PersonAdapter personAdapter;
    public ArrayList<InviteFriendsVO> invitefriendslist = new ArrayList<>();
    public SwipeRefreshLayout mSwipeRefreshLayout;
    RelativeLayout rl_selecteditem, rl_deleteitem, rl_camerapermission;
    public TextView tv_selectecount, tv_selectedtext, tv_invitenow;
    public Button enablecamera;
    InviteFriendsAdapter mAdapter;
    ArrayList<String> numberlist = new ArrayList<>();
    public TextView tv_nolocation;
    public Button bt_refresh;
    RelativeLayout rl_nolocationfound;
    public ImageView iv_selectall;

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            appsingleton = Appsingleton.getinstance(this);
            appsingleton.finishAllSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (appsingleton.needPermissionCheck()) {
            //check marshmallo permisions
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                rl_camerapermission.setVisibility(View.VISIBLE);
            }//end of if permission
            else {
                init();
            }//end of else permission
        } else
            init();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        try {
//            appsingleton=Appsingleton.getinstance(this);
//            appsingleton.finishAllSession();
//        }catch (Exception e){e.printStackTrace();}
//        if(appsingleton.needPermissionCheck()) {
//            //check marshmallo permisions
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//                rl_camerapermission.setVisibility(View.VISIBLE);
//            }//end of if permission
//            else {
//                init();
//            }//end of else permission
//        }
//        else
//            init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            appsingleton = Appsingleton.getinstance(this);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_invite_friends);
            //appsingleton instance
            //get height width
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            appsingleton.deviceheight = metrics.heightPixels;
            appsingleton.devicewidth = metrics.widthPixels;
            appsingleton.activityArrayList.add(this);
            toolbar = (Toolbar) findViewById(R.id.tool_bar1);
            setSupportActionBar(toolbar);
            // add back arrow to toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            tv_toolbartitle = (TextView) toolbar.findViewById(R.id.tv_title);
            iv_selectall = (ImageView) toolbar.findViewById(R.id.iv_multipalselect);
            tv_toolbartitle.setText(getResources().getString(R.string.st_invitefriendstollbar_title));
            tv_toolbartitle.setTypeface(appsingleton.regulartype);
            appsingleton.setMargins(tv_toolbartitle, 0.20, 0, 0, 0);

            rl_camerapermission = (RelativeLayout) findViewById(R.id.rl_contactspermission);
            enablecamera = (Button) findViewById(R.id.bt_enablecontacts);

            if (appsingleton.needPermissionCheck()) {
                //check marshmallo permisions
                if (!appsingleton.phoneContactsPermission(this)) {
                    rl_camerapermission.setVisibility(View.VISIBLE);
                }//end of if permission
                else {
                    init();
                }//end of else permission
            } else
                init();
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            iv_selectall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (mAdapter != null) {
                            if (mAdapter.count != invitefriendslist.size()) {
                                for (InviteFriendsVO vo : invitefriendslist) {
                                    vo.setIsselected(true);
                                }//end of for
                                mAdapter.notifyDataSetChanged();
                                mAdapter.count = invitefriendslist.size();
                                updateCount(mAdapter.count);
                                isShowInviteLayout(true);
                            }//end of if
                            else {
                                for (InviteFriendsVO vo : invitefriendslist) {
                                    vo.setIsselected(false);
                                }//end of for
                                mAdapter.notifyDataSetChanged();
                                mAdapter.count = 0;
                                updateCount(mAdapter.count);
                                isShowInviteLayout(false);
                            }//end of else
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }//end of oncreate


    public void enableCamera(View view) {
        try {
            appsingleton.startInstalledAppDetailsActivity(this);
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /*
    *Init views
    */
    public void init() {
        try {
            rl_camerapermission.setVisibility(View.GONE);
            // camera surface view created
            //initialize views
            rl_selecteditem = (RelativeLayout) findViewById(R.id.rl_Selecteditem);
            rl_deleteitem = (RelativeLayout) findViewById(R.id.rl_deleteitem);
            tv_selectecount = (TextView) findViewById(R.id.tv_itemcount);
            tv_selectedtext = (TextView) findViewById(R.id.tv_selected);
            tv_invitenow = (TextView) findViewById(R.id.tv_invitenow);
            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
            tv_nolocation = (TextView) findViewById(R.id.tv_logintitle);
            bt_refresh = (Button) findViewById(R.id.bt_enablegps);
            rl_nolocationfound = (RelativeLayout) findViewById(R.id.rl_nolocationfoundlayout);
            //setfonts
            tv_selectecount.setTypeface(appsingleton.lighttype);
            tv_selectedtext.setTypeface(appsingleton.lighttype);
            tv_invitenow.setTypeface(appsingleton.regulartype);
            tv_nolocation.setTypeface(appsingleton.regulartype);
            bt_refresh.setTypeface(appsingleton.regulartype);

            //initialize recyclerView
            recyclerView = (RecyclerView) findViewById(R.id.stickylist);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setSmoothScrollbarEnabled(true);
            recyclerView.setScrollbarFadingEnabled(true);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            loadData();
            //click listeners
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Refresh items
                    refreshItems();
                }
            });
            bt_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        loadData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }

                }
            });
            rl_deleteitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        JSONArray jsArray = new JSONArray();
                        for (int i = 0; i < invitefriendslist.size(); i++) {
                            JSONObject obj = new JSONObject();
                            if (invitefriendslist.get(i).getisselected()) {
                                obj.put("number", "" + invitefriendslist.get(i).getContact());
                                jsArray.put(obj);
                            }
                        }//end of for
                        String result = jsArray.toString();

                        if (AppUtils.isNetworkAvailable(InviteFriendsActivity.this)) {
                            new InvitefriendsBySMS(result).execute();
                        }//end of network if
                        else {
                            //reloadListData(new ArrayList<InviteFriendsVO>());
                            appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error), "", rl_nolocationfound);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    void refreshItems() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onItemsLoadComplete();
            }
        }, 1000);
        try {
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /*
     * Refresh list without changing current position
     */
    public void reloadListData(ArrayList<InviteFriendsVO> list) {
        try {
            invitefriendslist = list;
            isShowInviteLayout(false);
            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                rl_nolocationfound.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                rl_nolocationfound.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                Collections.sort(invitefriendslist, new Comparator<InviteFriendsVO>() {
                    public int compare(InviteFriendsVO s1, InviteFriendsVO s2) {
                        return s1.getUsername().compareToIgnoreCase(s2.getUsername());
                    }
                });
                if (mAdapter != null) {
                    recyclerView.setVisibility(View.VISIBLE);
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
    public void bindList(ArrayList<InviteFriendsVO> list) {
        try {
            invitefriendslist = list;
            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                rl_nolocationfound.setVisibility(View.VISIBLE);
                //emptySwipeRefreshLayout.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                rl_nolocationfound.setVisibility(View.GONE);
                // emptySwipeRefreshLayout.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);

                mAdapter = new InviteFriendsAdapter(this, invitefriendslist);
                personDataProvider = new PersonDataProvider();
                personAdapter = new PersonAdapter(this, personDataProvider);
                top = new StickyHeadersBuilder()
                        .setAdapter(personAdapter)
                        .setRecyclerView(recyclerView)
                        .setStickyHeadersAdapter(new InviteFriendHeaderAdapter(invitefriendslist))
                        .setOnHeaderClickListener(this)
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
   * function to hide show toolbar layout
   */
    public void isShowInviteLayout(boolean isshow) {
        try {
            if (isshow) {
                rl_selecteditem.setVisibility(View.VISIBLE);
            } else {
                rl_selecteditem.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /*
   *function to update count
   */
    public void updateCount(int count) {
        try {
            if (count > 0) {
                if (rl_selecteditem.getVisibility() == View.GONE)
                    isShowInviteLayout(true);
                tv_selectecount.setText("" + count);
            } else {
                isShowInviteLayout(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    @Override
    public void onHeaderClick(View header, long headerId) {
        TextView text = (TextView) header.findViewById(R.id.title);
        // Toast.makeText(getApplicationContext(), "Click on " + text.getText(), Toast.LENGTH_SHORT).show();
    }

    // load initial data
    public void loadData() {
        try {
            numberlist.clear();
            invitefriendslist.clear();
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String photoUri = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                Long contactID = phones.getLong(phones.getColumnIndex(ContactsContract.Contacts._ID));
                System.out.println("\n" + name + "\n" + phoneNumber + "\n" + photoUri + "\n" + contactID + "\n\n");
                InviteFriendsVO vo = new InviteFriendsVO(name, phoneNumber, "male", photoUri, false);
                invitefriendslist.add(vo);
                numberlist.add(phoneNumber);
            }//end of While
            phones.close();
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
        try {
            JSONArray jsArray = new JSONArray();
            for (int i = 0; i < numberlist.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("number", "" + numberlist.get(i));
                jsArray.put(obj);
            }//end of for
            String result = jsArray.toString();

            if (AppUtils.isNetworkAvailable(this)) {
                new Invitefriends(jsArray.toString()).execute();
            }//end of network if
            else {
                reloadListData(new ArrayList<InviteFriendsVO>());
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error), "", rl_nolocationfound);
            }


        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
        }
    }//end of on back pressed

    /*
       * Invitefriends read web service
       *
       * @author user
       */
    public class Invitefriends extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getInvitefriendsUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        String message;

        public Invitefriends(String message) {
            this.message = message;
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(InviteFriendsActivity.this);
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
                        .key("mobile_no").value(message)
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

                                try {
                                    ArrayList<InviteFriendsVO> templiveusers = new ArrayList<>();
                                    templiveusers.clear();
                                    String resultArray = object.getString("result");
                                    JSONArray jsonArray = new JSONArray(resultArray);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject arrayObject = jsonArray.getJSONObject(i);
                                        String number = arrayObject.getString("mobile_no");
                                        if (arrayObject.getInt("status") == 0) {
                                            for (int j = 0; j < invitefriendslist.size(); j++) {
                                                InviteFriendsVO vo = invitefriendslist.get(j);
                                                if (vo.getContact().equals(number)) {
                                                    templiveusers.add(vo);
                                                }
                                            }//end of inner for
                                        }//end of if
                                    }//end of for

                                    reloadListData(templiveusers);
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

    /* Invitefriends read web service
    *
            * @author user
    */
    public class InvitefriendsBySMS extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getInvitefriendsUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        String message;

        public InvitefriendsBySMS(String message) {
            this.message = message;
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(InviteFriendsActivity.this);
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
                        .key("mobile_no").value(message)
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
                                if (mAdapter != null) {
                                    for (InviteFriendsVO vo : invitefriendslist) {
                                        vo.setIsselected(false);
                                    }//end of for
                                    mAdapter.notifyDataSetChanged();
                                    mAdapter.count = 0;
                                    updateCount(mAdapter.count);
                                    isShowInviteLayout(false);

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
}//end of Activity


