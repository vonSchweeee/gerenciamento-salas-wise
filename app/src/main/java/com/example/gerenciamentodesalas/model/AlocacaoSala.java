package com.example.gerenciamentodesalas.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlocacaoSala {
    private Sala sala;
    private final int id;
    private final Sala idSala;
    private final String organizador;
    private final Date dataHoraInicio;
    private final Date dataHoraFim;
    private final String descricao;
    private final Boolean ativo;
    private final Date dataCriacao;
    private final Date dataAlteracao;

    public AlocacaoSala(int id, Sala idSala, String organizador, Date dataHoraInicio, Date dataHoraFim, String descricao, Boolean ativo, Date dataCriacao, Date dataAlteracao) {
        this.id = id;
        this.idSala = idSala;
        this.organizador = organizador;
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
    public String getOrganizador() {
        return organizador;
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
