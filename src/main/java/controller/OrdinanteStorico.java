package controller;

import framework.data.DataException;
import data.dao.impl.WMDataLayer;
import data.model.Richiesta;
import data.model.Utente;
import framework.result.SplitSlashesFmkExt;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class OrdinanteStorico extends BaseController {

     private void action_default(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException, ServletException, TemplateManagerException, DataException {
        
         try {   
            
            List<Richiesta> richieste = ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().getRichiesteConcluse(userId) ;

            request.setAttribute("richieste", richieste);

            request.setAttribute("page_title", "Storico");

            //mapping pagina HTML
            TemplateResult res = new TemplateResult(getServletContext());

            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            res.activate("ordinante_storico.ftl.html", request, response);
            
            }
        catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
           
    }
     
    private void action_redirect(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException, ServletException, TemplateManagerException, DataException {
        
         try {   
            
            List<Richiesta> richieste = ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().getRichiesteByOrd(userId) ;

            request.setAttribute("richieste", richieste);

            request.setAttribute("page_title", "Storico");

            //mapping pagina HTML
            TemplateResult res = new TemplateResult(getServletContext());

            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            
             res.activate("ordinante_storico.ftl.html", request, response);
            
            }
        catch (DataException ex) {
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

        // Recupero l'ID dell'utente dalla sessione
        String userId = (String) session.getAttribute("userid");
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(userId);
        
        if (u != null) {
            request.setAttribute("user", u);
        }

        String action = request.getParameter("action");
        if (action != null && action.equals("storicoRichiesta")) {
               action_redirect(request, response, userId);
           } else {
                action_default(request, response, userId);
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
        return "Ordinante Storico servlet";
    }

}