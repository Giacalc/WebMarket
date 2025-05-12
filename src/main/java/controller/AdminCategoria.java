package controller;

import data.dao.impl.WMDataLayer;
import data.model.Categoria;
import data.model.impl.CategoriaImpl;
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


public class AdminCategoria extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException, DataException {
        request.setAttribute("page_title", "Aggiungi Categoria");
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
        res.activate("admin_categoria.ftl.html", request, response);
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
            String pcategoria = request.getParameter("cat");
            Categoria c;
            
            if (action != null && action.equals("aggiungiCategoria")) {
                //form di aggiunta
                action_creaCategoria(request, response);
            } else {
                if(pcategoria != null) {
                    //gestione errore 404
                    c = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getCategoria(pcategoria);
                    if (c == null) {
                        TemplateResult res = new TemplateResult(getServletContext());
                        request.setAttribute("page_title", "Errore");
                        request.setAttribute("error", "Pagina non trovata");
                        request.setAttribute("err", "o");
                        res.activate("error.ftl.html", request, response);
                        return;
                    }
                    
                    //se abbiamo una categoria in get visualizza pag modifica
                    action_defaultModifica(request,response);
                } else {
                    if(action!= null && action.equals("modificaCategoria"))
                        //form di modifica
                        action_modificaCategoria(request,response);
                    else{
                        //aggiungi categoria
                        action_default(request, response);
                    }
                }
            }
        } catch (IOException | TemplateManagerException | DataException ex) {
        handleError(ex, request, response);
        }
    }
    
    private void action_creaCategoria(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, DataException, TemplateManagerException {
        String nome = request.getParameter("nome");
        
        Categoria categoria = new CategoriaImpl();
        categoria.setNome(nome);
        
        ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().storeCategoria(categoria, null);
        
        response.sendRedirect("admin_categorie");
    }
    
    private void action_defaultModifica(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException, DataException, TemplateManagerException {
    request.setAttribute("page_title", "Modifica Categoria");
    
    //categoria da modificare
    Categoria c = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getCategoria(request.getParameter("cat"));
    request.setAttribute("pcategoria", c);

    TemplateResult res = new TemplateResult(getServletContext());
    res.activate("admin_categoria.ftl.html", request, response);
}
    
    private void action_modificaCategoria(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException, DataException, TemplateManagerException {  
        
    String oldID = request.getParameter("OLD");
    Categoria c = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getCategoria(oldID);

    String nome = request.getParameter("nome");
    
    c.setNome(nome);

    ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().storeCategoria(c, oldID);
    response.sendRedirect("admin_categorie");
}


    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Admin Categoria servlet";
    }

}