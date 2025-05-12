package controller;

import framework.data.DataException;
import data.dao.impl.WMDataLayer;
import data.model.Utente;
import data.model.impl.UtenteImpl;
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

public class AdminUtente extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException {
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Aggiungi Utente");
        res.activate("admin_utente.ftl.html", request, response);
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException {
    try {
            //se l'admin non è loggato        
            HttpSession session = SecurityHelpers.checkSession(request);
            if (session == null) {
              response.sendRedirect("login");
              return;
            }
            request.setAttribute("active", "au");

            String action = request.getParameter("action");
            String u = request.getParameter("u");
            Utente ut;

            if (action != null && action.equals("createUser")) {
                action_createUser(request, response);
            } else {
                if(u != null) {
                    //gestione errore 404
                    ut = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(u);
                    if (ut == null) {
                        TemplateResult res = new TemplateResult(getServletContext());
                        request.setAttribute("page_title", "Errore");
                        request.setAttribute("error", "Pagina non trovata");
                        request.setAttribute("err", "o");
                        res.activate("error.ftl.html", request, response);
                        return;
                    }
                    action_defaultEdit(request,response);
                } else {
                    if(action != null && action.equals("updateUser"))
                        action_editUser(request,response);
                    else {
                        action_default(request, response);
                    }
                }
            }
    } catch (IOException | TemplateManagerException | DataException ex) {
        handleError(ex, request, response);
    }   catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AdminUtente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(AdminUtente.class.getName()).log(Level.SEVERE, null, ex);
        }
}

    private void action_createUser(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, DataException, TemplateManagerException, NoSuchAlgorithmException, InvalidKeySpecException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password1");
        String cPassword = request.getParameter("password2");
        String ruolo = request.getParameter("ruolo");
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");

    
        //gestione eccezioni
        if (username == null || email == null || password == null || cPassword == null || ruolo == null) {
            request.setAttribute("error", "Tutti i campi sono obbligatori.");
            action_default(request, response);
            return;
        }
        if (!password.equals(cPassword)) {
            request.setAttribute("error", "Le password non coincidono.");
            action_default(request, response);
            return;
        }
    
        // controllo username esistente nel database
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(username);
        if (u != null) {
            request.setAttribute("error", "Questo username è già utilizzato");
            action_default(request, response);
            return;
        }

        String pass = SecurityHelpers.getPasswordHashPBKDF2(password);

        Utente nu = new UtenteImpl();
        nu.setUsername(username);
        nu.setMail(email);
        nu.setNome(nome);
        nu.setCognome(cognome);
        nu.setPassword(pass);
        nu.setRuolo(ruolo);

        ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().storeUtente(nu, null);
        response.sendRedirect("admin_utenti");
    }
    
    private void action_defaultEdit(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException, DataException, TemplateManagerException {

        String username = request.getParameter("u");
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(username);

        request.setAttribute("utente", u);
        request.setAttribute("page_title", "Modifica Utente");

        TemplateResult res = new TemplateResult(getServletContext());
        res.activate("admin_utente.ftl.html", request, response);
    }
    private void action_editUser(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, DataException, TemplateManagerException, NoSuchAlgorithmException, InvalidKeySpecException {
           
        
        String oldID = request.getParameter("OLD");
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(oldID);

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password1");
        String cPassword = request.getParameter("password2");
        String ruolo = request.getParameter("ruolo");
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");

        //gestione eccezioni
        if (username == null || email == null || password == null || cPassword == null || ruolo == null) {
            request.setAttribute("error", "Tutti i campi sono obbligatori.");
            action_default(request, response);
            return;
        }
        if (!password.equals(cPassword)) {
       
            request.setAttribute("utente", u);
            request.setAttribute("page_title", "Modifica Utente");
            request.setAttribute("error", "Le password non coincidono");

            TemplateResult res = new TemplateResult(getServletContext());
            res.activate("admin_utente.ftl.html", request, response);
            return;
        }

        String pass = SecurityHelpers.getPasswordHashPBKDF2(password);

        u.setUsername(username);
        u.setMail(email);
        u.setNome(nome);
        u.setCognome(cognome);
        if(password.equals(""))
            u.setPassword(u.getPassword());
            else
             u.setPassword(pass);
        u.setRuolo(ruolo);

        ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().storeUtente(u, oldID);
        response.sendRedirect("admin_utenti");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Gestione Utente servlet";
    }

}
