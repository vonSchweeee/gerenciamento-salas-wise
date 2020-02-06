package com.example.gerenciamentodesalas.dao;

import android.content.Context;

import com.example.gerenciamentodesalas.model.Sala;
import com.example.gerenciamentodesalas.service.FileWritterService;
import com.example.gerenciamentodesalas.ui.activity.ListaSalasActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SalasDAO {

        public List<Sala> lista(String nomeOrganizacao, Context context) {
            FileWritterService fileWritter = new FileWritterService();
            List<Sala> salas = new ArrayList<Sala>();
            String jsonSalasStr = fileWritter.lerArquivo(context, nomeOrganizacao + "_salas.json");
            try {
                JSONArray jsonArraySalas = new JSONArray(jsonSalasStr);
                JSONObject salaObj;
                if (jsonArraySalas != null) {
                    for (int i=0;i<jsonArraySalas.length();i++){
                        Gson gson = new Gson();
                        salaObj = jsonArraySalas.getJSONObject(i);
                        JsonObject gsonObj = new JsonParser().parse(salaObj.toString()).getAsJsonObject();
                        Sala sala = gson.fromJson(gsonObj, Sala.class);
                        salas.add(sala);
                    }
                    return salas;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }