package data.dao;

import data.model.Caratteristica;
import framework.data.DataException;
import java.util.*;

public interface CaratteristicaDAO {

    Caratteristica createCaratteristica();

    Caratteristica getCaratteristica(String caratteristica_key) throws DataException;
    
    List<Caratteristica> getCaratteristiche() throws DataException;
    
    List<Caratteristica> getCaratteristicheByCategoria(String microcategoria_key) throws DataException;
    
    Map<String,String> getValoriCaratteristicheByRichiesta(int idRichiesta) throws DataException;
    

    void storeCaratteristica(Caratteristica caratteristica, String oldID, String oldM) throws DataException;
    
    void deleteCaratteristica(Caratteristica caratteristica) throws DataException;
}
