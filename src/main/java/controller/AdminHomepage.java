package controller;

import framework.data.DataException;
import data.dao.impl.WMDataLayer;
import data.model.Caratteristica;
import data.model.Categoria;
import data.model.Microcategoria;
import data.model.Ordine;
import data.model.Proposta;
import data.model.Richiesta;
import data.model.Sottocategoria;
import data.model.Utente;
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


public class AdminHomepage extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response, String user) throws IOException, ServletException, TemplateManagerException {
        try {
            request.setAttribute("page_title", "Homepage");
            request.setAttribute("utente", user);
            
            List<Utente> utenti = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtenti() ;
            int nord=0;
            int ntec=0;
            if(utenti.size()>0){
                for(Utente u: utenti){
                    if(u.getRuolo().equals("Ordinante"))
                        nord++;
                    if(u.getRuolo().equals("Tecnico"))
                        ntec++;
                }
            }
            request.setAttribute("nutenti", utenti.size());
            request.setAttribute("nord", nord); 
            request.setAttribute("ntec", ntec);
            
            List<Richiesta> richieste = ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().getRichieste();
            int caperte = 0;
            int cchiuse = 0;
            if(richieste.size() > 0) {
                for(Richiesta r : richieste) {
                    if(r.getStato().equals("Completata"))
                        cchiuse++;
                    else
                        caperte++;
                }
            }
            request.setAttribute("nrichieste", richieste.size());
            request.setAttribute("nrichiesteap", caperte);
            request.setAttribute("nrichiestechiuse", cchiuse);
            
            List<Proposta> proposte = ((WMDataLayer) request.getAttribute("datalayer")).getPropostaDAO().getProposte();
            int papete  = 0;
            int pchiuse = 0;
            if(proposte.size() > 0) {
                for(Proposta p : proposte) {
                    if(p.getRevisione().equals("Conclusa"))
                        pchiuse++;
                    else
                        papete++;
                }
            }
            request.setAttribute("nproposte", proposte.size());
            request.setAttribute("nproposteap", papete);
            request.setAttribute("npropostechiuse", pchiuse);
            
            List<Ordine> ordini = ((WMDataLayer) request.getAttribute("datalayer")).getOrdineDAO().getOrdini();
            int oac = 0;
            int orif = 0;
            if(ordini.size() > 0) {
                for(Ordine o : ordini) {
                    if(o.getStato().equals("Accettato"))
                        oac++;
                    if(o.getStato().equals("Rifiutato: non conforme") || o.getStato().equals("Rifiutato: non funzionante"))
                        orif++; 
                }
            }
            request.setAttribute("nordini", ordini.size());
            request.setAttribute("nordiniac", oac);
            request.setAttribute("nordinirif", orif);
            
            List<Categoria> categorie = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getCategorie();
            request.setAttribute("ncategorie", categorie.size());
            List<Sottocategoria> scategorie = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getSottocategorie();
            request.setAttribute("nscategorie", scategorie.size());
            List<Microcategoria> mcategorie = ((WMDataLayer) request.getAttribute("datalayer")).getCategoriaDAO().getMicrocategorie();
            request.setAttribute("nmcategorie", mcategorie.size());

            List<Caratteristica> caratteristiche = ((WMDataLayer) request.getAttribute("datalayer")).getCaratteristicaDAO().getCaratteristiche();
            request.setAttribute("ncaratteristiche", caratteristiche.size());
            
            //mapping pagina HTML
            TemplateResult res = new TemplateResult(getServletContext());
            request.setAttribute("strip_slashes", new SplitSlashesFmkExt());
            res.activate("admin_homepage.ftl.html", request, response);
        }
        catch (DataException ex) {
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
        
        // Recupero l'ID dell'utente dalla sessione
        String userId = (String) session.getAttribute("userid");
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(userId);
        
        if (u != null) {
            request.setAttribute("user", u);
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
        return "Admin Homepage servlet";
    }

}