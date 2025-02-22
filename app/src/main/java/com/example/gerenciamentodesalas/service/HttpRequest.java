package com.example.gerenciamentodesalas.service;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gerenciamentodesalas.model.Constants;
import com.example.gerenciamentodesalas.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class HttpRequest {

    Context context;

    Map<String, String> headers;
    String url;
    int reqMethod;
    String eventName;
    int statusCode;
    public HttpRequest(Context c, Map<String, String> h, String u, String reqMethod, String eventName) {
        this.context = c;
        this.headers = h;
        this.url = u;
        this.eventName = eventName;
        switch (reqMethod) {
            case "GET":
                this.reqMethod = Request.Method.GET;
                break;
            case "POST":
                this.reqMethod = Request.Method.POST;
                break;
            case "PUT":
                this.reqMethod = Request.Method.PUT;
                break;
            case "DELETE":
                this.reqMethod = Request.Method.DELETE;
                break;
        }
    }

    public void doRequest() {

        try {
            // Formulate the request and handle the response.
            StringRequest stringRequest = new StringRequest(reqMethod, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("sucesso");
                            EventBus.getDefault().post(new Event(eventName + Constants.eventSuccessLabel, response, statusCode));
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                System.out.println(12314);
                                if (error.networkResponse != null) {
                                    String errorResult = new String(error.networkResponse.data, "UTF-8");
                                    try {
                                        JSONObject jsonObject = new JSONObject(errorResult);
                                        String errorMsg = jsonObject.getString("data");
                                        EventBus.getDefault().post(new Event(eventName + Constants.eventErrorLabel, errorMsg, error.networkResponse.statusCode));
                                    }
                                    catch (JSONException e) {
                                        EventBus.getDefault().post(new Event(eventName + Constants.eventErrorLabel, errorResult, error.networkResponse.statusCode));
                                    }
                                } else {
                                    System.out.println("dubaraduba");
                                    EventBus.getDefault().post(new Event(eventName + Constants.eventErrorLabel, "Sem resposta do servidor", 0));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return headers;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    statusCode = response.statusCode;
                    return super.parseNetworkResponse(response);

                }
            };
            if (eventName.equals("AutoLogin")) {
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        1500,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        -0.8f));
                VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
            }
            if (eventName.equals("verificarDominio")) {
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        500,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        2.5f));
                VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
            }
            else {
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        3000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        -0.2f));
                VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
