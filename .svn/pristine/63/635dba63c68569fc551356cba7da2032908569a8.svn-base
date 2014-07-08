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
package fr.univlr.cri.planning.components;
/*
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software. You can use, 
 * modify and/or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and, more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.server.components.CktlAdminLoginResponder;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

/**
 * Cette page correspond a la page d'acceuil a l'application. L'acces a
 * l'application est protégée par un mot de passe.
 * 
 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
 */
public class PageLogin extends SPComponent {
  private LoginResponder responder;
  private WOContext currentContext;
  
  public PageLogin(WOContext context) {
    super(context);
    currentContext = context;
  }

  /* (non-Javadoc)
   * @see com.webobjects.appserver.WOComponent#appendToResponse(com.webobjects.appserver.WOResponse, com.webobjects.appserver.WOContext)
   */
  public void appendToResponse(WOResponse response, WOContext context) {
  	// TODO Auto-generated method stub
  	super.appendToResponse(response, context);
  	addLocalJScript(response, "jscript/CRIDecrypter.js", "FwkCktlWebApp");
  }
  
  public LoginResponder getLoginResponder() {
    if (responder == null) responder = new LoginResponder();
    return responder;
  }
  
  private class LoginResponder implements CktlAdminLoginResponder {
    public WOComponent connectAccepted() {
      CktlLog.log("Admin: login, IP: "+ cktlApp.getRequestIPAddress(currentContext.request())+" - OK");
      return cktlSession().getSavedPageWithName(ServeurPlanningDefaultPage.class.getName());
    }
  }
  
  public String imageLigneSrc() {
    return spApp().imageLigneSrc();
  }
  
  public String imageClefsSrc() {
    return spApp().imageClefsSrc();
  }
}
