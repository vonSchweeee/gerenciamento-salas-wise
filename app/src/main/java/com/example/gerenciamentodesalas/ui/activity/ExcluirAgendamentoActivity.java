package com.example.gerenciamentodesalas.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.example.gerenciamentodesalas.model.Usuario;
import com.example.gerenciamentodesalas.service.HttpRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExcluirAgendamentoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    AlertDialog.Builder builder;
    TinyDB tinyDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excluir_agendamento);
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
        TextView navNome =  headerView.findViewById(R.id.nav_usuario);
        TextView navOrg = headerView.findViewById(R.id.nav_org);
        TextView navEmail = headerView.findViewById(R.id.nav_email);
        navNome.setText(tinyDB.getObject("usuario", Usuario.class).getNome());
        navOrg.setText(tinyDB.getObject("usuario", Usuario.class).getIdOrganizacao().getNome());
        navEmail.setText(tinyDB.getObject("usuario", Usuario.class).getEmail());
        final TextView textInicio = findViewById(R.id.textInicioExcluir);
        final TextView textFim = findViewById(R.id.textFimExcluir);
        final TextInputEditText textDescricao = findViewById(R.id.inputDescricaoExcluir);
        final Button btnExcluir = findViewById(R.id.btnConfirmarExcluir);
        final SimpleDateFormat formatoTempo = new SimpleDateFormat("HH:mm");
        Date dataHoraInicio = null;
        Date dataHoraFim = null;
        String descricao = null;
        String horaInicio = null;
        String horaFim = null;
        AlocacaoSala alocacaoSelecionada = null;
        try {
            alocacaoSelecionada = tinyDB.getObject("alocacaoSelecionada", AlocacaoSala.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            dataHoraInicio = alocacaoSelecionada.getDataHoraInicio();
            dataHoraFim = alocacaoSelecionada.getDataHoraFim();
            descricao = alocacaoSelecionada.getDescricao();
            horaInicio = formatoTempo.format(dataHoraInicio);
            horaFim = formatoTempo.format(dataHoraFim);
        } catch (Exception e) {
            e.printStackTrace();
        }
        textInicio.setText(horaInicio);
        textFim.setText(horaFim);
        textDescricao.setText(descricao);

        final AlocacaoSala finalAlocacaoSelecionada = alocacaoSelecionada;
        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String retorno=null;
                try {
                    Resources resources = getResources();
                    String url = resources.getString(R.string.ip) + "alocacao/excluir";
                    String id = String.valueOf(finalAlocacaoSelecionada.getId());
                    DeletarAlocacao(url, id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    public void onStop(){
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void DeletarAlocacao(String url, String id) {
        Map<String, String> params = new HashMap<String, String>();
        Resources resources = getResources();
        String auth = resources.getString(R.string.auth);
        params.put("id", id);
        params.put("authorization", auth);
        new HttpRequest(ExcluirAgendamentoActivity.this, params, url, "DELETE", "deleteAlocacao").doRequest();
    }

    @Subscribe
    public void customEventReceived(Event event){
        builder = new AlertDialog.Builder(ExcluirAgendamentoActivity.this);
        if (event.getEventName().equals("deleteAlocacao" + Constants.eventSuccessLabel)) {
            builder.setMessage("Alocação desativada com sucesso.").setTitle("Sucesso!");
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Retornar para alocações", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ExcluirAgendamentoActivity.this.finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (event.getEventName().equals("deleteAlocacao" + Constants.eventErrorLabel)) {
            builder.setMessage(event.getEventMsg()).setTitle("Erro.");
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (event.getEventName().startsWith("deleteAlocacao" + Constants.eventErrorLabel)) {
            builder.setMessage("Erro no servidor.").setTitle("Erro.");
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                tinyDB.clear();
                Intent intent = new Intent(ExcluirAgendamentoActivity.this, LoginActivity.class);
                ExcluirAgendamentoActivity.this.startActivity(intent);
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
