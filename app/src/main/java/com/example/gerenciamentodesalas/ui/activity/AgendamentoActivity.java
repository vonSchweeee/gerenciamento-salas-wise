package com.example.gerenciamentodesalas.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.TinyDB;
import com.example.gerenciamentodesalas.model.AlocacaoSala;
import com.example.gerenciamentodesalas.model.Constants;
import com.example.gerenciamentodesalas.model.Event;
import com.example.gerenciamentodesalas.model.Sala;
import com.example.gerenciamentodesalas.model.Usuario;
import com.example.gerenciamentodesalas.service.FileWritterService;
import com.example.gerenciamentodesalas.service.HttpRequest;
import com.example.gerenciamentodesalas.service.get.HttpServiceGetAlocacoesData;
import com.example.gerenciamentodesalas.ui.adapter.AgendamentoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgendamentoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    AlertDialog.Builder builder;
    TinyDB tinyDB;
    String dataStr;
    String fimDiaEscolhido;
    String ip;
    String idSala;
    Boolean stopped = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento);
        tinyDB = new TinyDB(getApplicationContext());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View headerView = navigationView.getHeaderView(0);
        Usuario usuario = tinyDB.getObject("usuario", Usuario.class);
        TextView navNome =  headerView.findViewById(R.id.nav_usuario);
        TextView navOrg = headerView.findViewById(R.id.nav_org);
        TextView navEmail = headerView.findViewById(R.id.nav_email);
        navNome.setText(usuario.getNome());
        navOrg.setText(usuario.getIdOrganizacao().getNome());
        navEmail.setText(usuario.getEmail());
        final Intent intentCriacaoAgendamento = new Intent(AgendamentoActivity.this, CriacaoAgendamentoActivity.class);
        Resources resources = getResources();
        ip = resources.getString(R.string.ip);
        SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoDataBr = new SimpleDateFormat("dd/MM/yyyy");
        TextView textViewData = findViewById(R.id.textViewDataEscolhida);
        final Sala sala = tinyDB.getObject("salaEscolhida", Sala.class);
        String data = tinyDB.getString("dataEscolhida");
        Date dataRaw = null;
        try {
            dataRaw = formatoDataBr.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateTime dtOrg = new DateTime(dataRaw);
        DateTime dtPlusOne = dtOrg.plusDays(1);
        Date dataFim = dtPlusOne.toDate();
        fimDiaEscolhido = formatoData.format(dataFim);
        dataStr = formatoData.format(dataRaw);
        final TextView textSala = findViewById(R.id.textNomeSala);
        textSala.setText(sala.getNome());
        textViewData.setText(data);
        idSala = null;
        try {
            idSala = String.valueOf(sala.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final FloatingActionButton fabAddAlocacao = findViewById(R.id.fabAddAlocacao);

        builder = new AlertDialog.Builder(AgendamentoActivity.this);
        fabAddAlocacao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AgendamentoActivity.this.startActivity(intentCriacaoAgendamento);
            }
        });
        try {
            getAlocacoes(ip + "alocacao/getalocacoesbysaladata", idSala, dataStr, fimDiaEscolhido);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                tinyDB.clear();
                Intent intent = new Intent(AgendamentoActivity.this, LoginActivity.class);
                AgendamentoActivity.this.startActivity(intent);
        }
        return true;
    }
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    public void onStop(){
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    public void onPause() {
        super.onPause();
        stopped = true;
    }

    public void onResume(){
        super.onResume();
        if (stopped)
            getAlocacoes(ip + "alocacao/getalocacoesbysaladata", idSala, dataStr, fimDiaEscolhido);
        stopped = false;
    }

    public void getAlocacoes( String url, String idSala, String diaEscolhido, String fimDiaEscolhido){
        System.out.println("pegandosalas");
        Map<String, String> params = new HashMap<String, String>();
        Resources resources = getResources();
        String auth = resources.getString(R.string.auth);
        params.put("authorization", auth);
        params.put("idSala", idSala);
        params.put("diaEscolhido", diaEscolhido);
        params.put("fimDiaEscolhido", fimDiaEscolhido);
        new HttpRequest(AgendamentoActivity.this, params, url, "GET", "getAlocacoes").doRequest();
    }

    @Subscribe
    public void customEventReceived(Event event){
        if (event.getEventName().equals("getAlocacoes" + Constants.eventSuccessLabel)) {
            final Intent intentExcluirAgendamento = new Intent(AgendamentoActivity.this, ExcluirAgendamentoActivity.class);
            tinyDB = new TinyDB(getApplicationContext());
            final Gson gson = new Gson();
            List<AlocacaoSala> listaSalas = new ArrayList<AlocacaoSala>();
            JSONArray alocacoesArray = null;
            System.out.println(event.getEventMsg());
            try {
                alocacoesArray = new JSONArray(event.getEventMsg());
                for(int i=0;i<alocacoesArray.length();i++) {
                    AlocacaoSala alocacao = gson.fromJson(alocacoesArray.getJSONObject(i).toString(), AlocacaoSala.class);
                    listaSalas.add(i, alocacao);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            final ListView listaDeAlocacao = findViewById(R.id.lista_alocacao_listview);
            listaDeAlocacao.setAdapter(new AgendamentoAdapter(listaSalas, this));
            final FloatingActionButton fabRemAlocacao = findViewById(R.id.fabRemAlocacao);

            final JSONArray finalAlocacoesArray = alocacoesArray;
            fabRemAlocacao.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    builder.setMessage("Selecione uma alocação para desagendar").setTitle("Remover alocação");
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    listaDeAlocacao.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            try {
                                AlocacaoSala alocacao = gson.fromJson(finalAlocacoesArray.getJSONObject(position).toString(), AlocacaoSala.class);
                                tinyDB.putObject("alocacaoSelecionada", alocacao);
                                AgendamentoActivity.this.startActivity(intentExcluirAgendamento);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            if (event.getEventName().startsWith("getAlocacoes" + Constants.eventErrorLabel)) {

            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }
}
