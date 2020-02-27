package com.example.gerenciamentodesalas.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.TinyDB;
import com.example.gerenciamentodesalas.model.AlocacaoSala;
import com.example.gerenciamentodesalas.model.Usuario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AgendamentoAdapter extends BaseAdapter {

    private final List<AlocacaoSala> listaAlocacaoSala;
    private Context context;
    TinyDB tinyDB;


    public AgendamentoAdapter(List<AlocacaoSala> listaAlocacao, Context context){
        this.listaAlocacaoSala = listaAlocacao;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listaAlocacaoSala.size();
    }

    @Override
    public AlocacaoSala getItem(int position) {
        return listaAlocacaoSala.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View viewCriada = LayoutInflater.from(context).inflate(R.layout.item_alocacao, parent, false);
        tinyDB = new TinyDB(context);
        SimpleDateFormat formatoHorario = new SimpleDateFormat("HH:mm", new Locale("pt", "BR"));
        formatoHorario.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
        AlocacaoSala alocacaoSala = listaAlocacaoSala.get(position);
        TextView textHorario = viewCriada.findViewById(R.id.textViewHoraAlocacao);
        TextView textDescAlocacao = viewCriada.findViewById(R.id.textViewCardAluguel);
        TextView textUsuario = viewCriada.findViewById(R.id.textViewUsuarioAlocacao);
        textDescAlocacao.setText(alocacaoSala.getDescricao());
        textHorario.setText(formatoHorario.format(alocacaoSala.getDataHoraInicio()) + "\r\n" + formatoHorario.format(alocacaoSala.getDataHoraFim()));
        List<Usuario> listaUsuario = (ArrayList<Usuario>)(Object) tinyDB.getListObject("usuarioList", Usuario.class) ;
        try {
            int idUsuario = alocacaoSala.getIdUsuario();
            System.out.println(idUsuario);
            String nomeUsuario = listaUsuario.get(idUsuario - 1).getNome();
            System.out.println(nomeUsuario);
            textUsuario.setText(nomeUsuario);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return viewCriada;


    }
}
