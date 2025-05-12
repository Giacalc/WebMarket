package data.dao.impl;

import data.dao.ProdottoDAO;
import data.model.Prodotto;
import data.model.impl.proxy.ProdottoProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataItemProxy;
import framework.data.DataLayer;
import framework.data.OptimisticLockException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class ProdottoDAO_Impl extends DAO implements ProdottoDAO {
    
    private PreparedStatement sByID; //select by ID
    private PreparedStatement s;    //select
    private PreparedStatement i, u, d; //insert, update, delete

    public ProdottoDAO_Impl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            //precompiliamo tutte le query utilizzate nella classe
            //precompile all the queries uses in this class
            sByID = connection.prepareStatement("SELECT * FROM prodotto WHERE codice=?");
            s = connection.prepareStatement("SELECT codice FROM prodotto");
            i = connection.prepareStatement("INSERT INTO prodotto (codice,prezzo,nome,nome_produttore,URL) VALUES(?,?,?,?,?)");
            u = connection.prepareStatement("UPDATE prodotto SET codice=?,prezzo=?,nome=?,nome_produttore=?,URL=?,version=? WHERE codice=? AND version=?");
            d = connection.prepareStatement("DELETE FROM prodotto WHERE codice=?");

        } catch (SQLException ex) {
            throw new DataException("Errore nell'inizializzazione del DataLayer di prodotto", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            //chiusura statement
            sByID.close();
            s.close();
            i.close();
            u.close();
            d.close();

        } catch (SQLException ex) {
            
        }
        super.destroy();
    }
    
    @Override
    public Prodotto createProdotto() {
        return new ProdottoProxy(getDataLayer());
    }
    
     //helper
    private ProdottoProxy createProdotto(ResultSet rs) throws DataException {
        ProdottoProxy c = (ProdottoProxy) createProdotto();
        try {
            c.setKey(rs.getString("codice"));
            c.setNome(rs.getString("nome"));
            c.setNome_produttore(rs.getString("nome_produttore"));
            c.setPrezzo(rs.getDouble("prezzo"));
            c.setUrl(rs.getString("url"));
            
           
            c.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Impossibile creare l'oggetto Prodotto dal ResultSet", ex);
        }
        return c;
    }
    
    @Override
    public Prodotto getProdotto(String prodotto_key) throws DataException {
        Prodotto c = null;
        //prima vediamo se l'oggetto è già stato caricato
        if (dataLayer.getCache().has(Prodotto.class, prodotto_key)) {
            c = dataLayer.getCache().get(Prodotto.class, prodotto_key);
        } else { 
            //altrimenti lo carichiamo dal database
            try {
                sByID.setString(1, prodotto_key);
                try (ResultSet rs = sByID.executeQuery()) {
                    if (rs.next()) {
                        c = createProdotto(rs);
                        //e lo mettiamo anche nella cache
                        dataLayer.getCache().add(Prodotto.class, c);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Impossibile trovare il prodotto con il codice fornito", ex);
            }
        }
        return c;
    }

    @Override
    public List<Prodotto> getProdotti() throws DataException {
        List<Prodotto> result = new ArrayList();

        try (ResultSet rs = s.executeQuery()) {
            while (rs.next()) {
                result.add((Prodotto)getProdotto(rs.getString("username")));
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare i prodotti", ex);
        }
        return result;
    }
    
    @Override
    public void storeProdotto(Prodotto prodotto, String oldID) throws DataException {
        try {
            if (getProdotto(prodotto.getKey())!= null ) { //update
                //non facciamo nulla se l'oggetto è un proxy e indica di non aver subito modifiche
                if (prodotto instanceof DataItemProxy && !((DataItemProxy) prodotto).isModified()) {
                    return;
                }
                u.setString(1, prodotto.getKey());
                u.setDouble(2, prodotto.getPrezzo());
                u.setString(3, prodotto.getNome());
                u.setString(4, prodotto.getNome_produttore());
                u.setString(5, prodotto.getUrl());
                
                
                long current_version = prodotto.getVersion();
                long next_version = current_version + 1;
                
                u.setLong(6, next_version);
                u.setString(7, oldID);
                u.setLong(8, current_version);

                if (u.executeUpdate() == 0) {
                    throw new OptimisticLockException(prodotto);
                } else {
                    prodotto.setVersion(next_version);
                }
            } else { //insert
                             
                i.setString(1, prodotto.getKey());
                i.setDouble(2, prodotto.getPrezzo());
                i.setString(3, prodotto.getNome());
                i.setString(4, prodotto.getNome_produttore());
                i.setString(5, prodotto.getUrl());
                        
                if (i.executeUpdate() == 1) {
                    prodotto.setKey(prodotto.getKey());
                    //inseriamo il nuovo oggetto nella cache
                    dataLayer.getCache().add(Prodotto.class, prodotto);
                    }
                
                }
            
            //se abbiamo un proxy, resettiamo il suo attributo dirty
            if (prodotto instanceof DataItemProxy) {
                ((DataItemProxy) prodotto).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Impossibile inserire il prodotto", ex);
        }
    }

}