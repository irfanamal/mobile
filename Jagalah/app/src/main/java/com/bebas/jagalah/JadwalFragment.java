package com.bebas.jagalah;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class JadwalFragment extends Fragment implements View.OnClickListener {

    private static String waktu_subuh = null;
    private static String waktu_dzuhur = null;
    private static String waktu_ashar = null;
    private static String waktu_maghrib = null;
    private static String waktu_isya = null;
    private static String cityName = null;
    private Thread network;
    private TextView header;
    private TextView subuh;
    private TextView dzuhur;
    private TextView ashar;
    private TextView maghrib;
    private TextView isya;


    public JadwalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_jadwal, container, false);
        header = (TextView)RootView.findViewById(R.id.jadwal_sholat);
        subuh = (TextView)RootView.findViewById(R.id.waktu_subuh);
        dzuhur = (TextView)RootView.findViewById(R.id.waktu_dzuhur);
        ashar = (TextView)RootView.findViewById(R.id.waktu_ashar);
        maghrib = (TextView)RootView.findViewById(R.id.waktu_maghrib);
        isya = (TextView)RootView.findViewById(R.id.waktu_isya);
        FloatingActionButton share = (FloatingActionButton)RootView.findViewById(R.id.share);
        share.setOnClickListener(this);

        ConnectivityManager cm =
                (ConnectivityManager) Objects.requireNonNull(getContext()).getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean connected = activeNetwork != null &&
                activeNetwork.isConnected();

        if (connected) {
            header.setText("Please wait");
            getLastLocation();
        } else {
            if (waktu_subuh == null) {
                header.setText("No network connection");
            } else {
                String subuhStr = getString(R.string.subuh) + waktu_subuh;
                String dzuhurStr = getString(R.string.dzuhur) + waktu_dzuhur;
                String asharStr = getString(R.string.ashar) + waktu_ashar;
                String maghribStr = getString(R.string.maghrib) + waktu_maghrib;
                String isyaStr = getString(R.string.isya) + waktu_isya;
                header.setText(getString(R.string.jadwal_sholat));
                subuh.setText(subuhStr);
                dzuhur.setText(dzuhurStr);
                ashar.setText(asharStr);
                maghrib.setText(maghribStr);
                isya.setText(isyaStr);
            }
        }

        // Inflate the layout for this fragment
        return RootView;
    }

    private void doInBackground() {
        network = new Thread() {
            public void run() {
                FatimahAPI api = new FatimahAPI();
                try {
                    JSONObject jadwal = api.retrieveJadwal(cityName);
                    waktu_subuh = api.getJadwal(jadwal, "subuh");
                    waktu_dzuhur = api.getJadwal(jadwal, "dzuhur");
                    waktu_ashar = api.getJadwal(jadwal, "ashar");
                    waktu_maghrib = api.getJadwal(jadwal, "maghrib");
                    waktu_isya = api.getJadwal(jadwal, "isya");
                } catch (IOException e) {
                    Log.d("IOException", "IOException");
                } catch (JSONException e) {
                    Log.d("JSONException", "JSONException");
                }
            }
        };
        network.start();
    }

    public void getLastLocation() {
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 7171);
        } else {
            locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        try {
                            onLocationChanged(location);
                            if (cityName == null) {
                                header.setText("City not registered");
                            } else {
                                if (waktu_subuh == null) {
                                    doInBackground();
                                    try {
                                        network.join();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (waktu_subuh == null) {
                                    header.setText("City not registered");
                                } else {
                                    String subuhStr = getString(R.string.subuh) + waktu_subuh;
                                    String dzuhurStr = getString(R.string.dzuhur) + waktu_dzuhur;
                                    String asharStr = getString(R.string.ashar) + waktu_ashar;
                                    String maghribStr = getString(R.string.maghrib) + waktu_maghrib;
                                    String isyaStr = getString(R.string.isya) + waktu_isya;
                                    header.setText(getString(R.string.jadwal_sholat));
                                    subuh.setText(subuhStr);
                                    dzuhur.setText(dzuhurStr);
                                    ashar.setText(asharStr);
                                    maghrib.setText(maghribStr);
                                    isya.setText(isyaStr);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void onLocationChanged(Location location) throws IOException {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        if (addresses.size() > 0) {
            cityName = addresses.get(0).getLocality();
        }
    }

    public void shareJadwal(View view) {
        String toBeShared = header.getText().toString() + "\n" + subuh.getText().toString() + "\n" + dzuhur.getText().toString() + "\n" + ashar.getText().toString() + "\n" + maghrib.getText().toString() + "\n" + isya.getText().toString();
        String mimeType ="text/plain";
        ShareCompat.IntentBuilder.from(getActivity()).setType(mimeType).setChooserTitle("Share jadwal").setText(toBeShared).startChooser();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share:
                shareJadwal(v);
                break;
        }
    }
}
