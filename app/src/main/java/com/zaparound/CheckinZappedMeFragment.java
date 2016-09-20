package com.zaparound;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.util.Attributes;
import com.zaparound.Adapters.ZappedmeSwipmeAdapter;
import com.zaparound.AppUtils.AppUtils;
import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.ModelVo.ChatUserListVO;
import com.zaparound.ModelVo.CheckinZapmeVO;
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

public class CheckinZappedMeFragment extends Fragment {
    public Appsingleton appsingleton;
    private RecyclerView mRecyclerView;
    ArrayList<CheckinZapmeVO> zaparoundlist = new ArrayList<CheckinZapmeVO>();
    ArrayList<CheckinZapmeVO> allzaparoundlist = new ArrayList<CheckinZapmeVO>();
    public SwipeRefreshLayout mSwipeRefreshLayout,emptySwipeRefreshLayout;
    RelativeLayout rl_locationlayout;
    ZappedmeSwipmeAdapter mAdapter;
    public MyWebRequestReceiver receiver;
    public String FILTER="all";
    public static Fragment newInstance() {
        CheckinZappedMeFragment f = new CheckinZappedMeFragment();
        return f;
    }
    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            appsingleton=Appsingleton.getinstance(getActivity());
            appsingleton.finishAllSession();
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_checkin_zaparound, null);
        try {
            appsingleton = Appsingleton.getinstance(getActivity());
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
            emptySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout_emptyView);
            // Layout Managers:
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            rl_locationlayout = (RelativeLayout) rootView.findViewById(R.id.rl_nolocationfoundlayout);
            // Item Decorator:
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
            // mRecyclerView.setItemAnimator(new FadeInLeftAnimator());

            ((ImageView) rootView.findViewById(R.id.iv_LOGOIMAGE)).setImageResource(R.drawable.no_zap_me_foundzap_icon);
            ((TextView) rootView.findViewById(R.id.tv_logintitle)).setText(getResources().getString(R.string.st_noZaps));
            try {

                //Check internet permission
                if (AppUtils.isNetworkAvailable(getActivity())) {
                    new CheckinPulltorefresh(appsingleton.getCheckinPlaceid()).execute();
                }//end of network if
                else {
                    appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error), "", rl_locationlayout);
                }

            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }


            if (zaparoundlist.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                rl_locationlayout.setVisibility(View.VISIBLE);

            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                rl_locationlayout.setVisibility(View.GONE);
            }



        /* Scroll Listeners */
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Log.e("RecyclerView", "onScrollStateChanged");
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
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

            IntentFilter filter = new IntentFilter(MyWebRequestReceiver.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            receiver = new MyWebRequestReceiver();
            getActivity().registerReceiver(receiver, filter);
        } catch (Exception e) {
            appsingleton.ToastMessage("" + e.getMessage());
        }
        return rootView;
    }//end of oncreate view

    void refreshItems() {
        try {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onItemsLoadComplete();
                }
            }, 1000);
            //Check internet permission
            if (AppUtils.isNetworkAvailable(getActivity())) {
                new CheckinPulltorefresh(appsingleton.getCheckinPlaceid()).execute();
            }//end of network if
            else {
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error), "", rl_locationlayout);
            }

        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        if(mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
        if(emptySwipeRefreshLayout.isRefreshing())
            emptySwipeRefreshLayout.setRefreshing(false);
    }

    /*
      * Refresh list without changing current position
      */
    public void reloadListData(ArrayList<CheckinZapmeVO> list) {
        try {
            zaparoundlist = list;
            if (list.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                rl_locationlayout.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                emptySwipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                // intrestgridView.setVisibility(ListView.VISIBLE);
                if (mAdapter != null) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    rl_locationlayout.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    emptySwipeRefreshLayout.setVisibility(View.GONE);
                    mAdapter.reload(list);
                } else {
                    bindList(list);
                }
            }
            appsingleton.dismissDialog();
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /**
     * Bind all orders to list view
     */
    public void bindList(ArrayList<CheckinZapmeVO> list) {
        try {
            zaparoundlist = list;
            if (list.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                rl_locationlayout.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                emptySwipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                rl_locationlayout.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                emptySwipeRefreshLayout.setVisibility(View.GONE);
                SetGenderAdapter(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }


    /**
     * set adapter
     */
    public void SetGenderAdapter(ArrayList<CheckinZapmeVO> zaparoundlist) {
        try {
            if (zaparoundlist.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                rl_locationlayout.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                emptySwipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                rl_locationlayout.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                emptySwipeRefreshLayout.setVisibility(View.GONE);
                // Creating Adapter object
                mAdapter = new ZappedmeSwipmeAdapter(getActivity(), CheckinZappedMeFragment.this, zaparoundlist);

                // Setting Mode to Single to reveal bottom View for one item in List
                // Setting Mode to Mutliple to reveal bottom Views for multile items in List
                ((ZappedmeSwipmeAdapter) mAdapter).setMode(Attributes.Mode.Single);

                mRecyclerView.setAdapter(mAdapter);
                YoYo.with(Techniques.FadeInUp).duration(appsingleton.animationduration).interpolate(new AccelerateDecelerateInterpolator()).playOn(mRecyclerView);
            }

        } catch (Exception e) {
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /**
     * Reloadlist all methods to list view
     */
    public void FilterList(String gender) {
        try {
            FILTER=gender;
            ArrayList<CheckinZapmeVO> templist = new ArrayList<>();
            templist.clear();

            if (gender.equalsIgnoreCase("m") || gender.equalsIgnoreCase("f")) {
                for (int i = 0; i < allzaparoundlist.size(); i++) {
                    CheckinZapmeVO vo = allzaparoundlist.get(i);
                    String compairstr = vo.getGender();
                    if (gender.equalsIgnoreCase(compairstr)) {
                        templist.add(vo);
                    }
                }// end of for
                reloadListData(templist);
            } else {
                templist.addAll(allzaparoundlist);
                reloadListData(templist);
            }

        } catch (Exception e) {
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }


    /*
    *function to update unread count
    */
    public void updateUnreadcount() {
        try {
            int count = 0;
            CheckinTabscriptActivity tabscriptActivity = (CheckinTabscriptActivity) getActivity();
            for (int i = 0; i < allzaparoundlist.size(); i++) {
                if (allzaparoundlist.get(i).getIsread() == 1) {
                    count++;
                }
            }//end of for
            tabscriptActivity.updateCount(count);
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /**
     * Checkin Pullto Refresh web service
     *
     * @author user
     */
    public class CheckinPulltorefresh extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getZapsUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        String placeid = "";

        public CheckinPulltorefresh(String placeid) {
            try {
                this.placeid = placeid;
            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }

        @Override
        public void onPreExecute() {
            try {
                //               appsingleton.initDialog(getActivity());
//                appsingleton.showDialog();
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
                        .key("place_id").value(placeid)
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
                                    ArrayList<CheckinZapmeVO> tempzaparoundlist = new ArrayList<>();
                                    tempzaparoundlist.clear();

                                    String resultArray = object.getString("result");
                                    JSONArray jsonArray = new JSONArray(resultArray);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject arrayObject = jsonArray.getJSONObject(i);
                                        CheckinZapmeVO vo = new CheckinZapmeVO();
                                        vo.setUsername(arrayObject.getString("user_name"));
                                        vo.setGender(arrayObject.getString("gender"));
                                        vo.setAge(arrayObject.getString("age"));
                                        vo.setShow_age(arrayObject.getString("show_age"));
                                        vo.setUserid(arrayObject.getString("user_id"));
                                        vo.setPlace_name(arrayObject.getString("place_name"));
                                        vo.setProfileurl(arrayObject.getString("user_selfie"));
                                        vo.setIsread(arrayObject.getInt("is_read"));
                                        vo.setRequestid(arrayObject.getString("request_id"));
                                        vo.setUser_selfie_thumb(arrayObject.getString("user_selfie_thumb"));
                                        vo.setSimilar_interest_count(arrayObject.getString("similar_interest_count"));
                                        vo.setSimilar_interest_name(arrayObject.getString("similar_interest_name"));
                                        tempzaparoundlist.add(vo);
                                    }//end of for

                                    zaparoundlist.clear();
                                    allzaparoundlist.clear();
                                    allzaparoundlist.addAll(tempzaparoundlist);
                                    zaparoundlist.addAll(tempzaparoundlist);
                                    updateUnreadcount();
                                    reloadListData(tempzaparoundlist);
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
                // appsingleton.dismissDialog();
            } catch (Exception e) {

                e.printStackTrace();
                //appsingleton.dismissDialog();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }
    }//end of Checkin Pullto refresh Asynctask

    public void callCheckinrequest(String placeid, String user_id_from, String user_id_to, String status, String checkinid, int pos) {
        try {
            if (AppUtils.isNetworkAvailable(getActivity())) {
                new callCheckinrequest(placeid, user_id_from, user_id_to, status, checkinid, pos).execute();
            }//end of network if
            else {
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error), "", rl_locationlayout);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Checkin Pullto Refresh web service
     *
     * @author user
     */
    public class callCheckinrequest extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getCheckinZapRequestUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        String placeid = "", user_id_from = "", user_id_to = "", status = "", checkin_id = "";
        int pos;

        public callCheckinrequest(String placeid, String user_id_from, String user_id_to, String status, String checkin_id, int pos) {
            try {
                this.placeid = placeid;
                this.user_id_from = user_id_from;
                this.user_id_to = user_id_to;
                this.status = status;
                this.checkin_id = checkin_id;
                this.pos = pos;
            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(getActivity());
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
                        .key("user_id_from").value(user_id_from)
                        .key("user_id_to").value(user_id_to)
                        .key("place_id").value(placeid)
                        .key("checkin_id").value(checkin_id)
                        .key("status").value(status)
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
                                    for (int i = 0; i < allzaparoundlist.size(); i++) {
                                        CheckinZapmeVO vo = allzaparoundlist.get(i);
                                        if (vo.getUserid().equalsIgnoreCase(user_id_to)) {
                                            try {
                                                allzaparoundlist.remove(i);
                                                i = 0;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                appsingleton.ToastMessage("" + e.getMessage());
                                            }
                                        }//end of if
                                    }//end od for
                                    try {
                                        updateUnreadcount();
                                        mAdapter.removeItemfromlist(pos, user_id_to);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        appsingleton.ToastMessage("" + e.getMessage());
                                    }

                                    try{
                                        //Check internet permission
                                        if (AppUtils.isNetworkAvailable(getActivity())) {
                                            new ConnectionsPulltorefresh().execute();
                                        }//end of network if
                                        else
                                        {
                                            appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",rl_locationlayout);
                                        }
                                    }catch(Exception e){
                                        e.printStackTrace();
                                        appsingleton.ToastMessage("" + e.getMessage());
                                    }

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
               // appsingleton.dismissDialog();
            } catch (Exception e) {

                e.printStackTrace();
                appsingleton.dismissDialog();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }
    }//end of Checkin Pullto refresh Asynctask


    public void callCheckinZapread(String user_id_from, String user_id_to) {
        try {
            if (AppUtils.isNetworkAvailable(getActivity())) {
                new callCheckinZapread(user_id_from, user_id_to).execute();
            }//end of network if
            else {
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error), "", rl_locationlayout);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
    * Checkin read web service
    *
    * @author user
    */
    public class callCheckinZapread extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getZapunreadUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        String user_id_from = "", user_id_to = "";


        public callCheckinZapread(String user_id_from, String user_id_to) {
            try {

                this.user_id_from = user_id_from;
                this.user_id_to = user_id_to;

            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(getActivity());
                // appsingleton.showDialog();
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
                        .key("user_id").value(user_id_from)
                        .key("request_id").value(user_id_to)
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
                                    for (int i = 0; i < allzaparoundlist.size(); i++) {

                                        CheckinZapmeVO vo = allzaparoundlist.get(i);
                                        if (vo.getUserid().equalsIgnoreCase(user_id_to)) {
                                            try {
                                                allzaparoundlist.get(i).setIsread(0);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                appsingleton.ToastMessage("" + e.getMessage());
                                            }
                                        }//end of if

                                    }//end od for

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
    }//end of Checkin Pullto refresh Asynctask

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
 //               appsingleton.initDialog(getActivity());
//                if(!mSwipeRefreshLayout.isRefreshing()&&!emptySwipeRefreshLayout.isRefreshing()) {
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

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    appsingleton.ToastMessage("" + e.getMessage());
                                }

                                try{
                                    appsingleton.getZaptabhostcontext().changeTab(2);
                                }catch(Exception e){
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

    //----------------------------------------------
    public class MyWebRequestReceiver extends BroadcastReceiver {

        public static final String PROCESS_RESPONSE = "com.zaparound.intent.action.ZAPEDME";

        @Override
        public void onReceive(Context context, Intent intent) {
//             String responseString = intent.getStringExtra(ZapWhosIntentService.RESPONSE_STRING);
//             String reponseMessage = intent.getStringExtra(ZapWhosIntentService.RESPONSE_MESSAGE);
            try{
                allzaparoundlist.clear();
                allzaparoundlist.addAll(appsingleton.tempzapmelist);
                FilterList(FILTER);
                updateUnreadcount();
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }

        }


    }


    //-----------------------------------------------
}//end of fragement
