package com.bebas.jagalah;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class JadwalFragment extends Fragment {

    private static String waktu_subuh = null;
    private static String waktu_dzuhur = null;
    private static String waktu_ashar = null;
    private static String waktu_maghrib = null;
    private static String waktu_isya = null;
    private Thread network;

    public JadwalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_jadwal, container, false);
        TextView header = (TextView)RootView.findViewById(R.id.jadwal_sholat);
        TextView subuh = (TextView)RootView.findViewById(R.id.waktu_subuh);
        TextView dzuhur = (TextView)RootView.findViewById(R.id.waktu_dzuhur);
        TextView ashar = (TextView)RootView.findViewById(R.id.waktu_ashar);
        TextView maghrib = (TextView)RootView.findViewById(R.id.waktu_maghrib);
        TextView isya = (TextView)RootView.findViewById(R.id.waktu_isya);

        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean connected = activeNetwork != null &&
                activeNetwork.isConnected();

        if (connected) {
            if (waktu_subuh == null) {
                doInBackground();
                try {
                    network.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
        } else {
            if (waktu_subuh == null) {
                header.setText("No internet connection");
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
                    JSONObject jadwal = api.retrieveJadwal("bandung");
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
}
