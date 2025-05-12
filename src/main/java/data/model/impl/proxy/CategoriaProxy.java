package data.model.impl.proxy;

import data.model.impl.CategoriaImpl;
import framework.data.DataItemProxy;
import framework.data.DataLayer;


public class CategoriaProxy extends CategoriaImpl implements DataItemProxy {
    
    protected boolean modified;
    
    protected DataLayer dataLayer;
    
    public CategoriaProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
    }
    
    @Override
    public void setKey(String key) {
        super.setKey(key);
        this.modified = true;
    }
    
    @Override
    public void setNome(String nome) {
        super.setNome(nome);
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
