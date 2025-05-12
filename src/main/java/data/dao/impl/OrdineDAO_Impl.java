package data.dao.impl;

import data.dao.OrdineDAO;
import data.model.Ordine;
import data.model.impl.proxy.OrdineProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataItemProxy;
import framework.data.DataLayer;
import framework.data.OptimisticLockException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrdineDAO_Impl extends DAO implements OrdineDAO {
    private PreparedStatement sByID; //select by ID
    private PreparedStatement s;    //select
    private PreparedStatement sByuID; //select by User ID
    private PreparedStatement sBytID; //select by Tecnico ID
    private PreparedStatement i, u, d; //insert, update, delete

    public OrdineDAO_Impl(DataLayer d) {
        super(d);
    }
    
    @Override
    public void init() throws DataException {
        try {
            super.init();
            sByID = connection.prepareStatement("SELECT * FROM ordine WHERE ID=?");
            s = connection.prepareStatement("SELECT ID FROM ordine ORDER BY ID");
            sByuID = connection.prepareStatement("SELECT * FROM ordine"
                                            + " INNER JOIN proposta on proposta.ID=ordine.ID_proposta"
                                            + " INNER JOIN richiesta on richiesta.ID=proposta.ID_richiesta"
                                            + " WHERE ID_ordinante=? ORDER BY ordine.ID");
            sBytID = connection.prepareStatement("SELECT * FROM ordine"
                                            + " INNER JOIN proposta on proposta.ID=ordine.ID_proposta"
                                            + " WHERE ID_tecnico=?");
      
            i = connection.prepareStatement("INSERT INTO ordine(stato,dataOrdine,dataConsegna,ID_proposta) VALUES (?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            u = connection.prepareStatement("UPDATE ordine SET stato=?,dataOrdine=?,dataConsegna=?,ID_proposta=?,version=? WHERE ID=? AND version=?");
            d = connection.prepareStatement("DELETE FROM ordine WHERE ID=?");

        } catch (SQLException ex) {
            throw new DataException("Errore nell'inizializazione del DataLayer di ordine", ex);
        }
    }
     @Override
    public void destroy() throws DataException {
        try {
            //chiusura statement
            sByID.close();
            sByuID.close();
            sBytID.close();
            s.close();
            i.close();
            u.close();
            d.close();

        } catch (SQLException ex) {
            
        }
        super.destroy();
    }
    @Override
    public Ordine createOrdine() {
        return new OrdineProxy(getDataLayer());
    }
      //helper
    private OrdineProxy createOrdine(ResultSet rs) throws DataException {
        OrdineProxy c = (OrdineProxy) createOrdine();
        try {
            c.setKey(rs.getInt("ID"));
            c.setStato(rs.getString("stato"));
            c.setDataOrdine(rs.getDate("dataOrdine"));
            c.setDataConsegna(rs.getDate("dataConsegna"));
            c.setIdProp(rs.getInt("ID_Proposta"));
            
            c.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Impossibile creare l'oggetto Ordine dal ResultSet", ex);
        }
        return c;
    }
    
    @Override
    public Ordine getOrdine(int ordine_key) throws DataException {
        Ordine c = null;
        //prima vediamo se l'oggetto è già stato caricato
        if (dataLayer.getCache().has(Ordine.class, ordine_key)) {
            c = dataLayer.getCache().get(Ordine.class, ordine_key);
        } else { 
            //altrimenti lo carichiamo dal database
            try {
                sByID.setInt(1, ordine_key);
                try (ResultSet rs = sByID.executeQuery()) {
                    if (rs.next()) {
                        c = createOrdine(rs);
                        //e lo mettiamo anche nella cache
                        dataLayer.getCache().add(Ordine.class, c);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Impossibile trovare l'Ordine con l'ID fornito", ex);
            }
        }
        return c;
    }
    
    @Override
    public List<Ordine> getOrdinibyUser(String user_key) throws DataException {
        List<Ordine> ordini = new ArrayList();
        try {
            sByuID.setString(1, user_key);
            try (ResultSet rs = sByuID.executeQuery()) {
                while (rs.next()) {
                    ordini.add(createOrdine(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile caricare gli ordini per l'utente specificato", ex);
        }
        return ordini;
    }
    
    @Override
    public List<Ordine> getOrdinibyTecnico(String tecnico_key) throws DataException{
        List<Ordine> ordini = new ArrayList();
        try {
            sBytID.setString(1, tecnico_key);
            try (ResultSet rs = sBytID.executeQuery()) {
                while (rs.next()) {
                    ordini.add(createOrdine(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile caricare gli ordini per il tecnico specificato", ex);
        }
        return ordini;
    }
    
    @Override
    public List<Ordine> getOrdini() throws DataException {
        List<Ordine> result = new ArrayList();

        try (ResultSet rs = s.executeQuery()) {
            while (rs.next()) {
                result.add((Ordine)getOrdine(rs.getInt("ID")));
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare gli ordini", ex);
        }
        return result;
    }
    
    @Override
    public void storeOrdine(Ordine ordine) throws DataException {
        try {
            if (ordine.getKey() != null) { //update
                //non facciamo nulla se l'oggetto è un proxy e indica di non aver subito modifiche
                if (ordine instanceof DataItemProxy && !((DataItemProxy) ordine).isModified()) {
                return;
                }

                u.setString(1, ordine.getStato());
                u.setDate(2, new java.sql.Date(ordine.getDataOrdine().getTime()));
                
                Date today = new Date(System.currentTimeMillis());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(today);
                calendar.add(Calendar.DAY_OF_YEAR, 3);

                Date arrivo = new Date(calendar.getTimeInMillis());
                u.setDate(3, arrivo);
                u.setInt(4, ordine.getIdProp());

                long current_version = ordine.getVersion();
                long next_version = current_version + 1;
                
                u.setLong(5, next_version);
                u.setInt(6, ordine.getKey());
                u.setLong(7, current_version);
                
                if (u.executeUpdate() == 0) {
                    throw new OptimisticLockException(ordine);
                } else {
                    ordine.setVersion(next_version);
                }
            } else { //insert
                i.setString(1, ordine.getStato());
                i.setDate(2, new java.sql.Date(ordine.getDataOrdine().getTime()));
                
                Date today = new Date(System.currentTimeMillis());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(today);
                calendar.add(Calendar.DAY_OF_YEAR, 3);

                Date arrivo = new Date(calendar.getTimeInMillis());
                i.setDate(3, arrivo);
                i.setInt(4, ordine.getIdProp());
                        
                if (i.executeUpdate() == 1) {
                    try (ResultSet keys = i.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            ordine.setKey(key);
                            dataLayer.getCache().add(Ordine.class, ordine);
                        }
                    }
                }
            }
            //se abbiamo un proxy, resettiamo il suo attributo dirty
            if (ordine instanceof DataItemProxy) {
                ((DataItemProxy) ordine).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Impossibile inserire l'ordine", ex);
        }
    }
}
