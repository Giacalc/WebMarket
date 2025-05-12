package data.model;

import framework.data.DataItem;

public interface Prodotto extends DataItem<String> {
    String getNome();
    
    void setNome(String nome);
    
    String getNome_produttore();
    
    void setNome_produttore(String nome_produttore);
    
    double getPrezzo();
    
    void setPrezzo(double prezzo);

    String getUrl();
    
    void setUrl(String url);
            
}
