package data.dao;

import data.model.Categoria;
import data.model.Microcategoria;
import data.model.Sottocategoria;
import framework.data.DataException;
import java.util.List;

public interface CategoriaDAO {
    
    //metodi Categoria
    Categoria createCategoria();

    Categoria getCategoria(String categoria_key) throws DataException;
    
    List<Categoria> getCategorie() throws DataException;
    
    void storeCategoria(Categoria categoria, String oldID) throws DataException;
    
    
    //metodi Sottocategoria
    Sottocategoria createSottocategoria();

    Sottocategoria getSottocategoria(String sc_key) throws DataException;
      
    String getSottocategoriaByMc(String microcat) throws DataException;

    List<Sottocategoria> getSottocategorie() throws DataException;
    
    void storeSottocategoria(Sottocategoria sottocategoria, String oldID) throws DataException;
    
    
    //metodi Microcategoria
    Microcategoria createMicrocategoria();

    Microcategoria getMicrocategoria(String mc_key) throws DataException;
    
    List<Microcategoria> getMicrocategorie() throws DataException;
    
    void storeMicrocategoria(Microcategoria microcategoria, String oldID) throws DataException;

    
    //getby
    List<Sottocategoria> getSCByC(Categoria categoria) throws DataException;
    List<Microcategoria> getMCBySC(Sottocategoria sottocategoria) throws DataException;
    
    List<String> getSCNamesByC(Categoria categoria) throws DataException;
    List<String> getMCNamesBySC(Sottocategoria sottocategoria) throws DataException;
    
    //delete
    void deleteCategoria(Categoria categoria) throws DataException;
    
    void deleteSottoCategoria(Sottocategoria sottocategoria) throws DataException;

    void deleteMicroCategoria(Microcategoria microcategoria) throws DataException;
}
