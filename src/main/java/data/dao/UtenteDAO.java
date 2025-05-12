package data.dao;

import data.model.Utente;
import framework.data.DataException;
import java.util.List;

public interface UtenteDAO {

    Utente createUtente();

    Utente getUtente(String utente_key) throws DataException;
    
    List<Utente> getUtenti() throws DataException;
    
    void storeUtente(Utente utente, String oldID) throws DataException;
    
    void deleteUtente(Utente utente) throws DataException;
}
