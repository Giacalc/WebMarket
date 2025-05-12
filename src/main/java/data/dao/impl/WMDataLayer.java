package data.dao.impl;

import data.dao.CaratteristicaDAO;
import data.dao.CategoriaDAO;
import data.dao.OrdineDAO;
import data.dao.ProdottoDAO;
import data.dao.PropostaDAO;
import data.dao.RichiestaDAO;
import data.dao.UtenteDAO;
import data.model.Caratteristica;
import data.model.Categoria;
import data.model.Ordine;
import data.model.Prodotto;
import data.model.Proposta;
import data.model.Richiesta;
import data.model.Utente;
import framework.data.DataException;
import framework.data.DataLayer;
import java.sql.SQLException;
import javax.sql.DataSource;

public class WMDataLayer extends DataLayer {

    public WMDataLayer(DataSource datasource) throws SQLException {
        super(datasource);
    }

    @Override
    public void init() throws DataException {
        //registriamo i nostri dao
        //register our daos
        registerDAO(Caratteristica.class, new CaratteristicaDAO_Impl(this));
        registerDAO(Categoria.class, new CategoriaDAO_Impl(this));
        registerDAO(Ordine.class, new OrdineDAO_Impl(this));
        registerDAO(Prodotto.class, new ProdottoDAO_Impl(this));
        registerDAO(Proposta.class, new PropostaDAO_Impl(this));
        registerDAO(Richiesta.class, new RichiestaDAO_Impl(this));
        registerDAO(Utente.class, new UtenteDAO_Impl(this));
    }

    //helpers    
    public CaratteristicaDAO getCaratteristicaDAO() {
        return (CaratteristicaDAO) getDAO(Caratteristica.class);
    }

    public CategoriaDAO getCategoriaDAO() {
        return (CategoriaDAO) getDAO(Categoria.class);
    }

    
    public OrdineDAO getOrdineDAO() {
        return (OrdineDAO) getDAO(Ordine.class);
    }
    
    public ProdottoDAO getProdottoDAO() {
        return (ProdottoDAO) getDAO(Prodotto.class);
    }
    
    public PropostaDAO getPropostaDAO() {
        return (PropostaDAO) getDAO(Proposta.class);
    }
    
    public RichiestaDAO getRichiestaDAO() {
        return (RichiestaDAO) getDAO(Richiesta.class);
    }
    
    public UtenteDAO getUtenteDAO() {
        return (UtenteDAO) getDAO(Utente.class);
    }

}
