package com.example.gerenciamentodesalas.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Organizacao {
    private final int id;
    private final String nome;
    private final int idOrganizacaoPai;
    private final String tipoOrganizacao;
    private final String dominio;
    private final Date dataCriacao;
    private final Date dataAlteracao;
    private final String CEP;

    public Organizacao(int id, String nome, int idOrganizacaoPai, String tipoOrganizacao, String dominio, Date dataCriacao, Date dataAlteracao, String CEP) {
        this.id = id;
        this.nome = nome;
        this.idOrganizacaoPai = idOrganizacaoPai;
        this.tipoOrganizacao = tipoOrganizacao;
        this.dominio = dominio;
        this.dataCriacao = dataCriacao;
        this.dataAlteracao = dataAlteracao;
        this.CEP = CEP;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getIdOrganizacaoPai() {
        return idOrganizacaoPai;
    }

    public String getTipoOrganizacao() {
        return tipoOrganizacao;
    }

    public String getDominio() {
        return dominio;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public String getCEP() {
        return CEP;
    }
}
