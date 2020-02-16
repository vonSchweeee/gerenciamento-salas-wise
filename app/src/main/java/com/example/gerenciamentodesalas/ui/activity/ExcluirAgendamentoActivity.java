package com.example.gerenciamentodesalas.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.TinyDB;
import com.example.gerenciamentodesalas.model.Usuario;
import com.example.gerenciamentodesalas.service.delete.HttpServiceDeleteAlocacao;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        SharedPreferences sp = getSharedPreferences(LoginActivity.USER_PREFERENCE, MODE_PRIVATE);
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
        final SimpleDateFormat formatoDataSqlOriginal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        final Intent intentCalendarioAgendamento = new Intent (ExcluirAgendamentoActivity.this, CalendarioAgendamentoActivity.class);
        Intent intent = getIntent();
        JSONObject jsonAlocacaoSelecionada = null;
        Date dataHoraInicio = null;
        Date dataHoraFim = null;
        String descricao = null;
        String horaInicio = null;
        String horaFim = null;
        try {
            jsonAlocacaoSelecionada = new JSONObject(intent.getExtras().getString("jsonAlocacaoSelecionada"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            String dataHoraInicioStr = jsonAlocacaoSelecionada.getString("dataHoraInicio").replace("Z[UTC]", "");
            String dataHoraFimStr = jsonAlocacaoSelecionada.getString("dataHoraFim").replace("Z[UTC]", "");
            dataHoraInicio = formatoDataSqlOriginal.parse(dataHoraInicioStr);
            dataHoraFim = formatoDataSqlOriginal.parse(dataHoraFimStr);
            descricao = jsonAlocacaoSelecionada.getString("descricao");
            horaInicio = formatoTempo.format(dataHoraInicio);
            horaFim = formatoTempo.format(dataHoraFim);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textInicio.setText(horaInicio);
        textFim.setText(horaFim);
        textDescricao.setText(descricao);

        final JSONObject finalJsonAlocacaoSelecionada = jsonAlocacaoSelecionada;
        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String retorno=null;
                try {
                    builder = new AlertDialog.Builder(ExcluirAgendamentoActivity.this);
                    Resources resources = getResources();
                    String ip = resources.getString(R.string.ip);
                    String id = finalJsonAlocacaoSelecionada.getString("id");
                    retorno = new HttpServiceDeleteAlocacao(ip, id).execute().get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (retorno.equals("Alocação desativada com sucesso.")) {
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
                            ExcluirAgendamentoActivity.this.startActivity(intentCalendarioAgendamento);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    builder.setMessage(retorno).setTitle("Erro.");
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
        });
    }
    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                SharedPreferences.Editor editorUser = getSharedPreferences(LoginActivity.USER_PREFERENCE, MODE_PRIVATE).edit();
                editorUser.clear();
                editorUser.apply();
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
