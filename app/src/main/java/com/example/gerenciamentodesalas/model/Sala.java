package com.example.gerenciamentodesalas.model;

import java.math.BigDecimal;
import java.util.Date;

public class Sala {

    private final int id;
    private final Organizacao idOrganizacao;
    private final String nome;
    private final int quantidadePessoasSentadas;
    private final boolean possuiMultimidia;
    private final boolean possuiAC;
    private final BigDecimal areaDaSala;
    private final String localizacao;
    private final Double latitude;
    private final Double longitude;
    private final boolean ativo;
    private final Date dataCriacao;
    private final Date dataAlteracao;

    public Sala (int id, Organizacao idOrganizacao, String nome, int quantidadePessoasSentadas, boolean possuiMultimidia, boolean possuiAC, BigDecimal areaDaSala, String localizacao, Double latitude, Double longitude, boolean ativo, Date dataCriacao, Date dataAlteracao) {
        this.id = id;
        this.idOrganizacao = idOrganizacao;
        this.nome = nome;
        this.quantidadePessoasSentadas = quantidadePessoasSentadas;
        this.possuiMultimidia = possuiMultimidia;
        this.possuiAC = possuiAC;
        this.areaDaSala = areaDaSala;
        this.localizacao = localizacao;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.dataAlteracao = dataAlteracao;
    }

    public int getId() {
        return id;
    }

    public Organizacao getIdOrganizacao() {
        return idOrganizacao;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidadePessoasSentadas() {
        return quantidadePessoasSentadas;
    }

    public boolean isPossuiMultimidia() {
        return possuiMultimidia;
    }

    public boolean isPossuiAC() {
        return possuiAC;
    }

    public BigDecimal getAreaDaSala() {
        return areaDaSala;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }
}