package controller;

import data.dao.impl.WMDataLayer;
import data.model.Utente;
import framework.data.DataException;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.*;
import javax.servlet.http.*;

public class Login extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, TemplateManagerException {
        TemplateResult result = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Login");
        request.setAttribute("referrer", request.getParameter("referrer"));
        result.activate("login.ftl.html", request, response);
    }

    private void action_login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (!username.isEmpty() && !password.isEmpty()) {
            try {
                Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(username);
                if (u != null && SecurityHelpers.checkPasswordHashPBKDF2(password, u.getPassword())) {
                    //se la validazione ha successo
                    String ruolo = u.getRuolo();
                    SecurityHelpers.createSession(request, username, ruolo);
                    
                    String redirectPage;
                    redirectPage = switch (ruolo) {
    
                        case "Ordinante" -> "ordinante_homepage";
                        case "Tecnico" -> "tecnico_homepage";
                        case "Amministratore" -> "admin_homepage";
                        default -> "login";
                    };
                    
                    String referrer = request.getParameter("referrer");
                 
                    if (referrer != null) {
                        response.sendRedirect(referrer);
                    } else {
                        response.sendRedirect(redirectPage);
                    }
                    return;
                } else {
                    request.setAttribute("error", "Username o password non corretti");
                    action_default(request, response);
               }
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | DataException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TemplateManagerException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        try {
            if (request.getParameter("login") != null) {
                action_login(request, response);
            } else {
                String https_redirect_url = SecurityHelpers.checkHttps(request);
                request.setAttribute("https-redirect", https_redirect_url);
                action_default(request, response);
            }
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
