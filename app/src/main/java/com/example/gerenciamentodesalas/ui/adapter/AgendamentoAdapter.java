package com.example.gerenciamentodesalas.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.model.AlocacaoSala;

import java.text.SimpleDateFormat;
import java.util.List;

public class AgendamentoAdapter extends BaseAdapter {

    private final List<AlocacaoSala> listaAlocacaoSala;
    private Context context;


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
        SimpleDateFormat formatoHorario = new SimpleDateFormat("HH:mm");
        AlocacaoSala alocacaoSala = listaAlocacaoSala.get(position);
        TextView textHorario = viewCriada.findViewById(R.id.textViewHoraAlocacao);
        TextView textDescAlocacao = viewCriada.findViewById(R.id.textViewCardAluguel);
        textDescAlocacao.setText(alocacaoSala.getDescricao());
        textHorario.setText(formatoHorario.format(alocacaoSala.getDataHoraInicio()));
        return viewCriada;


    }
}
