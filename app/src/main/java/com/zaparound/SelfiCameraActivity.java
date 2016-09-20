package com.zaparound;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zaparound.AppUtils.AppUtils;
import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.ModelVo.CheckinZaparoundVO;
import com.zaparound.Singleton.Appsingleton;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class SelfiCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        View.OnClickListener {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Button flipCamera;
    private Button flashCameraButton;
    private Button captureImage;
    private int cameraId;
    private boolean flashmode = false;
    private int rotation;
    Handler handler = new Handler();
    public RelativeLayout rl_capturelayout, rl_savecapturedlayout, rl_whitelayout, rl_camerapermission;
    public Appsingleton appsingleton;
    //Save to gallery image
    private File imageFile;
    Bitmap loadedImage = null;
    Bitmap rotatedBitmap = null;
    public boolean isFlashSet = true;
    public Button enablecamera;

    @Override
    protected void onRestart() {
        super.onRestart();
        releaseCamera();
        if (appsingleton.needPermissionCheck()) {
            //check marshmallo permisions
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                rl_camerapermission.setVisibility(View.VISIBLE);
            }//end of if permission
            else {
                init();
            }//end of else permission
        } else
            init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (appsingleton.needPermissionCheck()) {
            //check marshmallo permisions
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                rl_camerapermission.setVisibility(View.VISIBLE);
            }//end of if permission
            else {
                init();
            }//end of else permission
        } else
            init();

        try {
            appsingleton=Appsingleton.getinstance(this);
            appsingleton.finishAllSession();
        }catch (Exception e){e.printStackTrace();}
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            appsingleton = Appsingleton.getinstance(this);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_selfi_camera);
            rl_camerapermission = (RelativeLayout) findViewById(R.id.rl_Camerapermission);
            enablecamera = (Button) findViewById(R.id.bt_enablecamera);

            enablecamera.setTypeface(appsingleton.regulartype);
            appsingleton.activityArrayList.add(this);
            if (appsingleton.needPermissionCheck()) {
                //check marshmallo permisions
                if (!appsingleton.cameraPermission(this) && !appsingleton.externalreadPermission(this)) {
                    rl_camerapermission.setVisibility(View.VISIBLE);
                }//end of if permission
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    rl_camerapermission.setVisibility(View.VISIBLE);
                }//end of if permission
                else {
                    init();
                }//end of else permission
            } else {
                init();
            }//end of else permission

        } catch (Exception e) {
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }//end of oncreate

    public void enableCamera(View view) {
        try {
            startInstalledAppDetailsActivity(this);
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
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            flipCamera = (Button) findViewById(R.id.flipCamera);
            flashCameraButton = (Button) findViewById(R.id.flash);
            captureImage = (Button) findViewById(R.id.captureImage);
            surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
            //flipCamera.setOnClickListener(this);
            captureImage.setOnClickListener(this);
            flashCameraButton.setOnClickListener(this);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            //initialize views
            rl_capturelayout = (RelativeLayout) findViewById(R.id.rl_captureimageLayout);
            rl_savecapturedlayout = (RelativeLayout) findViewById(R.id.rl_SaveimageLayout);
            rl_whitelayout = (RelativeLayout) findViewById(R.id.rl_whitelayout);
            rl_savecapturedlayout.setVisibility(View.GONE);
            rl_capturelayout.setVisibility(View.VISIBLE);
            if (Camera.getNumberOfCameras() > 1) {
                flipCamera.setVisibility(View.VISIBLE);
            }
            if (!getBaseContext().getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                //flashCameraButton.setVisibility(View.GONE);
                // flashCameraButton.setBackgroundResource(R.drawable.flash_icon_inactive);
                //flashCameraButton.setEnabled(false);
            }
            if (!isFlashSet) {
                flashCameraButton.setBackgroundResource(R.drawable.flash_icon_inactive);
            } else {
                flashCameraButton.setBackgroundResource(R.drawable.flash_icon_active);
            }
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
            alertCameraDialog();
        }
    }

    private boolean openCamera(int id) {
        boolean result = false;
        try {
            cameraId = id;
            releaseCamera();
            try {
                camera = Camera.open(cameraId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (camera != null) {
                try {
                    setUpCamera(camera);
                    camera.setErrorCallback(new Camera.ErrorCallback() {

                        @Override
                        public void onError(int error, Camera camera) {

                        }
                    });
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.startPreview();
                    result = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    result = false;
                    releaseCamera();
                }
            }
        }catch (Exception e){e.printStackTrace();}

        return result;
    }

    private void setUpCamera(Camera c) {
        try {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;

            default:
                break;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing
            rotation = (info.orientation + degree) % 330;
            rotation = (360 - rotation) % 360;
        } else {
            // Back-facing
            rotation = (info.orientation - degree + 360) % 360;
        }
        c.setDisplayOrientation(rotation);
        Camera.Parameters params = c.getParameters();

        showFlashButton(params);

        List<String> focusModes = params.getSupportedFlashModes();
        if (focusModes != null) {
            if (focusModes
                    .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }

        params.setRotation(rotation);
        }catch (Exception e){e.printStackTrace();}
    }

    private void showFlashButton(Camera.Parameters params) {
        boolean showFlash = (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH) && params.getFlashMode() != null)
                && params.getSupportedFlashModes() != null
                && params.getSupportedFocusModes().size() > 1;

//        flashCameraButton.setVisibility(showFlash ? View.VISIBLE
//                : View.INVISIBLE);
//        if (showFlash) {
//            flashCameraButton.setBackgroundResource(R.drawable.flash_icon_active);
//            flashCameraButton.setEnabled(true);
//
//        } else {
//            flashCameraButton.setBackgroundResource(R.drawable.flash_icon_inactive);
//            flashCameraButton.setEnabled(false);
//        }


    }

    private void releaseCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.flash:
                    //flashOnButton();
                    if (!isFlashSet) {
                        isFlashSet = true;
                        flashCameraButton.setBackgroundResource(R.drawable.flash_icon_active);
                    } else {
                        isFlashSet = false;
                        flashCameraButton.setBackgroundResource(R.drawable.flash_icon_inactive);
                    }

                    break;
                case R.id.flipCamera:
                    flipCamera();
                    break;
                case R.id.captureImage:
                    if (isFlashSet) {
                        rl_whitelayout.setVisibility(View.VISIBLE);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                takeImage();
                            }
                        }, 100);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rl_whitelayout.setVisibility(View.GONE);

                            }
                        }, 800);
                    } else {
                        takeImage();
                    }

                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /*
    *function back to camera
    */
    public void Retake(View view) {
        try {
            flipCamera();
            rl_savecapturedlayout.setVisibility(View.GONE);
            rl_capturelayout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage(e.getMessage());
        }

    }//end of retake

    /*
   *function to back press
   */
    public void Backtolist(View view) {
        try {
            onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }//end of retake

    /*
  *function to Checkin feed Activity
  */
    public void Setimage(View view) {
        try {
            try {
                // rotate Image
                Matrix rotateMatrix = new Matrix();
                rotateMatrix.postRotate(270);
                rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                        loadedImage.getWidth(), loadedImage.getHeight(),
                        rotateMatrix, false);

                Bitmap thumb = appsingleton.resize(rotatedBitmap, appsingleton.maxwidth, appsingleton.maxheight);
                rotatedBitmap = thumb;

                String state = Environment.getExternalStorageState();
                File folder;
                if (state.contains(Environment.MEDIA_MOUNTED)) {
                    folder = new File(Environment
                            .getExternalStorageDirectory() + "/Zaparound");
                } else {
                    folder = new File(Environment
                            .getExternalStorageDirectory() + "/Zaparound");
                }

                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {
                    rl_savecapturedlayout.setVisibility(View.VISIBLE);
                    rl_capturelayout.setVisibility(View.GONE);
                    java.util.Date date = new java.util.Date();
                    imageFile = new File(folder.getAbsolutePath()
                            + File.separator
                            + new Timestamp(date.getTime()).toString()
                            + "Image.jpg");

                    imageFile.createNewFile();
                } else {
                    Toast.makeText(getBaseContext(), "Image Not saved",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ByteArrayOutputStream ostream = new ByteArrayOutputStream();

                // save image into gallery
                //rotatedBitmap.compress(CompressFormat.JPEG, 100, ostream);
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);

                FileOutputStream fout = new FileOutputStream(imageFile);
                fout.write(ostream.toByteArray());
                fout.close();
                try {
                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        String placeid = extras.getString("PLACE_ID", "");
                        String placename = extras.getString("PLACE_NAME", "");
                        String photoreference = extras.getString("PLACE_PHOTOREFERENCE", "");
                        String placeaddress = extras.getString("PLACE_ADDRESS", "");
                        String placelat = extras.getString("PLACE_LAT", "0.0");
                        String placelang = extras.getString("PLACE_LANG", "0.0");
                        String placedecription = extras.getString("PLACE_DESCRIPTION", "");


                        if (AppUtils.isNetworkAvailable(this)) {
                            new CheckinSelfi(placeid,placename,photoreference,placeaddress,placelat,placelang,placedecription).execute();

                        }//end of network if
                        else
                        {
                            appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",flashCameraButton);
                        }
                    }
                    else
                    {
                        appsingleton.ToastMessage("Place data missing");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                imageFile.delete();
                e.printStackTrace();
                appsingleton.ToastMessage(e.getMessage());
            }
//            Intent intent = new Intent(this, ZapfeedTabhostActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            //           finish();
        } catch (Exception e) {
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }//end of Setimage

    /*
   *function back to camera
   */
    public void SaveimagetoGallery(View view) {
        try {
            // rotate Image
            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(270);
            //rotateMatrix.preScale(-1, 1);
            rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                    loadedImage.getWidth(), loadedImage.getHeight(),
                    rotateMatrix, false);
            String state = Environment.getExternalStorageState();
            File folder;
            if (state.contains(Environment.MEDIA_MOUNTED)) {
                folder = new File(Environment
                        .getExternalStorageDirectory() + "/Zaparound");
            } else {
                folder = new File(Environment
                        .getExternalStorageDirectory() + "/Zaparound");
            }

            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {
                rl_savecapturedlayout.setVisibility(View.VISIBLE);
                rl_capturelayout.setVisibility(View.GONE);
                java.util.Date date = new java.util.Date();
                imageFile = new File(folder.getAbsolutePath()
                        + File.separator
                        + new Timestamp(date.getTime()).toString()
                        + "Image.jpg");

                imageFile.createNewFile();
            } else {
                Toast.makeText(getBaseContext(), "Image Not saved",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ByteArrayOutputStream ostream = new ByteArrayOutputStream();

            // save image into gallery
            //rotatedBitmap.compress(CompressFormat.JPEG, 100, ostream);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);

            FileOutputStream fout = new FileOutputStream(imageFile);
            fout.write(ostream.toByteArray());
            fout.close();
            try {
                String path = imageFile.getAbsolutePath();
                appsingleton.SnackbarMessage("Image Saved", "" + imageFile.getName(), rl_whitelayout);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            String photopath = imageFile.getPath().toString();
//            Bitmap bmp = BitmapFactory.decodeFile(photopath);
//
//            Matrix matrix = new Matrix();
//            matrix.postRotate(180);
//            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
//
//            FileOutputStream fOut1;
//            try {
//                fOut1 = new FileOutputStream(imageFile);
//                bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut1);
//                fOut1.flush();
//                fOut1.close();
//
//            } catch (FileNotFoundException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                flipCamera();
                rl_savecapturedlayout.setVisibility(View.GONE);
                rl_capturelayout.setVisibility(View.VISIBLE);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }//end of SaveimagetoGallery

    private void takeImage() {
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    // convert byte array into bitmap

                    loadedImage = BitmapFactory.decodeByteArray(data, 0,
                            data.length);

                    // rotate Image
                    Matrix rotateMatrix = new Matrix();
                    rotateMatrix.postRotate(rotation);
                    rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                            loadedImage.getWidth(), loadedImage.getHeight(),
                            rotateMatrix, false);

                    rl_savecapturedlayout.setVisibility(View.VISIBLE);
                    rl_capturelayout.setVisibility(View.GONE);


//
//                    String state = Environment.getExternalStorageState();
//                    File folder;
//                    if (state.contains(Environment.MEDIA_MOUNTED)) {
//                        folder = new File(Environment
//                                .getExternalStorageDirectory() + "/Zaparound");
//                    } else {
//                        folder = new File(Environment
//                                .getExternalStorageDirectory() + "/Zaparound");
//                    }
//
//                    boolean success = true;
//                    if (!folder.exists()) {
//                        success = folder.mkdirs();
//                    }
//                    if (success) {
//                        rl_savecapturedlayout.setVisibility(View.VISIBLE);
//                        rl_capturelayout.setVisibility(View.GONE);
//                        java.util.Date date = new java.util.Date();
//                        imageFile = new File(folder.getAbsolutePath()
//                                + File.separator
//                                + new Timestamp(date.getTime()).toString()
//                                + "Image.jpg");
//
//                        imageFile.createNewFile();
//                    } else {
//                        Toast.makeText(getBaseContext(), "Image Not saved",
//                                Toast.LENGTH_SHORT).show();
//                        return;
//                    }


//                    ContentValues values = new ContentValues();
//
//                    values.put(MediaStore.Images.Media.DATE_TAKEN,
//                            System.currentTimeMillis());
//                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//                    values.put(MediaStore.MediaColumns.DATA,
//                            imageFile.getAbsolutePath());
//
//                    SelfiCameraActivity.this.getContentResolver().insert(
//                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void flipCamera() {
        int id = (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ? Camera.CameraInfo.CAMERA_FACING_FRONT
                : Camera.CameraInfo.CAMERA_FACING_FRONT);
        if (!openCamera(id)) {
            // alertCameraDialog();
            finish();
            Intent intent = new Intent(this, SelfiCameraActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        }
    }

    private void alertCameraDialog() {

        try{
            AlertDialog.Builder dialog = createAlert(SelfiCameraActivity.this,
                    "Camera info", "error to open camera");
            dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            });
            dialog.show();
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }


    }

    private AlertDialog.Builder createAlert(Context context, String title, String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(
                new ContextThemeWrapper(context,
                        android.R.style.Theme_Holo_Light_Dialog));
        dialog.setIcon(R.drawable.cross_icon);
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.setTitle("Information");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;

    }

    private void flashOnButton() {

        try{
            if (camera != null) {
                try {
                    Camera.Parameters param = camera.getParameters();
                    param.setFlashMode(!flashmode ? Camera.Parameters.FLASH_MODE_TORCH
                            : Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(param);
                    flashmode = !flashmode;
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            releaseCamera();
        } catch (Exception e) {

            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /**
     * Checkin selfi web service
     *
     * @author user
     */
    public class CheckinSelfi extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getCheckinSelfiUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        String placeid = "";
        String placename = "";
        String place_photoreference = "";
        String placeaddress = "";
        String placelat = "";
        String place_long = "";
        String placedescription="";

        public CheckinSelfi(String placeid, String placename, String place_photoreference,String placeaddress, String placelat, String place_long, String placedescription) {
            try {
                this.placeid = placeid;
                this.placename = placename;
                this.place_photoreference = place_photoreference;
                this.placeaddress = placeaddress;
                this.placelat = placelat;
                this.place_long = place_long;
                this.placedescription = placedescription;
            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(SelfiCameraActivity.this);
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
                HttpConnectionParams.setConnectionTimeout(parameters, 1000000);
                HttpConnectionParams.setSoTimeout(parameters, 2000000);
                client = new DefaultHttpClient(parameters);

                JSONStringer item = new JSONStringer()
                        .object()
                        .key("user_id").value(appsingleton.getUserid())
                        .key("place_id").value(placeid)
                        .key("place_name").value(placename)
                        .key("place_image_reference").value(place_photoreference)
                        .key("place_address").value(placeaddress)
                        .key("place_lat").value(placelat)
                        .key("place_long").value(place_long)
                        .key("description").value(placedescription)
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
                /*Selfiimage image*/
                    if (imageFile.getAbsolutePath() != null && imageFile.getAbsolutePath().length() > 0) {
                        File file = new File(imageFile.getAbsolutePath());
                        entity.addPart("selfie", new FileBody(file));
                    }
                }catch (Exception e){
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
                                    appsingleton.zaparoundlist.clear();
                                    String resultArray = object.getString("result");
                                    String checkin_id = object.getString("checkin_id");

                                    float checkoutdestance=200;
                                    try{
                                         checkoutdestance =Float.valueOf( object.getString("checkout_distance"));
                                    }catch(Exception e){
                                        e.printStackTrace();
                                        checkoutdestance=200;
                                        appsingleton.ToastMessage("" + e.getMessage());
                                    }

                                    JSONArray jsonArray = new JSONArray(resultArray);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject arrayObject = jsonArray.getJSONObject(i);
                                        CheckinZaparoundVO vo=new CheckinZaparoundVO();
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
                                        appsingleton.zaparoundlist.add(vo);
                                    }//end of for
                                    SharedPreferences.Editor editor = appsingleton.sharedPreferences.edit();
                                    editor.putString("CHECKINPLACE_ID", placeid);
                                    editor.putString("CHECKIN_ID", checkin_id);
                                    editor.putString("PLACENAME", placename);
                                    editor.putString("PRE_LATITUDE", ""+appsingleton.currentLatitude);
                                    editor.putString("PRE_LONGITUDE", ""+appsingleton.currentLongitude);
                                    editor.putFloat("CHECKOUTDESTANCE", checkoutdestance);
                                    editor.apply();
                                    imageFile.delete();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    appsingleton.ToastMessage("" + e.getMessage());
                                }

                                Intent intent = new Intent(SelfiCameraActivity.this, ZapfeedTabhostActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                finish();
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
                try {
                   imageFile.delete();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    appsingleton.ToastMessage("" + e1.getMessage());
                }
                e.printStackTrace();
                appsingleton.dismissDialog();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }
    }//end of ValidateUsername Asynctask

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            releaseCamera();
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
        }
    }//end of on back pressed

}//end of activity
