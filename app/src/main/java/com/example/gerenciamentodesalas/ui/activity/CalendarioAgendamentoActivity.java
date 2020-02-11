package com.example.gerenciamentodesalas.ui.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gerenciamentodesalas.R;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CalendarioAgendamentoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private CalendarView calendario;
    private DatePickerDialog.OnDateSetListener pegarData;
    int ano, mes, dia;
    private static final String TAG = "CalendarioActivity";
    private String dataEscolhidaRaw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario_agendamento);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        final SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Button btn = findViewById(R.id.btn);
        calendario = findViewById(R.id.calendario);
        final TextView textViewData= findViewById(R.id.textViewData);
        final TextView textViewSala = findViewById(R.id.textViewSala);
        Intent intent = getIntent();
        final String jsonSalas = intent.getExtras().getString("jsonSalas");
        final String salaEscolhida = intent.getExtras().getString("sala");
        textViewSala.setText(salaEscolhida.toString());
        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                String dataRaw;
                String dataString;
                Log.d(TAG, "onDateSet: dd/MM/yyyy: " + dayOfMonth + "/" + month + "/" + year);
                dataRaw = dayOfMonth + "/" + month + "/" + year;
                String parts[] = dataRaw.split("/");
                month++;
                if(month>10) {
                    dataString = dayOfMonth + "/" + month + "/" + year;
                }
                else {
                    dataString = dayOfMonth + "/" + "0" + month + "/" + year;
                }
                int dia = Integer.parseInt(parts[0]);
                int mes = Integer.parseInt(parts[1]);
                int ano = Integer.parseInt(parts[2]);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, ano);
                calendar.set(Calendar.MONTH, mes);
                calendar.set(Calendar.DAY_OF_MONTH, dia);

                dataEscolhidaRaw = formatoData.format(calendar.getTime());
                textViewData.setText(dataString);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dataEscolhida;
                dataEscolhida =  textViewData.getText().toString();
                Intent intent = new Intent(CalendarioAgendamentoActivity.this, AgendamentoActivity.class);
                intent.putExtra("dataEscolhida", dataEscolhida);
                intent.putExtra("salaEscolhida", salaEscolhida);
                try {
                    Date dataEscolhidaFormatada = formatoData.parse(dataEscolhidaRaw);
                    intent.putExtra("dataFormatadaEscolhida", dataEscolhidaFormatada);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                intent.putExtra("jsonSalas", jsonSalas);
                CalendarioAgendamentoActivity.this.startActivity(intent);
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
                Intent intent = new Intent(CalendarioAgendamentoActivity.this, LoginActivity.class);
                CalendarioAgendamentoActivity.this.startActivity(intent);
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


