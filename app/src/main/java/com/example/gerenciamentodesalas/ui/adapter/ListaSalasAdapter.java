package com.example.gerenciamentodesalas.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.model.Sala;

import java.util.List;

public class ListaSalasAdapter extends BaseAdapter {

    private final List<Sala> salasList;
    private Context context;


    public ListaSalasAdapter(List<Sala> salasList, Context context){

        this.salasList = salasList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return salasList.size();
    }

    @Override
    public Sala getItem(int position) {
        return salasList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View viewCriada = LayoutInflater.from(context).inflate(R.layout.item_sala, parent, false);

        Sala salas = salasList.get(position);

        TextView textView = viewCriada.findViewById(R.id.textViewSalas);
        textView.setText(salas.getNome());
        return viewCriada;


    }
}
