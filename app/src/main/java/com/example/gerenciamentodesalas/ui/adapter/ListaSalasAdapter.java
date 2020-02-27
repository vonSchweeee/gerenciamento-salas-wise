package com.example.gerenciamentodesalas.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.model.Sala;
import com.example.gerenciamentodesalas.service.VolleySingleton;

import java.util.List;

public class ListaSalasAdapter extends BaseAdapter {

    private final List<Sala> salasList;
    private Context context;
    private LruCache<String, Bitmap> memoryCache;

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
        if (view != null) return view;
        View viewCriada = LayoutInflater.from(context).inflate(R.layout.item_sala, parent, false);

        Sala sala = salasList.get(position);
        String urlImagem = sala.getUrlImagem();

        TextView textViewSalas = viewCriada.findViewById(R.id.textViewSalas);
        final ImageView imageViewSala = viewCriada.findViewById(R.id.imageViewSala);
        if (urlImagem != null) {
            try {
                final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
                final int cacheSize = maxMemory / 10;
                memoryCache = new LruCache<String, Bitmap>(cacheSize);
                if (memoryCache.get("imgSala_" + position) != null) {
                    imageViewSala.setImageBitmap(memoryCache.get("imgSala_" + position));
                }
                else {
                    ImageRequest imageRequest = new ImageRequest(urlImagem, new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            Bitmap imagem = null;
                            memoryCache.put("imgSala_" + position, response);
                            imageViewSala.setImageBitmap(memoryCache.get("imgSala_" + position));
                        }
                    }, 1280, 720, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    imageRequest.setRetryPolicy(new DefaultRetryPolicy(
                            1500,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            -0.8f));
                    VolleySingleton.getInstance(context).addToRequestQueue(imageRequest);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        textViewSalas.setText(sala.getNome());
        return viewCriada;

    }

}
