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

import fr.univlr.cri.planning.constant.LocalSPConstantes;

/**
 * Page d'administration des methodes serveur : faire le lien entre
 * les methodes productrices et les applications installées dans le SI.
 *
 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
 */

public class PageAdminMethServeur extends SPComponent {

	/** le display group affichant la liste des methodes serveur connues */
	public WODisplayGroup dgMethServeur;
	
	/** item du dg <code>dgMethServeur</code>*/
	public CktlRecord methServeurItem;
	
	/** le display group affichant la liste des applications connues */
	public WODisplayGroup dgApplication;
	
	/** item du dg <code>dgApplication</code>*/
	public CktlRecord applicationItem;
	
	/** le display group affichant la liste des methodes serveur affectees a une application */
	public WODisplayGroup dgMethServeurAffectee;
	
	/** item du dg <code>dgMethServeurAffectee</code>*/
	public CktlRecord methServeurAffecteeItem;
	
	public PageAdminMethServeur(WOContext context) {
		super(context);
	}

	/**
	 * Effectuer l'association entre methode serveur selectionnee(s)
	 * et application selectionnee. Si des association existent deja
	 * alors elles sont ecrasees.
	 * @return
	 */
	public WOComponent associer() {
		if (dgApplication.selectedObjects().count() > 0) {
			CktlRecord recApplication = (CktlRecord) dgApplication.selectedObjects().lastObject();
			for (int i = 0; i < dgMethServeur.selectedObjects().count(); i++) {
				CktlRecord recMethServeur = (CktlRecord) dgMethServeur.selectedObjects().objectAtIndex(i);
				recMethServeur.takeStoredValueForKey(
						recApplication.valueForKey(LocalSPConstantes.BD_SERVEUR_APPLI), LocalSPConstantes.BD_SERVEUR_APPLI);
				recMethServeur.takeStoredValueForKey(recApplication,	"sp_Appli");
			}
			// sauver
			try {
				ec().lock();
				ec().saveChanges();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ec().unlock();
			}
			// rafraichir la liste des methodes serveur affectees
			dgMethServeurAffectee.fetch();
		}
		return null;
	}
	
}