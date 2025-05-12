package controller;

import data.dao.impl.WMDataLayer;
import data.model.Caratteristica;
import data.model.Microcategoria;
import data.model.Sottocategoria;
import data.model.impl.CaratteristicaImpl;
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

public class AdminCaratteristica extends BaseController{
    
    //inizio: process_request
    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException, DataException {
        try {
            request.setAttribute("page_title", "Aggiungi Caratteristica");
            
            String mic = request.getParameter("mcat");
            Microcategoria pMic = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getMicrocategoria(mic);
            request.setAttribute("pmicrocategoria", pMic);
            
            Sottocategoria sc = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategoria(pMic.getSottocategoria());
            request.setAttribute("socat", sc.getCategoria());
            
            request.setAttribute("microcategorie", ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getMicrocategorie());
                
            TemplateResult res = new TemplateResult(getServletContext());
              
            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            res.activate("admin_caratteristica.ftl.html", request, response);
          
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
            String c = request.getParameter("c");
            Caratteristica cm;
            
            if (action != null && action.equals("aggiungiCaratteristica")) {
                action_creaCaratteristica(request, response);
            } else {
                if(c != null) {
                    cm = ((WMDataLayer) request.getAttribute("datalayer")).getCaratteristicaDAO().getCaratteristica(c);
                    if (cm == null) {
                        TemplateResult res = new TemplateResult(getServletContext());
                        request.setAttribute("error", "Pagina non trovata");
                        request.setAttribute("err", "o");
                        res.activate("error.ftl.html", request, response);
                        return;
                    }
                    action_defaultEdit(request, response);
                } else {
                    if(action != null && action.equals("modificaCaratteristica"))
                        action_editCaratteristica(request,response);
                    else{
                        action_default(request, response);
                    }
                }
            }
        } catch (IOException | TemplateManagerException | DataException ex) {
            handleError(ex, request, response);
        }
    }
    
    private void action_creaCaratteristica(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, DataException, TemplateManagerException {
        String nome = request.getParameter("nome");
        String mcategoria = request.getParameter("mcategoria");

        Caratteristica caratteristica = new CaratteristicaImpl();
        caratteristica.setNome(nome);     
        caratteristica.setMicrocategoria(mcategoria);

        ((WMDataLayer) request.getAttribute("datalayer")).getCaratteristicaDAO().storeCaratteristica(caratteristica, null, null);

        response.sendRedirect("admin_caratteristiche"+"?mcat="+ mcategoria);
    }
    
    private void action_defaultEdit(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException, DataException, TemplateManagerException {
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Modifica Caratteristica");

        String mic = request.getParameter("mcat");
        Microcategoria pMic = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getMicrocategoria(mic);
        //gestione errore 404
        if (pMic == null) {
            request.setAttribute("page_title", "Errore");
            request.setAttribute("error", "Pagina non trovata");
            request.setAttribute("err", "o");
            res.activate("error.ftl.html", request, response);
            return;
        }
        
        String car = request.getParameter("c");
        Caratteristica pCar = ((WMDataLayer) request.getAttribute("datalayer")).getCaratteristicaDAO().getCaratteristica(car);

        Sottocategoria sc = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategoria(pMic.getSottocategoria());
        List<Microcategoria> pMicList = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getMicrocategorie();

        request.setAttribute("microcategorie", pMicList);
        request.setAttribute("pmicrocategoria", pMic);
        request.setAttribute("caratteristica", pCar);
        request.setAttribute("socat", sc.getCategoria());     
        
        res.activate("admin_caratteristica.ftl.html", request, response);
    }
        
    private void action_editCaratteristica(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, DataException, TemplateManagerException { 
            
        String oldID = request.getParameter("OLD");
        String oldM = request.getParameter("OLDm");
        Caratteristica c = ((WMDataLayer) request.getAttribute("datalayer")).getCaratteristicaDAO().getCaratteristica(oldID);    
        
        String nome = request.getParameter("nome");
        String microcategoria_padre = request.getParameter("mcategoria");
        
        c.setNome(nome);
        c.setMicrocategoria(microcategoria_padre);

        ((WMDataLayer) request.getAttribute("datalayer")).getCaratteristicaDAO().storeCaratteristica(c, oldID, oldM); ;
        request.setAttribute("success", "Sottocategoria aggiornata con successo!");
        response.sendRedirect("admin_caratteristiche"+"?mcat="+ microcategoria_padre);
    }
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Admin caratteristiche servlet";
    }

    
}
