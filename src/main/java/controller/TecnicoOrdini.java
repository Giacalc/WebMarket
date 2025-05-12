package controller;

import data.dao.impl.WMDataLayer;
import framework.data.DataException;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TecnicoOrdini extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response, String user) throws IOException, ServletException, TemplateManagerException, DataException {
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Ordini");

        request.setAttribute("ordini", ((WMDataLayer) request.getAttribute("datalayer")).getOrdineDAO().getOrdinibyTecnico(user));

        res.activate("tecnico_ordini.ftl.html", request, response);
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
        request.setAttribute("active", "tord");
        
        String user = (String)session.getAttribute("username");
        action_default(request, response, user);
            
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        } catch (DataException ex) {
                Logger.getLogger(TecnicoOrdini.class.getName()).log(Level.SEVERE, null, ex);
        }
}
    @Override
    public String getServletInfo() {
        return "Servlet ordini tecnico";
    }
}