package data.dao.impl;

import data.dao.CategoriaDAO;
import data.model.Categoria;
import data.model.Sottocategoria;
import data.model.Microcategoria;
import data.model.impl.proxy.CategoriaProxy;
import data.model.impl.proxy.SottocategoriaProxy;
import data.model.impl.proxy.MicrocategoriaProxy;
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



public class CategoriaDAO_Impl extends DAO implements CategoriaDAO {
    
    private PreparedStatement sByID; //select by ID
    private PreparedStatement s;    //select
    private PreparedStatement i, u, d; //insert, update, delete
    
    private PreparedStatement sByID_sc; //select by ID
    private PreparedStatement s_sc;    //select
    private PreparedStatement i_sc, u_sc, d_sc; //insert, update, delete
    
    private PreparedStatement sByID_mc; //select by ID
    private PreparedStatement s_mc;    //select
    private PreparedStatement i_mc, u_mc, d_mc; //insert, update, delete
    
    private PreparedStatement s_SCByC, s_MCBySC, s_SCnByC, s_MCnBySC, s_SCByMC;

    public CategoriaDAO_Impl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            //precompiliamo tutte le query utilizzate nella classe
            sByID = connection.prepareStatement("SELECT * FROM categoria WHERE nome=?");
            s = connection.prepareStatement("SELECT nome FROM categoria");
            i = connection.prepareStatement("INSERT INTO categoria (nome) VALUES(?)");
            u = connection.prepareStatement("UPDATE categoria SET nome=?,version=? WHERE nome=? AND version=?");
            d = connection.prepareStatement("DELETE FROM categoria WHERE nome=?");
            
            sByID_sc = connection.prepareStatement("SELECT * FROM sotto_categoria WHERE nome=?");
            s_sc = connection.prepareStatement("SELECT nome FROM sotto_categoria");
            i_sc = connection.prepareStatement("INSERT INTO sotto_categoria (nome,categoria) VALUES(?,?)");
            u_sc = connection.prepareStatement("UPDATE sotto_categoria SET nome=?,categoria=?,version=? WHERE nome=? AND version=?");
            d_sc = connection.prepareStatement("DELETE FROM sotto_categoria WHERE nome=?");
            
            sByID_mc = connection.prepareStatement("SELECT * FROM micro_categoria WHERE nome=?");
            s_mc = connection.prepareStatement("SELECT nome FROM micro_categoria");
            i_mc = connection.prepareStatement("INSERT INTO micro_categoria (nome,sottocategoria) VALUES(?,?)");
            u_mc = connection.prepareStatement("UPDATE micro_categoria SET nome=?,sottocategoria=?,version=? WHERE nome=? AND version=?");
            d_mc = connection.prepareStatement("DELETE FROM micro_categoria WHERE nome=?");
            
            s_SCByC = connection.prepareStatement("SELECT * FROM sotto_categoria WHERE categoria=?");
            s_MCBySC = connection.prepareStatement("SELECT * FROM micro_categoria WHERE sottocategoria=?");
            s_SCnByC = connection.prepareStatement("SELECT nome FROM sotto_categoria WHERE categoria=?");
            s_MCnBySC = connection.prepareStatement("SELECT nome FROM micro_categoria WHERE sottocategoria=?");
            s_SCByMC = connection.prepareStatement("SELECT sottocategoria FROM micro_categoria WHERE nome=?");

        } catch (SQLException ex) {
            throw new DataException("Errore nell'inizializzazione del DataLayer di Categoria", ex);
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
            
            sByID_sc.close();
            s_sc.close();
            i_sc.close();
            u_sc.close();
            d_sc.close();
            
            sByID_mc.close();
            s_mc.close();
            i_mc.close();
            u_mc.close();
            d_mc.close();
            
            s_SCByMC.close();
            s_MCBySC.close();
            s_SCByMC.close();
            s_MCnBySC.close();
            s_SCnByC.close();

        } catch (SQLException ex) {
            
        }
        super.destroy();
    }

     @Override
    public Categoria createCategoria() {
        return new CategoriaProxy(getDataLayer());
    }
    
     //helper
    private CategoriaProxy createCategoria(ResultSet rs) throws DataException {
        CategoriaProxy c = (CategoriaProxy) createCategoria();
        try {
            c.setKey(rs.getString("nome"));
            c.setNome(rs.getString("nome"));
            c.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Impossibile creare l'oggetto Categoria dal ResultSet", ex);
        }
        return c;
    }
    
    @Override
    public Sottocategoria createSottocategoria() {
        return new SottocategoriaProxy(getDataLayer());
    }
    
     //helper
    private SottocategoriaProxy createSottocategoria(ResultSet rs) throws DataException {
        SottocategoriaProxy c = (SottocategoriaProxy) createSottocategoria();
        try {
            c.setKey(rs.getString("nome"));
            c.setNome(rs.getString("nome"));
            c.setCategoria(rs.getString("categoria"));
                     
            c.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Impossibile creare l'oggetto Sottocategoria dal ResultSet", ex);
        }
        return c;
    }
    
    @Override
    public Microcategoria createMicrocategoria() {
        return new MicrocategoriaProxy(getDataLayer());
    }
    
     //helper
    private MicrocategoriaProxy createMicrocategoria(ResultSet rs) throws DataException {
        MicrocategoriaProxy c = (MicrocategoriaProxy) createMicrocategoria();
        try {
            c.setKey(rs.getString("nome"));
            c.setNome(rs.getString("nome"));
            c.setSottocategoria(rs.getString("sottocategoria"));
                     
            c.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Impossibile creare l'oggetto Microcategoria dal ResultSet", ex);
        }
        return c;
    }
    
    @Override
    public Categoria getCategoria(String categoria_key) throws DataException {
        Categoria c = null;
        //prima vediamo se l'oggetto è già stato caricato
        if (dataLayer.getCache().has(Categoria.class, categoria_key)) {
            c = dataLayer.getCache().get(Categoria.class, categoria_key);
        } else { 
            //altrimenti lo carichiamo dal database
            try {
                sByID.setString(1, categoria_key);
                try (ResultSet rs = sByID.executeQuery()) {
                    if (rs.next()) {
                        c = createCategoria(rs);
                        //e lo mettiamo anche nella cache
                        dataLayer.getCache().add(Categoria.class, c);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Impossibile trovare la categoria con il nome fornito", ex);
            }
        }
        return c;
    }
    
    @Override
    public Sottocategoria getSottocategoria(String sc_key) throws DataException {
        Sottocategoria c = null;
        //prima vediamo se l'oggetto è già stato caricato
        if (dataLayer.getCache().has(Sottocategoria.class, sc_key)) {
            c = dataLayer.getCache().get(Sottocategoria.class, sc_key);
        } else { 
            //altrimenti lo carichiamo dal database
            try {
                sByID_sc.setString(1, sc_key);
                try (ResultSet rs = sByID_sc.executeQuery()) {
                    if (rs.next()) {
                        c = createSottocategoria(rs);
                        //e lo mettiamo anche nella cache
                        dataLayer.getCache().add(Sottocategoria.class, c);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Impossibile trovare la sottocategoria con il nome fornito", ex);
            }
        }
        return c;
    }
    @Override
    public String getSottocategoriaByMc(String mc) throws DataException {
        String c = null;
            try {
                s_SCByMC.setString(1, mc);
                try (ResultSet rs = s_SCByMC.executeQuery()) {
                    if (rs.next()) {
                        c = rs.getString("sottocategoria");
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Impossibile trovare la sottocategoria con la microcategoria fornita ", ex);
            }
        
        return c;
    }
    
    
    
    @Override
    public Microcategoria getMicrocategoria(String mc_key) throws DataException {
        Microcategoria c = null;
        //prima vediamo se l'oggetto è già stato caricato
        if (dataLayer.getCache().has(Microcategoria.class, mc_key)) {
            c = dataLayer.getCache().get(Microcategoria.class, mc_key);
        } else { 
            //altrimenti lo carichiamo dal database
            try {
                sByID_mc.setString(1, mc_key);
                try (ResultSet rs = sByID_mc.executeQuery()) {
                    if (rs.next()) {
                        c = createMicrocategoria(rs);
                        //e lo mettiamo anche nella cache
                        dataLayer.getCache().add(Microcategoria.class, c);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Impossibile trovare la microcategoria con il nome fornito", ex);
            }
        }
        return c;
    }

    @Override
    public List<Categoria> getCategorie() throws DataException {
        List<Categoria> result = new ArrayList();

        try (ResultSet rs = s.executeQuery()) {
            while (rs.next()) {
                result.add((Categoria) getCategoria(rs.getString("nome")));
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare le categorie", ex);
        }
        return result;
    }
    
    @Override
    public List<Sottocategoria> getSottocategorie() throws DataException {
        List<Sottocategoria> result = new ArrayList();

        try (ResultSet rs = s_sc.executeQuery()) {
            while (rs.next()) {
                result.add((Sottocategoria)getSottocategoria(rs.getString("nome")));
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare le sottocategorie", ex);
        }
        return result;
    }
    
    @Override
    public List<Microcategoria> getMicrocategorie() throws DataException {
        List<Microcategoria> result = new ArrayList();

        try (ResultSet rs = s_mc.executeQuery()) {
            while (rs.next()) {
                result.add((Microcategoria)getMicrocategoria(rs.getString("nome")));
            }
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare le microcategorie", ex);
        }
        return result;
    }

    @Override
    public List<Sottocategoria> getSCByC(Categoria categoria) throws DataException {
        List<Sottocategoria> result = new ArrayList<>();
        try {
            s_SCByC.setString(1, categoria.getKey());
            try (ResultSet rs = s_SCByC.executeQuery()) {
                while (rs.next()) {
                    result.add(getSottocategoria(rs.getString("nome")));
                }
            }
            return result;
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare le sottocategorie data la categoria", ex);
        }
    }

    @Override
    public List<Microcategoria> getMCBySC(Sottocategoria sottocategoria) throws DataException {
        List<Microcategoria> result = new ArrayList<>();
        try {
            s_MCBySC.setString(1, sottocategoria.getKey());
            try (ResultSet rs = s_MCBySC.executeQuery()) {
                while (rs.next()) {
                    result.add(getMicrocategoria(rs.getString("nome")));
                }
            }
        return result;
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare le microcategorie data la sottocategoria", ex);
        }
    }
    
    @Override
    public List<String> getSCNamesByC(Categoria categoria) throws DataException {
        List<String> result = new ArrayList<>();
        try {
            s_SCnByC.setString(1, categoria.getKey());
            try (ResultSet rs = s_SCnByC.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString("nome"));
                }
            }
            return result;
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare le sottocategorie data la categoria", ex);
        }
    }

    @Override
    public List<String> getMCNamesBySC(Sottocategoria sottocategoria) throws DataException {
        List<String> result = new ArrayList<>();
        try {
            s_MCnBySC.setString(1, sottocategoria.getKey());
            try (ResultSet rs = s_MCnBySC.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString("nome"));
                }
            }
        return result;
        } catch (SQLException ex) {
            throw new DataException("Impossibile trovare le microcategorie data la sottocategoria", ex);
        }
    }
        
    @Override
    public void storeCategoria(Categoria categoria,String oldID) throws DataException {
        try {
            if (categoria.getKey() != null) { //update
                //non facciamo nulla se l'oggetto è un proxy e indica di non aver subito modifiche
                if (categoria instanceof DataItemProxy && !((DataItemProxy) categoria).isModified()) {
                    return;
                }
                u.setString(1, categoria.getNome());
                u.setString(3, oldID);

                long current_version = categoria.getVersion();
                long next_version = current_version + 1;
                
                u.setLong(2, next_version);
                u.setLong(4, current_version);                

                
                if (u.executeUpdate() == 0) {
                    throw new OptimisticLockException(categoria);
                } else {
                    categoria.setVersion(next_version);
                }
            } else { //insert
                i.setString(1, categoria.getNome());
                
                if (i.executeUpdate() == 1) {
                    categoria.setKey(categoria.getNome());
                    //inseriamo il nuovo oggetto nella cache
                    dataLayer.getCache().add(Categoria.class, categoria);
                    }
                
                }
            
            //se abbiamo un proxy, resettiamo il suo attributo dirty
            if (categoria instanceof DataItemProxy) {
                ((DataItemProxy) categoria).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Impossibile inserire la categoria", ex);
        }
    }
    
    @Override
    public void storeSottocategoria(Sottocategoria sottocategoria,String oldID) throws DataException {
        try {
            if (sottocategoria.getKey() != null) { //update
                //non facciamo nulla se l'oggetto è un proxy e indica di non aver subito modifiche
                if (sottocategoria instanceof DataItemProxy && !((DataItemProxy) sottocategoria).isModified()) {
                    return;
                }
               
                u_sc.setString(1, sottocategoria.getNome());
                u_sc.setString(2, sottocategoria.getCategoria());
                u_sc.setString(4, oldID);
              
                long current_version = sottocategoria.getVersion(); //1
                long next_version = current_version + 1;
                
                u_sc.setLong(3, next_version);
                u_sc.setLong(5, current_version);                

                
                if (u_sc.executeUpdate() == 0) {
                    throw new OptimisticLockException(sottocategoria);
                } else {
                    sottocategoria.setVersion(next_version);
                }  
            } else { //insert

                i_sc.setString(1, sottocategoria.getNome());
                i_sc.setString(2, sottocategoria.getCategoria());
                        
                if (i_sc.executeUpdate() == 1) {
                    sottocategoria.setKey(sottocategoria.getNome());
                    //inseriamo il nuovo oggetto nella cache
                    dataLayer.getCache().add(Sottocategoria.class, sottocategoria);
                    }
                
                }
            
            //se abbiamo un proxy, resettiamo il suo attributo dirty
            if (sottocategoria instanceof DataItemProxy) {
                ((DataItemProxy) sottocategoria).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Impossibile inserire la sottocategoria", ex);
        }
    }
    
    @Override
    public void storeMicrocategoria(Microcategoria microcategoria, String oldID) throws DataException {
        try {
            if (microcategoria.getKey() != null) { //update
                //non facciamo nulla se l'oggetto è un proxy e indica di non aver subito modifiche
                if (microcategoria instanceof DataItemProxy && !((DataItemProxy) microcategoria).isModified()) {
                    return;
                }
               
                u_mc.setString(1, microcategoria.getNome());
                u_mc.setString(2, microcategoria.getSottocategoria());
                u_mc.setString(4, oldID);
              
                System.out.println("Nome: " + microcategoria.getNome());
                System.out.println("Sottocategoria: " + microcategoria.getSottocategoria());

                
                long current_version = microcategoria.getVersion(); //1
                long next_version = current_version + 1;
                
                u_mc.setLong(3, next_version);
                u_mc.setLong(5, current_version);                

                
                if (u_mc.executeUpdate() == 0) {
                    throw new OptimisticLockException(microcategoria);
                } else {
                    microcategoria.setVersion(next_version);
                } 
            } else { //insert

                i_mc.setString(1, microcategoria.getNome());
                i_mc.setString(2, microcategoria.getSottocategoria());
                        
                if (i_mc.executeUpdate() == 1) {
                    microcategoria.setKey(microcategoria.getNome());
                    //inseriamo il nuovo oggetto nella cache
                    dataLayer.getCache().add(Microcategoria.class, microcategoria);
                    }
                
                }
            
            //se abbiamo un proxy, resettiamo il suo attributo dirty
            if (microcategoria instanceof DataItemProxy) {
                ((DataItemProxy) microcategoria).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Impossibile inserire la microcategoria", ex);
        }
    }
    
     @Override
    public void deleteCategoria(Categoria categoria) throws DataException {
        try {
            //Lo cancello prima dalla cache
            dataLayer.getCache().delete(Categoria.class, categoria);
            d.setString(1, categoria.getKey());
            d.executeUpdate();

        } catch (SQLException e) {
            throw new DataException("Unable to delete CategoriaPadre", e);
        }
    }

    @Override
    public void deleteSottoCategoria(Sottocategoria sottocategoria) throws DataException {
        try {
            dataLayer.getCache().delete(Sottocategoria.class, sottocategoria);
            d_sc.setString(1, sottocategoria.getKey());
            d_sc.executeUpdate();

        } catch (SQLException e) {
            throw new DataException("Impossibile eliminare la sottocategoria", e);
        }
    }

    @Override
    public void deleteMicroCategoria(Microcategoria microcategoria) throws DataException {
        try {
            dataLayer.getCache().delete(Microcategoria.class, microcategoria);
            d_mc.setString(1, microcategoria.getKey());
            d_mc.executeUpdate();

        } catch (SQLException e) {
            throw new DataException("Impossibile eliminare la microcategoria", e);
        }

    }
}