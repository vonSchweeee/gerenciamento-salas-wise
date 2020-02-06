package com.example.gerenciamentodesalas.model;

public class Usuario {
    private final int id;
    private final String nome;
    private final String email;
    private final int idOrganizacao;

    public Usuario(int id, String nome, String email, int idOrganizacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.idOrganizacao = idOrganizacao;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public int getIdOrganizacao() {
        return idOrganizacao;
    }
}
