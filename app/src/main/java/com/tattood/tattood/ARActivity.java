package com.tattood.tattood;

import java.io.File;
import java.io.FileOutputStream;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.wikitude.architect.ArchitectJavaScriptInterfaceListener;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.CaptureScreenCallback;
import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;
import com.wikitude.common.camera.CameraSettings;

import org.json.JSONException;
import org.json.JSONObject;

public class ARActivity extends AbstractArchitectCamActivity implements SensorEventListener {

    private static final String TAG = "SampleCamActivity";
    /**
     * last time the calibration toast was shown, this avoids too many toast shown when compass needs calibration
     */
    private long lastCalibrationToastShownTimeMillis = System.currentTimeMillis();

    protected Bitmap screenCapture = null;

    private static final int WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 3;

    private static final String EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL =
            "Samples" + File.separator + "ARCamera" + File.separator + "index.html";

    private static final String EXTRAS_KEY_ACTIVITY_TITLE_STRING = "ARCamera";

    private static final boolean EXTRAS_KEY_ACTIVITY_GEO = true;
    private static final boolean EXTRAS_KEY_ACTIVITY_IR = true;
    private static final boolean EXTRAS_KEY_ACTIVITY_INSTANT = true;

    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.architectView.callJavascript("World.changeTattoo('assets/indir.png');");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null)
        {
            Log.e("Heleloy", "sensor yok");
        }
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public String getARchitectWorldPath() {
//        return getIntent().getExtras().getString(
//                MainSamplesListActivity.EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL);
        return EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL;
    }

    @Override
    public String getActivityTitle() {
        return (getIntent().getExtras() != null && getIntent().getExtras().get(
                this.EXTRAS_KEY_ACTIVITY_TITLE_STRING) != null) ? getIntent()
                .getExtras().getString(this.EXTRAS_KEY_ACTIVITY_TITLE_STRING)
                : "Test-World";
    }

    @Override
    public int getContentViewId() {
        return R.layout.sample_cam;
    }

    @Override
    public int getArchitectViewId() {
        return R.id.architectView;
    }

    @Override
    public String getWikitudeSDKLicenseKey() {
        return WikitudeSDKConstants.WIKITUDE_SDK_KEY;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        Log.e("Heleloy", "girdik2");
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (event.sensor == sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR))
        {
            this.architectView.callJavascript("World.changeRotation(" + (2 * Math.asin(event.values[0]) * 180 / Math.PI) + "," + (2 * Math.asin(event.values[1]) * 180 / Math.PI) + ")");
        }
    }

    @Override
    public SensorAccuracyChangeListener getSensorAccuracyListener() {
        return new SensorAccuracyChangeListener() {
            @Override
            public void onCompassAccuracyChanged( int accuracy ) {
				/* UNRELIABLE = 0, LOW = 1, MEDIUM = 2, HIGH = 3 */
                if ( accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM && ARActivity.this != null && !ARActivity.this.isFinishing() && System.currentTimeMillis() - ARActivity.this.lastCalibrationToastShownTimeMillis > 5 * 1000) {
                    Toast.makeText( ARActivity.this, R.string.compass_accuracy_low, Toast.LENGTH_LONG ).show();
                    ARActivity.this.lastCalibrationToastShownTimeMillis = System.currentTimeMillis();
                }
            }
        };
    }

    @Override
    public ArchitectJavaScriptInterfaceListener getArchitectJavaScriptInterfaceListener() {
        return new ArchitectJavaScriptInterfaceListener() {
            @Override
            public void onJSONObjectReceived(JSONObject jsonObject) {
                try {
                    switch (jsonObject.getString("action")) {
                        case "present_poi_details":
                            final Intent poiDetailIntent = new Intent(ARActivity.this, SamplePoiDetailActivity.class);
                            poiDetailIntent.putExtra(SamplePoiDetailActivity.EXTRAS_KEY_POI_ID, jsonObject.getString("id"));
                            poiDetailIntent.putExtra(SamplePoiDetailActivity.EXTRAS_KEY_POI_TITILE, jsonObject.getString("title"));
                            poiDetailIntent.putExtra(SamplePoiDetailActivity.EXTRAS_KEY_POI_DESCR, jsonObject.getString("description"));
                            ARActivity.this.startActivity(poiDetailIntent);
                            break;

                        case "capture_screen":
                            ARActivity.this.architectView.captureScreen(ArchitectView.CaptureScreenCallback.CAPTURE_MODE_CAM_AND_WEBVIEW, new CaptureScreenCallback() {
                                @Override
                                public void onScreenCaptured(final Bitmap screenCapture) {
                                    if ( ContextCompat.checkSelfPermission(ARActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                                        ARActivity.this.screenCapture = screenCapture;
                                        ActivityCompat.requestPermissions(ARActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                                    } else {
                                        ARActivity.this.saveScreenCaptureToExternalStorage(screenCapture);
                                    }
                                }
                            });
                            break;
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "onJSONObjectReceived: ", e);
                }
            }
        };
    }

    @Override
    public ArchitectView.ArchitectWorldLoadedListener getWorldLoadedListener() {
        return new ArchitectView.ArchitectWorldLoadedListener() {
            @Override
            public void worldWasLoaded(String url) {
                Log.i(TAG, "worldWasLoaded: url: " + url);
            }

            @Override
            public void worldLoadFailed(int errorCode, String description, String failingUrl) {
                Log.e(TAG, "worldLoadFailed: url: " + failingUrl + " " + description);
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    this.saveScreenCaptureToExternalStorage(ARActivity.this.screenCapture);
                } else {
                    Toast.makeText(this, "Please allow access to external storage, otherwise the screen capture can not be saved.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public ILocationProvider getLocationProvider(final LocationListener locationListener) {
        return new LocationProvider(this, locationListener);
    }

    @Override
    public float getInitialCullingDistanceMeters() {
        // you need to adjust this in case your POIs are more than 50km away from user here while loading or in JS code (compare 'AR.context.scene.cullingDistance')
        return ArchitectViewHolderInterface.CULLING_DISTANCE_DEFAULT_METERS;
    }

    @Override
    protected boolean hasGeo() {
        return EXTRAS_KEY_ACTIVITY_GEO;
    }

    @Override
    protected boolean hasIR() {
        return EXTRAS_KEY_ACTIVITY_IR;
    }

    @Override
    protected boolean hasInstant() {
        return EXTRAS_KEY_ACTIVITY_INSTANT;
    }

    @Override
    protected CameraSettings.CameraPosition getCameraPosition() {
        return CameraSettings.CameraPosition.DEFAULT;
    }

    protected void saveScreenCaptureToExternalStorage(Bitmap screenCapture) {
        if ( screenCapture != null ) {
            // store screenCapture into external cache directory
            final File screenCaptureFile = new File(Environment.getExternalStorageDirectory().toString(), "screenCapture_" + System.currentTimeMillis() + ".jpg");

            // 1. Save bitmap to file & compress to jpeg. You may use PNG too
            try {

                final FileOutputStream out = new FileOutputStream(screenCaptureFile);
                screenCapture.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

                // 2. create send intent
                final Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpg");
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(screenCaptureFile));

                // 3. launch intent-chooser
                final String chooserTitle = "Share Snaphot";
                ARActivity.this.startActivity(Intent.createChooser(share, chooserTitle));

            } catch (final Exception e) {
                // should not occur when all permissions are set
                ARActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // show toast message in case something went wrong
                        Toast.makeText(ARActivity.this, "Unexpected error, " + e, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
