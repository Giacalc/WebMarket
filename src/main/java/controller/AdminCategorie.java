package controller;

import framework.data.DataException;
import data.dao.impl.WMDataLayer;
import data.model.Categoria;
import data.model.Utente;
import framework.result.SplitSlashesFmkExt;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminCategorie extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException, ServletException, TemplateManagerException, DataException {
        try {
            request.setAttribute("categorie", ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getCategorie());
           
            request.setAttribute("page_title", "Categorie");
                
            TemplateResult res = new TemplateResult(getServletContext());
              
            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            res.activate("admin_categorie.ftl.html", request, response);
          
        } catch (DataException ex) {
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
            request.setAttribute("active", "ac");
            
            String action = request.getParameter("action");
            String pcategoria = request.getParameter("cat");
            // Recupero l'ID dell'utente dalla sessione
            String userId = (String) session.getAttribute("userid");
            Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(userId);

            if (u != null) {
                request.setAttribute("user", u);
            }
            if(pcategoria != null ){ 
                action_deleteCategoria(request, response, pcategoria);
            }else{
                action_default(request, response, userId);
            }
            

        } catch (IOException | TemplateManagerException | DataException ex) {
            handleError(ex, request, response);
        }
    }
    
    private void action_deleteCategoria(HttpServletRequest request, HttpServletResponse response, String pcategoria) 
        throws IOException, ServletException, DataException, TemplateManagerException {
        try {
            Categoria categoria = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getCategoria(pcategoria);
            if (categoria != null) {
                ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().deleteCategoria(categoria);
                response.sendRedirect("admin_categorie");
            } else {
                handleError("Unable to find category to delete", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Admin Categorie servlet";
    }
}
