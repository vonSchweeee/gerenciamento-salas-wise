package com.example.gerenciamentodesalas.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.Map;

public class HttpRequestImagem {

    Context context;
    Map<String, String> headers;
    String url;
    String eventName;
    ImageView imgView;
    public HttpRequestImagem(Context c, String u, ImageView imgView) {
        this.context = c;
        this.url = u;
        this.imgView = imgView;
    }

    public void doRequest() {
            ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    imgView.setImageBitmap(response);
                }
            },0,0, null,null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    e.printStackTrace();
                }
            }
            );
         VolleySingletonImg.getInstance(context).addToRequestQueue(request);


    }
}
