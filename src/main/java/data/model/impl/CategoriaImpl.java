package data.model.impl;
import data.model.*;
import framework.data.DataItemImpl;

public class CategoriaImpl extends DataItemImpl<String> implements Categoria{
    private String nome;

    public CategoriaImpl() {
        this.nome="";
    }
    @Override
    public String getNome() {
        return nome;
    }
    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    
}
