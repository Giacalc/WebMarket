package data.dao;

import data.model.Proposta;
import framework.data.DataException;
import java.util.List;

public interface PropostaDAO {

    Proposta createProposta();

    Proposta getProposta(int proposta_key) throws DataException;
    
    List<Proposta> getPropostebyUser(String user_key) throws DataException;
    
    List<Proposta> getPropostebyTecnico(String tecnico_key) throws DataException;
    
    List<Proposta> getProposte() throws DataException;
    
    List<Proposta> getPropostebyRichiesta(int richiesta_key) throws DataException;
    
    void storeProposta(Proposta proposta) throws DataException;
}
