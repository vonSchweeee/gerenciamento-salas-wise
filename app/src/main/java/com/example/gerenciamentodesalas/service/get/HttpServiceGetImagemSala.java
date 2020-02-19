package com.example.gerenciamentodesalas.service.get;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpServiceGetImagemSala extends AsyncTask<String, Void, Bitmap> {

        private final String urlImagem;


        public HttpServiceGetImagemSala(String urlImagem) {
            this.urlImagem = urlImagem;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                StringBuilder result = new StringBuilder();
                URL url = new URL(urlImagem);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                conn.setConnectTimeout(7000);
                InputStream input = conn.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
}
