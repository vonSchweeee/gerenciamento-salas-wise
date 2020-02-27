package com.example.gerenciamentodesalas.model;

import androidx.annotation.Nullable;

import java.util.Date;

public class AlocacaoSalaForm {
    @Nullable private final int id;
    private final int idSala;
    private final int idUsuario;
    private final String dataHoraInicio;
    private final String dataHoraFim;
    private final String descricao;
//    @Nullable private final Boolean ativo;
//    @Nullable private final Date dataCriacao;
//    @Nullable private final Date dataAlteracao;

    public AlocacaoSalaForm(int id, int idSala, int idUsuario, String dataHoraInicio, String dataHoraFim, String descricao/*, Boolean ativo, Date dataCriacao, Date dataAlteracao*/) {
        this.id = id;
        this.idSala = idSala;
        this.idUsuario = idUsuario;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.descricao = descricao;
//        this.ativo = ativo;
//        this.dataCriacao = dataCriacao;
//        this.dataAlteracao = dataAlteracao;
    }
    public int getId() {return id;}
    public int getIdSala() {
        return idSala;
    }
    public int getIdUsuario() {
        return idUsuario;
    }
    public String getDataHoraInicio() {
        return dataHoraInicio;
    }
    public String getDataHoraFim() {
        return dataHoraFim;
    }
    public String getDescricao() {
        return descricao;
    }
//    public Boolean getAtivo() {
//        return ativo;
//    }
//    public Date getDataCriacao() {
//        return dataCriacao;
//    }
//    public Date getDataAlteracao() {
//        return dataAlteracao;
//    }
}
