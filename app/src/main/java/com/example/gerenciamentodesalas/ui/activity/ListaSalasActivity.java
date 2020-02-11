package com.example.gerenciamentodesalas.ui.activity;

import android.content.Context;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.dao.SalasDAO;
import com.example.gerenciamentodesalas.model.Sala;
import com.example.gerenciamentodesalas.service.FileWritterService;
import com.example.gerenciamentodesalas.service.get.HttpServiceGetSalas;
import com.example.gerenciamentodesalas.ui.adapter.ListaSalasAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.List;


public class ListaSalasActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_salas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final ListView listaDeSalas = findViewById(R.id.lista_salas_listview);
        final Intent intent = new Intent(ListaSalasActivity.this, CalendarioAgendamentoActivity.class);
        SharedPreferences sp = getSharedPreferences(LoginActivity.USER_PREFERENCE, MODE_PRIVATE);
        Resources resources = getResources();
        String ip=resources.getString(R.string.ip);
        String nomeOrganizacao = sp.getString("nomeOrganizacao", null);
        String idOrganizacao = sp.getString("idOrganizacao", null);
        String jsonSalasString = null;
        try {
            jsonSalasString = new HttpServiceGetSalas(idOrganizacao, ip).execute().get();
            intent.putExtra("jsonSalas", jsonSalasString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileWritterService fileWritterService = new FileWritterService();
        boolean arquivoExiste = fileWritterService.arquivoExiste(ListaSalasActivity.this, nomeOrganizacao + "_salas.json");
        if(arquivoExiste) {
            String jsonSalasLocalStr = fileWritterService.lerArquivo(ListaSalasActivity.this, nomeOrganizacao + "_salas.json");
            if(! jsonSalasLocalStr.equals(jsonSalasString)) {
                boolean arquivoDeletado = fileWritterService.arquivoDeletado(ListaSalasActivity.this, nomeOrganizacao + "_salas.json");
                if (arquivoDeletado) {
                    boolean isFileCreated = fileWritterService.criarArquivo(ListaSalasActivity.this, nomeOrganizacao + "_salas.json", jsonSalasString);
                    if (isFileCreated) {
                        System.out.println("JSON das Salas criado com sucesso.");
                    } else {
                        System.out.println("Falha ao atualizar o JSON das salas.");
                    }
                }
            }
        } else {
            boolean isFileCreated = fileWritterService.criarArquivo(ListaSalasActivity.this, nomeOrganizacao + "_salas.json", jsonSalasString);
            if (isFileCreated) {
                System.out.println("JSON das Salas criado com sucesso.");
            } else {
                System.out.println("Falha ao criar o JSON das salas.");
            }
        }
        List<Sala> salas = new SalasDAO().lista(nomeOrganizacao, this);
        listaDeSalas.setAdapter(new ListaSalasAdapter(salas, ListaSalasActivity.this));
        listaDeSalas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView salaEscolha = view.findViewById(R.id.textViewSalas);
                Object listItem = listaDeSalas.getItemAtPosition(position);
                String salaEscolhida = salaEscolha.getText().toString();
                intent.putExtra("position", position);
                intent.putExtra("sala", salaEscolhida);
                ListaSalasActivity.this.startActivity(intent);
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                SharedPreferences.Editor editorUser = getSharedPreferences(LoginActivity.USER_PREFERENCE, MODE_PRIVATE).edit();
                editorUser.clear();
                editorUser.apply();
                Intent intent = new Intent(ListaSalasActivity.this, LoginActivity.class);
                ListaSalasActivity.this.startActivity(intent);
        }

        return true;
    }
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
            super.onBackPressed();
    }

}