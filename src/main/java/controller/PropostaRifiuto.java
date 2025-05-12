package controller;

import data.dao.impl.WMDataLayer;
import data.model.Proposta;
import data.model.Richiesta;
import data.model.Utente;
import framework.data.DataException;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import framework.utils.EmailSender;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class PropostaRifiuto extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException, DataException{
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Rifiuto Proposta");

        int propKey = Integer.parseInt(request.getParameter("p"));
        Proposta proposta = ((WMDataLayer) request.getAttribute("datalayer")).getPropostaDAO().getProposta(propKey);
        //gestione errore 404
        if (proposta == null) {
            request.setAttribute("page_title", "Errore");
            request.setAttribute("error", "Pagina non trovata");
            request.setAttribute("err", "o");
            res.activate("error.ftl.html", request, response);
            return;
        }
        
        request.setAttribute("proposta", String.valueOf(propKey));
        res.activate("proposta_rifiuto.ftl.html", request, response);
    }
    
    private void action_rifiutoProp(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException, DataException {
        int propKey = Integer.parseInt(request.getParameter("p"));
        String motivazione = request.getParameter("motivazione");

        //si aggiorna proposta
        Proposta prop = ((WMDataLayer) request.getAttribute("datalayer")).getPropostaDAO().getProposta(propKey);
        prop.setRevisione("Rifiutata");
        prop.setRev_motivazione(motivazione);
        ((WMDataLayer) request.getAttribute("datalayer")).getPropostaDAO().storeProposta(prop);
        
        //si aggiorna richiesta
        Richiesta ric = ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().getRichiesta(prop.getIDric());
        ric.setStato("Presa in carico");
        ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().storeRichiesta(ric);
        
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(prop.getIDtec());
        //invio mail
        String toEmail = u.getMail(); 
        String subject = "Proposta #" + prop.getKey() + " rifiutata";
        String body = "La proposta per la richiesta #" + prop.getIDric() + " è stata rifiutata.\n"
                + "Riprovare con l'inserimento di una nuova proposta"
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
        
        response.sendRedirect("ordinante_proposte"); 
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
        request.setAttribute("active", "ordp");

        String action = request.getParameter("action");
        if (action != null && action.equals("rifiutaProposta")) {
            action_rifiutoProp(request, response);
        } else{
            action_default(request, response);
        }

    } catch (IOException | TemplateManagerException ex) {
        handleError(ex, request, response);
    }   catch (DataException ex) {
            Logger.getLogger(PropostaRifiuto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Rifiuto proposta servlet";
    }

}