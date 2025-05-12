/*
 * FailureResult.java
 * 
 * Si tratta di una semplice classe che incapsula un TemplateResult per offrire
 * un comodo sistema di visualizzazione degli errori. Si basa su un template
 * il cui nome deve essere presente nella configurazione dell'applicazione 
 * (web.xml, parametro view.error_template). In mancanza di questo, degrada
 * a un errore http.
 * 
 * This simple class wraps TemplateResult to provide an easy error displaying
 * system. It uses a template whose name must be configured as a context
 * parameter (web.xml, view.error_template parameter). If no template is found,
 * the class uses simple http errors.
 * 
 */
package framework.view;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Giuseppe Della Penna
 */
public class FailureResult {

    protected ServletContext context;
    private final TemplateResult template;

    public FailureResult(ServletContext context) {
        this.context = context;
        template = new TemplateResult(context);
    }

    public void activate(Exception exception, HttpServletRequest request, HttpServletResponse response) {
        String message = "Unknown exception";
        if (exception != null && exception.getMessage() != null && !exception.getMessage().isEmpty()) {
            message = exception.getMessage();
        } else if (exception != null) {
            message = exception.getClass().getName();
        }
        activate(message, request, response);
    }

    public void activate(HttpServletRequest request, HttpServletResponse response) {
        //assumiamo che l'eccezione sia passata tramite gli attributi della request
        //ma per sicurezza controlliamo comunque il tipo effettivo dell'oggetto
        //we assume that the exception has been passed using the request attributes        
        //but we always check the real object type
        if (request.getAttribute("exception") instanceof Exception) {
            activate((Exception) request.getAttribute("exception"), request, response);
        } else {
            activate("Unknown error", request, response);
        }
    }

    public void activate(String message, HttpServletRequest request, HttpServletResponse response) {
        try {
            //Scriviamo il messaggio di errore nel log del server
            //Log the error message in the server log
            System.err.println(message);
            // ATTENZIONE: in un ambiente di produzione, i messaggi di errore DEVONO essere limitati a informazioni generiche, non a stringhe di complete di eccezione
            //e.g., potremmo mappare solo la classe dell'eccezione (IOException, SQLException, ecc.) in messaggi come "Errore IO", "Errore database", ecc.
            //WARNING: in a production environment, error messages MUST be limited to generic information, not full exception strings
            //e.g., we may map the exception class only (IOException, SQLException, etc.) to messages like "IO Error", "Database Error", etc.

            //se abbiamo registrato un template per i messaggi di errore, proviamo a usare quello
            //if an error template has been configured, try it
            if (context.getInitParameter("view.error_template") != null) {
                request.setAttribute("error", message);
                request.setAttribute("outline_tpl", "");
                template.activate(context.getInitParameter("view.error_template"), request, response);
            } else {
                //altrimenti, inviamo un errore HTTP
                //otherwise, use HTTP errors
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            }
        } catch (Exception ex) {
            //se qualcosa va male inviamo un errore HTTP
            //if anything goue wrong, sent an HTTP error
            message += ". In addition, the following exception was generated while trying to display the error page: " + ex.getMessage();
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            } catch (IOException ex1) {
                Logger.getLogger(FailureResult.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
}
