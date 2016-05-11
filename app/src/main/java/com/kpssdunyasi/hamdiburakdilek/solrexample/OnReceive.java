package com.kpssdunyasi.hamdiburakdilek.solrexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.TextView;

public class OnReceive extends AppCompatActivity {
    TextView kategoriText, pamaciText, pozetiText, uniText;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_receive);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        kategoriText = (TextView) findViewById(R.id.kategori);
        pamaciText = (TextView) findViewById(R.id.pamaci);
        pozetiText = (TextView) findViewById(R.id.pozeti);
        uniText = (TextView) findViewById(R.id.uni);
        String padi, kategori, pamaci, pozeti, uni;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                padi = null;
                kategori = null;
                pamaci = null;
                pozeti = null;
                uni = null;
            } else {
                padi = extras.getString("padi");
                kategori = extras.getString("kategori");
                pamaci = extras.getString("pamaci");
                pozeti = extras.getString("pozeti");
                uni = extras.getString("uni");
            }
        } else {
            padi = (String) savedInstanceState.getSerializable("padi");
            kategori = (String) savedInstanceState.getSerializable("kategori");
            pamaci = (String) savedInstanceState.getSerializable("pamaci");
            pozeti = (String) savedInstanceState.getSerializable("pozeti");
            uni = (String) savedInstanceState.getSerializable("uni");
        }
        getSupportActionBar().setTitle(padi);
        kategoriText.setText(Html.fromHtml(kategori));
        pamaciText.setText(Html.fromHtml(pamaci));
        pozetiText.setText(Html.fromHtml(pozeti));
        uniText.setText(Html.fromHtml(uni));
    }
}

