package com.example.gerenciamentodesalas.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gerenciamentodesalas.ItemClickListener;
import com.example.gerenciamentodesalas.R;
import com.example.gerenciamentodesalas.TinyDB;
import com.example.gerenciamentodesalas.model.AlocacaoSala;
import com.example.gerenciamentodesalas.model.Usuario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AgendamentoAdapter extends RecyclerView.Adapter {

    private final List<AlocacaoSala> listaAlocacaoSala;
    private Context context;
    TinyDB tinyDB;


    public AgendamentoAdapter(List<AlocacaoSala> listaAlocacao, Context context){
        this.listaAlocacaoSala = listaAlocacao;
        this.context = context;
    }

    private static ItemClickListener itemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alocacao, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getUsuarioAlocacao(List<Usuario> listaUsuario, int idUsuario){
        Usuario usuarioStream = listaUsuario.stream()
                .filter(usuario -> idUsuario == usuario.getId())
                .findFirst()
                .orElse(null);
        if (usuarioStream != null)
            return usuarioStream.getNome();
        else
            return null;
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        AlocacaoSala alocacao = listaAlocacaoSala.get(position);
        tinyDB = new TinyDB(context);
        SimpleDateFormat formatoHorario = new SimpleDateFormat("HH:mm", new Locale("pt", "BR"));
        formatoHorario.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
        holder.textDescAlocacao.setText(alocacao.getDescricao());
        holder.textHorario.setText(formatoHorario.format(alocacao.getDataHoraInicio()) + "\r\n" + formatoHorario.format(alocacao.getDataHoraFim()));
        List<Usuario> listaUsuario = (ArrayList<Usuario>)(Object) tinyDB.getListObject("usuarioList", Usuario.class) ;
        try {
            int idUsuario = alocacao.getIdUsuario();
            System.out.println(idUsuario);
            String nomeUsuario = "Usuário";
            if(Build.VERSION.SDK_INT > 24) {
                nomeUsuario = getUsuarioAlocacao(listaUsuario, idUsuario);
            }
            else {
                for(Usuario usuario: listaUsuario){
                    if(usuario.getId() == idUsuario){
                        nomeUsuario = usuario.getNome();
                    }
                }
            }
            holder.textUsuario.setText(nomeUsuario);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView textHorario;
        final TextView textDescAlocacao;
        final TextView textUsuario;
        public ViewHolder(View view) {
            super(view);
            textHorario = view.findViewById(R.id.textViewHoraAlocacao);
            textDescAlocacao = view.findViewById(R.id.textViewCardAluguel);
            textUsuario = view.findViewById(R.id.textViewUsuarioAlocacao);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if(itemClickListener != null) {
                itemClickListener.onItemClick(getAdapterPosition());
            }
        }

    }
    @Override public int getItemCount() { return listaAlocacaoSala.size(); }

}
