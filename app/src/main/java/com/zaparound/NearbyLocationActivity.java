package com.zaparound;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zaparound.Adapters.NearbyLocationAdapter;
import com.zaparound.AppUtils.AppUtils;
import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.ModelVo.CheckinLocationListVO;
import com.zaparound.ModelVo.CheckinZaparoundVO;
import com.zaparound.ModelVo.NearbyMapVO;
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
import java.util.List;

public class NearbyLocationActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    public TextView tv_nolocation, tv_toplocation, tv_toolbartitle, tv_raduscount, tv_taptoradius, tv_raduisfortext;
    public Button bt_refresh;
    RelativeLayout rl_setting, rl_nolocationfound, rl_maplayout, rl_updateradius, rl_recycleviewlayout;
    ImageView iv_logoimage,iv_refreshtoolbar;
    Toolbar toolbar;
    public int strock = 5;
    private static  LatLng CURRENTLOC = new LatLng(0, 0);
    private static double DEFAULT_RADIUS = 10;
    public static final double RADIUS_OF_EARTH_METERS = 6371009;

    public SeekBar mRadiusseekbar = null;
    private GoogleMap mMap;

    private List<DraggableCircle> mCircles = new ArrayList<>(1);

    private int mStrokeColor;
    private int mFillColor;

    public ArrayList<Marker> markerArrayList = new ArrayList<>();
    private ArrayList<NearbyMapVO> nearbylist = new ArrayList<>();
    public NearbyLocationAdapter adapter = null;
    public RecyclerView recyclerView;
    int radiousvalue=0,defaultradious=25;
  //  public SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onResume() {
        super.onResume();
        ((ScrollView) findViewById(R.id.scrollview)).smoothScrollTo(0, 0);
        try {
            appsingleton=Appsingleton.getinstance(this);
            appsingleton.finishAllSession();
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            appsingleton = Appsingleton.getinstance(this);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_nearby_location);
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
            iv_refreshtoolbar = (ImageView) toolbar.findViewById(R.id.iv_refresh);
            tv_toolbartitle.setText(getResources().getString(R.string.st_maptoolbar));
            //initialize views
            iv_logoimage = (ImageView) findViewById(R.id.iv_LOGOIMAGE);
            tv_nolocation = (TextView) findViewById(R.id.tv_logintitle);
            tv_toplocation = (TextView) findViewById(R.id.tv_gpsmess);
            tv_raduscount = (TextView) findViewById(R.id.tv_milesnumber);
            tv_taptoradius = (TextView) findViewById(R.id.tv_taptochange);
            tv_raduisfortext = (TextView) findViewById(R.id.tv_radisfortop);
            bt_refresh = (Button) findViewById(R.id.bt_enablegps);
            rl_nolocationfound = (RelativeLayout) findViewById(R.id.rl_nolocationfoundlayout);
            rl_maplayout = (RelativeLayout) findViewById(R.id.rl_MapLayout);
            rl_updateradius = (RelativeLayout) findViewById(R.id.rl_updateradus);
            rl_recycleviewlayout = (RelativeLayout) findViewById(R.id.rl_Nearbylocationlayout);
            //mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
            //initialize recyclerView
            recyclerView = (RecyclerView) findViewById(R.id.rc_nearby_recyclerview);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setSmoothScrollbarEnabled(true);
            recyclerView.setScrollbarFadingEnabled(true);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);

            /*set custom fonts*/
            tv_nolocation.setTypeface(appsingleton.regulartype);
            tv_toplocation.setTypeface(appsingleton.boldtype);
            tv_raduscount.setTypeface(appsingleton.regulartype);
            tv_taptoradius.setTypeface(appsingleton.regulartype);
            tv_raduisfortext.setTypeface(appsingleton.regulartype);
            bt_refresh.setTypeface(appsingleton.regulartype);
            tv_toolbartitle.setTypeface(appsingleton.regulartype);

            try{
                radiousvalue=appsingleton.sharedPreferences.getInt("RADIOUS",defaultradious);
            }catch(Exception e){
                e.printStackTrace();
                radiousvalue=defaultradious;
                appsingleton.ToastMessage("" + e.getMessage());
            }

            tv_raduscount.setText(radiousvalue+"mi");
            CURRENTLOC = new LatLng(appsingleton.currentLatitude,appsingleton.currentLongitude);
            //set Height width
            SetHeightWidth(rl_maplayout, 0, 0.30);

            //DEFAULT_RADIUS = (mRadius.getProgress()*1609.34);
            DEFAULT_RADIUS = (radiousvalue * 1609.34);
            final SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            rl_updateradius.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String title = getResources().getString(R.string.st_toplocations);
                        if (mRadiusseekbar == null) {
                            showMilesDialog(title, ""+radiousvalue);
                        } else {
                            int miles = mRadiusseekbar.getProgress();
                            if (miles < 1)
                                miles = 1;
                            showMilesDialog(title, "" + miles);
                        }

                    } catch (Exception e) {
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

           // reloadListData(setData());

            ((ScrollView) findViewById(R.id.scrollview)).smoothScrollTo(0, 0);
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
//            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    // Refresh items
//                    refreshItems();
//                }
//            });
            try{
                String lat=""+appsingleton.currentLatitude;
                String long_=""+appsingleton.currentLongitude;
                String radious=""+radiousvalue;

                if (AppUtils.isNetworkAvailable(this)) {
                    new TopLocations(lat,long_,radious).execute();
                }//end of network if
                else
                {
                    rl_nolocationfound.setVisibility(View.VISIBLE);
                    rl_recycleviewlayout.setVisibility(ListView.GONE);
                    appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",rl_nolocationfound);
                }
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
            iv_refreshtoolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        String lat=""+appsingleton.currentLatitude;
                        String long_=""+appsingleton.currentLongitude;
                        String radious="";
                        if(mRadiusseekbar!=null)
                         radious=""+mRadiusseekbar.getProgress();
                        else
                            radious=""+radiousvalue;


                        if (AppUtils.isNetworkAvailable(NearbyLocationActivity.this)) {
                            new TopLocations(lat,long_,radious).execute();
                        }//end of network if
                        else
                        {
                            appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",rl_nolocationfound);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            bt_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        String lat=""+appsingleton.currentLatitude;
                        String long_=""+appsingleton.currentLongitude;
                        String radious="";
                        if(mRadiusseekbar!=null)
                            radious=""+mRadiusseekbar.getProgress();
                        else
                            radious=""+radiousvalue;


                        if (AppUtils.isNetworkAvailable(NearbyLocationActivity.this)) {
                            new TopLocations(lat,long_,radious).execute();
                        }//end of network if
                        else
                        {
                            appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",rl_nolocationfound);
                        }
                    }catch(Exception e){
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
    void refreshItems() {
        // Load items
        // ...
        // Load complete
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
       // mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Refresh list without changing current position
     */
    public void reloadListData(ArrayList<NearbyMapVO> list) {
        try {
            nearbylist = list;
            if (list.isEmpty()) {
                rl_nolocationfound.setVisibility(View.VISIBLE);
                rl_recycleviewlayout.setVisibility(ListView.GONE);
            } else {
                rl_recycleviewlayout.setVisibility(ListView.VISIBLE);
                rl_nolocationfound.setVisibility(View.GONE);
                if (adapter != null) {
                    drawMarkers(nearbylist);
                    adapter.reload(list);

                } else {
                    bindList(list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Bind all orders to list view
     */
    public void bindList(ArrayList<NearbyMapVO> list) {
        try {
            nearbylist = list;
            if (list.isEmpty()) {
                rl_nolocationfound.setVisibility(View.VISIBLE);
                rl_recycleviewlayout.setVisibility(ListView.GONE);
            } else {
                drawMarkers(nearbylist);
                rl_recycleviewlayout.setVisibility(ListView.VISIBLE);
                rl_nolocationfound.setVisibility(View.GONE);

                adapter = new NearbyLocationAdapter(this, list);
                recyclerView.setAdapter(adapter);
                YoYo.with(Techniques.FadeInUp).duration(appsingleton.animationduration).interpolate(new AccelerateDecelerateInterpolator()).playOn(recyclerView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * function to add circle
    */
    public void drawMarkers(ArrayList<NearbyMapVO> data) {
        try {
            Marker m;
            markerArrayList.clear();
            for (NearbyMapVO object : data) {
                m = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(object.getLatitude(),object.getLongitude()))
                        .title(object.getPlacename())
                        .snippet(object.getPlace_description())
                        .icon(BitmapDescriptorFactory.fromBitmap(getIcon(R.drawable.location_normal_icon))));
                markerArrayList.add(m);
            }//end of for
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /*
   * function to add circle
   */
    public void changeMarker() {
        try{
           for (int i=0;i<nearbylist.size();i++)
           {
               if(nearbylist.get(i).getisselected()) {
                   markerArrayList.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(getIcon(R.drawable.location_active_icon)));
               }
               else
                   markerArrayList.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(getIcon(R.drawable.location_normal_icon)));
           }

        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /*
    *function to get map icon
    */
    public Bitmap getIcon(int id) {
        try {
            int width = ((int) (appsingleton.devicewidth * 0.075));
            int height = ((int) (appsingleton.devicewidth * 0.09));
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
            Bitmap thumb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(thumb);
            canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                    new Rect(0, 0, thumb.getWidth(), thumb.getHeight()), null);
            Drawable drawable = new BitmapDrawable(getResources(), thumb);
            return thumb;
        } catch (Exception e) {
            appsingleton.ToastMessage("" + e.getMessage());
            return null;
        }
    }

    private class DraggableCircle {
        private final Marker centerMarker;
        //private final Marker radiusMarker;
        private final Circle circle;
        private double radius;

        public DraggableCircle(LatLng center, double radius, boolean clickable) {
            this.radius = radius;

            int width = ((int) (appsingleton.devicewidth * 0.075));
            int height = ((int) (appsingleton.devicewidth * 0.09));
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_current_icon);
            Bitmap thumb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(thumb);
            canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                    new Rect(0, 0, thumb.getWidth(), thumb.getHeight()), null);
            Drawable drawable = new BitmapDrawable(getResources(), thumb);

            centerMarker = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .draggable(false).icon(BitmapDescriptorFactory.fromBitmap(thumb)));


//            radiusMarker = mMap.addMarker(new MarkerOptions()
//                    .position(toRadiusLatLng(center, radius))
//                    .draggable(true)
//                    .icon(BitmapDescriptorFactory.defaultMarker(
//                            BitmapDescriptorFactory.HUE_AZURE)));
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeWidth(strock)
                    .strokeColor(mStrokeColor)
                    .fillColor(mFillColor)
                    );
        }

        public DraggableCircle(LatLng center, LatLng radiusLatLng, boolean clickable) {
            this.radius = toRadiusMeters(center, radiusLatLng);
            centerMarker = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .draggable(false));
//            radiusMarker = mMap.addMarker(new MarkerOptions()
//                    .position(radiusLatLng)
//                    .draggable(true)
//                    .icon(BitmapDescriptorFactory.defaultMarker(
//                            BitmapDescriptorFactory.HUE_AZURE)));
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeWidth(strock)
                    .strokeColor(mStrokeColor)
                    .fillColor(mFillColor)
                    );
        }

        public boolean onMarkerMoved(Marker marker) {
            if (marker.equals(centerMarker)) {
                circle.setCenter(marker.getPosition());
                // radiusMarker.setPosition(toRadiusLatLng(marker.getPosition(), radius));
                return true;
            }
//            if (marker.equals(radiusMarker)) {
//                radius = toRadiusMeters(centerMarker.getPosition(), radiusMarker.getPosition());
//                circle.setRadius(radius);
//                return true;
//            }
            return false;
        }

        public void onStyleChange() {
            circle.setStrokeWidth(strock);
            circle.setFillColor(mFillColor);
            circle.setStrokeColor(mStrokeColor);
        }

        public void setClickable(boolean clickable) {

        }

    }

    /**
     * Generate LatLng of radius marker
     */
    private static LatLng toRadiusLatLng(LatLng center, double radius) {
        double radiusAngle = Math.toDegrees(radius / RADIUS_OF_EARTH_METERS) /
                Math.cos(Math.toRadians(center.latitude));
        return new LatLng(center.latitude, center.longitude + radiusAngle);
    }

    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        return result[0];
    }

    /*
    *function to map initialization and load
    */
    @Override
    public void onMapReady(GoogleMap map) {
        try {


            mMap = map;

            // Override the default content description on the view, for accessibility mode.
            map.setContentDescription(getString(R.string.app_name));

            //mRadius.setOnSeekBarChangeListener(this);
            mMap.setOnMarkerDragListener(this);
            mMap.setOnMapLongClickListener(this);
            mMap.setOnMarkerClickListener(this);

//        mFillColor = Color.HSVToColor(
//                mAlphaBar.getProgress(), new float[]{mColorBar.getProgress(), 1, 1});
            mFillColor = getResources().getColor(R.color.cl_circlefill);
            mStrokeColor = getResources().getColor(R.color.cl_circlevorder);

            DraggableCircle circle =
                    new DraggableCircle(CURRENTLOC, DEFAULT_RADIUS, false);
            mCircles.add(circle);

            // Move the map so that it is centered on the initial circle
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 4.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CURRENTLOC, 9.1f));
            try{
                mMap.setMyLocationEnabled(true);
               // mMap.getUiSettings().setMyLocationButtonEnabled(true);

            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
//        CameraUpdate center=CameraUpdateFactory.newLatLng(SYDNEY);
//        CameraUpdate zoom=CameraUpdateFactory.zoomTo(20);
//        mMap.moveCamera(center);
//        mMap.animateCamera(zoom);

            // Set up the click listener for the circle.
//            map.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
//                @Override
//                public void onCircleClick(Circle circle) {
//                    // Flip the r, g and b components of the circle's stroke color.
//                    int strokeColor = circle.getStrokeColor() ^ 0x00ffffff;
//                    circle.setStrokeColor(strokeColor);
//                }
//            });

            /*
            *add location markers
            */
//            if (nearbylist.size() > 0)
//                drawMarkers(nearbylist);
        } catch (Exception e) {
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

//        if (seekBar == mRadiusseekbar) {
//            mMap.clear();
//            //((TextView)findViewById(R.id.tvSampletext)).setText(""+progress);
//            DraggableCircle circle =
//                    new DraggableCircle(CURRENTLOC, (progress*1609.34), false);
//            mCircles.add(circle);
//        }

        for (DraggableCircle draggableCircle : mCircles) {
            draggableCircle.onStyleChange();
        }
    }
    @Override
    public boolean onMarkerClick(final Marker marker) {
        try{
            for (int i = 0; i < nearbylist.size(); i++) {
                nearbylist.get(i).setIsselected(false);
            }//end of for
            for (int i = 0; i < markerArrayList.size(); i++) {
                if (marker.equals(markerArrayList.get(i)))
                {
                     nearbylist.get(i).setIsselected(true);
                    appsingleton.ToastMessage(marker.getTitle()+" pos=  "+i);
                     changeMarker();
                    adapter.notifyDataSetChanged();

                    try{
                        String title = nearbylist.get(i).getPlacename();
                        String message = nearbylist.get(i).getPlace_description();
                        String miles1 = nearbylist.get(i).getMiles();
                        String profileurl = nearbylist.get(i).getProfileurl();
                        adapter.showDialog(title, message, miles1, profileurl,i);
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                     break;
                }
            }//end of for
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
        return true;
    }
    @Override
    public void onMarkerDragStart(Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        onMarkerMoved(marker);
    }

    private void onMarkerMoved(Marker marker) {
        for (DraggableCircle draggableCircle : mCircles) {
            if (draggableCircle.onMarkerMoved(marker)) {
                break;
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        // We know the center, let's place the outline at a point 3/4 along the view.
//        View view = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
//                .getView();
//        LatLng radiusLatLng = mMap.getProjection().fromScreenLocation(new Point(
//                view.getHeight() * 3 / 4, view.getWidth() * 3 / 4));
//
//        // ok create it

//        mMap.clear();
//        DraggableCircle circle =
//                new DraggableCircle(CURRENTLOC, DEFAULT_RADIUS, false);
//        mCircles.add(circle);
    }

    public void toggleClickability(View view) {
        boolean clickable = ((CheckBox) view).isChecked();
        // Set each of the circles to be clickable or not, based on the
        // state of the checkbox.
        for (DraggableCircle draggableCircle : mCircles) {
            draggableCircle.setClickable(clickable);
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

    /*
 * to display map miles dialog
 * */
    public void showMilesDialog(String title, String miles) {
        try {
            final Dialog dialog = new Dialog(this);
            // Include dialog.xml file
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.map_seekbar_popup);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(appsingleton.devicewidth, WindowManager.LayoutParams.WRAP_CONTENT);
            final TextView tv_miles, tv_title, tv_milesmsg, tv_min, tv_max, tv_1mile, tv_50miles;
            ImageView iv_close;
            tv_title = (TextView) dialog.findViewById(R.id.tv_popuptitle);
            tv_miles = (TextView) dialog.findViewById(R.id.tvmilescount);
            tv_milesmsg = (TextView) dialog.findViewById(R.id.tvmiles);
            tv_min = (TextView) dialog.findViewById(R.id.tv_mintitle);
            tv_max = (TextView) dialog.findViewById(R.id.tv_maxtitle);
            tv_1mile = (TextView) dialog.findViewById(R.id.tv_1miletitle);
            tv_50miles = (TextView) dialog.findViewById(R.id.tv_50miletitle);
            mRadiusseekbar = (SeekBar) dialog.findViewById(R.id.seekBar);
            iv_close = (ImageView) dialog.findViewById(R.id.iv_Crossimage);

             /*Assining fonts*/
            tv_title.setTypeface(appsingleton.regulartype);
            tv_miles.setTypeface(appsingleton.boldtype);
            tv_milesmsg.setTypeface(appsingleton.regulartype);
            tv_min.setTypeface(appsingleton.regulartype);
            tv_max.setTypeface(appsingleton.regulartype);
            tv_1mile.setTypeface(appsingleton.regulartype);
            tv_50miles.setTypeface(appsingleton.regulartype);
            try {
                tv_title.setText(title);
                tv_miles.setText(miles);
                int width = ((int) (appsingleton.devicewidth * 0.15));
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.seekbar_icon);
                Bitmap thumb = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(thumb);
                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                        new Rect(0, 0, thumb.getWidth(), thumb.getHeight()), null);
                Drawable drawable = new BitmapDrawable(getResources(), thumb);

                mRadiusseekbar.setThumb(drawable);
                mRadiusseekbar.setThumbOffset(-1);

                mRadiusseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        try {
                            if (progress != 0) {
                                tv_miles.setText("" + progress);
                                if (seekBar == mRadiusseekbar) {
                                    mMap.clear();
                                    tv_raduscount.setText(progress +"mi");
                                    DraggableCircle circle =
                                            new DraggableCircle(CURRENTLOC, (progress * 1609.34), false);
                                    mCircles.add(circle);

//                                        if (nearbylist.size() > 0)
//                                            drawMarkers(nearbylist);
                                }
                            } else {
                                tv_miles.setText("" + 1);
                                if (seekBar == mRadiusseekbar) {
                                    mMap.clear();
                                    tv_raduscount.setText(1+"mi");
                                    DraggableCircle circle =
                                            new DraggableCircle(CURRENTLOC, (1 * 1609.34), false);
                                    mCircles.add(circle);
                                     /*
                                     *add location markers
                                       */
                                }
                            }
                        } catch (Exception e) {
                            appsingleton.ToastMessage("" + e.getMessage());
                        }
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        try{

                            try{
                                SharedPreferences.Editor editor=appsingleton.sharedPreferences.edit();
                                editor.putInt("RADIOUS",mRadiusseekbar.getProgress());
                                editor.apply();

                            }catch(Exception e){
                                e.printStackTrace();
                                appsingleton.ToastMessage("" + e.getMessage());
                            }

                            dialog.dismiss();
                            String lat=""+appsingleton.currentLatitude;
                            String long_=""+appsingleton.currentLongitude;
                            String radious="";
                            if(mRadiusseekbar!=null)
                                radious=""+mRadiusseekbar.getProgress();
                            else
                                radious=""+radiousvalue;

                            if (AppUtils.isNetworkAvailable(NearbyLocationActivity.this)) {
                                new TopLocations(lat,long_,radious).execute();
                            }//end of network if
                            else
                            {
                                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",rl_nolocationfound);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            appsingleton.ToastMessage("" + e.getMessage());
                        }
                    }

                });


                if (Integer.parseInt(miles) < 2) {
                    mRadiusseekbar.setProgress(0);
                } else {
                    mRadiusseekbar.setProgress(Integer.parseInt(miles));
                }
            } catch (Exception e) {
                appsingleton.ToastMessage("" + e.getMessage());
            }

            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }//end of show dialog

    /*
      *function load data
      */
    public ArrayList<NearbyMapVO> setData() {
        ArrayList<NearbyMapVO> list = new ArrayList<>();
//        NearbyMapVO vo1 = new NearbyMapVO("De Nearby Hotel", "4 mile", "", new LatLng(18.490286, 73.820948), false);
//        NearbyMapVO vo2 = new NearbyMapVO("Pizza Hut", "4 mile", "", new LatLng(18.518210, 73.840291), false);
//        NearbyMapVO vo3 = new NearbyMapVO("Usbank", "4 mile", "", new LatLng(18.530118, 73.821959), false);
//        NearbyMapVO vo4 = new NearbyMapVO("Raymond Hotel", "4 mile", "", new LatLng(18.523275, 73.761477), false);
//        NearbyMapVO vo5 = new NearbyMapVO("BSNL", "4 mile", "", new LatLng(18.509342, 73.790607), false);
//        list.add(vo1);
//        list.add(vo2);
//        list.add(vo3);
//        //list.add(vo4);
//        list.add(vo5);

        return list;
    }

    /**
     * NearBY LOCATION Pullto Refresh web service
     *
     * @author user
     */
    public class TopLocations extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getToplocationsUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        String lat_="";
        String long_="";
        String radious="";
        public TopLocations(String lat_,String long_,String radious) {
            try {
                this.lat_ = lat_;
                this.long_ = long_;
                this.radious = radious;

            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(NearbyLocationActivity.this);
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
                        .key("user_id").value(appsingleton.getUserid())
                        .key("user_lat").value(lat_)
                        .key("user_long").value(long_)
                        .key("radius").value(radious)
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
                                    ArrayList<NearbyMapVO> tempzaparoundlist = new ArrayList<>();
                                    tempzaparoundlist.clear();

                                    String resultArray = object.getString("result");
                                    JSONArray jsonArray = new JSONArray(resultArray);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject arrayObject = jsonArray.getJSONObject(i);
                                        NearbyMapVO vo = new NearbyMapVO();
                                        vo.setPlaceid(arrayObject.getString("place_id"));
                                        vo.setProfileurl(arrayObject.getString("place_image_reference"));
                                        vo.setPlacename(arrayObject.getString("place_name"));
                                        vo.setLatitude(arrayObject.getDouble("place_lat"));
                                        vo.setLongitude(arrayObject.getDouble("place_long"));
                                        vo.setPlace_description(arrayObject.getString("place_desc"));
                                        vo.setMiles(arrayObject.getString("place_distance"));
                                        vo.setTotal_checkin(arrayObject.getString("total_checkin"));
                                        tempzaparoundlist.add(vo);
                                    }//end of for

                                    nearbylist.clear();
                                    nearbylist.addAll(tempzaparoundlist);
                                    reloadListData(tempzaparoundlist);
                                    drawMarkers(nearbylist);
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
                    reloadListData(new ArrayList<NearbyMapVO>());
                }
                appsingleton.dismissDialog();
            } catch (Exception e) {
                reloadListData(new ArrayList<NearbyMapVO>());
                e.printStackTrace();
                appsingleton.dismissDialog();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }
    }//end of Checkin Pullto refresh Asynctask

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
        }
    }//end of on back pressed
}//end of Activity
