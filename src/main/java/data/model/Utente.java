package data.model;

import framework.data.DataItem;

public interface Utente extends DataItem<String> {
    
    String getUsername();
    
    void setUsername(String username);
    
    String getNome();
    
    void setNome(String nome);
    
    String getCognome();
    
    void setCognome(String cognome);
    
    String getPassword();
    
    void setPassword(String password);
    
    String getMail();
    
    void setMail(String mail);
    
    String getRuolo();
    
    void setRuolo(String ruolo);
}
