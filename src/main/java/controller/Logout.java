package controller;

import framework.security.SecurityHelpers;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Logout extends BaseController {

    private void action_logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SecurityHelpers.disposeSession(request);
        response.sendRedirect("login");
 
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        try {
            action_logout(request, response);
        } catch (IOException ex) {
            handleError(ex, request, response);
        }
    }    
}
