package controller;

import framework.data.DataException;
import data.dao.impl.WMDataLayer;
import data.model.Utente;
import framework.result.SplitSlashesFmkExt;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class AdminUtenti extends BaseController {

     private void action_default(HttpServletRequest request, HttpServletResponse response, String user) throws IOException, ServletException, TemplateManagerException {
        try {   
            
            List<Utente> utenti = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtenti() ;

            request.setAttribute("utenti", utenti);
            
            request.setAttribute("utente", user);

            request.setAttribute("page_title", "Utenti");

            //mapping pagina HTML
            TemplateResult res = new TemplateResult(getServletContext());

            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            res.activate("admin_utenti.ftl.html", request, response);
            
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
        request.setAttribute("active", "au");
        
        // Recupero l'ID dell'utente dalla sessione
        String userId = (String) session.getAttribute("userid");
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(userId);
        if (u != null) {
            request.setAttribute("user", u);
        }
        
        String utenteToDelete = (String) request.getParameter("duser");
        
        if(utenteToDelete != null)
            action_deleteUtente(request, response, utenteToDelete);
        else
            action_default(request, response, userId);
        
    } catch (IOException | TemplateManagerException  ex) {
        handleError(ex, request, response);
    }    catch (DataException ex) {
             Logger.getLogger(AdminUtenti.class.getName()).log(Level.SEVERE, null, ex);
         }
}
   private void action_deleteUtente(HttpServletRequest request, HttpServletResponse response, String utenteToDelete) 
        throws IOException, ServletException, DataException, TemplateManagerException {
        try {
            Utente utente = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(utenteToDelete);
            if (utente != null) {
                ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().deleteUtente(utente);
                response.sendRedirect("admin_utenti");
            } else {
                handleError("Unable to find category to delete", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Admin utenti servlet";
    }

}