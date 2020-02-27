package com.example.gerenciamentodesalas.dao;

import android.content.Context;

import com.example.gerenciamentodesalas.TinyDB;
import com.example.gerenciamentodesalas.model.Sala;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SalasDAO {
        public List<Sala> lista(Context context) {
            TinyDB tinyDB = new TinyDB(context);
            List<Sala> salas = new ArrayList<Sala>();
            String jsonSalasStr = tinyDB.getString("jsonSalasStr");
            try {
                JSONArray jsonArraySalas = new JSONArray(jsonSalasStr);
                JSONObject salaObj;
                    for (int i=0;i<jsonArraySalas.length();i++){
                        Gson gson = new Gson();
                        salaObj = jsonArraySalas.getJSONObject(i);
                        JsonObject gsonObj = new JsonParser().parse(salaObj.toString()).getAsJsonObject();
                        Sala sala = gson.fromJson(gsonObj, Sala.class);
                        salas.add(sala);
                    }
                    return salas;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }