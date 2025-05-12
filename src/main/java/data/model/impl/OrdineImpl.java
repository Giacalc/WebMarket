package data.model.impl;
import data.model.*;
import framework.data.DataItemImpl;
import java.util.Date;

public class OrdineImpl extends DataItemImpl<Integer> implements Ordine {
    private String stato;
    private Date dataOrdine;
    private Date dataConsegna;
    private int idProp;
    
    public OrdineImpl(){
        this.stato="";
        this.dataOrdine=new Date();
        this.dataConsegna=new Date();
        this.idProp=0;
    }
    @Override
    public String getStato(){
        return stato;
    }
    @Override
    public void setStato(String stato){
        this.stato=stato;
    }
    @Override
    public Date getDataOrdine(){
        return dataOrdine;
    }
    @Override
    public void setDataOrdine(Date data){
        this.dataOrdine=data;
    }
    @Override
    public Date getDataConsegna(){
        return dataConsegna;
    }
    @Override
    public void setDataConsegna(Date data){
        this.dataConsegna=data;
    }
    @Override
    public int getIdProp(){
        return idProp;
    }
    @Override
    public void setIdProp(int idProp){
        this.idProp=idProp;
    }  
}
