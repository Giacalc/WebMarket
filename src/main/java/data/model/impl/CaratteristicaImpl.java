package data.model.impl;
import data.model.Caratteristica;
import framework.data.DataItemImpl;

public class CaratteristicaImpl extends DataItemImpl<String> implements Caratteristica{
    private String nome;
    //molti a molti
    private String microcategoria;

    public CaratteristicaImpl() {
        this.nome="";
    }
    @Override
    public String getNome() {
        return nome;
    }
    @Override   
    public void setNome(String nome) {
        this.nome = nome;
    }
    @Override
    public String getMicrocategoria() {
        return microcategoria;
    }
    @Override
    public void setMicrocategoria(String mcategoria) {
        this.microcategoria = mcategoria;
    }
 
}
