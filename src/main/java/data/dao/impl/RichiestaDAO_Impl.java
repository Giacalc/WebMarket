package data.dao.impl;

import data.dao.RichiestaDAO;
import data.model.Richiesta;
import data.model.impl.proxy.RichiestaProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataItemProxy;
import framework.data.DataLayer;
import framework.data.OptimisticLockException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RichiestaDAO_Impl extends DAO implements RichiestaDAO {
    
    private PreparedStatement sBy_Carico;
    private PreparedStatement sByID_Ord; 
    private PreparedStatement sByID; //select by ID
    private PreparedStatement sIc;
    private PreparedStatement sAc;
    private PreparedStatement sC;
    private PreparedStatement s;    //select
    private PreparedStatement sNe;
    private PreparedStatement i, u, d; //insert, update, delete
    private PreparedStatement i_cr;    //select
    public RichiestaDAO_Impl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            //precompiliamo tutte le query utilizzate nella classe
            //precompile all the queries uses in this class
            sBy_Carico = connection.prepareStatement("SELECT * FROM richiesta WHERE stato='Presa in carico' AND ID_tecnico=?");
            sByID_Ord = connection.prepareStatement("SELECT * FROM richiesta WHERE ID_ordinante=?");
            sByID = connection.prepareStatement("SELECT * FROM richiesta WHERE ID=?");
            s = connection.prepareStatement("SELECT ID FROM richiesta ORDER BY ID");
            sNe = connection.prepareStatement("SELECT * FROM richiesta WHERE stato = 'Da prendere in carico'");
            sIc = connection.prepareStatement("SELECT * FROM richiesta WHERE stato != 'Completata' AND ID_ordinante=?");
            sC = connection.prepareStatement("SELECT * FROM richiesta WHERE stato = 'Completata' AND ID_ordinante=?");
            i = connection.prepareStatement("INSERT INTO richiesta (data,note,stato,ID_ordinante,ID_categoria) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            u = connection.prepareStatement("UPDATE richiesta SET data=?,note=?,stato=?,ID_ordinante=?,ID_categoria=?,ID_tecnico=?,version=? WHERE ID=? AND version=?");
            d = connection.prepareStatement("DELETE FROM richiesta WHERE ID=?");
            i_cr = connection.prepareStatement("INSERT INTO caratteristica_richiesta (ID_richiesta, nome_caratteristica, valore) VALUES(?,?,?)" );
        } catch (SQLException ex) {
            throw new DataException("Errore nell'inizializzazione del DataLayer di richiesta", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            //chiusura statement
            sBy_Carico.close();
            sByID_Ord.close();
            sByID.close();
            s.close();
            i.close();
            u.close();
            d.close();
            i_cr.close();
            sNe.close();
            sIc.close();
            sAc.close();
            sC.close();
        } catch (SQLException ex) {
            
        }
        super.destroy();
    }
    @Override
    public Richiesta createRichiesta() {
        return new RichiestaProxy(getDataLayer());
    }
    
     //helper
    private RichiestaProxy createRichiesta(ResultSet rs) throws DataException {
        RichiestaProxy c = (RichiestaProxy) createRichiesta();
        try {
            c.setKey(rs.getInt("ID"));
            c.setData(rs.getDate("data"));
            c.setStato(rs.getString("stato"));
            c.setNote(rs.getString("note"));
            c.setIDord(rs.getString("ID_ordinante"));
            c.setIDcat(rs.getString("ID_categoria"));
            c.setIDtec(rs.getString("ID_tecnico"));
            c.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Impossibile creare l'oggetto Richiesta dal ResultSet", ex);
        }
        return c;
    }
    
    @Override
    public Richiesta getRichiesta(int richiesta_key) throws DataException {
        Richiesta c = null;
        //prima vediamo se l'oggetto è già stato caricato
        if (dataLayer.getCache().has(Richiesta.class, richiesta_key)) {
            c = dataLayer.getCache().get(Richiesta.class, richiesta_key);
        } else { 
            //altrimenti lo carichiamo dal database
            try {
                sByID.setInt(1, richiesta_key);
                try (ResultSet rs = sByID.executeQuery()) {
                    if (rs.next()) {
                        c = createRichiesta(rs);
                        //e lo mettiamo anche nella cache
                        dataLayer.getCache().add(Richiesta.class, c);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Impossibile trovare la richiesta con il codice fornito", ex);
            }
        }
        return c;
    }

    @Override
    public List<Richiesta> getRichieste() throws DataException {
        List<Richiesta> result = new ArrayList();

        try (ResultSet rs = s.executeQuery()) {
            while (rs.next()) {
                result.add((Richiesta)getRichiesta(rs.getInt("ID")));
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare le richieste", ex);
        }
        return result;
    }
    
    @Override
    public List<Richiesta> getRichiesteInCorso(String idOrd) throws DataException {
        List<Richiesta> result = new ArrayList();
        try{
        sIc.setString(1, idOrd);
        
        try (ResultSet rs = sIc.executeQuery()) {
            while (rs.next()) {
                result.add((Richiesta)getRichiesta(rs.getInt("ID")));
            }
        }} catch (SQLException ex) {
            throw new DataException("Impossibile trovare le richieste", ex);
        }
        return result;
    }
    
    @Override
    public List<Richiesta> getRichiesteNonEvase()throws DataException{
        List<Richiesta> richieste = new ArrayList();
        try (ResultSet rs = sNe.executeQuery()) {
            while (rs.next()) {           
                richieste.add((Richiesta)getRichiesta(rs.getInt("ID")));
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare le richieste", ex);
        }
  
        return richieste;   
    }
    
     @Override
    public List<Richiesta> getRichiesteConcluse(String ord_key)throws DataException{
        List<Richiesta> richieste = new ArrayList();
        try {
            sC.setString(1, String.valueOf(ord_key));
        try (ResultSet rs = sC.executeQuery()) {
            while (rs.next()) {           
                richieste.add((Richiesta)getRichiesta(rs.getInt("ID")));
            }
        } }catch (SQLException ex) {
            throw new DataException("Impossibile trovare le richieste", ex);
        }
  
        return richieste;   
    }

    
    @Override
    public List<Richiesta> getRichiesteByOrd(String ord_key) throws DataException {
        List<Richiesta> richieste = new ArrayList<>();

        try {
            // Imposta l'ID_ordinante nel prepared statement
            sByID_Ord.setString(1, String.valueOf(ord_key));
            try (ResultSet rs = sByID_Ord.executeQuery()) {
                while (rs.next()) {
                    richieste.add((Richiesta) createRichiesta(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile recuperare le richieste per l'ordinante specificato", ex);
        }

        return richieste;
    }

    
    @Override
    public void storeRichiesta(Richiesta richiesta) throws DataException {
        try {
            if (richiesta.getKey() != null) { //update
                //non facciamo nulla se l'oggetto è un proxy e indica di non aver subito modifiche
                if (richiesta instanceof DataItemProxy && !((DataItemProxy) richiesta).isModified()) {
                    return;
                }
                u.setDate(1, new java.sql.Date(richiesta.getData().getTime()));
                u.setString(2, richiesta.getNote());
                u.setString(3, richiesta.getStato());
                u.setString(4, richiesta.getIDord());
                u.setString(5, richiesta.getIDcat());
                u.setString(6, richiesta.getIDtec());
                             
                long current_version = richiesta.getVersion();
                long next_version = current_version + 1;
                
                u.setLong(7, next_version);
                u.setInt(8, richiesta.getKey()); 
                u.setLong(9, current_version); 
                
                if (u.executeUpdate() == 0) {
                    throw new OptimisticLockException(richiesta);
                } else {
                    richiesta.setVersion(next_version);
                }
            } else { //insert     
                i.setDate(1, new java.sql.Date(richiesta.getData().getTime()));
                i.setString(2, richiesta.getNote());
                i.setString(3, richiesta.getStato());
                i.setString(4, richiesta.getIDord());
                i.setString(5, richiesta.getIDcat());
                        
                if (i.executeUpdate() == 1) {
                    try (ResultSet keys = i.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            richiesta.setKey(key);
                            dataLayer.getCache().add(Richiesta.class, richiesta);
                        }
                    }
                }
            }
            
            //se abbiamo un proxy, resettiamo il suo attributo dirty
            if (richiesta instanceof DataItemProxy) {
                ((DataItemProxy) richiesta).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Impossibile inserire la richiesta", ex);
        }
    }
    public void storeCaratteristicaRichiesta(String nome, String valore, int idRichiesta) throws DataException{
        try {
                i_cr.setInt(1, idRichiesta);
                i_cr.setString(2, nome);
                i_cr.setString(3, valore);
                i_cr.executeUpdate();
        }
        catch(SQLException ex) {
            throw new DataException("Impossibile inserire la richiesta", ex);
        }
    }
    
    @Override
    public List<Richiesta> getRichieste_Carico(String ord_key) throws DataException{
        List<Richiesta> richieste = new ArrayList<>();

        try {
            sBy_Carico.setString(1, String.valueOf(ord_key));
            try (ResultSet rs = sBy_Carico.executeQuery()) {
                while (rs.next()) {
                    richieste.add((Richiesta) createRichiesta(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile recuperare le richieste per l'ordinante specificato", ex);
        }

        return richieste;
    }
    
}