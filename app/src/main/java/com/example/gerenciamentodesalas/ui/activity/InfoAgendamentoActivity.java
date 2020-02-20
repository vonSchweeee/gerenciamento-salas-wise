package com.example.gerenciamentodesalas.ui.activity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.TinyDB;
import com.example.gerenciamentodesalas.model.AlocacaoSala;
import com.example.gerenciamentodesalas.model.AlocacaoSalaPOST;
import com.example.gerenciamentodesalas.model.Constants;
import com.example.gerenciamentodesalas.model.Event;
import com.example.gerenciamentodesalas.model.Sala;
import com.example.gerenciamentodesalas.model.Usuario;
import com.example.gerenciamentodesalas.service.HttpRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InfoAgendamentoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    Boolean inicioAlterado = false;
    Boolean fimAlterado = false;
    Date horaInicio;
    Date horaFim;
    String data;
    AlertDialog.Builder builder;
    TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_agendamento);
        Toolbar toolbar = findViewById(R.id.toolbar);
        tinyDB = new TinyDB(getApplicationContext());
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View headerView = navigationView.getHeaderView(0);
        TextView navNome =  headerView.findViewById(R.id.nav_usuario);
        TextView navOrg = headerView.findViewById(R.id.nav_org);
        TextView navEmail = headerView.findViewById(R.id.nav_email);
        Sala sala = tinyDB.getObject("salaEscolhida", Sala.class);
        Usuario usuario = tinyDB.getObject("usuario", Usuario.class);
        Resources resources = getResources();
        final ImageView imgViewInicio = findViewById(R.id.imageViewHoraInicio);
        final ImageView imgViewFim = findViewById(R.id.imageViewHoraFim);
        final TextView textHoraInicio = findViewById(R.id.textHoraInicioHorario);
        final TextView textHoraFim = findViewById(R.id.textHoraFimHorario);
        final TextInputEditText textDescricao = findViewById(R.id.inputDescricao);
        final Button btnConfirmar = findViewById(R.id.btnConfirmarAgendamento);
        final SimpleDateFormat formatoTempo = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat formatoDataHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat formatoDataBr = new SimpleDateFormat("dd/MM/yyyy");
        final SimpleDateFormat formatoDataSql = new SimpleDateFormat("yyyy-MM-dd");
        final int idUsuario = usuario.getId();
        final int idSala = sala.getId();
        final String ip = resources.getString(R.string.ip);
        navNome.setText(usuario.getNome());
        navOrg.setText(usuario.getIdOrganizacao().getNome());
        navEmail.setText(usuario.getEmail());
        String modoAgendamento;
        modoAgendamento = tinyDB.getString("modoAgendamento");

        if (modoAgendamento.equals("criar")) {
            System.out.println("Criando Agendamento");
            try {
                data = formatoDataSql.format(formatoDataBr.parse(tinyDB.getString("dataEscolhida")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            imgViewInicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar tempoAgora = Calendar.getInstance();
                    int hour = tempoAgora.get(Calendar.HOUR_OF_DAY);
                    int minute = tempoAgora.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(InfoAgendamentoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            Date horaEscolhida = null;
                            try {
                                horaEscolhida = formatoTempo.parse(selectedHour + ":" + selectedMinute);
                                horaInicio = formatoDataHora.parse(data + " " + selectedHour + ":" + selectedMinute + ":" + "00");
                                System.out.println(horaInicio);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String horaEscolhidaStr = formatoTempo.format(horaEscolhida);
                            textHoraInicio.setText(horaEscolhidaStr);
                            inicioAlterado = true;
                        }
                    }, hour, minute, true);
                    mTimePicker.setTitle("Escolha o horário");
                    mTimePicker.show();
                }
            });
            imgViewFim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar horaAtual = Calendar.getInstance();
                    int hour = horaAtual.get(Calendar.HOUR_OF_DAY);
                    int minute = horaAtual.get(Calendar.MINUTE);
                    TimePickerDialog timePicker;
                    timePicker = new TimePickerDialog(InfoAgendamentoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            Date horaEscolhida = null;
                            try {
                                horaEscolhida = formatoTempo.parse(selectedHour + ":" + selectedMinute);
                                horaFim = formatoDataHora.parse(data + " " + selectedHour + ":" + selectedMinute + ":" + "00");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String horaEscolhidaStr = formatoTempo.format(horaEscolhida);
                            textHoraFim.setText(horaEscolhidaStr);
                            fimAlterado = true;
                        }
                    }, hour, minute, true);
                    timePicker.setTitle("Escolha o horário");
                    timePicker.show();
                }
            });
            btnConfirmar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder = new AlertDialog.Builder(InfoAgendamentoActivity.this);
                    if (inicioAlterado && fimAlterado) {
                        if (horaInicio.before(horaFim)) {
                            try {
                                String dataHoraInicio = formatoDataHora.format(horaInicio);
                                String dataHoraFim = formatoDataHora.format(horaFim);
                                System.out.println(dataHoraInicio + " " + dataHoraFim);
                                String descricao = textDescricao.getText().toString();
                                CriarAlocacao(ip, idSala, idUsuario, descricao, dataHoraInicio, dataHoraFim);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            builder.setMessage("O horário de início deve ser antes do fim.").setTitle("Dados incorretos.");
                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
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
        else if(modoAgendamento.equals("excluir"))  {
                textHoraInicio.setEnabled(false);
                textHoraFim.setEnabled(false);
                textDescricao.setEnabled(false);
                imgViewFim.setEnabled(false);
                imgViewInicio.setEnabled(false);
                System.out.println("Excluindo Agendamento");
                Date dataHoraInicio = null;
                Date dataHoraFim = null;
                String descricao = null;
                String horaInicio = null;
                String horaFim = null;
                AlocacaoSala alocacaoSelecionada = null;
                try {
                    alocacaoSelecionada = tinyDB.getObject("alocacaoSelecionada", AlocacaoSala.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    dataHoraInicio = alocacaoSelecionada.getDataHoraInicio();
                    dataHoraFim = alocacaoSelecionada.getDataHoraFim();
                    descricao = alocacaoSelecionada.getDescricao();
                    horaInicio = formatoTempo.format(dataHoraInicio);
                    horaFim = formatoTempo.format(dataHoraFim);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textHoraInicio.setText(horaInicio);
                textHoraFim.setText(horaFim);
                textDescricao.setText(descricao);

                final AlocacaoSala finalAlocacaoSelecionada = alocacaoSelecionada;
                btnConfirmar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String retorno = null;
                        try {
                            Resources resources = getResources();
                            String url = resources.getString(R.string.ip) + "alocacao/excluir";
                            String id = String.valueOf(finalAlocacaoSelecionada.getId());
                            DeletarAlocacao(url, id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        }
        else if (modoAgendamento.equals("alterar")) {
                textHoraInicio.setEnabled(false);
                textHoraFim.setEnabled(false);
                textDescricao.setEnabled(false);
                imgViewFim.setEnabled(false);
                imgViewInicio.setEnabled(false);
                System.out.println("Alterando Agendamento");
                Date dataHoraInicio = null;
                Date dataHoraFim = null;
                String descricao = null;
                String horaInicio = null;
                String horaFim = null;
                AlocacaoSala alocacaoSelecionada = null;
                try {
                    alocacaoSelecionada = tinyDB.getObject("alocacaoSelecionada", AlocacaoSala.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    dataHoraInicio = alocacaoSelecionada.getDataHoraInicio();
                    dataHoraFim = alocacaoSelecionada.getDataHoraFim();
                    descricao = alocacaoSelecionada.getDescricao();
                    horaInicio = formatoTempo.format(dataHoraInicio);
                    horaFim = formatoTempo.format(dataHoraFim);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textHoraInicio.setText(horaInicio);
                textHoraFim.setText(horaFim);
                textDescricao.setText(descricao);

                final AlocacaoSala finalAlocacaoSelecionada = alocacaoSelecionada;
                btnConfirmar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String retorno = null;
                        try {
                            Resources resources = getResources();
                            String url = resources.getString(R.string.ip) + "alocacao/excluir";
                            String id = String.valueOf(finalAlocacaoSelecionada.getId());
                            DeletarAlocacao(url, id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
    }
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    public void onStop(){
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    public void CriarAlocacao (String url, int idSala, int idUsuario, String descricao, String horaInicio, String horaFim) {
        Map<String, String> params = new HashMap<String, String>();
        System.out.println(horaInicio);
        Gson gson = new Gson();
        try {
            AlocacaoSalaPOST alocacao = new AlocacaoSalaPOST(idSala, idUsuario, horaInicio, horaFim, descricao);
            String jsonAlocacaoStr = gson.toJson(alocacao, AlocacaoSalaPOST.class);
            String novaAlocacao64 = Base64.encodeToString(jsonAlocacaoStr.getBytes("UTF-8"), Base64.NO_WRAP);
            System.out.println(jsonAlocacaoStr);
            params.put("novaAlocacao", novaAlocacao64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("authorization", "secret");

        new HttpRequest(getApplicationContext(), params, url + "alocacao/reservar", "POST", "Reservar").doRequest();
    }
    public void DeletarAlocacao(String url, String id) {
        Map<String, String> params = new HashMap<String, String>();
        Resources resources = getResources();
        String auth = resources.getString(R.string.auth);
        params.put("id", id);
        params.put("authorization", auth);
        new HttpRequest(InfoAgendamentoActivity.this, params, url, "DELETE", "deleteAlocacao").doRequest();
    }


    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                tinyDB = new TinyDB(getApplicationContext());
                tinyDB.clear();
                Intent intent = new Intent(InfoAgendamentoActivity.this, LoginActivity.class);
                InfoAgendamentoActivity.this.startActivity(intent);
        }
        return true;
    }

    @Subscribe
    public void customEventReceived(Event event) {
        final Intent intent = new Intent(InfoAgendamentoActivity.this, CalendarioAgendamentoActivity.class);
        builder = new AlertDialog.Builder(InfoAgendamentoActivity.this);
        if (event.getEventName().equals("Reservar" + Constants.eventSuccessLabel)) {
            if (event.getEventStatusCode() == 201) {
                builder.setMessage("Reserva realizada com sucesso!").setTitle("Sucesso!");
                builder.setPositiveButton("Ir para Alocações", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InfoAgendamentoActivity.this.finish();
                    }
                });
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                builder.setMessage(event.getEventMsg()).setTitle("Erro.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        else if (event.getEventName().equals("Reservar" + Constants.eventErrorLabel)) {
            builder.setMessage("Erro no servidor ao realizar a alocação.").setTitle("Erro.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
            AlertDialog dialog = builder.create();
            dialog.show();
            }
        else if (event.getEventName().startsWith("Reservar" + Constants.eventErrorLabel)) {
            builder.setMessage(event.getEventMsg()).setTitle("Erro.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        builder = new AlertDialog.Builder(InfoAgendamentoActivity.this);
        if (event.getEventName().equals("deleteAlocacao" + Constants.eventSuccessLabel)) {
            builder.setMessage("Alocação desativada com sucesso.").setTitle("Sucesso!");
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Retornar para alocações", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    InfoAgendamentoActivity.this.finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (event.getEventName().equals("deleteAlocacao" + Constants.eventErrorLabel)) {
            builder.setMessage(event.getEventMsg()).setTitle("Erro.");
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (event.getEventName().startsWith("deleteAlocacao" + Constants.eventErrorLabel)) {
            builder.setMessage("Erro no servidor.").setTitle("Erro.");
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
            super.onBackPressed();
    }
}
