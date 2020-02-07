package com.example.gerenciamentodesalas.service.get;

import android.os.AsyncTask;

import com.example.gerenciamentodesalas.model.AlocacaoSala;
import com.example.gerenciamentodesalas.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HttpServiceGetUsuario extends AsyncTask<String, Void, String> {

        private final String email;
        private final String ip;


        public HttpServiceGetUsuario(String email, String ip) {
            this.email = email;
            this.ip = ip;
        }

        @Override
        protected String doInBackground(String... strings) {
            String urlWS = "http://" + ip + "/ReservaDeSala/rest/usuario/getusuario";

            String authorizationHeader = "secret";
            try {
                StringBuilder result = new StringBuilder();
                URL url = new URL(urlWS);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("authorization", authorizationHeader);
                conn.setRequestProperty("email", email);
                conn.setConnectTimeout(7000);
                InputStream getConn = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(getConn));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
}
