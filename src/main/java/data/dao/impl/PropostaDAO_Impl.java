package data.dao.impl;

import data.dao.PropostaDAO;
import data.model.Proposta;
import data.model.impl.proxy.PropostaProxy;
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



public class PropostaDAO_Impl extends DAO implements PropostaDAO {
    
    private PreparedStatement sByID; //select by ID
    private PreparedStatement sByuID; //select by User ID
    private PreparedStatement sBytID; //select by Tecnico ID
    private PreparedStatement sByrID; //select by Richiesta
    private PreparedStatement s;    //select
    private PreparedStatement i, u, d; //insert, update, delete

    public PropostaDAO_Impl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sByID = connection.prepareStatement("SELECT * FROM proposta WHERE ID=?");
            sByuID = connection.prepareStatement("SELECT * FROM proposta INNER JOIN richiesta on richiesta.ID=proposta.ID_richiesta WHERE ID_ordinante=? AND richiesta.stato='In attesa' ORDER BY proposta.data");
            sBytID = connection.prepareStatement("SELECT * FROM proposta WHERE ID_tecnico=?" );
            sByrID = connection.prepareStatement("SELECT * FROM proposta WHERE ID_richiesta=?");
            s = connection.prepareStatement("SELECT ID FROM proposta ORDER BY ID");
      
            i = connection.prepareStatement("INSERT INTO proposta(data,note,ID_richiesta,ID_prodotto,ID_tecnico,revisione) VALUES(?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            u = connection.prepareStatement("UPDATE proposta SET data=?,revisione=?,note=?,rev_motivazione=?,ID_richiesta=?,ID_prodotto=?,ID_tecnico=?,version=? WHERE ID=? AND version=?");
            d = connection.prepareStatement("DELETE FROM proposta WHERE ID=?");      

        } catch (SQLException ex) {
            throw new DataException("Errore nell'inizializazione del DataLayer di proposta", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            //chiusura statement
            sByID.close();
            sByuID.close();
            sBytID.close();
            sByrID.close();
            s.close();
            i.close();
            u.close();
            d.close();

        } catch (SQLException ex) {
            
        }
        super.destroy();
    }
    @Override
    public Proposta createProposta() {
        return new PropostaProxy(getDataLayer());
    }
    
     //helper
    private PropostaProxy createProposta(ResultSet rs) throws DataException {
        PropostaProxy c = (PropostaProxy) createProposta();
        try {
            c.setKey(rs.getInt("ID"));
            c.setData(rs.getDate("data"));
            c.setRevisione(rs.getString("revisione"));
            c.setNote(rs.getString("note"));
            c.setRev_motivazione(rs.getString("rev_motivazione"));
            c.setIDric(rs.getInt("ID_richiesta"));
            c.setIDprod(rs.getString("ID_prodotto"));
            c.setIDtec(rs.getString("ID_tecnico"));
            
            c.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Impossibile creare l'oggetto Proposta dal ResultSet", ex);
        }
        return c;
    }
    
    @Override
    public Proposta getProposta(int proposta_key) throws DataException {
        Proposta c = null;
        //prima vediamo se l'oggetto è già stato caricato
        if (dataLayer.getCache().has(Proposta.class, proposta_key)) {
            c = dataLayer.getCache().get(Proposta.class, proposta_key);
        } else { 
            //altrimenti lo carichiamo dal database
            try {
                sByID.setInt(1, proposta_key);
                try (ResultSet rs = sByID.executeQuery()) {
                    if (rs.next()) {
                        c = createProposta(rs);
                        //e lo mettiamo anche nella cache
                        dataLayer.getCache().add(Proposta.class, c);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Impossibile trovare la proposta con l'ID fornito", ex);
            }
        }
        return c;
    }

    @Override
    public List<Proposta> getProposte() throws DataException {
        List<Proposta> result = new ArrayList();

        try (ResultSet rs = s.executeQuery()) {
            while (rs.next()) {
                result.add((Proposta)getProposta(rs.getInt("ID")));
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare le proposte", ex);
        }
        return result;
    }
    
    @Override
    public List<Proposta> getPropostebyUser(String user_key) throws DataException {
        List<Proposta> proposte = new ArrayList();
        try {
            sByuID.setString(1, user_key);
            try (ResultSet rs = sByuID.executeQuery()) {
                while (rs.next()) {
                    proposte.add(createProposta(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile caricare le proposte per l'utente specificato", ex);
        }
        return proposte;
    }
    
    @Override
    public List<Proposta> getPropostebyTecnico(String tecnico_key) throws DataException{
        List<Proposta> proposte = new ArrayList();
        try {
            sBytID.setString(1, tecnico_key);
            try (ResultSet rs = sBytID.executeQuery()) {
                while (rs.next()) {
                    proposte.add(createProposta(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile caricare le proposte per il tecnico specificato", ex);
        }
        return proposte;
    }
    
    @Override
    public List<Proposta> getPropostebyRichiesta(int richiesta_key) throws DataException {
         List<Proposta> proposte = new ArrayList();
        try {
            sByrID.setInt(1, richiesta_key);
            try (ResultSet rs = sByrID.executeQuery()) {
                while (rs.next()) {
                    proposte.add(createProposta(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile caricare le proposte per la richiesta specificata", ex);
        }
        return proposte;
    }

    
    @Override
    public void storeProposta(Proposta proposta) throws DataException {
        try {
            if (proposta.getKey() != null) { //update
                //non facciamo nulla se l'oggetto è un proxy e indica di non aver subito modifiche
                if (proposta instanceof DataItemProxy && !((DataItemProxy) proposta).isModified()) {
                    return;
                }
                u.setDate(1, new java.sql.Date(proposta.getData().getTime()));
                u.setString(2, proposta.getRevisione());
                u.setString(3, proposta.getNote());
                u.setString(4, proposta.getRev_motivazione());
                u.setInt(5, proposta.getIDric());
                u.setString(6, proposta.getIDprod());
                u.setString(7, proposta.getIDtec());

                long current_version = proposta.getVersion();
                long next_version = current_version + 1;
                
                u.setLong(8, next_version);
                u.setInt(9, proposta.getKey());
                u.setLong(10, current_version);

                if (u.executeUpdate() == 0) {
                    throw new OptimisticLockException(proposta);
                } else {
                    proposta.setVersion(next_version);
                }
            } else { //insert
                             
                i.setDate(1, new java.sql.Date(proposta.getData().getTime()));
                i.setString(2, proposta.getNote());
                i.setInt(3, proposta.getIDric());
                i.setString(4, proposta.getIDprod());
                i.setString(5, proposta.getIDtec());
                i.setString(6, proposta.getRevisione());
      
                if (i.executeUpdate() == 1) {
                     try (ResultSet keys = i.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            proposta.setKey(key);
                            dataLayer.getCache().add(Proposta.class, proposta);
                        }
                    }
                }
                
            }
            
            //se abbiamo un proxy, resettiamo il suo attributo dirty
            if (proposta instanceof DataItemProxy) {
                ((DataItemProxy) proposta).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Impossibile inserire la proposta", ex);
        }
    }


}