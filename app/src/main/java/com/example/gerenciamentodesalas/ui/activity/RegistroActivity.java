package com.example.gerenciamentodesalas.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.model.Organizacao;
import com.example.gerenciamentodesalas.service.HttpServiceRegistrar;
import com.example.gerenciamentodesalas.service.HttpServiceVerificarDominio;
import com.example.gerenciamentodesalas.ui.adapter.SpinnerAdapter;

import java.util.List;

public class RegistroActivity extends AppCompatActivity {
    private Spinner Spinner;
    private SpinnerAdapter adapter;
    int idEmpresaEscolhida;
    Boolean validacaoDominio = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        final EditText inputNome = findViewById(R.id.inputNomeRegistro);
        final EditText inputEmail = findViewById(R.id.inputEmailRegistro);
        final EditText inputSenha = findViewById(R.id.inputSenhaRegistro);
        final TextView textViewOrganizacao = findViewById(R.id.textViewOrganizacao);
        final Spinner spinnerEmpresa = findViewById(R.id.spinnerEmpresas);
        final Button btnRegistrar = findViewById(R.id.btnRegistrar);
        Resources resources=getResources();
        final String ip=resources.getString(R.string.ip);

        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String emailAfterTextChange = inputEmail.getText().toString();
                    if (emailAfterTextChange.contains("@")) {
                        String[] emailCompleto = emailAfterTextChange.split("@");
                        if (emailCompleto.length > 1) {
                            String dominio = emailCompleto[1];
                            if (dominio.contains(".")) {
                                try {
                                    List<Organizacao> organizacoes = new HttpServiceVerificarDominio(dominio, ip).execute().get();
                                    Organizacao[] organizacoesItems = new Organizacao[organizacoes.size()];

                                    for(int i = 0; i < organizacoes.size() ; i++)
                                    {
                                        organizacoesItems[i] = organizacoes.get(i);
                                    }
                                    if (organizacoes != null) {
                                        adapter = new SpinnerAdapter(RegistroActivity.this, android.R.layout.simple_spinner_item, organizacoesItems);
                                        spinnerEmpresa.setAdapter(adapter);
                                        textViewOrganizacao.setText(organizacoes.get(0).getNome() + " - " + organizacoes.get(0).getTipoOrganizacao() + " - " + organizacoes.get(0).getCEP());
                                        validacaoDominio = true;
                                    } else {
                                        textViewOrganizacao.setText("Domínio de e-mail inválido");
                                        validacaoDominio = false;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        });
        spinnerEmpresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                Organizacao empresaEscolhida = adapter.getItem(position);
                idEmpresaEscolhida = empresaEscolhida.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validacaoDominio == true) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistroActivity.this);
                    final Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                    String nome = inputNome.getText().toString();
                    String email = inputEmail.getText().toString();
                    String password = inputSenha.getText().toString();
                    int idEmpresa = idEmpresaEscolhida;
                    String resposta = "400";
                    try {
                        resposta = new HttpServiceRegistrar(nome, email, password, idEmpresa, ip).execute().get();
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
                    if (resposta == "201") {
                        builder.setMessage("Usuário cadastrado com sucesso!").setTitle("Sucesso!");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                RegistroActivity.this.startActivity(intent);
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else if (resposta == "400") {
                        builder.setMessage("Erro no processo de login").setTitle("Erro");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        builder.setMessage(resposta + ".").setTitle("Dados incorretos");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
        });
    }
}