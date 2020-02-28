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

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.gerenciamentodesalas.ItemClickListener;
import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.model.Constants;
import com.example.gerenciamentodesalas.model.Event;
import com.example.gerenciamentodesalas.model.Sala;
import com.example.gerenciamentodesalas.service.VolleySingleton;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ListaSalasAdapter extends RecyclerView.Adapter {

    private final List<Sala> salasList;
    private Context context;
    private LruCache<String, Bitmap> memoryCache;

    public ListaSalasAdapter(List<Sala> salasList, Context context){
        this.salasList = salasList;
        this.context = context;
    }
    private static ItemClickListener itemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sala, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        Sala sala = salasList.get(position);
        String urlImagem = sala.getUrlImagem();
        if (urlImagem != null) {
            try {
                final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
                final int cacheSize = maxMemory / 10;
                memoryCache = new LruCache<String, Bitmap>(cacheSize);
                if (memoryCache.get("imgSala_" + position) != null) {
                    holder.imageViewSalas.setImageBitmap(memoryCache.get("imgSala_" + position));
                }
                else {
                    ImageRequest imageRequest = new ImageRequest(urlImagem, new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            memoryCache.put("imgSala_" + position, response);
                            holder.imageViewSalas.setImageBitmap(memoryCache.get("imgSala_" + position));
                            EventBus.getDefault().post(new Event("getImagensSalas" + position + Constants.eventSuccessLabel, "Sucesso", 200));
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
        holder.nomeSala.setText(sala.getNome());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView nomeSala;
        final ImageView imageViewSalas;
        public ViewHolder(View view) {
            super(view);
            nomeSala = view.findViewById(R.id.textViewSalas);
            imageViewSalas = view.findViewById(R.id.imageViewSala);

            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if(itemClickListener != null) {
                itemClickListener.onItemClick(getAdapterPosition());
            }
        }

    }
    @Override public int getItemCount() { return salasList.size(); }


}
