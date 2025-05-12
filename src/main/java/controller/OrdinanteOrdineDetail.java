package controller;

import data.dao.impl.WMDataLayer;
import data.model.Ordine;
import data.model.Proposta;
import data.model.Richiesta;
import framework.data.DataException;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class OrdinanteOrdineDetail extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException, DataException{
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Dettaglio Ordine");

        int oKey = Integer.parseInt(request.getParameter("id"));

        Ordine ordine = ((WMDataLayer) request.getAttribute("datalayer")).getOrdineDAO().getOrdine(oKey);
        //gestione errore 404
        if (ordine == null) {
            request.setAttribute("page_title", "Errore");
            request.setAttribute("error", "Pagina non trovata");
            request.setAttribute("err", "o");
            res.activate("error.ftl.html", request, response);
            return;
        }
        
        //per ordinante a cui è associato ordine
        Proposta prop = ((WMDataLayer) request.getAttribute("datalayer")).getPropostaDAO().getProposta(ordine.getIdProp());
        Richiesta ric = ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().getRichiesta(prop.getIDric());
        
        request.setAttribute("ordine", ordine);
        request.setAttribute("richiesta", ric);
        
        res.activate("ordinante_detail_ordine.ftl.html", request, response);
    }
    
    private void action_accOrdine(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException, DataException {
        int oKey = Integer.parseInt(request.getParameter("o"));
        Ordine ord = ((WMDataLayer) request.getAttribute("datalayer")).getOrdineDAO().getOrdine(oKey);
        
        ord.setStato("Accettato");
        ((WMDataLayer) request.getAttribute("datalayer")).getOrdineDAO().storeOrdine(ord);
        
        response.sendRedirect("ordinante_ordini"); 
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
        request.setAttribute("active", "ordo");
       
        String action = request.getParameter("action");
        if (action != null && action.equals("AccettaOrdine")) {
            action_accOrdine(request, response);
        } else {
            action_default(request, response);
        }

    } catch (IOException | TemplateManagerException ex) {
        handleError(ex, request, response);
    }
    catch (DataException ex) {
        Logger.getLogger(OrdinanteOrdineDetail.class.getName()).log(Level.SEVERE, null, ex);
    }
}

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Dettaglio ordine ordinante servlet";
    }
}