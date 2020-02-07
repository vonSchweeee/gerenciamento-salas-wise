package com.example.gerenciamentodesalas.ui.activity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.service.post.HttpServiceNovaAlocacao;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CriacaoAgendamentoActivity extends AppCompatActivity {
    Boolean inicioAlterado = false;
    Boolean fimAlterado = false;
    Date horaInicio;
    Date horaFim;
    String data;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criacao_agendamento);
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
        final Intent intent = new Intent(CriacaoAgendamentoActivity.this, AgendamentoActivity.class);
        try {
            data = formatoDataSql.format(formatoDataBr.parse(intente.getExtras().getString("data")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CriacaoAgendamentoActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                mTimePicker.setTitle("Escolha o horário");
                mTimePicker.show();
            }
        });
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(CriacaoAgendamentoActivity.this);
                if (inicioAlterado == true && fimAlterado == true) {
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
                            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
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
}
