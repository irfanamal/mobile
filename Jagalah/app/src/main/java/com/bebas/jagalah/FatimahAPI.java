package com.bebas.jagalah;

import android.support.v4.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FatimahAPI {
    private String query = "https://api.banghasan.com/sholat/format/json";
//    private JSONObject jadwal;

    public JSONObject getResult(URL url) throws IOException, JSONException {
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        BufferedReader result = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = result.readLine()) != null) {
            response.append(inputLine).append("\n");
        }
        result.close();
        return new JSONObject(response.toString());
    }

    public String getKodeKota(String mNamaKota) throws IOException, JSONException {
        String link = query + "/kota/nama/" + mNamaKota;
        System.out.println(link);
        URL url = new URL(link);
        JSONObject result = getResult(url);
        return result.getJSONArray("kota").getJSONObject(0).getString("id");
    }

    public JSONObject retrieveJadwal(String mNamaKota) throws IOException, JSONException {
        String kodeKota = getKodeKota(mNamaKota);
        Date date = Calendar.getInstance().getTime();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
        String link = query + "/jadwal/kota/" + kodeKota + "/tanggal/" + dateStr;
        URL url = new URL(link);
        JSONObject result = getResult(url);
        return result.getJSONObject("jadwal").getJSONObject("data");
    }

    public String getJadwal(JSONObject jadwal, String waktu) throws JSONException {
        String jam = null;
        switch (waktu) {
            case "subuh":
                jam = jadwal.getString("subuh");
                break;
            case "dzuhur":
                jam = jadwal.getString("dzuhur");
                break;
            case "ashar":
                jam = jadwal.getString("ashar");
                break;
            case "maghrib":
                jam = jadwal.getString("maghrib");
                break;
            case "isya":
                jam = jadwal.getString("isya");
                break;
        }
        return jam;
    }

    public String[] getAllSurah() throws IOException, JSONException {
        String link = "https://api.banghasan.com/quran/format/json/surat";
        URL url = new URL(link);
        HttpURLConnection conn = null;

        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        conn.connect();

        if (conn.getResponseCode() == 200) {
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            String[] temp = new String[114];

            JSONObject myresponse = new JSONObject(response.toString());

            for (int i=0; i<114; i++) {
                String hasil = myresponse.getJSONArray("hasil").getJSONObject(i).getString("nama");
                temp[i] = Integer.toString(i+1) + ". " + hasil;
            }

            return temp;
        }

        return null;
    }

    public String getSurah(int num) throws IOException, JSONException {
        String link = "https://api.banghasan.com/quran/format/json/surat/" + Integer.toString(num);
        URL url = new URL(link);
        HttpURLConnection conn = null;

        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        conn.connect();

        if (conn.getResponseCode() == 200) {
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            JSONObject myresponse = new JSONObject(response.toString());
            JSONObject hasil = myresponse.getJSONArray("hasil").getJSONObject(0);

            return hasil.getString("ayat");
        }

        return null;
    }

    public String getAyat(int nSurat, int nAyat) throws IOException, JSONException {
        String link = "https://api.banghasan.com/quran/format/json/surat/" + Integer.toString(nSurat) + "/ayat/" + Integer.toString(nAyat) + "/bahasa/ar";;
        URL url = new URL(link);
        HttpURLConnection conn = null;

        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        conn.connect();

        if (conn.getResponseCode() == 200) {
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            JSONObject myresponse = new JSONObject(response.toString());
            JSONObject hasil = myresponse.getJSONObject("ayat").getJSONObject("data").getJSONArray("ar").getJSONObject(0);

            return hasil.getString("teks");
        }

        return null;
    }
}
