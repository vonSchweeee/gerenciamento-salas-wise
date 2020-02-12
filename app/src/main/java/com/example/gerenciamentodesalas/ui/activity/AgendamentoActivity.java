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
import com.example.gerenciamentodesalas.model.AlocacaoSala;
import com.example.gerenciamentodesalas.service.FileWritterService;
import com.example.gerenciamentodesalas.service.get.HttpServiceGetAlocacoesData;
import com.example.gerenciamentodesalas.ui.adapter.AgendamentoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgendamentoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View headerView = navigationView.getHeaderView(0);
        TextView navNome =  headerView.findViewById(R.id.nav_usuario);
        TextView navOrg = headerView.findViewById(R.id.nav_org);
        TextView navEmail = headerView.findViewById(R.id.nav_email);
        SharedPreferences sp = getSharedPreferences(LoginActivity.USER_PREFERENCE, MODE_PRIVATE);
        navNome.setText(sp.getString("nome", null));
        navOrg.setText(sp.getString("organizacao", null));
        navEmail.setText(sp.getString("email", null));
        Resources resources = getResources();
        final FloatingActionButton fabAddAlocacao = findViewById(R.id.fabAddAlocacao);
        final FloatingActionButton fabRemAlocacao = findViewById(R.id.fabRemAlocacao);
        final String ip = resources.getString(R.string.ip);
        final Intent intentCriacaoAgendamento = new Intent(AgendamentoActivity.this, CriacaoAgendamentoActivity.class);
        final Intent intentExcluirAgendamento = new Intent(AgendamentoActivity.this, ExcluirAgendamentoActivity.class);
        SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
        TextView textViewData = findViewById(R.id.textViewDataEscolhida);
        Date dataEscolhida = null;
        String salaEscolhida = null;
        Intent intent = getIntent();
        String sala = intent.getExtras().getString("salaEscolhida");
        final String data = intent.getExtras().getString("dataEscolhida");
        Date dataRaw = (Date) intent.getSerializableExtra("dataFormatadaEscolhida");
        String jsonSalasStr = intent.getExtras().getString("jsonSalas");
        DateTime dtOrg = new DateTime(dataRaw);
        DateTime dtPlusOne = dtOrg.plusDays(1);
        Date dataFim = dtPlusOne.toDate();
        String fimDiaEscolhido = formatoData.format(dataFim);
        String dataStr = formatoData.format(dataRaw);
        final TextView textSala = findViewById(R.id.textNomeSala);
        textSala.setText(sala);
        textViewData.setText(data);
        String retorno = null;
        final FileWritterService fileWritterService = new FileWritterService();
        String jsonAlocacoesStr = null;
        JSONArray arraySalas = null;
        int posSala = intent.getExtras().getInt("position");
        String idSala = null;
        try {
            arraySalas = new JSONArray(jsonSalasStr);
            idSala = arraySalas.getJSONObject(posSala).getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<AlocacaoSala> listaSalas = new ArrayList<AlocacaoSala>();
        String jsonSalaStr = fileWritterService.lerArquivo(AgendamentoActivity.this, "alocacoes_" + sala + "_" + data.replace("/", "-") + ".json");
        try {
            retorno = new HttpServiceGetAlocacoesData(ip, idSala, dataStr, fimDiaEscolhido).execute().get();
            boolean isFilePresent = fileWritterService.arquivoExiste(AgendamentoActivity.this, "alocacoes_" + sala + "_" + data + ".json");
            if (isFilePresent) {
                jsonAlocacoesStr = fileWritterService.lerArquivo(AgendamentoActivity.this, "alocacoes_" + sala + "_" + data.replace("/", "-") + ".json");
                if (!retorno.equals(jsonAlocacoesStr)) {
                    boolean isFileCreated = fileWritterService.criarArquivo(AgendamentoActivity.this, "alocoes_" + sala + "_" + data + ".json", retorno);
                    if (isFileCreated) {
                        System.out.println("JSON das alocações criado com sucesso.");
                    } else {
                        System.out.println("Falha ao atualizar JSON das alocações.");
                    }
                } else {
                    jsonAlocacoesStr = retorno;
                }
            } else {
                boolean isFileCreated = fileWritterService.criarArquivo(AgendamentoActivity.this, "alocacoes_" + sala + "_" + data.replace("/", "-") + ".json", retorno);
                if (isFileCreated) {
                    System.out.println("JSON das alocações criado com sucesso.");
                    jsonAlocacoesStr = retorno;
                } else {
                    System.out.println("Falha ao criar JSON das alocações.");
                }
            }
            JSONArray alocacaoSalaRaw = new JSONArray(jsonAlocacoesStr);
            JSONObject alocacaoSalaObj = null;
            if (alocacaoSalaRaw.length() > 0) {
                for (int i = 0; i < alocacaoSalaRaw.length(); i++) {
                    Gson gson = new Gson();
                    alocacaoSalaObj = alocacaoSalaRaw.getJSONObject(i);
                    JsonObject gsonObj = new JsonParser().parse(alocacaoSalaObj.toString()).getAsJsonObject();
                    AlocacaoSala alocacaoSala = gson.fromJson(gsonObj, AlocacaoSala.class);
                    listaSalas.add(alocacaoSala);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ListView listaDeAlocacao = findViewById(R.id.lista_alocacao_listview);
        listaDeAlocacao.setAdapter(new AgendamentoAdapter(listaSalas, this));
        JSONObject jsonUsuario;
        int idUsuario = 0;
        try {
            jsonUsuario = new JSONObject(fileWritterService.lerArquivo(AgendamentoActivity.this, "usuariologado.json"));
            idUsuario = jsonUsuario.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final int finalIdSala = Integer.parseInt(idSala);
        final int finalIdUsuario = idUsuario;
        builder = new AlertDialog.Builder(AgendamentoActivity.this);
        fabAddAlocacao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                intentCriacaoAgendamento.putExtra("data", data);
                intentCriacaoAgendamento.putExtra("idSala", finalIdSala);
                intentCriacaoAgendamento.putExtra("idUsuario", finalIdUsuario);
                AgendamentoActivity.this.startActivity(intentCriacaoAgendamento);
            }
        });
        final String finalJsonAlocacoesStr = jsonAlocacoesStr;
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
                            TextView viewAlocacaoSelecionada = findViewById(R.id.textViewCardAluguel);
                            JSONArray jsonAlocacoes = new JSONArray(finalJsonAlocacoesStr);
                            JSONObject jsonAlocacaoSelecionada = jsonAlocacoes.getJSONObject(position);
                            intentExcluirAgendamento.putExtra("jsonAlocacaoSelecionada", jsonAlocacaoSelecionada.toString());
                            AgendamentoActivity.this.startActivity(intentExcluirAgendamento);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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
                Intent intent = new Intent(AgendamentoActivity.this, LoginActivity.class);
                AgendamentoActivity.this.startActivity(intent);
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }
}
