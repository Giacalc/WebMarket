package controller;

import data.dao.impl.WMDataLayer;
import data.model.Categoria;
import data.model.Sottocategoria;
import framework.data.DataException;
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

public class AdminSottocategorie extends BaseController{
    
    private void action_default(HttpServletRequest request, HttpServletResponse response, String cat) throws IOException, ServletException, TemplateManagerException, DataException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            Categoria categoria = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getCategoria(cat);
            if (categoria != null) {
                request.setAttribute("categoria", categoria);
                
                List<Sottocategoria> sottocategorie = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSCByC(categoria);
                
                request.setAttribute("sottocategorie", sottocategorie);
                
                request.setAttribute("page_title", "Sottocategorie di "+categoria.getNome());
               
                request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
                res.activate("admin_sottocategorie.ftl.html", request, response);
            } else {
                request.setAttribute("error", "Pagina non trovata");
                request.setAttribute("err", "o");
                res.activate("error.ftl.html", request, response);
                return;
            }
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
            
            String cat = request.getParameter("cat");
            String sottoCategoriaToDelete = request.getParameter("dscat");
            
            if(sottoCategoriaToDelete != null){
                action_deleteSottocategoria(request, response, sottoCategoriaToDelete);
            } else { 
                action_default(request, response, cat);
            } 
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        } catch (DataException ex) {
             Logger.getLogger(AdminCaratteristiche.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    private void action_deleteSottocategoria(HttpServletRequest request, HttpServletResponse response, String sottoCategoriaToDelete) 
        throws IOException, ServletException, DataException, TemplateManagerException {
        try {
            Sottocategoria sottocategoria = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategoria(sottoCategoriaToDelete);
            String categoria = sottocategoria.getCategoria();
            
            if (sottocategoria != null) {
                ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().deleteSottoCategoria(sottocategoria);
                response.sendRedirect("admin_sottocategorie"+"?cat="+categoria);
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
        return "Sottocategorie servlet";
    }
}
