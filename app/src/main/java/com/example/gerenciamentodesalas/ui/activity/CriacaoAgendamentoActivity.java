package com.example.gerenciamentodesalas.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.gerenciamentodesalas.R;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CriacaoAgendamentoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criacao_agendamento);
        final ImageView imgViewInicio = findViewById(R.id.imageViewHoraInicio);
        final ImageView imgViewFim = findViewById(R.id.imageViewHoraFim);
        final TextView textHoraInicio = findViewById(R.id.textHoraInicioHorario);
        final TextView textHoraFim = findViewById(R.id.textHoraFimHorario);
        imgViewInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CriacaoAgendamentoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        SimpleDateFormat formatoTempo = new SimpleDateFormat("HH:mm");
                        Date horaEscolhida = null;
                        try {
                            horaEscolhida = formatoTempo.parse(selectedHour + ":" + selectedMinute);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String horaEscolhidaStr = formatoTempo.format(horaEscolhida);
                        textHoraInicio.setText(horaEscolhidaStr);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Escolha o horário");
                mTimePicker.show();
            }
        });
        imgViewFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CriacaoAgendamentoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        SimpleDateFormat formatoTempo = new SimpleDateFormat("HH:mm");
                        Date horaEscolhida = null;
                        try {
                            horaEscolhida = formatoTempo.parse(selectedHour + ":" + selectedMinute);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String horaEscolhidaStr = formatoTempo.format(horaEscolhida);
                        textHoraFim.setText(horaEscolhidaStr);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Escolha o horário");
                mTimePicker.show();
            }
        });
    }
}
