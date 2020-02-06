package com.example.gerenciamentodesalas.ui.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.model.AlocacaoSala;
import com.example.gerenciamentodesalas.service.FileWritterService;
import com.example.gerenciamentodesalas.service.alocacaoPegar;
import com.example.gerenciamentodesalas.ui.adapter.AgendamentoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgendamentoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento);
        Resources resources=getResources();
        final FloatingActionButton fabAddAlocacao = findViewById(R.id.fabAddAlocacao);
        final String ip=resources.getString(R.string.ip);
        final Intent  intentCriacaoAgendamento = new Intent(AgendamentoActivity.this, CriacaoAgendamentoActivity.class);
        SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
        TextView textViewSala = findViewById(R.id.textViewNomeSala);
        TextView textViewData = findViewById(R.id.textViewDataEscolhida);
        Date dataEscolhida = null;
        String salaEscolhida = null;
        Intent intent = getIntent();
        String sala = intent.getExtras().getString("salaEscolhida");
        String data = intent.getExtras().getString("dataEscolhida");
        Date dataRaw = (Date)intent.getSerializableExtra("dataFormatadaEscolhida");
        String jsonSalasStr = intent.getExtras().getString("jsonSalas");
        DateTime dtOrg = new DateTime(dataRaw);
        DateTime dtPlusOne = dtOrg.plusDays(1);
        Date dataFim = dtPlusOne.toDate();
        String fimDiaEscolhido = formatoData.format(dataFim);
        String dataStr = formatoData.format(dataRaw);
        textViewSala.setText(sala);
        textViewData.setText(data);
        String retorno = null;
        FileWritterService fileWritterService = new FileWritterService();
        String jsonAlocacoesStr=null;
        JSONArray arraySalas = null;
        int posSala = intent.getExtras().getInt("position");
        String idSala=null;
        try {
            arraySalas = new JSONArray(jsonSalasStr);
            idSala = arraySalas.getJSONObject(posSala).getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<AlocacaoSala> listaSalas= new ArrayList<AlocacaoSala>();
        String jsonSalaStr = fileWritterService.lerArquivo(AgendamentoActivity.this, "alocacoes_" + sala + "_" + data.replace("/", "-") + ".json");
        try {
            retorno = new alocacaoPegar( ip, idSala, dataStr, fimDiaEscolhido).execute().get();
            boolean isFilePresent = fileWritterService.arquivoExiste(AgendamentoActivity.this, "alocacoes_" + sala + "_" + data + ".json");
            if(isFilePresent) {
                jsonAlocacoesStr = fileWritterService.lerArquivo(AgendamentoActivity.this, "alocacoes_" + sala + "_" + data.replace("/", "-") + ".json");
                if (! retorno.equals(jsonAlocacoesStr)) {
                    boolean isFileCreated = fileWritterService.criarArquivo(AgendamentoActivity.this, "alocoes_" + sala + "_" + data + ".json", retorno);
                    if(isFileCreated) {
                        System.out.println("JSON das alocações criado com sucesso.");
                    } else {
                        System.out.println("Falha ao atualizar JSON das alocações.");
                    }
                }
                else{
                    jsonAlocacoesStr = retorno;
                }
            } else {
                boolean isFileCreated = fileWritterService.criarArquivo(AgendamentoActivity.this, "alocacoes_" + sala + "_" + data.replace("/", "-")+ ".json", retorno);
                if(isFileCreated) {
                    System.out.println("JSON das alocações criado com sucesso.");
                    jsonAlocacoesStr = retorno;
                } else {
                    System.out.println("Falha ao criar JSON das alocações.");
                }
            }
            JSONArray alocacaoSalaRaw = new JSONArray(jsonAlocacoesStr);
            JSONObject alocacaoSalaObj=null;
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

        final ListView listaDeAlocacao = findViewById(R.id.lista_alugueis_listview);
        listaDeAlocacao.setAdapter(new AgendamentoAdapter(listaSalas, this));

        fabAddAlocacao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AgendamentoActivity.this.startActivity(intentCriacaoAgendamento);
            }
        });

        }



    }
