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

public class OrdinantePropostaDetail extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException, DataException{
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Dettaglio Proposta");

        String r = request.getParameter("r");
        

        if (r != null) {
            int ricInt = Integer.parseInt(r);
            Richiesta richiesta = ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().getRichiesta(ricInt);
            request.setAttribute("richiesta", richiesta);
        }
        
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
        
        res.activate("ordinante_detail_proposta.ftl.html", request, response);
    }
    
    private void action_accettaProp(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException,DataException {
        Proposta proposta = ((WMDataLayer) request.getAttribute("datalayer")).getPropostaDAO().getProposta(SecurityHelpers.checkNumeric(request.getParameter("p")));
        proposta.setRevisione("Accettata");
        ((WMDataLayer) request.getAttribute("datalayer")).getPropostaDAO().storeProposta(proposta); 
        
        
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(proposta.getIDtec());
        //invio mail
        String toEmail = u.getMail(); 
        String subject = "Proposta #" + proposta.getKey() + " accettata";
        String body = "La proposta per la richiesta #" + proposta.getIDric() + " è stata accettata.\n"
                + "Si può procedere con l'inserimento di un relativo ordine"
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
        
        response.sendRedirect("ordinante_detail_proposta?id="+proposta.getKey());
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
        if (action != null && action.equals("AccettaProposta")) {
            action_accettaProp(request, response);
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
        return "Dettaglio proposta ordinante servlet";
    }

}