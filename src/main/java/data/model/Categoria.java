package data.model;

import framework.data.DataItem;


public interface Categoria extends DataItem<String>{
    
    String getNome();
    
    void setNome(String nome);
}
