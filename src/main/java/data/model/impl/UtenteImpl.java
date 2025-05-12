package data.model.impl;
import data.model.*;
import framework.data.DataItemImpl;

public class UtenteImpl extends DataItemImpl<String> implements Utente {
    private String username;
    private String nome;
    private String cognome;
    private String password;
    private String mail;
    private String ruolo;

    public UtenteImpl() {
        this.username = "";
        this.nome = "";
        this.cognome = "";
        this.password = "";
        this.mail = "";
        this.ruolo = "";
    }
    @Override
    public String getNome() {
        return nome;
    }
    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }
    @Override
    public String getCognome() {
        return cognome;
    }
    @Override
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public void setUsername(String username) {
        this.username = username;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String getMail() {
        return mail;
    }
    @Override
    public void setMail(String mail) {
        this.mail = mail;
    }
    @Override
    public String getRuolo() {
        return ruolo;
    }
    @Override
    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }
}
