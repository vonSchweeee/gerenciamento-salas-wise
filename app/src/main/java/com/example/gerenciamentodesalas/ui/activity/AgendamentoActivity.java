package com.example.gerenciamentodesalas.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gerenciamentodesalas.ItemClickListener;
import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.TinyDB;
import com.example.gerenciamentodesalas.model.AlocacaoSala;
import com.example.gerenciamentodesalas.model.Constants;
import com.example.gerenciamentodesalas.model.Event;
import com.example.gerenciamentodesalas.model.Sala;
import com.example.gerenciamentodesalas.model.Usuario;
import com.example.gerenciamentodesalas.service.HttpRequest;
import com.example.gerenciamentodesalas.ui.adapter.AgendamentoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private ItemClickListener onItemClickListener;

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
        final Intent intentInfoAgendamento = new Intent(AgendamentoActivity.this, InfoAgendamentoActivity.class);
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
        Calendar cal = Calendar.getInstance();
        cal.setTime(dataRaw);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date dataFim = cal.getTime();
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
                System.out.println(true);
                    tinyDB.remove("modoAgendamento");
                    tinyDB.remove("alocacaoSelecionada");
                    tinyDB.putString("modoAgendamento", "criar");
                AgendamentoActivity.this.startActivity(intentInfoAgendamento);
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
        final Intent intentInfoAgendamento = new Intent(AgendamentoActivity.this, InfoAgendamentoActivity.class);
        if (event.getEventName().equals("getAlocacoes" + Constants.eventSuccessLabel)) {
            tinyDB = new TinyDB(getApplicationContext());
            final Gson gson = new Gson();
            List<AlocacaoSala> listaAlocacaoSalas = new ArrayList<AlocacaoSala>();
            JSONArray alocacoesArray = null;
            System.out.println(event.getEventMsg());
            try {
                alocacoesArray = new JSONArray(event.getEventMsg());
                for(int i=0;i<alocacoesArray.length();i++) {
                    AlocacaoSala alocacao = gson.fromJson(alocacoesArray.getJSONObject(i).toString(), AlocacaoSala.class);
                    listaAlocacaoSalas.add(i, alocacao);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            final RecyclerView recyclerViewAlocacao = findViewById(R.id.lista_alocacao_listview);
            RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerViewAlocacao.setLayoutManager(layout);
            AgendamentoAdapter adapter = new AgendamentoAdapter(listaAlocacaoSalas, this);
            final JSONArray finalAlocacoesArray = alocacoesArray;
            adapter.setOnItemClickListener(new ItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    try {
                        AlocacaoSala alocacao = gson.fromJson(finalAlocacoesArray.getJSONObject(position).toString(), AlocacaoSala.class);
                        tinyDB.remove("modoAgendamento");
                        tinyDB.remove("alocacaoSelecionada");
                        tinyDB.putObject("alocacaoSelecionada", alocacao);
                        tinyDB.putString("modoAgendamento", "alterar");
                        AgendamentoActivity.this.startActivity(intentInfoAgendamento);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            recyclerViewAlocacao.setAdapter(adapter);
            final FloatingActionButton fabRemAlocacao = findViewById(R.id.fabRemAlocacao);
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
                    adapter.setOnItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            try {
                                AlocacaoSala alocacao = gson.fromJson(finalAlocacoesArray.getJSONObject(position).toString(), AlocacaoSala.class);
                                    tinyDB.remove("modoAgendamento");
                                    tinyDB.remove("alocacaoSelecionada");
                                    tinyDB.putObject("alocacaoSelecionada", alocacao);
                                tinyDB.putString("modoAgendamento", "excluir");
                                AgendamentoActivity.this.startActivity(intentInfoAgendamento);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    recyclerViewAlocacao.setAdapter(adapter);
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
