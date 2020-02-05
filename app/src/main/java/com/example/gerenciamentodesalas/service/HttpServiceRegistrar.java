package com.example.gerenciamentodesalas.service;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Base64;

import com.example.gerenciamentodesalas.R;

public class HttpServiceRegistrar extends AsyncTask<String, Void, String> {

    private final String name;
    private final String email;
    private final String password;
    private final int idEmpresa;
    private final String ip;

    public HttpServiceRegistrar(String name, String email, String password, int idEmpresa, String ip) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.idEmpresa = idEmpresa;
        this.ip = ip;
    }
    @Override
    protected String doInBackground(String... strings) {
        String urlWS = "http://" + ip + "/ReservaDeSala/rest/usuario/cadastro";

        String authorizationHeader = "secret";
        try {
            JSONObject usuarioJson = new JSONObject();
            usuarioJson.put("email", email);
            usuarioJson.put("nome", name);
            usuarioJson.put("senha", password);
            usuarioJson.put("idOrganizacao", idEmpresa);
            String novoUsuarioEncodado = Base64.encodeToString(usuarioJson.toString().getBytes("UTF-8"), Base64.NO_WRAP);
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlWS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("authorization", authorizationHeader);
            conn.setRequestProperty("novoUsuario", novoUsuarioEncodado);
            conn.setConnectTimeout(7000);
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            if (result.toString().equals("Usuário criado com sucesso"))
                return "201";

            else
                return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "400";
        }

    }
}
