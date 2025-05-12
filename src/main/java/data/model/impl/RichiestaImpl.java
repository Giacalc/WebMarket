package data.model.impl;
import data.model.*;
import framework.data.DataItemImpl;
import java.util.Date;

public class RichiestaImpl extends DataItemImpl<Integer> implements Richiesta {
    private Date data;
    private String stato;
    private String note;
    private String IDord;
    private String IDcat;
    private String IDtec;

    public RichiestaImpl() {
        this.data = new Date();
        this.stato = "";
        this.note = "";
        this.IDord = "";
        this.IDcat = "";
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
    public String getStato() {
        return stato;
    }
    @Override
    public String getNote() {
        return note;
    }
    @Override
    public void setStato(String stato) {
        this.stato = stato;
    }
    @Override
    public void setNote(String note) {
        this.note = note;
    }
    @Override
    public String getIDord() {
        return IDord;
    }
    @Override
    public void setIDord(String idOrd) {
        this.IDord = idOrd;
    }
    @Override
    public String getIDcat() {
        return IDcat;
    }
    @Override
    public void setIDcat(String idCat) {
        this.IDcat = idCat;
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
