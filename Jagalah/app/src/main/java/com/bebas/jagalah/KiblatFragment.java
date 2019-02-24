package com.bebas.jagalah;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import static android.content.Context.SENSOR_SERVICE;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;



/**
 * A simple {@link Fragment} subclass.
 */
public class KiblatFragment extends Fragment implements SensorEventListener, LocationListener  {

    private ImageView imageView;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    private static final float ALPHA = 0.03f;



    public KiblatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_kiblat, container, false);
        mSensorManager = (SensorManager)getActivity().getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        imageView = (ImageView)RootView.findViewById(R.id.compass);
        return RootView;
    }
    protected float getArah(){
        LocationManager locationManager;
        double latitude_radians;
        double longitude_radians;
        double latitudeqibla_radians;
        double longitudeqibla_radians;
        double theta;

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            },7171);
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        longitude_radians = Math.toRadians(location.getLongitude());
        latitude_radians = Math.toRadians(location.getLatitude());
        longitudeqibla_radians = Math.toRadians(39.826195);
        latitudeqibla_radians = Math.toRadians(21.422505);
        theta = atan2((sin(longitudeqibla_radians-longitude_radians)*cos(latitudeqibla_radians)),
                (cos(latitude_radians)*sin(latitudeqibla_radians) - sin(latitude_radians)*cos(latitudeqibla_radians)*cos(longitudeqibla_radians-longitude_radians)));
        return (float)Math.toDegrees(theta);
    }
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    private float[] applyLowPassFilter(float[] input, float[] output) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.5f;
        if (event.sensor == mAccelerometer) {
            //System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
            mLastAccelerometer = applyLowPassFilter(event.values.clone(), mLastAccelerometer);
        } else if (event.sensor == mMagnetometer) {
            //System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
            mLastMagnetometer = applyLowPassFilter(event.values.clone(), mLastMagnetometer);
//            mLastMagnetometer[0] = alpha*mLastMagnetometer[0]+(1-alpha)*mLastMagnetometer[0];
//            mLastMagnetometer[1] = alpha*mLastMagnetometer[1]+(1-alpha)*mLastMagnetometer[1];
//            mLastMagnetometer[2] = alpha*mLastMagnetometer[0]+(1-alpha)*mLastMagnetometer[2];
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            float azimuthIn360 = 0 ;
            float[] I = new float [9];
            SensorManager.getRotationMatrix(mR, I, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float abc;
            abc = getArah();
            float azimuthInRadians = mOrientation[0];
            if (abc < 0){
                azimuthIn360 = (azimuthIn360 + abc + 360);
            }
            else if (abc >=360){
                azimuthIn360 = (azimuthIn360 + abc - 360) ;
            }
            imageView.setRotation((float)((Math.toDegrees
                    (Math.toRadians(azimuthIn360)))%360));
            float azimuthInDegress = (float)((Math.toDegrees(azimuthInRadians)+360)%360);
            RotateAnimation animation = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);
            Log.d("jagalah",String.valueOf(azimuthInDegress));
            animation.setDuration(250);

            animation.setFillAfter(true);

            imageView.startAnimation(animation);
            mCurrentDegree = -azimuthInDegress;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
