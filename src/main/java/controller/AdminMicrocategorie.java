package controller;

import data.dao.impl.WMDataLayer;
import data.model.Microcategoria;
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

public class AdminMicrocategorie extends BaseController{

    private void action_default(HttpServletRequest request, HttpServletResponse response, String scat) throws IOException, ServletException, TemplateManagerException, DataException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            Sottocategoria sottocategoria = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategoria(scat) ;
            if (sottocategoria != null) {
                //riempio i placeholder del template
                request.setAttribute("sottocategoria", sottocategoria);
                
                List<Microcategoria> microcategorie = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getMCBySC(sottocategoria);
                
                request.setAttribute("microcategorie", microcategorie);
                
                request.setAttribute("page_title", "Microcategorie di "+sottocategoria.getNome());
                
                //mapping pagina HTML
                request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
                res.activate("admin_microcategorie.ftl.html", request, response);
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
            
            String scat = request.getParameter("scat");
            String microCategoriaToDelete = request.getParameter("dmcat");
            
            if(microCategoriaToDelete != null)
                action_deleteMicrocategoria(request, response, microCategoriaToDelete);
            else
                action_default(request, response, scat);
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        } catch (DataException ex) {
             Logger.getLogger(AdminCaratteristiche.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    
    private void action_deleteMicrocategoria(HttpServletRequest request, HttpServletResponse response, String microCategoriaToDelete) 
        throws IOException, ServletException, DataException, TemplateManagerException {
        try {
            Microcategoria microcategoria = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getMicrocategoria(microCategoriaToDelete);
            String sottocategoria = microcategoria.getSottocategoria();
            
            if (microcategoria != null) {
                ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().deleteMicroCategoria(microcategoria);
                response.sendRedirect("admin_microcategorie"+"?scat="+sottocategoria);
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
        return "Microcategorie servlet";
    }
}
