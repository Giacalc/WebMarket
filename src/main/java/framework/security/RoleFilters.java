package framework.security;

import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoleFilters implements Filter {
    // Mappa dei ruoli richiesti per ogni endpoint
    private static final Map<String, List<String>> roleMapping = new HashMap<>();
    private String errorTemplate;
    private static TemplateResult result;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        roleMapping.put("Ordinante", Arrays.asList(
                "impostazioni",
                "ordinante_detail_ordine",
                "ordinante_detail_proposta",
                "ordinante_detail_richiesta",
                "ordinante_homepage",
                "ordinante_ordini",
                "ordinante_proposte",
                "ordinante_storico",
                "richiesta",
                "proposta_rifiuto",
                "ordine_rifiuto"));
        roleMapping.put("Tecnico",Arrays.asList(
                "impostazioni",
                "proposta",
                "tecnico_detail_ordine",
                "tecnico_detail_proposta",
                "tecnico_detail_richiesta",
                "tecnico_homepage",
                "tecnico_richieste",
                "tecnico_ordini",
                "tecnico_proposte"));
        roleMapping.put("Amministratore",Arrays.asList(
                "impostazioni",
                "admin_caratteristica",
                "admin_caratteristiche",
                "admin_categoria",
                "admin_categorie",
                "admin_detail_categoria",
                "admin_homepage",
                "admin_microcategorie",
                "admin_sottocategorie",
                "admin_utente",
                "admin_utenti",
                "admin_sottocategoria",
                "admin_microcategoria"));
        
        errorTemplate = filterConfig.getServletContext().getInitParameter("view.error_template");
        result = new TemplateResult(filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestedURI = httpRequest.getRequestURI();

        // Esclude la servlet di login
        if (requestedURI.endsWith("/login")) {
            chain.doFilter(request, response); // Salta il filtro
            return;
        }
        
        HttpSession session = SecurityHelpers.checkSession((HttpServletRequest) request);
        
        String ruolo = (session != null) ? (String) session.getAttribute("ruolo") : null;

        // Recupera il nome della pagina richiesta
        String page = getRequestedPage(httpRequest);

        // Recupera i ruoli consentiti per questa pagina
        List<String> pagine = roleMapping.get(ruolo);

        //non è stato effettuato l'accesso
        if (pagine == null) {
            httpRequest.setAttribute("page_title", "Errore");
            httpRequest.setAttribute("error", "Devi effettuare l'accesso per visualizzare la pagina.");
            try {
                result.activate(errorTemplate, httpRequest, httpResponse);
            } catch (TemplateManagerException ex) {
                Logger.getLogger(RoleFilters.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        // Se i ruoli non corrispondono, blocca l'accesso
        if (!pagine.contains(page)) {
            httpRequest.setAttribute("page_title", "Errore");
            httpRequest.setAttribute("ruolo_err", ruolo);
            httpRequest.setAttribute("error", "Accesso non autorizzato alla pagina richiesta.");
            try {
                result.activate(errorTemplate, httpRequest, httpResponse);
            } catch (TemplateManagerException ex) {
                Logger.getLogger(RoleFilters.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }

        // L'utente ha i permessi, continua la richiesta
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        
    }

    // Metodo per ottenere il nome della pagina richiesta
    private String getRequestedPage(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.substring(uri.lastIndexOf("/") + 1);
    }
}
