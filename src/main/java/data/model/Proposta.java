package data.model;

import framework.data.DataItem;
import java.util.Date;

public interface Proposta extends DataItem<Integer>{
    
    
    Date getData();
    
    String getRevisione();
    
    void setRevisione(String revisione);
    
    String getNote();
    
    void setNote(String note);
    
    void setData(Date data);
    
    String getRev_motivazione();
    
    void setRev_motivazione(String rev_motivazione);
    
    int getIDric();
    
    void setIDric(int idRic);
    
    String getIDprod();
    
    void setIDprod(String idProd);
    
    String getIDtec();
    
    void setIDtec(String idTec);
    
}
