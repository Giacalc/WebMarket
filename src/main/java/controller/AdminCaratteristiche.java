package controller;

import data.dao.impl.WMDataLayer;
import data.model.Caratteristica;
import data.model.Microcategoria;
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

public class AdminCaratteristiche extends BaseController{
    
    private void action_default(HttpServletRequest request, HttpServletResponse response, String mcat) throws IOException, ServletException, TemplateManagerException, DataException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            Microcategoria microcategoria = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getMicrocategoria(mcat);
            if (microcategoria != null) {
                //riempio i placeholder del template
                request.setAttribute("microcategoria", microcategoria);
                
                String sottocategoria = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategoriaByMc(mcat);
                
                String categoria = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategoria(sottocategoria).getCategoria();
                
                request.setAttribute("categoria", categoria);
                                             
                List<Caratteristica> caratteristiche = ((WMDataLayer) request.getAttribute("datalayer")).getCaratteristicaDAO().getCaratteristicheByCategoria(mcat);
                
                request.setAttribute("caratteristiche", caratteristiche);
                
                request.setAttribute("page_title", "Caratteristiche di "+microcategoria.getNome());

                request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
                res.activate("admin_caratteristiche.ftl.html", request, response);
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
            
            String mcat = request.getParameter("mcat");
            String caratteristicaToDelete = request.getParameter("dcar");
            
            if(caratteristicaToDelete != null)
                action_deleteCaratteristica(request, response, caratteristicaToDelete);
            else 
                action_default(request, response, mcat);
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        } catch (DataException ex) {
             Logger.getLogger(AdminCaratteristiche.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    
    private void action_deleteCaratteristica(HttpServletRequest request, HttpServletResponse response, String caratteristicaToDelete) 
        throws IOException, ServletException, DataException, TemplateManagerException {
        try {
            Caratteristica caratteristica = ((WMDataLayer) request.getAttribute("datalayer")).getCaratteristicaDAO().getCaratteristica(caratteristicaToDelete);
            String microcategoria = caratteristica.getMicrocategoria();
            
            if (caratteristica != null) {
                ((WMDataLayer) request.getAttribute("datalayer")).getCaratteristicaDAO().deleteCaratteristica(caratteristica);
                response.sendRedirect("admin_caratteristiche"+"?mcat="+microcategoria);
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
        return "Caratteristiche servlet";
    }
}
