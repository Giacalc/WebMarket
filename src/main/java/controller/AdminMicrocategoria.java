package controller;

import data.dao.impl.WMDataLayer;
import data.model.Microcategoria;
import data.model.Sottocategoria;
import data.model.impl.MicrocategoriaImpl;
import framework.data.DataException;
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


public class AdminMicrocategoria extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException, DataException {
        try {
            request.setAttribute("page_title", "Aggiungi Microcategoria");
            
            Sottocategoria s = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategoria(request.getParameter("scat"));
            request.setAttribute("psottocategoria", s);

            List<Sottocategoria> sottocategorie = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategorie();
            request.setAttribute("sottocategorie", sottocategorie);
  
            TemplateResult res = new TemplateResult(getServletContext());
              
            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            res.activate("admin_microcategoria.ftl.html", request, response);
          
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
            String pmicrocategoria = request.getParameter("mcat");
            Microcategoria mc;
            
            if(action!= null && action.equals("aggiungiMicroCategoria"))
                action_creaMicroCategoria(request,response);
            else {
                if(pmicrocategoria!= null) {
                    mc = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getMicrocategoria(pmicrocategoria);
                    //gestione errore 404
                    if (mc == null) {
                        TemplateResult res = new TemplateResult(getServletContext());
                        request.setAttribute("page_title", "Errore");
                        request.setAttribute("error", "Pagina non trovata");
                        request.setAttribute("err", "o");
                        res.activate("error.ftl.html", request, response);
                        return;
                    }
                    action_defaultModificaMicroCategoria(request,response);
                } else {
                    if(action!= null && action.equals("modificaMicrocategoria"))
                        action_modificaMicroCategoria(request,response);
                    else {
                        action_default(request, response);
                    }
                }
            }
        } catch (IOException | TemplateManagerException | DataException ex) {
        handleError(ex, request, response);
        }
    }
    
    private void action_creaMicroCategoria(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, DataException, TemplateManagerException {
        String nome = request.getParameter("nome");
        String sottocategoria_padre = request.getParameter("sottocategoria_padre");
        
        Microcategoria mcategoria = new MicrocategoriaImpl();
        mcategoria.setNome(nome);     
        mcategoria.setSottocategoria(sottocategoria_padre);

        ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().storeMicrocategoria(mcategoria,null);
        response.sendRedirect("admin_microcategorie"+"?scat="+ sottocategoria_padre);
    }
    
    private void action_defaultModificaMicroCategoria(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException, DataException, TemplateManagerException {
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Modifica Microcategoria");
        
        Sottocategoria s = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategoria(request.getParameter("scat"));
        //gestione errore 404
        if (s == null) {
            request.setAttribute("page_title", "Errore");
            request.setAttribute("error", "Pagina non trovata");
            request.setAttribute("err", "o");
            res.activate("error.ftl.html", request, response);
            return;
        }
        Microcategoria m = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getMicrocategoria(request.getParameter("mcat"));

        request.setAttribute("psottocategoria", s);
        request.setAttribute("pmicrocategoria", m);

        List<Sottocategoria> sottocategorie = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategorie();
        request.setAttribute("sottocategorie", sottocategorie);

        
        res.activate("admin_microcategoria.ftl.html", request, response);
    }
    
    private void action_modificaMicroCategoria(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException, DataException, TemplateManagerException {      
        
    String oldID = request.getParameter("OLD");
    Microcategoria m = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getMicrocategoria(oldID);    
    
    String nome = request.getParameter("nome");
    String sottocategoria_padre = request.getParameter("sottocategoria_padre");
    m.setSottocategoria(sottocategoria_padre);
    m.setNome(nome);

    ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().storeMicrocategoria(m, oldID);
    response.sendRedirect("admin_microcategorie"+"?scat="+ sottocategoria_padre);
}

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Admin Microcategoria servlet";
    }

}
