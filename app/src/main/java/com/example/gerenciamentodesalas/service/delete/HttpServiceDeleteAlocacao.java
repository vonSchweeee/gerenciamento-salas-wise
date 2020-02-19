package com.example.gerenciamentodesalas.service.delete;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpServiceDeleteAlocacao extends AsyncTask<String, Void, String> {

    private final String ip;
    private final String idAlocacao;

    public HttpServiceDeleteAlocacao(String ip, String idAlocacao) {
        this.idAlocacao = idAlocacao;
        this.ip = ip;
    }
    @Override
    protected String doInBackground(String... strings) {

        String urlWS = "http://" + ip + "/ReservaDeSala/rest/alocacao/excluir";

        String authorizationHeader = "secret";
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlWS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("authorization", authorizationHeader);
            conn.setRequestProperty("id", idAlocacao);
            conn.setConnectTimeout(7000);
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao desativar a alocação.";
        }
    }
}
