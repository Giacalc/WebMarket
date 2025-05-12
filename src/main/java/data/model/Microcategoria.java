package data.model;

import framework.data.DataItem;

public interface Microcategoria extends DataItem<String> {
    
    String getNome();
    
    void setNome(String nome);
    
    String getSottocategoria();
    
    void setSottocategoria(String sottocategoria);
}
