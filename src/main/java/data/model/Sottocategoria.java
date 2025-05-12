package data.model;

import framework.data.DataItem;

public interface Sottocategoria extends DataItem<String> {
    
    String getNome();

    void setNome(String nome);
    
    String getCategoria();
    
    void setCategoria(String categoria);
}
