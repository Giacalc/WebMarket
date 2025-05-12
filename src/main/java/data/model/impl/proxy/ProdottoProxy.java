package data.model.impl.proxy;

import data.model.impl.ProdottoImpl;
import framework.data.DataItemProxy;
import framework.data.DataLayer;

public class ProdottoProxy extends ProdottoImpl implements DataItemProxy {
    
    protected boolean modified;
    
    protected DataLayer dataLayer;
    
    public ProdottoProxy(DataLayer d) {
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
    
    @Override
    public void setNome_produttore(String nome_produttore) {
        super.setNome_produttore(nome_produttore);
        this.modified = true;
    }
    
    @Override
    public void setPrezzo(double prezzo) {
        super.setPrezzo(prezzo);
        this.modified = true;
    }
    
    
    @Override
    public void setUrl(String url) {
        super.setUrl(url);
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
