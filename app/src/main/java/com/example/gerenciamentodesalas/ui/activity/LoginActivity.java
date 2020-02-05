package com.example.gerenciamentodesalas.ui.activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.service.FileWritterService;
import com.example.gerenciamentodesalas.service.HttpServiceGetUsuario;
import com.example.gerenciamentodesalas.service.HttpServiceLogar;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    TextView viewEmail;
    TextView viewSenha;
    Button btnLogin, btnRegistro;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        builder = new AlertDialog.Builder(LoginActivity.this);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);
        viewEmail  = findViewById(R.id.inputLogin);
        viewSenha = findViewById(R.id.inputSenha);
        final Intent intent = new Intent(LoginActivity.this, ListaSalasActivity.class);
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
                String email = viewEmail.getText().toString();
                String password = viewSenha.getText().toString();
                String resposta = "400";
                Resources resources=getResources();
                String ip=resources.getString(R.string.ip);
                try {
                    resposta = new HttpServiceLogar(email, password, ip).execute().get();
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
                if (resposta == "200"){
                    try {
                        String usuarioJSONStr = new HttpServiceGetUsuario(email, ip).execute().get();
                        JSONObject usuarioJSON = new JSONObject(usuarioJSONStr);
                        String nomeOrganizacao = usuarioJSON.getJSONObject("idOrganizacao").getString("nome");
                        String idOrganizacao = usuarioJSON.getJSONObject("idOrganizacao").getString("id");
                        intent.putExtra("nomeOrganizacao", nomeOrganizacao);
                        intent.putExtra("idOrganizacao", idOrganizacao);
                        FileWritterService fileWritterService = new FileWritterService();
                        boolean isFilePresent = fileWritterService.arquivoExiste(LoginActivity.this, email + ".json");
                        if(isFilePresent) {
                            String jsonStringLocal = fileWritterService.lerArquivo(LoginActivity.this, email + ".json");
                            if (jsonStringLocal.equals(usuarioJSONStr)) {
                                builder.setMessage("Login efetuado com sucesso!").setTitle("Sucesso!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        LoginActivity.this.startActivity(intent);
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                            else {
                                boolean arquivoDeletado = fileWritterService.arquivoDeletado(LoginActivity.this, email + ".json");
                                if (arquivoDeletado) {
                                    boolean isFileCreated = fileWritterService.criarArquivo(LoginActivity.this, email + ".json", usuarioJSONStr);
                                    if(isFileCreated) {
                                        builder.setMessage("Login efetuado com sucesso!").setTitle("Sucesso!");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                LoginActivity.this.startActivity(intent);
                                            }
                                        });
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    } else {
                                        builder.setMessage("Falha ao recuperar os dados do usuário, tente novamente!").setTitle("Falha");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                                else {
                                    builder.setMessage("Falha ao atualizar os dados do usuário, tente novamente!").setTitle("Falha");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }
                        } else {
                            boolean isFileCreated = fileWritterService.criarArquivo(LoginActivity.this, email + ".json", usuarioJSONStr);
                            if(isFileCreated) {
                                builder.setMessage("Login efetuado com sucesso!").setTitle("Sucesso!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        LoginActivity.this.startActivity(intent);
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } else {
                                builder.setMessage("Falha ao recuperar os dados do usuário, tente novamente!").setTitle("Falha");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        builder.setMessage("Falha ao recuperar os dados do usuário, tente novamente!").setTitle("Falha");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }
                else {
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
        });
    }
}
