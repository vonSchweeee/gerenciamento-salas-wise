package com.example.gerenciamentodesalas.service.get;

import android.os.AsyncTask;

import com.example.gerenciamentodesalas.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpServiceLogar extends AsyncTask<String, Void, String> {

    private final String email;
    private final String password;
    private final String ip;

    public HttpServiceLogar(String email, String password, String ip) {
        this.email = email;
        this.password = password;
        this.ip = ip;
    }
    @Override
    protected String doInBackground(String... strings) {
        String urlWS = "http://" + ip + "/ReservaDeSala/rest/usuario/login/";

        String authorizationHeader = "secret";
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlWS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("authorization", authorizationHeader);
            conn.setRequestProperty("email", email);
            conn.setRequestProperty("password", password);
            conn.setConnectTimeout(7000);
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            if (result.toString().equals("login efetuado com sucesso!"))
                return "200";
            else
                return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "400";
        }

    }
}
