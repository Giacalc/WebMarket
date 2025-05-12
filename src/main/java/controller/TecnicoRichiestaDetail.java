package controller;

import framework.data.DataException;
import data.dao.impl.WMDataLayer;
import data.model.Richiesta;
import data.model.Utente;
import framework.result.SplitSlashesFmkExt;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import framework.utils.EmailSender;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class TecnicoRichiestaDetail extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException, DataException {
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Dettaglio Richiesta");
        
        //se arriviamo in questa pagina da una proposta...
        String p = request.getParameter("p");
        if (p != null) {
            request.setAttribute("proposta", p);
        }
        
        int richiestaId = Integer.parseInt(request.getParameter("r"));
        Richiesta ric = ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().getRichiesta(richiestaId);
        //gestione errore 404
        if (ric == null) {
            request.setAttribute("page_title", "Errore");
            request.setAttribute("error", "Pagina non trovata");
            request.setAttribute("err", "o");
            res.activate("error.ftl.html", request, response);
            return;
        }
        
        request.setAttribute("richiesta", ric);

        request.setAttribute("caratteristiche", ((WMDataLayer) request.getAttribute("datalayer")).getCaratteristicaDAO().getValoriCaratteristicheByRichiesta(richiestaId));
        request.setAttribute("strip_slashes", new SplitSlashesFmkExt());

        res.activate("tecnico_detail_richiesta.ftl.html", request, response);
    }
    
     private void action_updateTec(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException, ServletException, TemplateManagerException, DataException {
        
        int richiestaId = Integer.parseInt(request.getParameter("r"));

        // Recupero la richiesta dal database utilizzando il DAO
        request.setAttribute("richiesta", ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().getRichiesta(richiestaId));
        Richiesta ric = ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().getRichiesta(richiestaId);
        
        ric.setIDtec(userId);
        ric.setStato("Presa in carico");
        
        ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().storeRichiesta(ric);
       
        //ordinante
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(ric.getIDord());
        
        //invio mail
        String toEmail = u.getMail(); //si assume l'invio al gruppo di mail dei tecnici
        String subject = "Aggiornamento Richiesta #" + ric.getKey();
        String body = "La tua richiesta è stata presa in carico dal tecnico '" + userId + "'"
                + "\nSeguiranno aggiornamenti."
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

        response.sendRedirect("tecnico_homepage");
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
        request.setAttribute("active", "tric");
        
        // Recupero l'ID dell'utente dalla sessione
        String userId = (String) session.getAttribute("userid");
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(userId);
        
        if (u != null) {
            request.setAttribute("user", u);
        }

        String action = request.getParameter("action");
            if (action != null && action.equals("updateTec")) {
                action_updateTec(request, response, userId);
            } else {
                action_default(request, response);
            }

    } catch (IOException | TemplateManagerException | DataException ex) {
        handleError(ex, request, response);
    }
}

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Tecnico detail richiesta servlet";
    }

}