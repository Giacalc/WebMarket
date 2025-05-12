package data.model.impl.proxy;

import data.model.impl.UtenteImpl;
import framework.data.DataItemProxy;
import framework.data.DataLayer;

public class UtenteProxy extends UtenteImpl implements DataItemProxy {
    
    protected boolean modified;
    
    protected DataLayer dataLayer;
    
    public UtenteProxy(DataLayer d) {
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
    public void setUsername(String username) {
        super.setUsername(username);
        this.modified = true;
    }
    
    @Override
    public void setPassword(String password) {
        super.setPassword(password);
        this.modified = true;
    }
    
    @Override
    public void setMail(String mail) {
        super.setMail(mail);
        this.modified = true;
    }
    
    @Override
    public void setRuolo(String ruolo) {
        super.setRuolo(ruolo);
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
