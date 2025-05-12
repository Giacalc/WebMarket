package controller;

import data.dao.impl.WMDataLayer;
import data.model.*;
import data.model.impl.RichiestaImpl;
import framework.data.DataException;
import framework.result.SplitSlashesFmkExt;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import framework.utils.EmailSender;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class OrdinanteRichiesta extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response, String m) throws IOException, ServletException, TemplateManagerException, DataException {
        try {
            //riprendo la microcategoria definita
            Microcategoria mcategoria = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getMicrocategoria(m);
            if (mcategoria != null) {
                request.setAttribute("mcategoria", mcategoria.getNome());
                
                List<Caratteristica> caratteristiche = ((WMDataLayer) request.getAttribute("datalayer")).getCaratteristicaDAO().getCaratteristicheByCategoria(m);

                request.setAttribute("caratteristiche", caratteristiche);
                request.setAttribute("page_title", "Nuova Richiesta: Caratteristiche");

                TemplateResult res = new TemplateResult(getServletContext());
                
                request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
                res.activate("richiesta.ftl.html", request, response);
            } else {
                handleError("Impossibile caricare le caratteristiche della categoria", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }
    
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        try {
            HttpSession session = SecurityHelpers.checkSession(request);
            if (session == null) {
                response.sendRedirect("login");
                return;
            }
            request.setAttribute("active", "ords");

            String micro = request.getParameter("mcategoria");
            
            String action = request.getParameter("action");
            if (action != null && action.equals("createRichiesta")) {
                action_creaRichiesta(request, response, micro);
            } else {
                action_default(request, response, micro);
            }
    
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
    }    catch (DataException ex) {
             Logger.getLogger(OrdinanteRichiesta.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
  
    private void action_creaRichiesta(HttpServletRequest request, HttpServletResponse response, String micro) throws DataException, IOException, TemplateManagerException {        
        Richiesta r = new RichiestaImpl(); 
        
        // prendo l'ordinante in sessione
        HttpSession session = SecurityHelpers.checkSession(request);
        String user = (String)session.getAttribute("username");
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(user);
       
        r.setData(new Date());
        r.setIDord(u.getUsername());
        r.setStato("Da prendere in carico");
        
        //prendo la categoria
        Microcategoria mcat = ((WMDataLayer) request.getAttribute("datalayer")).
                getCategoriaDAO().getMicrocategoria(micro);
        r.setIDcat(mcat.getNome());
        
        //prendo le note dell'ordinante
        String note = request.getParameter("note");
        if (!note.isEmpty()) {
            r.setNote(note);
        } else {
            r.setNote("Nessuna nota aggiuntiva");
        }

        //inserisco la richiesta nel DB
        ((WMDataLayer) request.getAttribute("datalayer")).
                getRichiestaDAO().storeRichiesta(r);
        int idRichiesta = r.getKey();   
        
        // Riprendo le caratteristiche della microcategoria
        List<Caratteristica> c_all = ((WMDataLayer) request.getAttribute("datalayer"))
                .getCaratteristicaDAO().getCaratteristicheByCategoria(micro);

        // e le associo alla richiesta
        for (Caratteristica c : c_all) {
            
            // vedo se indifferente
            String valore = request.getParameter(c.getKey());
     
            if (valore != "") {
                 ((WMDataLayer) request.getAttribute("datalayer"))
                    .getRichiestaDAO().storeCaratteristicaRichiesta(c.getNome(),valore,idRichiesta);
            } else {
                 ((WMDataLayer) request.getAttribute("datalayer"))
                .getRichiestaDAO().storeCaratteristicaRichiesta(c.getNome(),"Indifferente",idRichiesta);
                
            }  
        }
        
        //invio mail
        SimpleDateFormat europeanFormat = new SimpleDateFormat("dd/MM/yyyy");
        String toEmail = "Tecnici"; //si assume l'invio al gruppo di mail dei tecnici
        String subject = "Nuova richiesta";
        String body = "È stata inserita una nuova richiesta:\n"
                + "\nCodice: " + idRichiesta
                + "\nData: " + europeanFormat.format((Date)r.getData())
                + "\nUtente: " + r.getIDord()
                + "\nCategoria: " + r.getIDcat()
                + "\n"
                + "\nGentilmente,"
                + "\nlo staff di WebMarket";

        try {
            EmailSender.sendEmail(toEmail, subject, body);
        } catch (Exception e) {
            request.setAttribute("page_title", "Errore");
            TemplateResult res = new TemplateResult(getServletContext());
            request.setAttribute("error", "Errore generale");
            request.setAttribute("err", "o");
            res.activate("error.ftl.html", request, response);
            return;
        }
        
        response.sendRedirect("ordinante_detail_richiesta?r="+ String.valueOf(idRichiesta));
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Richiesta servlet";
    }

}