package data.dao.impl;

import data.dao.CaratteristicaDAO;
import data.model.Caratteristica;
import data.model.impl.proxy.CaratteristicaProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataItemProxy;
import framework.data.DataLayer;
import framework.data.OptimisticLockException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.*;

public class CaratteristicaDAO_Impl extends DAO implements CaratteristicaDAO {
    
    private PreparedStatement sByID; //select by ID
    private PreparedStatement s;    //select
    private PreparedStatement s_CarByCat; //select by categoria
    private PreparedStatement i_MC; //associazione microcategoria-caratteristica
    private PreparedStatement u_MC; //associazione microcategoria-caratteristica
    private PreparedStatement i, u, d; //insert, update, delete
    private PreparedStatement sV;    //selectValoreCaratteristica

    public CaratteristicaDAO_Impl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            
            sByID = connection.prepareStatement("SELECT caratteristica.nome as nome, caratteristica_mc.nome_mc as microcategoria , caratteristica.version as version" +
                                                " FROM web_engineering.caratteristica INNER JOIN web_engineering.caratteristica_mc" +
                                                " ON caratteristica.nome = caratteristica_mc.nome_caratteristica" +
                                                " WHERE nome=?");
            s = connection.prepareStatement("SELECT nome FROM caratteristica");
      
            i = connection.prepareStatement("INSERT INTO caratteristica (nome) VALUES(?)");
            i_MC = connection.prepareStatement("INSERT INTO caratteristica_MC (nome_caratteristica,nome_mc) VALUES(?,?)");
            
            u = connection.prepareStatement("UPDATE caratteristica SET nome=?, version=? WHERE nome=? AND version=?");
            u_MC = connection.prepareStatement("UPDATE caratteristica_MC SET nome_caratteristica=?,nome_mc=? WHERE nome_caratteristica=? AND nome_mc=?");
            d = connection.prepareStatement("DELETE FROM caratteristica WHERE nome=?");
            
            s_CarByCat = connection.prepareStatement("SELECT * FROM caratteristica"
                    + " INNER JOIN caratteristica_MC on caratteristica.nome=caratteristica_MC.nome_caratteristica"
                    + " WHERE nome_mc=?");
            sV = connection.prepareStatement("SELECT nome_caratteristica,valore FROM caratteristica_richiesta WHERE ID_richiesta=? ");

        } catch (SQLException ex) {
            throw new DataException("Errore nell'inizializzazzione del DataLayer di caratteristica", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            //chiusura statement
            sByID.close();
            s.close();
            s_CarByCat.close();
            sV.close();
            i.close();
            i_MC.close();
            u.close();
            u_MC.close();
            d.close();

        } catch (SQLException ex) {
            
        }
        super.destroy();
    }
    
    @Override
    public Caratteristica createCaratteristica() {
        return new CaratteristicaProxy(getDataLayer());
    }
    
    //helper
    private CaratteristicaProxy createCaratteristica(ResultSet rs) throws DataException {
        CaratteristicaProxy c = (CaratteristicaProxy) createCaratteristica();
        try {
            c.setKey(rs.getString("nome"));
            c.setNome(rs.getString("nome"));
            c.setMicrocategoria(rs.getString("microcategoria"));
            c.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Impossibile creare l'oggetto Caratteristica dal ResultSet", ex);
        }
        return c;
    }
    
    @Override
    public Caratteristica getCaratteristica(String caratteristica_key) throws DataException {
        Caratteristica c = null;
        //prima vediamo se l'oggetto è già stato caricato
        if (dataLayer.getCache().has(Caratteristica.class, caratteristica_key)) {
            c = dataLayer.getCache().get(Caratteristica.class, caratteristica_key);
        } else { 
            //altrimenti lo carichiamo dal database
            try {
                sByID.setString(1, caratteristica_key);
                try (ResultSet rs = sByID.executeQuery()) {
                    if (rs.next()) {
                        c = createCaratteristica(rs);
                        //e lo mettiamo anche nella cache
                        dataLayer.getCache().add(Caratteristica.class, c);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Impossibile trovare la caratteristica con il nome fornito", ex);
            }
        }
        return c;
    }

    @Override
    public List<Caratteristica> getCaratteristiche() throws DataException {
        List<Caratteristica> result = new ArrayList();

        try (ResultSet rs = s.executeQuery()) {
            while (rs.next()) {
                result.add((Caratteristica) getCaratteristica(rs.getString("nome")));
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare le caratteristiche", ex);
        }
        return result;
    }
    
    @Override
    public List<Caratteristica> getCaratteristicheByCategoria(String microcategoria_key) throws DataException {
        List<Caratteristica> result = new ArrayList();
        try {
            s_CarByCat.setString(1, microcategoria_key);
            try (ResultSet rs = s_CarByCat.executeQuery()) {
                while (rs.next()) {
                    result.add((Caratteristica) getCaratteristica(rs.getString("nome_caratteristica")));
                }
            }
            } catch (SQLException ex) {
                throw new DataException("Impossibile trovare le caratteristiche data la categoria", ex);
        }
        return result;
    }
    
    public Map<String,String> getValoriCaratteristicheByRichiesta(int idRichiesta)throws DataException{
        Map<String,String> result = new HashMap();
        try{
            sV.setInt(1,idRichiesta);
            try (ResultSet rs = sV.executeQuery()){
                while(rs.next()){
                    result.put(rs.getString("nome_caratteristica"),rs.getString("valore"));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare i valori delle caratteristiche ", ex);
        }
    
        return result;
    }
    @Override
    public void storeCaratteristica(Caratteristica caratteristica, String oldID, String oldM) throws DataException {
        try {
            if (caratteristica.getKey() != null) { //update
                //non facciamo nulla se l'oggetto è un proxy e indica di non aver subito modifiche
                if (caratteristica instanceof DataItemProxy && !((DataItemProxy) caratteristica).isModified()) {
                    return;
                }
                u.setString(1, caratteristica.getNome());
                u.setString(3, oldID);

                long current_version = caratteristica.getVersion();
                long next_version = current_version + 1;
                
                u.setLong(2, next_version);
                u.setLong(4, current_version);                

                if (u.executeUpdate() == 0) {
                    throw new OptimisticLockException(caratteristica);
                } else {
                    caratteristica.setVersion(next_version);
                }
                
                //si aggiorna l'associazione alla microcategoria
                u_MC.setString(1, caratteristica.getNome());
                u_MC.setString(2, caratteristica.getMicrocategoria());
                u_MC.setString(3, caratteristica.getNome());
                u_MC.setString(4, oldM);
                if(u_MC.executeUpdate() == 0) 
                    throw new OptimisticLockException(caratteristica);
                } else { //insert
                
                //si inserisce la caratteristica
                i.setString(1, caratteristica.getNome());
                //i.setString(2, caratteristica.getDescrizione());
                
                //si associa alla microcategoria
                i_MC.setString(1, caratteristica.getNome());
                i_MC.setString(2, caratteristica.getMicrocategoria());
                
                //se la caratteristica esiste, si associa semplicemente, atrimenti si deve anche aggiungere
                if(getCaratteristica(caratteristica.getNome()) != null) {
                    i_MC.executeUpdate();
                } else {
                    if (i.executeUpdate() == 1 && i_MC.executeUpdate() == 1) {
                    caratteristica.setKey(caratteristica.getNome());
                    //inseriamo il nuovo oggetto nella cache
                    dataLayer.getCache().add(Caratteristica.class, caratteristica);
                    }
                }
            }
            
            //se abbiamo un proxy, resettiamo il suo attributo dirty
            if (caratteristica instanceof DataItemProxy) {
                ((DataItemProxy) caratteristica).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Impossibile eseguire l'operazione sulla caratteristica", ex);
        }
        
        
    }
    @Override
    public void deleteCaratteristica(Caratteristica caratteristica) throws DataException {
        try {
            dataLayer.getCache().delete(Caratteristica.class, caratteristica);
            d.setString(1, caratteristica.getKey());
            d.executeUpdate();

        } catch (SQLException e) {
            throw new DataException("Impossibile eliminare la microcategoria", e);
        }

    }
}