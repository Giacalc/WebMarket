package data.model.impl;
import data.model.*;
import framework.data.DataItemImpl;

public class MicrocategoriaImpl extends DataItemImpl<String> implements Microcategoria {
    private String nome;
    private String sottocategoria;

    public MicrocategoriaImpl() {
        this.nome = "";
        this.sottocategoria = "";
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
    public String getSottocategoria() {
        return sottocategoria;
    }
    @Override
    public void setSottocategoria(String sottocategoria) {
        this.sottocategoria = sottocategoria;
    }
    
}

