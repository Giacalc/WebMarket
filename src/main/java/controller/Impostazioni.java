package controller;

import data.dao.impl.WMDataLayer;
import data.model.Utente;
import framework.data.DataException;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Impostazioni extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response, String user) throws IOException, ServletException, TemplateManagerException, DataException {
        TemplateResult res = new TemplateResult(getServletContext());
        
        request.setAttribute("page_title", "Impostazioni");
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(user);
        request.setAttribute("utente", u);

        res.activate("impostazioni.ftl.html", request, response);
        
        
    }
    
    private void action_update_profilo(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException, ServletException, TemplateManagerException, DataException, NoSuchAlgorithmException, InvalidKeySpecException {
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(userId);
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        u.setUsername(username);
        u.setMail(email);
        u.setNome(nome);
        u.setCognome(cognome);
        ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().storeUtente(u, userId);
        if(u.getRuolo().equals("Tecnico")){
            response.sendRedirect("tecnico_homepage");

        }
        if(u.getRuolo().equals("Ordinante")){
            response.sendRedirect("ordinante_homepage");
        }
    }

    
    private void action_update(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException, ServletException, TemplateManagerException, DataException, NoSuchAlgorithmException, InvalidKeySpecException {
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(userId);
        String vpassword = request.getParameter("vpassword");
        String npassword1 = request.getParameter("npassword1");
        String npassword2 = request.getParameter("npassword2");
        
        if (vpassword == null || vpassword.trim().isEmpty() || npassword1 == null || npassword1.trim().isEmpty()||npassword2 == null || npassword2.trim().isEmpty()) {
            request.setAttribute("errore", "Tutti i campi devono essere compilati!");
            action_default(request, response, userId);
            return; 
        }
        
        if (!npassword1.equals(npassword2)) {
            request.setAttribute("errore", "Le password non coincidono.");
            action_default(request, response, userId);
            return;
            }
        
        if (!SecurityHelpers.checkPasswordHashPBKDF2(vpassword, u.getPassword())){
            request.setAttribute("errore", "La password corrente è errata");
            action_default(request, response, userId);
            return;  
        }
        
        String hashedPass = SecurityHelpers.getPasswordHashPBKDF2(npassword1);
        u.setPassword(hashedPass);
        ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().storeUtente(u, u.getKey());
        if(u.getRuolo().equals("Tecnico")){
            response.sendRedirect("tecnico_homepage");
        }
        if(u.getRuolo().equals("Ordinante")){
            response.sendRedirect("ordinante_homepage");
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
        request.setAttribute("active", "i");
        
        String user = (String)session.getAttribute("username");
        String action = request.getParameter("action");
        if (action != null && action.equals("cambiopassword")){
            action_update(request, response, user);
        } 
        if(action != null && action.equals("impostazioni")){
            action_update_profilo(request, response, user);
        }
        else{
            action_default(request, response, user);
        }
        
    } catch (IOException | TemplateManagerException ex) {
        handleError(ex, request, response);
    }   catch (DataException ex) {
            Logger.getLogger(Impostazioni.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Impostazioni.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Impostazioni.class.getName()).log(Level.SEVERE, null, ex);
        }
}


    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Impostazioni servlet";
    }

}