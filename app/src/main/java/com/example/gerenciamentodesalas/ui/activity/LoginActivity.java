package com.example.gerenciamentodesalas.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.service.FileWritterService;
import com.example.gerenciamentodesalas.service.get.HttpServiceGetUsuario;
import com.example.gerenciamentodesalas.service.get.HttpServiceLogar;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    TextView viewEmail;
    TextView viewSenha;
    Button btnLogin, btnRegistro;
    AlertDialog.Builder builder;
    public static final String USER_PREFERENCE = "INFO_AUTO_LOGIN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final FileWritterService fileWritterService = new FileWritterService();
        final Intent intent = new Intent(LoginActivity.this, ListaSalasActivity.class);
        Boolean arquivoExiste = fileWritterService.arquivoExiste(this, "usuariologado.json");
        if (arquivoExiste) {
            try {
                Resources resources=getResources();
                String ip = resources.getString(R.string.ip);
                SharedPreferences sp = getSharedPreferences(USER_PREFERENCE, MODE_PRIVATE);
                String email = sp.getString("email", null);
                String senha= sp.getString("senha", null);
                String resposta = new HttpServiceLogar(email, senha, ip).execute().get();
                if (resposta == "200") {
                    LoginActivity.this.startActivity(intent);
                    LoginActivity.this.finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        builder = new AlertDialog.Builder(LoginActivity.this);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);
        viewEmail  = findViewById(R.id.inputLogin);
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
                String email = viewEmail.getText().toString();
                String password = viewSenha.getText().toString();
                String resposta = "400";
                Resources resources=getResources();
                String ip = resources.getString(R.string.ip);
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
                        SharedPreferences.Editor editorUser = getSharedPreferences(USER_PREFERENCE, MODE_PRIVATE).edit();
                        String usuarioJSONStr = new HttpServiceGetUsuario(email, ip).execute().get();
                        JSONObject usuarioJSON = new JSONObject(usuarioJSONStr);
                        String nomeOrganizacao = usuarioJSON.getJSONObject("idOrganizacao").getString("nome");
                        String idOrganizacao = usuarioJSON.getJSONObject("idOrganizacao").getString("id");
                        String nomeUsuario = usuarioJSON.getString("nome");
                        editorUser.putString("nomeOrganizacao", nomeOrganizacao);
                        editorUser.putString("idOrganizacao", idOrganizacao);
                        boolean isFilePresent = fileWritterService.arquivoExiste(LoginActivity.this, "usuariologado.json");
                        if(isFilePresent) {
                            String jsonStringLocal = fileWritterService.lerArquivo(LoginActivity.this, "usuariologado.json");
                            if (jsonStringLocal.equals(usuarioJSONStr)) {
                                editorUser.putString("email", email);
                                editorUser.putString("senha", password);
                                editorUser.putString("nome", nomeUsuario);
                                editorUser.putString("organizacao", nomeOrganizacao);
                                editorUser.apply();
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
                                boolean arquivoDeletado = fileWritterService.arquivoDeletado(LoginActivity.this, "usuariologado.json");
                                if (arquivoDeletado) {
                                    boolean isFileCreated = fileWritterService.criarArquivo(LoginActivity.this, "usuariologado.json", usuarioJSONStr);
                                    if(isFileCreated) {
                                        builder.setMessage("Login efetuado com sucesso!").setTitle("Sucesso!");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                LoginActivity.this.startActivity(intent);
                                            }
                                        });
                                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
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
                            boolean isFileCreated = fileWritterService.criarArquivo(LoginActivity.this, "usuariologado.json", usuarioJSONStr);
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
