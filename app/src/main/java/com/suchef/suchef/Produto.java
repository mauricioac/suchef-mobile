package com.suchef.suchef;

import java.util.Objects;

/**
 * Created by mauricio on 28/11/2017.
 */

public class Produto {
    private int id;
    private float preco;
    private String nome;
    private String descricao;
    private int filiais_id;

    public Produto(int id, float preco, String nome, String descricao, int filiais_id) {
        this.id = id;
        this.preco = preco;
        this.nome = nome;
        this.descricao = descricao;
        this.filiais_id = filiais_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getPreco() {
        return preco;
    }

    public void setPreco(float preco) {
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getFiliais_id() {
        return filiais_id;
    }

    public void setFiliais_id(int filiais_id) {
        this.filiais_id = filiais_id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Produto))
            return false;
        if (obj == this)
            return true;

        Produto rhs = (Produto) obj;
        return rhs.getId() == getId();
    }
}
