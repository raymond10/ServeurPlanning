/*
 * Copyright COCKTAIL (www.cocktail.org), 1995, 2010 This software
 * is governed by the CeCILL license under French law and abiding by the
 * rules of distribution of free software. You can use, modify and/or
 * redistribute the software under the terms of the CeCILL license as
 * circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * As a counterpart to the access to the source code and rights to copy, modify
 * and redistribute granted by the license, users are provided only with a
 * limited warranty and the software's author, the holder of the economic
 * rights, and the successive licensors have only limited liability. In this
 * respect, the user's attention is drawn to the risks associated with loading,
 * using, modifying and/or developing or reproducing the software by the user
 * in light of its specific status of free software, that may mean that it
 * is complicated to manipulate, and that also therefore means that it is
 * reserved for developers and experienced professionals having in-depth
 * computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling
 * the security of their systems and/or data to be ensured and, more generally,
 * to use and operate it in the same conditions as regards security. The
 * fact that you are presently reading this means that you have had knowledge
 * of the CeCILL license and that you accept its terms.
 */
package fr.univlr.cri.planning.datacenter;

	import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.server.CktlWebApplication;
import org.cocktail.fwkcktlwebapp.server.CktlWebSession;
import org.cocktail.fwkcktlwebapp.server.database._CktlBasicDataBus;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSMutableDictionary;

public class SPDataCenter {

		private CktlWebSession session;

		// variables de cache
		private CktlWebApplication _sPApp;
		
	  /** Le message d'erreur (s'il existe) */
	  protected String errorMessage;
		
	  /** La cache local des gestionnaire d'acces a la base de donnees. */
	  private NSMutableDictionary dataBusCache = new NSMutableDictionary();

		public SPDataCenter(CktlWebSession aSession) {
			session = aSession;
		}
		
	  /**
	   * Cree une nouvelle instance de gestionnaire central sessionless
	   */
	  public SPDataCenter() {    
	  }
	  
		public CktlWebSession sPSession() {
			return session;
		}

	  /**
	   * Retourne la reference vers l'application en cours d'execution.
	   */
	  private CktlWebApplication sPApp() {
	  	if (_sPApp == null)
	  		_sPApp = (CktlWebApplication)CktlWebApplication.application();
	  	return _sPApp;
	  }
	  
	  /**
	   * Indique si une erreur est survenue pendant l'appel de la derniere
	   * operation. Retourne <em>null</em> si l'operation est execute sans erreurs.
	   */
	  public String errorMessage() {
	    return errorMessage;
	  }
	  
	  /**
	   * Indique si une erreur est survenue pendant l'appel de la derniere
	   * operation. Dans le cas d'une erreur, la methore <code>errorMessage</code>
	   * retourne son message.
	   * 
	   * @see #errorMessage
	   */
	  public boolean hasError() {
	    return (errorMessage != null);
	  }
		
//	 ============= Gestion des BUS ==================
	  
	  /**
	   * Cree et retourne une instance du gestionnaire avec le nom
	   * <code>busName</code> (le nom de la classe). Si une nouvelle instance est
	   * creee, elle est ajoutee dans le cache local des gestionnaires. Elle sera
	   * ensuite reutilisee prochaine fois que l'acces au bus <code>busName</code>
	   * sera demande.
	   */
	  private _CktlBasicDataBus getBusForName(String busName) {
	    errorMessage = null;
	    Object aBus = dataBusCache.objectForKey(busName);
	    // Si le bus n'est pas dans le cache, on le cree
	    if (aBus == null) {
	      try {
	        // On utilise la reflexion de Java
	        Object arguments[] = {sPApp().dataBus().editingContext()};
	        Class argumentTypes[] = {EOEditingContext.class};
	        Class busClass = Class.forName("fr.univlr.cri.planning.datacenter."+busName);
	        aBus = busClass.getConstructor(argumentTypes).newInstance(arguments);
	        // On memorise dans le cache
	        dataBusCache.setObjectForKey(aBus, busName);
	      } catch (Throwable e) {
	        e.printStackTrace();
	        errorMessage = CktlLog.getMessageForException(e);
	      }
	    }
	    // On definit les objets cles pour le nouveau bus
	    if (aBus instanceof A_SPDataBus) {
	      ((A_SPDataBus)aBus).setSPSession(sPSession());
	      ((A_SPDataBus)aBus).setSPDataCenter(this);
	    }
	    return (_CktlBasicDataBus)aBus;
	  }
	  
	  /**
	   * Retourne une instance du bus "generic" <code>PlngDataBus</code>.
	   */
	  public A_SPDataBus sPlanDataBus() {
	    return (A_SPDataBus)getBusForName("SPDataBus");
	  }

	  /**
	   * Retourne une instance du bus de la gestion des parametres
	   * <code>ParamBus</code>.
	   */
	  public ParamBus paramBus() {
	    return (ParamBus)getBusForName("ParamBus");
	  }
	  
	  /**
	   * Retourne une instance du bus de la gestion des plannings partages
	   * <code>SharedPlanningBus</code>.
	   */
	  public SharedPlanningBus sharedPlanningBus() {
	    return (SharedPlanningBus)getBusForName("SharedPlanningBus");
	  }
	  
	  /**
	   * Retourne une instance du bus de la gestion fichiers .ics
	   * <code>iCalendarBus</code>.
	   */
	  public ICalendarBus iCalendarBus() {
	    return (ICalendarBus)getBusForName("ICalendarBus");
	  }
	}