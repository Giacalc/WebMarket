package data.dao.impl;

import data.dao.UtenteDAO;
import data.model.Utente;
import data.model.impl.proxy.UtenteProxy;
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

public class UtenteDAO_Impl extends DAO implements UtenteDAO {
    
    private PreparedStatement sByID; //select by ID
    private PreparedStatement s;    //select
    private PreparedStatement i, u, d; //insert, update, delete

    public UtenteDAO_Impl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            //precompiliamo tutte le query utilizzate nella classe
            //precompile all the queries uses in this class
            sByID = connection.prepareStatement("SELECT * FROM utente WHERE username=?");
            s = connection.prepareStatement("SELECT * FROM utente");
      
            i = connection.prepareStatement("INSERT INTO utente (username,email,password,nome,cognome,ruolo) VALUES(?,?,?,?,?,?)");
            u = connection.prepareStatement("UPDATE utente SET username=?,email=?,password=?,nome=?,cognome=?,ruolo=?, version=? WHERE username=? AND version=?");
            d = connection.prepareStatement("DELETE FROM utente WHERE username=?");

        } catch (SQLException ex) {
            throw new DataException("Errore nell'inizializzazione del DataLayer di utente", ex);
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
    public Utente createUtente() {
        return new UtenteProxy(getDataLayer());
    }
    
     //helper
    private UtenteProxy createUtente(ResultSet rs) throws DataException {
        UtenteProxy c = (UtenteProxy) createUtente();
        try {
            c.setKey(rs.getString("username"));
            c.setUsername(rs.getString("username"));
            c.setNome(rs.getString("nome"));
            c.setCognome(rs.getString("cognome"));
            c.setMail(rs.getString("email"));
            c.setPassword(rs.getString("password"));
            c.setRuolo(rs.getString("ruolo"));
            c.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Impossibile creare l'oggetto Utente dal ResultSet", ex);
        }
        return c;
    }
    
    @Override
    public Utente getUtente(String utente_key) throws DataException {
        Utente c = null;
        //prima vediamo se l'oggetto è già stato caricato
        if (dataLayer.getCache().has(Utente.class, utente_key)) {
            c = dataLayer.getCache().get(Utente.class, utente_key);
        } else { 
            //altrimenti lo carichiamo dal database
            try {
                sByID.setString(1, utente_key);
                try (ResultSet rs = sByID.executeQuery()) {
                    if (rs.next()) {
                        c = createUtente(rs);
                        //e lo mettiamo anche nella cache
                        dataLayer.getCache().add(Utente.class, c); //pure qui
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Impossibile trovare l'utente con l'username fornito", ex);
            }
        }
        return c;
    }

    @Override
    public List<Utente> getUtenti() throws DataException {
        List<Utente> result = new ArrayList();

        try (ResultSet rs = s.executeQuery()) {
            while (rs.next()) {
                result.add((Utente)getUtente(rs.getString("username")));
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare gli utenti", ex);
        }
        return result;
    }
    
    @Override
    public void storeUtente(Utente utente, String oldID) throws DataException {
        try {
            if (utente.getKey() != null) { //update
                //non facciamo nulla se l'oggetto è un proxy e indica di non aver subito modifiche
                if (utente instanceof DataItemProxy && !((DataItemProxy) utente).isModified()) {
                    return;
                }
                    
                u.setString(1, utente.getUsername());
                u.setString(2, utente.getMail());
                u.setString(3, utente.getPassword());
                u.setString(4, utente.getNome());
                u.setString(5, utente.getCognome());
                u.setString(6, utente.getRuolo());
                u.setString(8, oldID);
              
                long current_version = utente.getVersion();
                long next_version = current_version + 1;
                
                u.setLong(7, next_version);
                u.setLong(9, current_version);                

                
                if (u.executeUpdate() == 0) {
                    throw new OptimisticLockException(utente);
                } else {
                    utente.setVersion(next_version);
                }  
                } else { //insert

                i.setString(1, utente.getUsername());
                i.setString(2, utente.getMail());
                i.setString(3, utente.getPassword());
                i.setString(4, utente.getNome());
                i.setString(5, utente.getCognome());
                i.setString(6, utente.getRuolo());
                        
                if (i.executeUpdate() == 1) {
                    utente.setKey(utente.getNome());
                    //inseriamo il nuovo oggetto nella cache
                    dataLayer.getCache().add(Utente.class, utente);
                    }
                
                }
            
            //se abbiamo un proxy, resettiamo il suo attributo dirty
            if (utente instanceof DataItemProxy) {
                ((DataItemProxy) utente).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Impossibile inserire il utente", ex);
        }
    }
    
    public void deleteUtente(Utente utente) throws DataException {
        try {
            dataLayer.getCache().delete(Utente.class, utente);
            d.setString(1, utente.getKey());
            d.executeUpdate();

        } catch (SQLException e) {
            throw new DataException("Impossibile eliminare la microcategoria", e);
        }
    }
    
}