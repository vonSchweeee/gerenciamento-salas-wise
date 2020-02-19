package com.example.gerenciamentodesalas.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.TinyDB;
import com.example.gerenciamentodesalas.model.Constants;
import com.example.gerenciamentodesalas.model.Event;
import com.example.gerenciamentodesalas.model.Usuario;
import com.example.gerenciamentodesalas.service.HttpRequest;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextView viewEmail;
    TextView viewSenha;
    Button btnLogin, btnRegistro;
    AlertDialog.Builder builder;
    String email;
    String password;
    TinyDB tinyDB;
    LottieAnimationView viewLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tinyDB = new TinyDB(getApplicationContext());
        viewLoading = findViewById(R.id.viewLoading);
        builder = new AlertDialog.Builder(LoginActivity.this);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);
        viewEmail = findViewById(R.id.inputLogin);
        viewSenha = findViewById(R.id.inputSenha);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentReg = new Intent(LoginActivity.this, RegistroActivity.class);
                    LoginActivity.this.startActivity(intentReg);

                }
            });
        btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tinyDB.remove("usuario");
                    viewLoading.setVisibility(View.VISIBLE);
                    btnLogin.setEnabled(false);
                    Resources resources = getResources();
                    String ip = resources.getString(R.string.ip);
                    email = viewEmail.getText().toString();
                    password = viewSenha.getText().toString();
                    hideKeyboard(LoginActivity.this);
                    try {
                        login(ip, email, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                        builder.setMessage("Falha na conexão").setTitle("Erro");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }
            });
        }



    public void login(String ip, String email, String senha) {
        Map<String, String> params = new HashMap<String, String>();
        Resources resources = getResources();
        params.put("authorization", resources.getString(R.string.auth));
        params.put("email", email);
        params.put("password", senha);
        new HttpRequest(getApplicationContext(), params, ip + "usuario/login/", "GET", "login").doRequest();
    }

    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    public void onStop(){
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void customEventReceived(Event event) {
        final Intent intent = new Intent(LoginActivity.this, ListaSalasActivity.class);
        if (event.getEventName().equals("login" + Constants.eventSuccessLabel) || event.getEventStatusCode() == 200) {
                try {
                    String usuarioJSONStr = event.getEventMsg();
                    JSONObject usuarioJSON = new JSONObject(usuarioJSONStr);
                    String nomeOrganizacao = usuarioJSON.getJSONObject("idOrganizacao").getString("nome");
                    Gson gson = new Gson();
                    Usuario usuario = gson.fromJson(usuarioJSONStr, Usuario.class);
                    System.out.println(usuarioJSON.toString());
                    System.out.println(usuario.getEmail());
                    tinyDB.putObject("usuario", usuario);
                    tinyDB.putString("senha", viewSenha.getText().toString());
                    tinyDB.putString("nomeOrganizacao", nomeOrganizacao);
                    builder.setMessage("login efetuado com sucesso!").setTitle("Sucesso!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            LoginActivity.this.startActivity(intent);
                            LoginActivity.this.finish();
                        }
                    });
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            LoginActivity.this.startActivity(intent);
                            LoginActivity.this.finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        else if (event.getEventName().equals("login" + Constants.eventErrorLabel)){
            builder.setMessage("Falha na conexão." + "\r\nErro na comunicação com o servidor.").setTitle("Erro");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (event.getEventName().equals("login" + Constants.eventErrorLabel)){
            builder.setMessage("Falha na conexão." + "\r\nErro na comunicação com o servidor.").setTitle("Erro");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        btnLogin.setEnabled(true);
        viewLoading.setVisibility(View.GONE);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}

//    public void login() {
//        try {
//            // Formulate the request and handle the response.
//            StringRequest stringRequest = new StringRequest(Request.Method.GET, ip,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            System.out.println("Response login: " + response);
//                            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
//                            viewLoading.setVisibility(View.GONE);
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            try {
//                                String errorResult = new String(error.networkResponse.data, "UTF-8");
//                                Toast.makeText(getApplicationContext(), errorResult, Toast.LENGTH_SHORT).show();
//                                System.out.println("Response Error: " + errorResult);
//                                viewLoading.setVisibility(View.GONE);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                viewLoading.setVisibility(View.GONE);
//                            }
//                        }
//                    }) {
//                @Override
//
//            };
//
//            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    5000,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
