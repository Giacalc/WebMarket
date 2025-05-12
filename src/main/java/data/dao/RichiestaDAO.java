package data.dao;

import data.model.Richiesta;
import framework.data.DataException;
import java.util.List;

public interface RichiestaDAO {

    Richiesta createRichiesta();

    Richiesta getRichiesta(int richiestaKey) throws DataException;
    
    List<Richiesta> getRichieste() throws DataException;
    
    List<Richiesta> getRichiesteNonEvase()throws DataException;
    
    List<Richiesta> getRichiesteByOrd(String ord_key) throws DataException;
    
    List<Richiesta> getRichieste_Carico(String ord_key) throws DataException;
    
    List<Richiesta> getRichiesteInCorso(String idOrd) throws DataException;
    
    List<Richiesta> getRichiesteConcluse(String idOrd)throws DataException;

    void storeRichiesta(Richiesta richiesta) throws DataException;
    
    void storeCaratteristicaRichiesta(String nome, String valore, int idRichiesta) throws DataException;
}
