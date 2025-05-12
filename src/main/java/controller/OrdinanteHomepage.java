package controller;

import com.google.gson.Gson;
import framework.data.DataException;
import data.dao.impl.WMDataLayer;
import data.model.Categoria;
import data.model.Richiesta;
import data.model.Sottocategoria;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class OrdinanteHomepage extends BaseController {
    
    private void action_default(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException, ServletException, TemplateManagerException, DataException {
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Homepage");
        
        // form richiesta
        request.setAttribute("categorie", ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getCategorie());

        //richieste in corso
        List<Richiesta> richiesteInCorso = ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().getRichiesteInCorso(userId);
        request.setAttribute("richiesteInCorso", richiesteInCorso);
        
        res.activate("ordinante_homepage.ftl.html", request, response);
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

         // Recupero l'ID dell'utente dalla sessione
        String userId = (String) session.getAttribute("userid");
        String u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(userId).getUsername();

        //placeholder nome utente con username
        if (u != null) {
            request.setAttribute("utente", u);
        }

        //gestione sottocategorie dinamiche con JavaScript
        String cat = request.getParameter("categoria");
        if (cat != null) {
            Categoria c = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getCategoria(cat);
            if (c != null) {
                List<String> scat_options = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSCNamesByC(c);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(new Gson().toJson(scat_options));
                return;
            }
            else {
                TemplateResult res = new TemplateResult(getServletContext());
                request.setAttribute("error", "Pagina non trovata");
                request.setAttribute("err", "o");
                res.activate("error.ftl.html", request, response);
                return;
            }
        }
        //gestione microcategorie dinamiche con JavaScript
        String scat = request.getParameter("sottocategoria");
        if (scat != null) {
            Sottocategoria sc = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategoria(scat);
            if (sc != null) {
                List<String> mcat_options = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getMCNamesBySC(sc);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(new Gson().toJson(mcat_options));
                return;
            }
            else {
                TemplateResult res = new TemplateResult(getServletContext());
                request.setAttribute("error", "Pagina non trovata");
                request.setAttribute("err", "o");
                res.activate("error.ftl.html", request, response);
                return;
            }
        }

        action_default(request, response, userId);

    } catch (IOException | TemplateManagerException | DataException ex) {
        handleError(ex, request, response);
    }
}

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Ordinante Homepage servlet";
    }
    
}