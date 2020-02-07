package com.example.gerenciamentodesalas.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.service.delete.HttpServiceDeleteAlocacao;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcluirAgendamentoActivity extends AppCompatActivity {
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excluir_agendamento);
        final TextView textInicio = findViewById(R.id.textInicioExcluir);
        final TextView textFim = findViewById(R.id.textFimExcluir);
        final TextInputEditText textDescricao = findViewById(R.id.inputDescricaoExcluir);
        final Button btnExcluir = findViewById(R.id.btnConfirmarExcluir);
        final SimpleDateFormat formatoTempo = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat formatoDataSqlOriginal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
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
                            Intent intent = new Intent(ExcluirAgendamentoActivity.this, AgendamentoActivity.class);
                            ExcluirAgendamentoActivity.this.startActivity(intent);
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
}
