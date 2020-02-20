package com.example.gerenciamentodesalas.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.model.Constants;
import com.example.gerenciamentodesalas.model.Event;
import com.example.gerenciamentodesalas.model.Organizacao;
import com.example.gerenciamentodesalas.service.HttpRequest;
import com.example.gerenciamentodesalas.service.post.HttpServiceRegistrar;
import com.example.gerenciamentodesalas.service.get.HttpServiceVerificarDominio;
import com.example.gerenciamentodesalas.ui.adapter.SpinnerAdapter;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {
    private Spinner Spinner;
    private SpinnerAdapter adapter;
    int idEmpresaEscolhida;
    Boolean validacaoDominio;
    List<Organizacao> organizacoes;
    EditText inputEmail;
    EditText inputNome;
    EditText inputSenha;
    Spinner spinnerEmpresa;
    TextView textViewOrganizacao;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        validacaoDominio = false;
        builder = new AlertDialog.Builder(RegistroActivity.this);
        inputNome = findViewById(R.id.inputNomeRegistro);
        inputEmail = findViewById(R.id.inputEmailRegistro);
        inputSenha = findViewById(R.id.inputSenhaRegistro);
        textViewOrganizacao = findViewById(R.id.textViewOrganizacao);
        spinnerEmpresa = findViewById(R.id.spinnerEmpresas);
        final Button btnRegistrar = findViewById(R.id.btnRegistrar);
        Resources resources=getResources();
        final String ip=resources.getString(R.string.ip);
        organizacoes = new ArrayList<Organizacao>();
        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    System.out.println("sem foco");
                    String emailAfterTextChange = inputEmail.getText().toString();
                    if (emailAfterTextChange.contains("@")) {
                        System.out.println("email contem arroba");
                        String[] emailCompleto = emailAfterTextChange.split("@");
                        if (emailCompleto.length > 1) {
                            System.out.println("email maior que 1");
                            String dominio = emailCompleto[1];
                            System.out.println("dominio");
                            if (dominio.contains(".")) {
                                System.out.println("dominio contem ponto");
                                try {
                                    verificarDominio(ip + "organizacao/organizacoesByDominio", dominio);
                                    System.out.println("dominio vefificado");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                textViewOrganizacao.setText("Domínio inválido");
                                organizacoes.clear();
                                esvaziaSpinner();
                            }
                        }
                        else {
                            textViewOrganizacao.setText("Domínio inválido");
                            organizacoes.clear();
                            esvaziaSpinner();
                        }
                    }
                    else{
                        textViewOrganizacao.setText("Domínio inválido");
                        organizacoes.clear();
                        esvaziaSpinner();
                    }
                }
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validacaoDominio == true) {
                    final Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                    String nome = inputNome.getText().toString();
                    String email = inputEmail.getText().toString();
                    String password = inputSenha.getText().toString();
                    int idEmpresa = idEmpresaEscolhida;
                    try {
                        fazerRegistro(ip + "usuario/cadastro", nome, email, password, String.valueOf(idEmpresa));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    builder.setMessage("Domínio não validado.").setTitle("Erro");
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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    public void onStop(){
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void fazerRegistro(String url, String nome, String email, String password, String idOrganizacao) {
        Map<String, String> params = new HashMap<String, String>();
        Resources resources = getResources();
        params.put("authorization", resources.getString(R.string.auth));
        JSONObject usuarioJson = new JSONObject();
        try {
            usuarioJson.put("email", email);
            usuarioJson.put("nome", nome);
            usuarioJson.put("senha", password);
            usuarioJson.put("idOrganizacao", idOrganizacao);
            String novoUsuarioEncodado = Base64.encodeToString(usuarioJson.toString().getBytes("UTF-8"), Base64.NO_WRAP);
            params.put("novoUsuario", novoUsuarioEncodado);
            new HttpRequest(RegistroActivity.this, params, url, "POST", "Registrar").doRequest();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    private void verificarDominio(String url, String dominio) {
        Map<String, String> params = new HashMap<String, String>();
        Resources resources = getResources();
        params.put("authorization", resources.getString(R.string.auth));
        params.put("dominio", dominio);
        new HttpRequest(RegistroActivity.this, params, url, "GET", "verificarDominio").doRequest();
    }

    @Subscribe
    public void customEventReceived(Event event) {
        System.out.println(event.getEventStatusCode());
        final Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroActivity.this);
        if (event.getEventName().equals("verificarDominio" + Constants.eventSuccessLabel)) {
            JSONArray organizacoesJson;
            try {
                Gson gson = new Gson();
                organizacoesJson = new JSONArray(event.getEventMsg());
                System.out.println(organizacoesJson.toString());
                for (int i = 0; i<organizacoesJson.length(); i++){
                    textViewOrganizacao.setText("Domínios encontrados");
                    String organizacaoJsStr = organizacoesJson.getJSONObject(i).toString();
                    organizacoes.add(i, gson.fromJson(organizacaoJsStr, Organizacao.class));
                }
            } catch (Exception e) {
                e.printStackTrace();
                textViewOrganizacao.setText("Domínio inválido");
                organizacoes.clear();
                esvaziaSpinner();
            }

            Organizacao[] organizacoesItems = new Organizacao[organizacoes.size()];

            for (int i = 0; i < organizacoes.size(); i++) {
                organizacoesItems[i] = organizacoes.get(i);
            }
            if (organizacoes.size() > 0 && ! event.getEventMsg().equals("[]")) {
                validacaoDominio = true;
                adapter = null;
                spinnerEmpresa.setAdapter(adapter);
                adapter = new SpinnerAdapter(RegistroActivity.this, android.R.layout.simple_spinner_item, organizacoesItems);
                organizacoes.clear();
                esvaziaSpinner();
                spinnerEmpresa.setAdapter(adapter);
                spinnerEmpresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view,
                                               int position, long id) {
                        try {
                            idEmpresaEscolhida = organizacoesItems[position].getId();
                            System.out.println(idEmpresaEscolhida);
                        }
                        catch (Exception e) {}
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapter) {
                    }
                });
                textViewOrganizacao.setText(organizacoes.get(0).getNome() + " - " + organizacoes.get(0).getTipoOrganizacao() + " - " + organizacoes.get(0).getCEP());
            }
            if (event.getEventMsg().equals("[]")) {
                textViewOrganizacao.setText("Domínio inválido");
                organizacoes.clear();
                esvaziaSpinner();
            }
        }
        else if (event.getEventName().startsWith("verificarDominio" + Constants.eventErrorLabel)) {
            textViewOrganizacao.setText("Domínio inválido");
            organizacoes.clear();
            esvaziaSpinner();
        }

        else if (event.getEventName().equals("Registrar" + Constants.eventSuccessLabel)) {
            if (event.getEventStatusCode() == 201) {
                builder.setMessage("Usuário cadastrado com sucesso!").setTitle("Sucesso!");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RegistroActivity.this.startActivity(intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        if(event.getEventStatusCode() == 400) {
            builder.setMessage(event.getEventMsg()).setTitle("Erro ao criar o usuário");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (event.getEventName().equals("Registrar" + Constants.eventErrorLabel)) {
            builder.setMessage("Falha na conexão").setTitle("Erro");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        else if (event.getEventName().startsWith("Registrar" + Constants.eventErrorLabel)){
            builder.setMessage(event.getEventMsg()).setTitle("Erro");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }
    private void esvaziaSpinner() {
        ArrayAdapter adapter = new ArrayAdapter<>(RegistroActivity.this,
                android.R.layout.simple_spinner_item, Collections.emptyList());
        spinnerEmpresa.setAdapter(adapter);
    }
}