package data.model.impl.proxy;

import data.model.impl.RichiestaImpl;
import framework.data.DataItemProxy;
import framework.data.DataLayer;
import java.util.Date;

public class RichiestaProxy extends RichiestaImpl implements DataItemProxy {
    
    protected boolean modified;
    
    protected DataLayer dataLayer;
    
    public RichiestaProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }
    @Override
    public void setData(Date data) {
        super.setData(data);
        this.modified = true;
    }
    @Override
    public void setStato(String stato) {
        super.setStato(stato);
        this.modified = true;
    }
    
    @Override
    public void setNote(String note) {
        super.setNote(note);
        this.modified = true;
    }
    
    
    @Override
    public void setIDcat(String idCat) {
        super.setIDcat(idCat);
        this.modified = true;
    }
    
    @Override
    public void setIDtec(String idTec) {
        super.setIDtec(idTec);
        this.modified = true;
    }
    
    @Override
    public void setIDord(String idOrd) {
        super.setIDord(idOrd);
        this.modified = true;
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
