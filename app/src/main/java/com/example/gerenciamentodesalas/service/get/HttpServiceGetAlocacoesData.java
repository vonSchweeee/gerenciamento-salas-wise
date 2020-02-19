package com.example.gerenciamentodesalas.service.get;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpServiceGetAlocacoesData extends AsyncTask<String, Void, String> {

    private final String ip;
    private final String idSala;
    private final String diaEscolhido;
    private final String fimDiaEscolhido;

    public HttpServiceGetAlocacoesData(String ip, String idSala, String diaEscolhido, String fimDiaEscolhido) {
        this.ip = ip;
        this.idSala = idSala;
        this.diaEscolhido = diaEscolhido;
        this.fimDiaEscolhido = fimDiaEscolhido;
    }
    @Override
    protected String doInBackground(String... strings) {

        String urlWS = ip + "alocacao/getalocacoesbysaladata";

        String authorizationHeader = "secret";
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlWS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("authorization", authorizationHeader);
            conn.setRequestProperty("idSala", idSala);
            conn.setRequestProperty("diaEscolhido", diaEscolhido);
            conn.setRequestProperty("fimDiaEscolhido", fimDiaEscolhido);
            conn.setConnectTimeout(7000);
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
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