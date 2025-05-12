package data.model;
import framework.data.DataItem;

public interface Caratteristica extends DataItem<String> {
    
    String getNome();
    
    void setNome(String nome);
    
    //relazione molti a molti
    String getMicrocategoria();
    
    void setMicrocategoria(String mcategoria);
}
