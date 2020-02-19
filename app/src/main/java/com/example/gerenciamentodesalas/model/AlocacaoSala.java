package com.example.gerenciamentodesalas.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlocacaoSala {
    private final int id;
    private final Sala idSala;
    private final int idUsuario;
    private final Date dataHoraInicio;
    private final Date dataHoraFim;
    private final String descricao;
    private final Boolean ativo;
    private final Date dataCriacao;
    private final Date dataAlteracao;

    public AlocacaoSala(int id, Sala idSala, int idUsuario, Date dataHoraInicio, Date dataHoraFim, String descricao, Boolean ativo, Date dataCriacao, Date dataAlteracao) {
        this.id = id;
        this.idSala = idSala;
        this.idUsuario = idUsuario;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.descricao = descricao;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.dataAlteracao = dataAlteracao;
    }
    public int getId() {return id;}
    public Sala getIdSala() {
        return idSala;
    }
    public int getIdUsuario() {
        return idUsuario;
    }
    public Date getDataHoraInicio() {
        return dataHoraInicio;
    }
    public Date getDataHoraFim() {
        return dataHoraFim;
    }
    public String getDescricao() {
        return descricao;
    }
    public Boolean getAtivo() {
        return ativo;
    }
    public Date getDataCriacao() {
        return dataCriacao;
    }
    public Date getDataAlteracao() {
        return dataAlteracao;
    }
}
