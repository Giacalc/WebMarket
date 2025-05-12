/*
 * DataModelFiller.java
 * 
 * Tramite questa interfaccia è possibile fornire al TemplateManager degli oggetti
 * che vengono utilizzati per inizializzare automaticamente tutti i data model.
 * Il metodo fillDataModel viene richiamato su tutti i data model usati da
 * TemplateManager, e può inserirvi dati generati automaticamente e validi per 
 * tutti i template (ad esempio la struttura di un menu principale, ecc.).
 * Per funzionare, il nome completo di una classe che implementa questa interfaccia deve essere
 * registrato nel parametro del contesto view.model_filler.
 * 
 * This interface allows to create objects that the TemplateManager class will use
 * to automatically initialize all the data models.
 * The fillDataModel method is called on all the data models used by TemplateManager,
 * and it can add new, dynamically generated data (e.g., the main menu structure, etc.)
 * To get it work, the complete name of a class implementing this interface must be registered in the
 * context parameter view.model_filler.
 * 
 */
package framework.result;

import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Giuseppe Della Penna
 */
public interface DataModelFiller {

    //request e context POSSONO ESSERE NULL
    //request and context MAY BE NULL
    public void fillDataModel(Map datamodel, HttpServletRequest request, ServletContext context);
}
