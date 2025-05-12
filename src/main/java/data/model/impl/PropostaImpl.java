package data.model.impl;
import data.model.*;
import framework.data.DataItemImpl;
import java.util.Date;

public class PropostaImpl extends DataItemImpl<Integer> implements Proposta {
    private Date data;
    private String revisione;
    private String note;
    private String rev_motivazione;
    private int IDric;
    private String IDprod;
    private String IDtec;

    public PropostaImpl() {
        this.data = new Date();
        this.revisione = "";
        this.note = "";
        this.rev_motivazione = "";
        this.IDric = 0;
        this.IDprod = "";
        this.IDtec = "";
        
    }
    
   
    @Override
    public Date getData() {
        return data;
    }
    @Override
    public void setData(Date data) {
        this.data = data;
    }
    @Override
    public String getRevisione() {
        return revisione;
    }
    @Override
    public void setRevisione(String revisione) {
        this.revisione = revisione;
    }
    @Override
    public String getNote() {
        return note;
    }
    @Override
    public void setNote(String note) {
        this.note = note;
    }
    @Override
    public String getRev_motivazione() {
        return rev_motivazione;
    }
    @Override
    public void setRev_motivazione(String rev_motivazione) {
        this.rev_motivazione = rev_motivazione;
    }
    
    @Override
    public int getIDric() {
        return IDric;
    }
    @Override
    public void setIDric(int idRic) {
        this.IDric = idRic;
    }
    @Override
    public String getIDprod() {
        return IDprod;
    }
    @Override
    public void setIDprod(String idProd) {
        this.IDprod = idProd;
        
    }
    @Override
    public String getIDtec() {
        return IDtec;
    }
    @Override
    public void setIDtec(String idTec) {
        this.IDtec = idTec;
    }

}
