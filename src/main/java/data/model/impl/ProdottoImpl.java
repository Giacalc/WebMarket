package data.model.impl;
import data.model.*;
import framework.data.DataItemImpl;

public class ProdottoImpl extends DataItemImpl<String> implements Prodotto {
    private String nome;
    private String nome_produttore;
    private double prezzo;
    private String url;

    public ProdottoImpl() {
        this.nome = "";
        this.nome_produttore = "";
        this.prezzo = 0;
        this.url = "";
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
    public String getNome_produttore() {
        return nome_produttore;
    }
    @Override
    public void setNome_produttore(String nome_produttore) {
        this.nome_produttore = nome_produttore;
    }
    @Override
    public double getPrezzo() {
        return prezzo;
    }
    @Override
    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    
    @Override
    public String getUrl() {
        return url;
    }
    @Override
    public void setUrl(String url) {
        this.url = url;
    }
}
