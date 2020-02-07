package com.example.gerenciamentodesalas.service.post;

import android.os.AsyncTask;
import android.util.Base64;

import com.example.gerenciamentodesalas.model.Sala;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpServiceNovaAlocacao extends AsyncTask<String, Void, String> {

    private final String ip;
    private final String descricao;
    private final int idSala;
    private final int idUsuario;
    private final Date dataHoraInicio;
    private final Date dataHoraFim;

    public HttpServiceNovaAlocacao(String ip, int idSala, int idUsuario, String descricao, Date dataHoraInicio, Date dataHoraFim) {
        this.ip = ip;
        this.idSala = idSala;
        this.idUsuario = idUsuario;
        this.descricao = descricao;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
    }

    @Override
    protected String doInBackground(String... strings) {
        String urlWS = "http://" + ip + "/ReservaDeSala/rest/alocacao/reservar";
        String authorizationHeader = "secret";
        SimpleDateFormat formatoDataHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            JSONObject alocacaoJson = new JSONObject();
            alocacaoJson.put("idSala", idSala);
            alocacaoJson.put("idUsuario", idUsuario);
            alocacaoJson.put("descricao", descricao);
            alocacaoJson.put("dataHoraInicio", formatoDataHora.format(dataHoraInicio));
            alocacaoJson.put("dataHoraFim", formatoDataHora.format(dataHoraFim));
            String novaAlocacaoEncodada = Base64.encodeToString(alocacaoJson.toString().getBytes("UTF-8"), Base64.NO_WRAP);

            StringBuilder result = new StringBuilder();
            URL url = new URL(urlWS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("authorization", authorizationHeader);
            conn.setRequestProperty("novaAlocacao", novaAlocacaoEncodada);
            conn.setConnectTimeout(7000);
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            if (result.toString().equals("Alocação realizada com sucesso")) {
                return "201";
            }
            else {
                return result.toString();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
