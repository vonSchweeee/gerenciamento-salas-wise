package com.example.gerenciamentodesalas.ui.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.gerenciamentodesalas.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;


public class CalendarioAgendamentoActivity extends AppCompatActivity {
    private CalendarView calendario;
    private DatePickerDialog.OnDateSetListener pegarData;
    int ano, mes, dia;
    private static final String TAG = "CalendarioActivity";
    private String dataEscolhidaRaw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario_agendamento);
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
}


