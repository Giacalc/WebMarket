package data.model.impl.proxy;

import data.model.impl.PropostaImpl;
import framework.data.DataItemProxy;
import framework.data.DataLayer;
import java.util.Date;

public class PropostaProxy extends PropostaImpl implements DataItemProxy {
    
    protected boolean modified;
    
    protected DataLayer dataLayer;
    
    public PropostaProxy(DataLayer d) {
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
    public void setRevisione(String revisione) {
        super.setRevisione(revisione);
        this.modified = true;
    }
    @Override
    public void setNote(String note) {
       super.setNote(note);
       this.modified = true;
    }
    @Override
    public void setRev_motivazione(String rev_motivazione) {
        super.setRev_motivazione(rev_motivazione);
        this.modified = true;
    }
    @Override
    public void setIDric(int idRic) {
       super.setIDric(idRic);
       this.modified = true;
    }
    
    @Override
    public void setIDprod(String idProd) {
        super.setIDprod(idProd);
        this.modified = true;
    }
    @Override
    public void setIDtec(String idTec) {
       super.setIDtec(idTec);
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
