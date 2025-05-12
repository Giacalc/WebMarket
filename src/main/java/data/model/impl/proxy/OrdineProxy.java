package data.model.impl.proxy;

import data.model.impl.OrdineImpl;
import framework.data.DataItemProxy;
import framework.data.DataLayer;
import java.util.Date;

public class OrdineProxy extends OrdineImpl implements DataItemProxy {
      protected boolean modified;
    
    protected DataLayer dataLayer;
    
    public OrdineProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
    }
    
     @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }
    public void setStato(String stato){
        super.setStato(stato);
        this.modified=true;
    }
    public void setDataOrdine(Date data){
        super.setDataOrdine(data);
        this.modified=true;
    }   
    public void setDataConsegna(Date data){
        super.setDataConsegna(data);
        this.modified=true;
    }   
    public void setIdProp(Integer idProp){
        super.setIdProp(idProp);
        this.modified=true;
    }
    
    //metodi di proxy
    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

    @Override
    public boolean isModified() {
        return modified;
    }
}