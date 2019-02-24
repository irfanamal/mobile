package com.bebas.jagalah;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuranFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static String surat = null;
    private static String isi_ayat = null;
    private static int no_surat = 1;
    private static String[] daftar_surat;
    private Thread network;

    public QuranFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_quran, container, false);

        Spinner dropdown = (Spinner) RootView.findViewById(R.id.daftar_surah);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, daftar_surat);
        dropdown.setOnItemSelectedListener(this);
        dropdown.setAdapter(adapter);

        List<String> list = new ArrayList<String>();
        for (int i=1; i<=7; i++) {
            list.add(Integer.toString(i));
        }

        Spinner ayat = (Spinner) RootView.findViewById(R.id.no_ayat);
        ArrayAdapter<String> no_ayat = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, list);
        no_ayat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ayat.setAdapter(no_ayat);
        ayat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                doInBackground2(no_surat, position + 1);
                try {
                    network.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TextView tview = (TextView)getActivity().findViewById(R.id.isi_ayat);
                tview.setText(isi_ayat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Inflate the layout for this fragment
        return RootView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        doInBackground(position + 1);
        no_surat = position+1;
        try {
            network.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<String> list = new ArrayList<String>();
        for (int i=1; i<=Integer.parseInt(surat); i++) {
            list.add(Integer.toString(i));
        }

        Spinner ayat = (Spinner) getActivity().findViewById(R.id.no_ayat);
        ArrayAdapter<String> no_ayat = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, list);
        no_ayat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ayat.setAdapter(no_ayat);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    public void doInBackground(final int numb) {
        network = new Thread() {
            public void run() {
                FatimahAPI api = new FatimahAPI();
                try {
                    surat = api.getSurah(numb);
                } catch (IOException e) {
                    Log.d("IOException", "IOException");
                } catch (JSONException e) {
                    Log.d("JSONException", "JSONException");
                }
            }
        };
        network.start();
    }

    public void doInBackground2(final int numbSurat, final int numbAyat) {
        network = new Thread() {
            public void run() {
                FatimahAPI api = new FatimahAPI();
                try {
                    isi_ayat = api.getAyat(numbSurat, numbAyat);
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