package com.example.gerenciamentodesalas.service;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpServiceGetSalas extends AsyncTask<String, Void, String> {

    private final String ip;
    private final String idOrganizacao;

    public HttpServiceGetSalas(String idOrganizacao, String ip) {
        this.idOrganizacao = idOrganizacao;
        this.ip = ip;
    }
    @Override
    protected String doInBackground(String... strings) {

        String urlWS = "http://" + ip + "/ReservaDeSala/rest/sala/getsalas";

        String authorizationHeader = "secret";
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlWS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("authorization", authorizationHeader);
            conn.setRequestProperty("idOrganizacao", idOrganizacao);
            conn.setConnectTimeout(7000);
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            return result.toString();
            //Retorno de ArrayList de Salas
//            JSONArray salaRaw = new JSONArray(result.toString());
//            JSONObject salaObj=null;
//            ArrayList<Sala> listaSalas= new ArrayList<Sala>();
//            if (salaRaw.length() > 0){
//
//                for (int i = 0; i < salaRaw.length(); i++) {
//                    Gson gson = new Gson();
//                    salaObj = salaRaw.getJSONObject(i);
//                    JsonObject gsonObj = new JsonParser().parse(salaObj.toString()).getAsJsonObject();
//                    Sala sala = gson.fromJson(gsonObj, Sala.class);
//                    listaSalas.add(sala);
//                }
//                return listaSalas;
//            }


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
