package com.example.gerenciamentodesalas.ui.activity;

import android.app.TimePickerDialog;
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
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.TinyDB;
import com.example.gerenciamentodesalas.model.Usuario;
import com.example.gerenciamentodesalas.service.post.HttpServiceNovaAlocacao;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CriacaoAgendamentoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    Boolean inicioAlterado = false;
    Boolean fimAlterado = false;
    Date horaInicio;
    Date horaFim;
    String data;
    AlertDialog.Builder builder;
    TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criacao_agendamento);
        Toolbar toolbar = findViewById(R.id.toolbar);
        tinyDB = new TinyDB(getApplicationContext());
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
        Resources resources = getResources();
        Intent intente = getIntent();
        final ImageView imgViewInicio = findViewById(R.id.imageViewHoraInicio);
        final ImageView imgViewFim = findViewById(R.id.imageViewHoraFim);
        final TextView textHoraInicio = findViewById(R.id.textHoraInicioHorario);
        final TextView textHoraFim = findViewById(R.id.textHoraFimHorario);
        final TextInputEditText textDescricao = findViewById(R.id.inputDescricao);
        final Button btnConfirmar = findViewById(R.id.btnConfirmarAgendamento);
        final SimpleDateFormat formatoTempo = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat formatoDataHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat formatoDataBr = new SimpleDateFormat("dd/MM/yyyy");
        final SimpleDateFormat formatoDataSql = new SimpleDateFormat("yyyy-MM-dd");
        final int idUsuario = intente.getExtras().getInt("idUsuario");
        final int idSala = intente.getExtras().getInt("idSala");
        final String ip = resources.getString(R.string.ip);
        final Intent intent = new Intent(CriacaoAgendamentoActivity.this, CalendarioAgendamentoActivity.class);
        try {
            data = formatoDataSql.format(formatoDataBr.parse(intente.getExtras().getString("data")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        imgViewInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar tempoAgora = Calendar.getInstance();
                int hour = tempoAgora.get(Calendar.HOUR_OF_DAY);
                int minute = tempoAgora.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CriacaoAgendamentoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Date horaEscolhida = null;
                        try {
                            horaEscolhida = formatoTempo.parse(selectedHour + ":" + selectedMinute);
                            horaInicio = formatoDataHora.parse(data + " " + selectedHour + ":" + selectedMinute + ":" + "00");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String horaEscolhidaStr = formatoTempo.format(horaEscolhida);
                        textHoraInicio.setText(horaEscolhidaStr);
                        inicioAlterado = true;
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Escolha o horário");
                mTimePicker.show();
            }
        });
        imgViewFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar horaAtual = Calendar.getInstance();
                int hour = horaAtual.get(Calendar.HOUR_OF_DAY);
                int minute = horaAtual.get(Calendar.MINUTE);
                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(CriacaoAgendamentoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Date horaEscolhida = null;
                        try {
                            horaEscolhida = formatoTempo.parse(selectedHour + ":" + selectedMinute);
                            horaFim = formatoDataHora.parse(data + " " + selectedHour + ":" + selectedMinute + ":" + "00");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String horaEscolhidaStr = formatoTempo.format(horaEscolhida);
                        textHoraFim.setText(horaEscolhidaStr);
                        fimAlterado = true;
                    }
                }, hour, minute, true);
                timePicker.setTitle("Escolha o horário");
                timePicker.show();
            }
        });
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(CriacaoAgendamentoActivity.this);
                if (inicioAlterado && fimAlterado) {
                    if (horaInicio.before(horaFim)){
                        String descricao = textDescricao.getText().toString();
                        String resposta=null;
                        try {
                            resposta = new HttpServiceNovaAlocacao(ip, idSala, idUsuario, descricao, horaInicio, horaFim).execute().get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (resposta.equals("201")){
                            builder.setMessage("Reserva realizada com sucesso!").setTitle("Sucesso!");
                            builder.setPositiveButton("Ir para Alocações", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    CriacaoAgendamentoActivity.this.startActivity(intent);
                                }
                            });
                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        else {
                            builder.setMessage(resposta).setTitle("Erro.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                    else {

                    }
                }
                else {
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
                Intent intent = new Intent(CriacaoAgendamentoActivity.this, LoginActivity.class);
                CriacaoAgendamentoActivity.this.startActivity(intent);
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
