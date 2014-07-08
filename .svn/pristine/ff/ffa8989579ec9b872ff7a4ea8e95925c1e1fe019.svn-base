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
package fr.univlr.cri.planning;

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.CktlUserInfo;
import org.cocktail.fwkcktlwebapp.server.CktlWebSession;
import org.cocktail.fwkcktlwebapp.server.components.CktlMenuItemSet;
import org.cocktail.fwkcktlwebapp.server.components.CktlMenuListener;

import com.webobjects.foundation.NSDictionary;

import fr.univlr.cri.planning.datacenter.SPDataCenter;

public class Session extends CktlWebSession {

  private SPDataCenter _dataCenter;

	public void setConnectedUserInfo(CktlUserInfo arg0) {
		super.setConnectedUserInfo(arg0);
	}

	public void terminate() {
		// Recupere le log de la fermeture de session
		String userLogin = null;
		if (connectedUserInfo() != null)
			userLogin = connectedUserInfo().login();
		StringBuffer log = new StringBuffer();
		log.append("<close session : ").append(sessionID());
		if (userLogin != null)
			log.append(", user : ").append(userLogin);
		log.append(">");
		CktlLog.log(log.toString());
		//
		super.terminate();
	}
  
  //

  /**
   * Pointeur vers le gestionnaires de donnees
   */
  public SPDataCenter dataCenter() {
    if (_dataCenter == null)
      _dataCenter = new SPDataCenter(this);
    return _dataCenter;
  }
  
  // ** GESTION DU MENU **

  private CktlMenuListener _listener;
  private CktlMenuItemSet _menuItemSet;
  
  public CktlMenuListener getMenuLister() {
    return _listener;
  }

  public void setMenuLister(CktlMenuListener value) {
    _listener = value;
  }

  public CktlMenuItemSet getDtMenuItemSet() {
    return _menuItemSet;
  }

  public void setDtMenuItemSet(CktlMenuItemSet value) {
    _menuItemSet = value;
  }

  // ** REPARTITION DU TEMPS **
  
  private NSDictionary dicoParam;
  
  public NSDictionary getDicoParam() {
  	return dicoParam;
  }
  
  public void setDicoParam(NSDictionary value) {
  	dicoParam = value;
  }
  
}