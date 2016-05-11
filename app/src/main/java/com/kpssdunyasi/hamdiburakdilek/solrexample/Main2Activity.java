package com.kpssdunyasi.hamdiburakdilek.solrexample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    SearchView searchView;
    private ProgressDialog pDialog;
    ListView lv;
    public ListAdapter adapter;

    private static final String TAG_PADI = "Proje_Adi";
    private static final String TAG_UNI = "Universite";
    private static final String TAG_POZETI = "Projenin_Ozeti";
    private static final String TAG_PAMACI = "Projenin_Amaci";
    private static final String TAG_KATEGORI = "Kategori";
    JSONArray verilerJ = null;

    final List<String> projeadlari = new ArrayList<String>();
    final List<String> universiteler = new ArrayList<String>();
    final List<String> ozetler = new ArrayList<String>();
    final List<String> amaclar = new ArrayList<String>();
    List<String> PAU = new ArrayList<String>();
    final List<String> kategoriler = new ArrayList<String>();
    JSONArray jArray = null;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        lv = (ListView) findViewById(R.id.listView);

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String q = newText;
                String en = null;
                try {
                    en = URLEncoder.encode(q, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String url = "http://10.0.3.2:8983/solr/GBYFdata/select?q=*%3A*&fq=" + en + "&wt=json&indent=true";
                System.out.println(url);
                new LoadData(url).execute();
                return false;
            }
        });
       // new LoadData("http://10.0.3.2:8983/solr/GBYFdata/select?q=*%3A*&wt=json&indent=true").execute();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Main2Activity.this, OnReceive.class);
                i.putExtra("padi", projeadlari.get(position));
                i.putExtra("pozeti",ozetler.get(position));
                i.putExtra("uni",universiteler.get(position));
                i.putExtra("pamaci",amaclar.get(position));
                i.putExtra("kategori",kategoriler.get(position));
                startActivity(i);
            }
        });
        searchView.requestFocus();
    }

    private class LoadData extends AsyncTask<Void, Void, Void> {
        String url;

        LoadData(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Main2Activity.this);
            pDialog.setMessage("Veriler Yükleniyor...");
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            projeadlari.clear();
            universiteler.clear();
            ozetler.clear();
            amaclar.clear();
            kategoriler.clear();
            PAU.clear();
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    jsonObj = jsonObj.getJSONObject("response");
                    verilerJ = jsonObj.getJSONArray("docs");

                    for (int g = 0; g < verilerJ.length(); g++) {
                        JSONObject json_data = verilerJ.getJSONObject(g);
                        String padi = json_data.getString(TAG_PADI).substring(2, json_data.getString(TAG_PADI).length() - 2);
                        String universite = json_data.getString(TAG_UNI).substring(2, json_data.getString(TAG_UNI).length() - 2);
                        String pozeti = json_data.getString(TAG_POZETI).substring(2, json_data.getString(TAG_POZETI).length() - 2);
                        String pamaci = json_data.getString(TAG_PAMACI).substring(2, json_data.getString(TAG_PAMACI).length() - 2);
                        String kategori = json_data.getString(TAG_KATEGORI).substring(2, json_data.getString(TAG_KATEGORI).length() - 2);

                        projeadlari.add(padi);
                        universiteler.add(universite);
                        ozetler.add(pozeti);
                        amaclar.add(pamaci);
                        kategoriler.add(kategori);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < projeadlari.size(); i++) {
                    PAU.add("\n" + projeadlari.get(i) + "\n" + universiteler.get(i) + "\n");
                }
            } else {
                Log.e("ServiceHandler", "Adresten veri alınamadı");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            adapter = new ArrayAdapter<String>(Main2Activity.this, android.R.layout.simple_list_item_1, PAU);
            lv.setAdapter(adapter);
        }
    }
}

