package com.example.gerenciamentodesalas.ui.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.TinyDB;
import com.example.gerenciamentodesalas.dao.SalasDAO;
import com.example.gerenciamentodesalas.model.Constants;
import com.example.gerenciamentodesalas.model.Event;
import com.example.gerenciamentodesalas.model.Sala;
import com.example.gerenciamentodesalas.model.Usuario;
import com.example.gerenciamentodesalas.service.HttpRequest;
import com.example.gerenciamentodesalas.ui.adapter.ListaSalasAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListaSalasActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    TinyDB tinyDB;
    String jsonSalasString, nomeOrganizacao, idOrganizacao;
    ListView listaDeSalas;
    TextView textLoading;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_salas);
        tinyDB = new TinyDB(getApplicationContext());
        progressBar = findViewById(R.id.progressBar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textLoading = findViewById(R.id.textViewLoadingSalas);
        drawer = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        TextView navNome = headerView.findViewById(R.id.nav_usuario);
        TextView navOrg = headerView.findViewById(R.id.nav_org);
        TextView navEmail = headerView.findViewById(R.id.nav_email);
        String nomeUser = tinyDB.getObject("usuario", Usuario.class).getNome();
        navNome.setText(nomeUser);
        navOrg.setText(tinyDB.getObject("usuario", Usuario.class).getIdOrganizacao().getNome());
        navEmail.setText(tinyDB.getObject("usuario", Usuario.class).getEmail());
        toggle.syncState();
        listaDeSalas = findViewById(R.id.lista_salas_listview);
        Resources resources = getResources();
        String url = resources.getString(R.string.ip);
        Usuario usuario = tinyDB.getObject("usuario", Usuario.class);
        nomeOrganizacao = tinyDB.getString(usuario.getIdOrganizacao().getNome());
        int idOrg = usuario.getIdOrganizacao().getId();
        idOrganizacao =  String.valueOf(idOrg);

        try {
            pegarSalas(idOrganizacao, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    public void onStop(){
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                tinyDB.clear();
                Intent intent = new Intent(ListaSalasActivity.this, LoginActivity.class);
                ListaSalasActivity.this.startActivity(intent);
                ListaSalasActivity.this.finish();
        }

        return true;
    }
    public void pegarSalas(String idOrganizacao, String url) {
        System.out.println("pegandosalas");
        Map<String, String> params = new HashMap<String, String>();
        Resources resources = getResources();
        String auth = resources.getString(R.string.auth);
        params.put("authorization", auth);
        params.put("idOrganizacao", idOrganizacao);
        new HttpRequest(ListaSalasActivity.this, params, url + "sala/getsalas", "GET", "getSalas").doRequest();
        System.out.println("salaspegadas");
    }

    @Subscribe
    public void customEventReceived(Event event){
        if (event.getEventName().equals("getSalas" + Constants.eventSuccessLabel)) {
            final Intent intent = new Intent(ListaSalasActivity.this, CalendarioAgendamentoActivity.class);
            jsonSalasString = event.getEventMsg();
            tinyDB.putString("jsonSalasStr", jsonSalasString);
            textLoading.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            final List<Sala> salas = new SalasDAO().lista(this);
            listaDeSalas.setAdapter(new ListaSalasAdapter(salas, ListaSalasActivity.this));
            listaDeSalas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView salaEscolha = view.findViewById(R.id.textViewSalas);
                    String salaEscolhida = salaEscolha.getText().toString();
                    Sala sala = salas.get(position);
                    tinyDB.putObject("salaEscolhida", sala);
                    ListaSalasActivity.this.startActivity(intent);
                }
            });
        }
        else if (event.getEventName().startsWith("getSalas" + Constants.eventErrorLabel)) {
            textLoading.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            Snackbar snackbar = Snackbar
                    .make(getCurrentFocus(), "Falha ao carregar as salas", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Tentar novamente", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ListaSalasActivity.this.recreate();
                        }
                    });

            snackbar.show();
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