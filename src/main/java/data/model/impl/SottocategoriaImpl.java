package data.model.impl;
import data.model.*;
import framework.data.DataItemImpl;

public class SottocategoriaImpl extends DataItemImpl<String> implements Sottocategoria {
    private String nome;
    private String categoria;

    public SottocategoriaImpl() {
        this.nome = "";
        this.categoria = "";
    }
    @Override
    public String getNome() {
        return nome;
    }
    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }
    @Override
    public String getCategoria() {
        return categoria;
    }
    @Override
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
}
