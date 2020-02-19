package com.example.gerenciamentodesalas.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

public class LoaginActivity extends AppCompatActivity {
    String email;
    String password;
    TinyDB tinyDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loagin);
        final Intent intentLoginFalha = new Intent(LoaginActivity.this, LoginActivity.class);
        tinyDB = new TinyDB(getApplicationContext());
        try {
            tinyDB.getObject("usuario", Usuario.class);
            Usuario usuario=null;
            try {
                usuario = tinyDB.getObject("usuario", Usuario.class);
            }
            catch (Exception e) {LoaginActivity.this.startActivity(intentLoginFalha);}
            if (usuario != null) {
                try {
                    email = usuario.getEmail();
                    System.out.println(email);
                    password = tinyDB.getString("senha");
                    System.out.println(password);
                    Resources resources = getResources();
                    String ip = resources.getString(R.string.ip);
                    System.out.println(ip);
                    Login(ip, email, password);
                } catch (Exception e) {
                    LoaginActivity.this.startActivity(intentLoginFalha);
                    LoaginActivity.this.finish();
                    e.printStackTrace();
                }
            }
            else {
                LoaginActivity.this.startActivity(intentLoginFalha);
                LoaginActivity.this.finish();
            }
        }
        catch (Exception e){
            LoaginActivity.this.startActivity(intentLoginFalha);
            LoaginActivity.this.finish();
        }
    }

    public void Login (String ip, String email, String senha) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("authorization", "secret");
        params.put("email", email);
        params.put("password", senha);
        new HttpRequest(getApplicationContext(), params, ip + "usuario/login/", "GET", "AutoLogin").doRequest();
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
        final Intent intentLoginSucesso = new Intent(LoaginActivity.this, ListaSalasActivity.class);
        final Intent intentLoginFalha = new Intent(LoaginActivity.this, LoginActivity.class);
        if (event.getEventName().equals("AutoLogin" + Constants.eventSuccessLabel)) {
            LoaginActivity.this.startActivity(intentLoginSucesso);
            LoaginActivity.this.finish();
        }

        else if (event.getEventName().startsWith("AutoLogin" + Constants.eventErrorLabel)) {
            LoaginActivity.this.startActivity(intentLoginFalha);
            LoaginActivity.this.finish();
        }



    }

}
