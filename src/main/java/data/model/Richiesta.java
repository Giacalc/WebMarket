package data.model;

import framework.data.DataItem;
import java.util.Date;

public interface Richiesta extends DataItem<Integer>{
    
    String getStato();
    
    String getNote();
    
    Date getData();
     
    void setStato(String stato);
     
    void setNote(String note);
    
    void setData(Date data);
    
    String getIDord();
    
    void setIDord(String idOrd);
    
    String getIDcat();
    
    void setIDcat(String idCat);
    
    String getIDtec();
    
    void setIDtec(String idTec);
}
