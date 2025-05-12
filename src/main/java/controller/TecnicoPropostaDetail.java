package controller;

import data.dao.impl.WMDataLayer;
import data.model.Ordine;
import data.model.Proposta;
import data.model.Richiesta;
import data.model.Utente;
import data.model.impl.OrdineImpl;
import framework.data.DataException;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import framework.utils.EmailSender;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class TecnicoPropostaDetail extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException, DataException{
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Dettaglio Proposta");

        int propKey = Integer.parseInt(request.getParameter("id"));

        // Recupero proposta
        Proposta proposta = ((WMDataLayer) request.getAttribute("datalayer")).getPropostaDAO().getProposta(propKey);
        //gestione errore 404
        if (proposta == null) {
            request.setAttribute("page_title", "Errore");
            request.setAttribute("error", "Pagina non trovata");
            request.setAttribute("err", "o");
            res.activate("error.ftl.html", request, response);
            return;
        }
        
        request.setAttribute("proposta", proposta);
        request.setAttribute("prodotto", ((WMDataLayer) 
        request.getAttribute("datalayer")).getProdottoDAO().getProdotto(proposta.getIDprod()));
        
        res.activate("tecnico_detail_proposta.ftl.html", request, response);
    }

     private void action_creaOrdine(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException, DataException {
        int propKey = Integer.parseInt(request.getParameter("p"));
        Proposta prop = ((WMDataLayer) request.getAttribute("datalayer")).getPropostaDAO().getProposta(propKey);
        
        prop.setRevisione("Conclusa");
        ((WMDataLayer) request.getAttribute("datalayer")).getPropostaDAO().storeProposta(prop);
        
        Richiesta ric = ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().getRichiesta(prop.getIDric());
        ric.setStato("Completata");
        ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().storeRichiesta(ric);
        

        Ordine ordine = new OrdineImpl();
        ordine.setIdProp(propKey);
        ordine.setStato("In attesa");
 
        ((WMDataLayer) request.getAttribute("datalayer")).getOrdineDAO().storeOrdine(ordine);
        
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(ric.getIDord());
        //invio mail
        SimpleDateFormat europeanFormat = new SimpleDateFormat("dd/MM/yyyy");
        String toEmail = u.getMail(); 
        String subject = "Ordine per la richiesta #" + ric.getKey() + " effettuato";
        String body = "È stato inserito e spedito l'ordine per la richiesta menzionata:\n"
                + "\nCodice: " + ordine.getKey()
                + "\nData spedizione: " + europeanFormat.format((Date)ordine.getDataOrdine())
                + "\nDi conseguenza la richiesta #"+ ric.getKey()+ " è stata chiusa."
                + "\nSeguiranno aggiornamenti"
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

        response.sendRedirect("tecnico_ordini"); 
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
        request.setAttribute("active", "tprop");
        
        
        String action = request.getParameter("action");
        if (action != null && action.equals("CreaOrdine")) {
            action_creaOrdine(request, response);
        } else {
            action_default(request, response);
        }

    } catch (IOException | TemplateManagerException ex) {
        handleError(ex, request, response);
    }
    catch (DataException ex) {
        Logger.getLogger(TecnicoPropostaDetail.class.getName()).log(Level.SEVERE, null, ex);
    }
}

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Dettaglio proposta tecnico servlet";
    }

}