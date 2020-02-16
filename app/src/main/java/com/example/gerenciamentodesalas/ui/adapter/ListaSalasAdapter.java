package com.example.gerenciamentodesalas.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.VolleySingletonImg;
import com.example.gerenciamentodesalas.model.Sala;
import com.example.gerenciamentodesalas.service.HttpRequest;
import com.example.gerenciamentodesalas.service.HttpRequestImagem;
import com.example.gerenciamentodesalas.service.VolleySingleton;
import com.example.gerenciamentodesalas.service.get.HttpServiceGetImagemSala;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ListaSalasAdapter extends BaseAdapter {

    private final List<Sala> salasList;
    private Context context;


    public ListaSalasAdapter(List<Sala> salasList, Context context){

        this.salasList = salasList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return salasList.size();
    }

    @Override
    public Sala getItem(int position) {
        return salasList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View viewCriada = LayoutInflater.from(context).inflate(R.layout.item_sala, parent, false);

        Sala sala = salasList.get(position);
        String urlImagem = sala.getUrlImagem();

        TextView textViewSalas = viewCriada.findViewById(R.id.textViewSalas);
        final ImageView imageViewSala = viewCriada.findViewById(R.id.imageViewSala);
        if (urlImagem != null) {
            try {
                ImageRequest imageRequest = new ImageRequest(urlImagem, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageViewSala.setImageBitmap(response);
                    }
                }, 1280, 720, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        error.printStackTrace();
                    }
                });
                imageRequest.setRetryPolicy(new DefaultRetryPolicy(
                        1500,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        -0.8f));
                VolleySingleton.getInstance(context).addToRequestQueue(imageRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        textViewSalas.setText(sala.getNome());
        return viewCriada;
    }
}
