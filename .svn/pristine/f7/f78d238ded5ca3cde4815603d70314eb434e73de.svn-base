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

import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.foundation.NSArray;

import fr.univlr.cri.planning.Session;
import fr.univlr.cri.planning.constant.LocalSPConstantes;
import fr.univlr.cri.planning.datacenter.ParamBus;

public class PageMethCliente extends WOComponent {

  // les méthodes clientes
  public WODisplayGroup dgMethClient;
  public CktlRecord itemMethClient;
  public NSArray selectedsMethClient;
  public CktlRecord selectedMethClient;
  public boolean shouldRefreshDGMethClient = false;
  
  // les repartitions
  public WODisplayGroup dgRepartition;
  public CktlRecord itemRepartition;
  public boolean shouldRefreshDGRepartition = false;
  
  //
  private Session session = (Session) session();
  
  public PageMethCliente(WOContext context) {
    super(context);
  }

  public void appendToResponse(WOResponse arg0, WOContext arg1) {
    if (shouldRefreshDGRepartition) {
      refreshDGRepartition();
      shouldRefreshDGRepartition = false;
    }
    super.appendToResponse(arg0, arg1);
  }
  
  /**
   * forcer les donnees du DisplayGroup a etre rechargees.
   */
  private void refreshDGRepartition() {
    if (selectedMethClient != null) {
      dgRepartition.queryBindings().setObjectForKey(
          EOUtilities.primaryKeyForObject(session.defaultEditingContext(), selectedMethClient).valueForKey("key"), 
          "metKeyClient");
      dgRepartition.qualifyDataSource(); // fetch
      //    selection de la 1ere affAnn
      dgRepartition.selectsFirstObjectAfterFetch();     
    }
  }
  
  /**
   * Setter permettant d'intercepter la selection de la methode.
   * On effectue un rechargement du display group.
   */  
  public void setSelectedsMethClient(NSArray value) {
    selectedsMethClient = value;
    if (selectedsMethClient != null && selectedsMethClient.count() > 0) {
      selectedMethClient = (CktlRecord) selectedsMethClient.lastObject();
      dgRepartition.setMasterObject(selectedMethClient);      
      shouldRefreshDGRepartition = true;
    }
  }

  /**
   * Affichage du libellé clair du type d'identifiant passé pour
   * une association methode client <-> methode serveur
   * @return
   */
  public String getTypeParamLabel() {
  	return ParamBus.getTypeParamLabelForIdKeyClient(itemRepartition.intForKey(LocalSPConstantes.BD_REPAR_CLIENTID));
  }
}