package com.kpssdunyasi.hamdiburakdilek.solrexample;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main3Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SearchView searchView;
    private ProgressDialog pDialog;
    ListView lv;
    public ListAdapter adapter;
    String uni = "*";
    String kat = "*";
    Set<String> uniSet = new HashSet<String>();
    Set<String> katSet = new HashSet<String>();
    NavigationView navigationView;

    private static final String TAG_PADI = "Proje_Adi";
    private static final String TAG_UNI = "Universite";
    private static final String TAG_POZETI = "Projenin_Ozeti";
    private static final String TAG_PAMACI = "Projenin_Amaci";
    private static final String TAG_KATEGORI = "Kategori";
    JSONArray verilerJ = null;

    final List<String> projeadlari = new ArrayList<String>();
    final List<String> fKategoriler = new ArrayList<String>();
    final List<Integer> fKategoriSayilar = new ArrayList<>();
    final List<String> fUniversiteler = new ArrayList<String>();
    final List<Integer> fUniversiteSayilar = new ArrayList<>();
    final List<String> universiteler = new ArrayList<String>();
    final List<String> ozetler = new ArrayList<String>();
    final List<String> amaclar = new ArrayList<String>();
    List<String> PAU = new ArrayList<String>();
    String en = null;
    String q;
    final List<String> kategoriler = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        lv = (ListView) findViewById(R.id.listView);
        FloatingActionButton filter = (FloatingActionButton) navigationView.findViewById(R.id.fabbFilter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uniSet.isEmpty()) {
                    uni = "*";
                } else {
                    uni = TextUtils.join("OR", uniSet);
                    try {
                        uni = URLEncoder.encode(uni, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                if (katSet.isEmpty()) {
                    kat = "*";
                } else {
                    kat = TextUtils.join("OR", katSet);
                    try {
                        kat = URLEncoder.encode(kat, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                String url = "http://10.0.3.2:8983/solr/GBYFdata/select?q=" + en + "&fq=(Universite%3A" + uni + ")AND(Kategori%3A" + kat + ")&wt=json&indent=true&facet=true&facet.field=Universite&facet.field=Kategori&facet.mincount=1";
                System.out.println(url);
                new LoadData(url,q).execute();
            }
        });

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                q = newText;
                if (q != "") {
                try {
                    en = URLEncoder.encode(q, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String url = "http://10.0.3.2:8983/solr/GBYFdata/select?q=*%3A*&fq=" + en + "&wt=json&indent=true&facet=true&facet.field=Universite&facet.field=Kategori&facet.mincount=1";

                new LoadData(url,q).execute();
                }
                return false;
            }
        });

        // new LoadData("http://10.0.3.2:8983/solr/GBYFdata/select?q=*%3A*&wt=json&indent=true").execute();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Main3Activity.this, OnReceive.class);
                i.putExtra("padi", projeadlari.get(position));
                i.putExtra("pozeti", ozetler.get(position));
                i.putExtra("uni", universiteler.get(position));
                i.putExtra("pamaci", amaclar.get(position));
                i.putExtra("kategori", kategoriler.get(position));
                startActivity(i);
            }
        });
        searchView.requestFocus();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String titleNormal = item.getTitle().toString();
        String title = titleNormal.substring(0, titleNormal.length() - 5).replaceAll("\\s+", " ").trim();
        System.out.println(title);
        if (item.isChecked() != true) {
            if (fUniversiteler.contains(title)) {
                uniSet.add("\"" + title + "\"");
            } else {

                katSet.add("\"" + title + "\"");
            }
            item.setChecked(true);
        } else {
            if (fUniversiteler.contains(title)) {
                uniSet.remove("\"" + title + "\"");
            } else {
                katSet.remove("\"" + title + "\"");

            }
            item.setChecked(false);
        }

/*

            try {
                uni = URLEncoder.encode("\"" + title + "\"", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // url = "http://10.0.3.2:8983/solr/GBYFdata/select?q=" + en + "&fq=Universite+%3A+" + uni + "&fq=Kategori+%3A+" + kat + "&wt=json&indent=true&facet=true&facet.field=Universite&facet.field=Kategori&facet.mincount=1";
            // http://localhost:8983/solr/GBYFdata/select?q=de&fq=Universite+%3ADokuz+Eyl%C3%BCl+%C3%9Cniversitesi&wt=json&indent=true&facet=true&facet.field=Universite


            System.out.println("Universite");

            try {
                kat = URLEncoder.encode("\"" + title + "\"", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        url = "http://10.0.3.2:8983/solr/GBYFdata/select?q=" + en + "&fq=(Universite%3A" + uni + ")AND+Kategori%3A+" + kat + "&wt=json&indent=true&facet=true&facet.field=Universite&facet.field=Kategori&facet.mincount=1";
        //
        System.out.println(url);
       //
*/


        return false;
    }


    private class LoadData extends AsyncTask<Void, Void, Void> {
        String url;
        String q;

        LoadData(String url, String q) {
            this.q = q;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Main3Activity.this);
            pDialog.setMessage("Veriler Yükleniyor...");


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
            fUniversiteSayilar.clear();
            fUniversiteler.clear();
            fKategoriSayilar.clear();
            fKategoriler.clear();
            PAU.clear();
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject jsonObjresponse = jsonObj.getJSONObject("response");
                    verilerJ = jsonObjresponse.getJSONArray("docs");
                    JSONObject jsonObjfacet = jsonObj.getJSONObject("facet_counts");
                    JSONObject fJSONObj = jsonObjfacet.getJSONObject("facet_fields");
                    JSONArray fJSONArrayUniversite = fJSONObj.getJSONArray("Universite");
                    JSONArray fJSONArrayKategori = fJSONObj.getJSONArray("Kategori");


                    for (int g = 0; g < verilerJ.length(); g++) {
                        JSONObject json_data = verilerJ.getJSONObject(g);
                        String padi = json_data.getString(TAG_PADI).substring(2, json_data.getString(TAG_PADI).length() - 2);
                        String universite = json_data.getString(TAG_UNI).substring(2, json_data.getString(TAG_UNI).length() - 2);
                        String pozeti = json_data.getString(TAG_POZETI).substring(2, json_data.getString(TAG_POZETI).length() - 2);
                        String pamaci = json_data.getString(TAG_PAMACI).substring(2, json_data.getString(TAG_PAMACI).length() - 2);
                        String kategori = json_data.getString(TAG_KATEGORI).substring(2, json_data.getString(TAG_KATEGORI).length()  - 2);


                        projeadlari.add(padi);

                        String[] list = universite.split(q);
                        String a = TextUtils.join("<strong><font color='blue'>"+q+"</font></strong>", list);
                        universiteler.add(a);

                        list = pozeti.split(q);
                        a = TextUtils.join("<strong><font color='blue'>"+q+"</font></strong>", list);
                        ozetler.add(a);

                        list = pamaci.split(q);
                        a = TextUtils.join("<strong><font color='blue'>"+q+"</font></strong>", list);
                        amaclar.add(a);

                        list = kategori.split(q);
                        a = TextUtils.join("<strong><font color='blue'>"+q+"</font></strong>", list);
                        kategoriler.add(a);
                    }
                    for (int g = 0; g < fJSONArrayKategori.length(); g = g + 2) {
                        String fkategori = fJSONArrayKategori.getString(g);
                        int fkategorisayi = fJSONArrayKategori.getInt(g + 1);
                        fKategoriler.add(fkategori);
                        fKategoriSayilar.add(fkategorisayi);
                        System.out.println(fkategori + "  " + fkategorisayi);
                    }
                    for (int g = 0; g < fJSONArrayUniversite.length(); g = g + 2) {
                        String funiversite = fJSONArrayUniversite.getString(g);
                        int funiversitesayi = fJSONArrayUniversite.getInt(g + 1);
                        fUniversiteler.add(funiversite);
                        fUniversiteSayilar.add(funiversitesayi);
                        System.out.println(funiversite + "  " + funiversitesayi);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < projeadlari.size(); i++) {
                    PAU.add("\n" + projeadlari.get(i) + "<br>" + universiteler.get(i) + "\n");
                }
            } else {
                Log.e("ServiceHandler", "Adresten veri alınamadı");
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            katSet.clear();
            uniSet.clear();
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            adapter = new ArrayAdapter<String>(Main3Activity.this, android.R.layout.simple_list_item_1, PAU){
                @Override
                public String getItem(int position)
                {
                    return Html.fromHtml(PAU.get(position)).toString();
                }
            };
            lv.setAdapter(adapter);
            Menu nav_menu = navigationView.getMenu();
            nav_menu.clear();
            if (!fUniversiteler.isEmpty()) {
                SubMenu universitelerSM = nav_menu.addSubMenu("Üniversiteler");
                MenuItem a;
                for (int i = 0; i < fUniversiteSayilar.size(); i++) {
                    a = universitelerSM.add(fUniversiteler.get(i) + "       " + fUniversiteSayilar.get(i));
                }

            }
            SubMenu kategorilerSM = nav_menu.addSubMenu("Kategoriler");
            if (!fKategoriSayilar.isEmpty()) {

                MenuItem a;
                for (int i = 0; i < fKategoriSayilar.size(); i++) {
                    kategorilerSM.add(fKategoriler.get(i) + "       " + fKategoriSayilar.get(i));
                }

            }

        }
    }
}
