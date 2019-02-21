package com.bebas.jagalah;


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
        TextView subuh = (TextView)RootView.findViewById(R.id.waktu_subuh);
        TextView dzuhur = (TextView)RootView.findViewById(R.id.waktu_dzuhur);
        TextView ashar = (TextView)RootView.findViewById(R.id.waktu_ashar);
        TextView maghrib = (TextView)RootView.findViewById(R.id.waktu_maghrib);
        TextView isya = (TextView)RootView.findViewById(R.id.waktu_isya);
        if (waktu_subuh == null) {
            doInBackground();
            try {
                network.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String subuhStr = subuh.getText().toString() + waktu_subuh;
        String dzuhurStr = dzuhur.getText().toString() + waktu_dzuhur;
        String asharStr = ashar.getText().toString() + waktu_ashar;
        String maghribStr = maghrib.getText().toString() + waktu_maghrib;
        String isyaStr = isya.getText().toString() + waktu_isya;
        subuh.setText(subuhStr);
        dzuhur.setText(dzuhurStr);
        ashar.setText(asharStr);
        maghrib.setText(maghribStr);
        isya.setText(isyaStr);
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
