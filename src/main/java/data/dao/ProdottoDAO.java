package data.dao;

import data.model.Prodotto;
import framework.data.DataException;
import java.util.List;

public interface ProdottoDAO {

    Prodotto createProdotto();

    Prodotto getProdotto(String prodotto_key) throws DataException;
    
    List<Prodotto> getProdotti() throws DataException;
    
    void storeProdotto(Prodotto prodotto, String oldID) throws DataException;
}
