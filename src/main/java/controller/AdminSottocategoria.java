package controller;

import data.dao.impl.WMDataLayer;
import data.model.Categoria;
import data.model.Sottocategoria;
import data.model.impl.SottocategoriaImpl;
import framework.data.DataException;
import framework.result.SplitSlashesFmkExt;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class AdminSottocategoria extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException, DataException {
        try {
            request.setAttribute("page_title", "Aggiungi Sottocategoria");
            Categoria c = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getCategoria(request.getParameter("cat"));
            request.setAttribute("pcategoria", c);
            
            request.setAttribute("categorie", ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getCategorie());
                
            TemplateResult res = new TemplateResult(getServletContext());
              
            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            res.activate("admin_sottocategoria.ftl.html", request, response);
          
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        try {
            //se la sessione è attiva
            HttpSession session = SecurityHelpers.checkSession(request);
            if (session == null) {
                response.sendRedirect("login");
                return;
            }
            request.setAttribute("active", "ac");
            
            String action = request.getParameter("action");

            String psottocategoria = request.getParameter("scat");
            Sottocategoria sc;
           
            if(action != null && action.equals("aggiungiSottoCategoria"))
                action_creaSottoCategoria(request, response);
            else {
                if(psottocategoria!= null) {
                    sc = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategoria(psottocategoria);
                    if (sc == null) {
                        TemplateResult res = new TemplateResult(getServletContext());
                        request.setAttribute("error", "Pagina non trovata");
                        request.setAttribute("err", "o");
                        res.activate("error.ftl.html", request, response);
                        return;
                    }
                    action_defaultModificaSottoCategoria(request,response);
                }else{
                    if(action!= null && action.equals("modificaSottocategoria"))
                        action_modificaSottoCategoria(request,response);
                    else{
                        action_default(request, response);
                    }
                }
            }
        } catch (IOException | TemplateManagerException | DataException ex) {
        handleError(ex, request, response);
        }
    }
    
    private void action_creaSottoCategoria(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, DataException, TemplateManagerException {
        
        String nome = request.getParameter("nome");
        String padre = request.getParameter("categoria_padre");
        
        Sottocategoria scategoria = new SottocategoriaImpl();
        scategoria.setNome(nome);     
        scategoria.setCategoria(padre);

        ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().storeSottocategoria(scategoria, null);

        response.sendRedirect("admin_sottocategorie"+"?cat="+ padre);
    }
    
    private void action_defaultModificaSottoCategoria(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException, DataException, TemplateManagerException {
    request.setAttribute("page_title", "Modifica Sottocategoria");
    TemplateResult res = new TemplateResult(getServletContext());
    Sottocategoria s = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategoria(request.getParameter("scat"));
    Categoria c = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getCategoria(request.getParameter("cat"));
    //gestione errore 404
    if (c == null) {
        request.setAttribute("page_title", "Errore");
        request.setAttribute("error", "Pagina non trovata");
        request.setAttribute("err", "o");
        res.activate("error.ftl.html", request, response);
        return;
    }
    
    request.setAttribute("pcategoria", c);
    request.setAttribute("psottocategoria", s);
    
    request.setAttribute("categorie", ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getCategorie());

    
    res.activate("admin_sottocategoria.ftl.html", request, response);
}
    
    private void action_modificaSottoCategoria(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException, DataException, TemplateManagerException {       
        
    String oldID = request.getParameter("OLD");
    Sottocategoria s = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategoria(oldID);    
    
    String nome = request.getParameter("nome");
    String categoria_padre = request.getParameter("categoria_padre");
    s.setCategoria(categoria_padre);
    s.setNome(nome);

    ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().storeSottocategoria(s, oldID);
    //request.setAttribute("success", "Sottocategoria aggiornata con successo!");
    response.sendRedirect("admin_sottocategorie"+"?cat="+ categoria_padre);
}
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Admin Sottocategoria servlet";
    }

}