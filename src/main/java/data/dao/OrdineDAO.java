package data.dao;

import data.model.Ordine;
import framework.data.DataException;
import java.util.List;


public interface OrdineDAO {
    
    Ordine createOrdine();

    Ordine getOrdine(int ordine_key) throws DataException;
    
    List<Ordine> getOrdinibyUser(String user_key) throws DataException;
    
    List<Ordine> getOrdinibyTecnico(String tecnico_key) throws DataException;
    
    List<Ordine> getOrdini() throws DataException;
    
    void storeOrdine(Ordine ordine) throws DataException;
    
}
