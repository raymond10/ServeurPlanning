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

public class PageMethServeur extends WOComponent {

  // dg sur les applications de SAUT
  public WODisplayGroup dgApplication;
  public CktlRecord itemApplication;
  public NSArray selectedsApplication;
  public CktlRecord selectedApplication;

  // les methodes serveur
  public WODisplayGroup dgMethServeur;
  public CktlRecord itemMethServeur;
  public NSArray selectedsMethServeur;
  public CktlRecord selectedMethServeur;
  public boolean shouldRefreshDGMethServeur = false;
  
  // les repartitions
  public WODisplayGroup dgRepartition;
  public CktlRecord itemRepartition;
  public boolean shouldRefreshDGRepartition = false;
  
  //
  private Session session = (Session) session();
  
  public PageMethServeur(WOContext context) {
    super(context);
  }

  public void appendToResponse(WOResponse arg0, WOContext arg1) {
    if (shouldRefreshDGMethServeur) {
      refreshDGMethServeur();
      shouldRefreshDGMethServeur = false;
    }
    if (shouldRefreshDGRepartition) {
      refreshDGRepartition();
      shouldRefreshDGRepartition = false;
    }
    super.appendToResponse(arg0, arg1);
  }
  
  /**
   * forcer les donnees du DisplayGroup a etre rechargees.
   */
  private void refreshDGMethServeur() {
    if (selectedApplication != null) {
      dgMethServeur.queryBindings().setObjectForKey(selectedApplication, "sp_Appli");
      dgMethServeur.qualifyDataSource(); // fetch
      //    selection de la 1ere affAnn
      dgMethServeur.selectsFirstObjectAfterFetch();     
    }
  }
  
  /**
   * forcer les donnees du DisplayGroup a etre rechargees.
   */
  private void refreshDGRepartition() {
    if (selectedMethServeur != null) {
      dgRepartition.queryBindings().setObjectForKey(
          EOUtilities.primaryKeyForObject(session.defaultEditingContext(), selectedMethServeur).valueForKey("key"), 
          "metKeyServ");
      dgRepartition.qualifyDataSource(); // fetch
      //    selection de la 1ere affAnn
      dgRepartition.selectsFirstObjectAfterFetch();     
    }
  }
  
  /**
   * Setter permettant d'intercepter la selection de l'application.
   * On effectue un rechargement du display group.
   */  
  public void setSelectedsApplication(NSArray value) {
    selectedsApplication = value;
    if (selectedsApplication != null && selectedsApplication.count() > 0) {
      selectedApplication = (CktlRecord) selectedsApplication.lastObject();
      dgMethServeur.setMasterObject(selectedApplication);      
      shouldRefreshDGMethServeur = true;
    }
   }

  
  /**
   * Setter permettant d'intercepter la selection de la methode.
   * On effectue un rechargement du display group.
   */  
  public void setSelectedsMethServeur(NSArray value) {
    selectedsMethServeur = value;
    if (selectedsMethServeur != null && selectedsMethServeur.count() > 0) {
      selectedMethServeur = (CktlRecord) selectedsMethServeur.lastObject();
      dgRepartition.setMasterObject(selectedMethServeur);      
      shouldRefreshDGRepartition = true;
    }
  }

}