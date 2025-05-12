package controller;

import data.dao.impl.WMDataLayer;
import data.model.Prodotto;
import data.model.Proposta;
import data.model.Richiesta;
import data.model.Utente;
import data.model.impl.ProdottoImpl;
import data.model.impl.PropostaImpl;
import framework.data.DataException;
import framework.result.TemplateManagerException;
import framework.result.TemplateResult;
import framework.security.SecurityHelpers;
import framework.utils.EmailSender;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

public class TecnicoProposta extends BaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response, int r_key) throws IOException, ServletException, TemplateManagerException, DataException {
        TemplateResult res = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Invio Proposta");
      
        //Recupero la richiesta usando la key
        request.setAttribute("richiesta", r_key);

        res.activate("proposta.ftl.html", request, response);
    }

    private void action_creaProposta(HttpServletRequest request, HttpServletResponse response, int r_key) throws IOException, ServletException, TemplateManagerException, DataException {
        
        HttpSession session = SecurityHelpers.checkSession(request);
        
        String produttore = request.getParameter("produttore");
        String prodotto = request.getParameter("prodotto");
        String codiceProdotto = request.getParameter("codiceprod");

        double prezzo = Float.parseFloat(request.getParameter("prezzo"));
        String url;
        if (request.getParameter("url").isEmpty()) {
            url = "Nessun URL di riferimento";
        } else {
            url = request.getParameter("url");
        }
        String note;
        if (request.getParameter("note").isEmpty()) {
            note = "Nessuna nota aggiuntiva";
        } else {
            note = request.getParameter("note");
        }

        // Creo la proposta con il relativo prodotto
        Proposta prop = new PropostaImpl();
        Prodotto prod = new ProdottoImpl();
        
        prod.setNome_produttore(produttore);
        prod.setNome(prodotto);
        prod.setPrezzo(prezzo);
        prod.setUrl(url);
        prod.setKey(codiceProdotto);
        ((WMDataLayer) request.getAttribute("datalayer")).getProdottoDAO().storeProdotto(prod,null);
        
        prop.setIDric(r_key);
        prop.setIDprod(codiceProdotto);
        prop.setData(new Date());
        prop.setIDtec((String)session.getAttribute("username"));
        prop.setNote(note);
        prop.setRevisione("In attesa");
        
        //aggiorno lo stato della richiesta
        Richiesta ric = ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().getRichiesta(r_key);
        ric.setStato("In attesa");
        ((WMDataLayer) request.getAttribute("datalayer")).getRichiestaDAO().storeRichiesta(ric);
       

        // Salvo la proposta nel database
        ((WMDataLayer) request.getAttribute("datalayer")).getPropostaDAO().storeProposta(prop);
        
        //Se esiste una o più proposte rifiutata associata alla richiesta, si modifica lo stato
        List<Proposta> propAss = ((WMDataLayer) request.getAttribute("datalayer")).getPropostaDAO().getPropostebyRichiesta(r_key);
        if(propAss.size()>0){
            for(Proposta p: propAss){
                if(p.getRevisione().equals("Rifiutata"))
                {
                    //se esiste aggiorno lo stato
                    p.setRevisione("Conclusa: Rifiutata");
                    ((WMDataLayer) request.getAttribute("datalayer")).getPropostaDAO().storeProposta(p);
                }      
            }
        }
        
        //ordinante
        Utente u = ((WMDataLayer) request.getAttribute("datalayer")).getUtenteDAO().getUtente(ric.getIDord());
        
        //invio mail
        SimpleDateFormat europeanFormat = new SimpleDateFormat("dd/MM/yyyy");
        String toEmail = u.getMail(); 
        String subject = "Nuova proposta per la richiesta #" + ric.getKey();
        String body = "È stata inserita una nuova proposta per la tua richiesta:\n"
                + "\nCodice: " + prop.getKey()
                + "\nData: " + europeanFormat.format((Date)prop.getData())
                + "\nTecnico incaricato: " + prop.getIDtec()
                + "\n"
                + "\nGentilmente,"
                + "\nlo staff di WebMarket";

        try {
            EmailSender.sendEmail(toEmail, subject, body);
        } catch (Exception e) {
            request.setAttribute("page_title", "Errore");
            TemplateResult res = new TemplateResult(getServletContext());
            request.setAttribute("error", "Errore generale");
            request.setAttribute("err", "o");
            res.activate("error.ftl.html", request, response);
            return;
        }
      
        String propKey = String.valueOf(prop.getKey());
        response.sendRedirect("tecnico_detail_proposta?id=" + propKey);
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
            request.setAttribute("active", "tprop");
        
            int r_key = Integer.parseInt(request.getParameter("r"));
            String action = request.getParameter("action");
            if (action != null && action.equals("creaProposta")) {
                action_creaProposta(request, response, r_key);
            } else {
                action_default(request, response, r_key);
            }

        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        } catch (DataException ex) {
            Logger.getLogger(TecnicoProposta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet invio Proposta per Richiesta";
    }
    
   
}