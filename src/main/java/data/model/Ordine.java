package data.model;
import framework.data.DataItem;
import java.util.Date;

public interface Ordine extends DataItem<Integer>{
    
    String getStato();
    void setStato(String stato);
    
    Date getDataOrdine();
    void setDataOrdine(Date datao);
    
    Date getDataConsegna();
    void setDataConsegna(Date datac);
    
    int getIdProp();
    void setIdProp(int idProp);
}
