package controller;

import data.dao.impl.WMDataLayer;
import data.model.Proposta;
import data.model.Richiesta;
import framework.data.DataException;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class OrdinanteRichiestaDetail extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response, String user) throws IOException, ServletException, TemplateManagerException, DataException{
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Dettaglio Richiesta");

        //se arriviamo in questa pagina da una proposta...
        String p = request.getParameter("p");
        if (p != null) {
            request.setAttribute("proposta", p);
        }
        
        int richiestaId = Integer.parseInt(request.getParameter("r"));
        // Recupero la richiesta
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
        // Recupero le caratteristiche
        Map<String,String> cars = ((WMDataLayer) request.getAttribute("datalayer")).getCaratteristicaDAO().getValoriCaratteristicheByRichiesta(richiestaId);
        request.setAttribute("caratteristiche", cars);
        // Recupero le proposte associate
        List<Proposta> proposte = ((WMDataLayer) request.getAttribute("datalayer")).getPropostaDAO().getPropostebyRichiesta(richiestaId) ;
        request.setAttribute("proposte", proposte);
        res.activate("ordinante_detail_richiesta.ftl.html", request, response);
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
        
    // Recupero l'ID dell'utente dalla sessione
        String userId = (String) session.getAttribute("userid");
        String u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(userId).getUsername();

        action_default(request, response, u);

    } catch (IOException | TemplateManagerException ex) {
        handleError(ex, request, response);
    }
    catch (DataException ex) {
        Logger.getLogger(OrdinanteRichiestaDetail.class.getName()).log(Level.SEVERE, null, ex);
    }
}


    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Dettaglio richiesta ordinante servlet";
    }

}