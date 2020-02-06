package com.example.gerenciamentodesalas.service;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.model.Organizacao;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class HttpServiceVerificarDominio extends AsyncTask<String, Void, List<Organizacao>> {

    private final String dominio;
    private final String ip;

    public HttpServiceVerificarDominio(String dominio, String ip) {
        this.ip = ip;
        this.dominio = dominio;
    }
    @Override
    protected List<Organizacao> doInBackground(String... strings) {

        String urlWS = "http://" + ip + "/ReservaDeSala/rest/organizacao/organizacoesByDominio";

        String authorizationHeader = "secret";
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlWS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("authorization", authorizationHeader);
            conn.setRequestProperty("dominio", dominio);
            conn.setConnectTimeout(7000);
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            JSONArray organizacaoRaw = new JSONArray(result.toString());
            JSONObject organizacaoObj=null;
            ArrayList<Organizacao> listaOrganizacoes= new ArrayList<Organizacao>();
            if (organizacaoRaw.length() > 0){

                for (int i = 0; i < organizacaoRaw.length(); i++) {
                    Gson gson = new Gson();
                    organizacaoObj = organizacaoRaw.getJSONObject(i);
                    JsonObject gsonObj = new JsonParser().parse(organizacaoObj.toString()).getAsJsonObject();
                    Organizacao organizacao = gson.fromJson(gsonObj, Organizacao.class);
                    listaOrganizacoes.add(organizacao);
                }
                return listaOrganizacoes;
            }


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
