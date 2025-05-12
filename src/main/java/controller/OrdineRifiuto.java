package controller;

import data.dao.impl.WMDataLayer;
import data.model.Ordine;
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

public class OrdineRifiuto extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException, DataException{
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Rifiuto Ordine");

        int oKey = Integer.parseInt(request.getParameter("o"));
        Ordine ordine = ((WMDataLayer) request.getAttribute("datalayer")).getOrdineDAO().getOrdine(oKey);
        //gestione errore 404
        if (ordine == null) {
            request.setAttribute("page_title", "Errore");
            request.setAttribute("error", "Pagina non trovata");
            request.setAttribute("err", "o");
            res.activate("error.ftl.html", request, response);
            return;
        }
        
        request.setAttribute("ordine", String.valueOf(oKey));
        res.activate("ordine_rifiuto.ftl.html", request, response);
    }
    
    private void action_rifiutoOrd(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException, DataException {
        int oKey = Integer.parseInt(request.getParameter("o"));

        //si aggiorna ordine
        Ordine ord = ((WMDataLayer) request.getAttribute("datalayer")).getOrdineDAO().getOrdine(oKey);
        String valore = request.getParameter("ord_rifiuto");
        if(valore != null && valore.equals("rnc")) {
            ord.setStato("Rifiutato: non conforme");
        } else if (valore != null && valore.equals("rnf")){
            ord.setStato("Rifiutato: non funzionante");
        }
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
        if (action != null && action.equals("rifiutaOrdine")) {
            action_rifiutoOrd(request, response);
        } else{
            action_default(request, response);
        }

    } catch (IOException | TemplateManagerException ex) {
        handleError(ex, request, response);
    }   catch (DataException ex) {
            Logger.getLogger(OrdineRifiuto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Rifiuto ordine servlet";
    }

}