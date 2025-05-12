package controller;

import framework.data.DataException;
import data.dao.impl.WMDataLayer;
import data.model.Richiesta;
import data.model.Utente;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class TecnicoHomepage extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException, ServletException, TemplateManagerException, DataException {
       TemplateResult res = new TemplateResult(getServletContext());
       request.setAttribute("page_title", "Homepage");

       List<Richiesta> richiestenc = ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().getRichiesteNonEvase();
       request.setAttribute("richieste", richiestenc);
       request.setAttribute("richieste_carico", ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().getRichieste_Carico(userId));

       res.activate("tecnico_homepage.ftl.html", request, response);
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

        // Recupero l'ID dell'utente dalla sessione
        String userId = (String) session.getAttribute("userid");
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(userId);
        
        if (u != null) {
            request.setAttribute("utente", userId);
        }

        action_default(request, response, userId);

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
        return "Tecnico Homepage servlet";
    }

}