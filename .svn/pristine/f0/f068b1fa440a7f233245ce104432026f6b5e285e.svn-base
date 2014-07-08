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

import java.io.UnsupportedEncodingException;
import java.util.GregorianCalendar;

import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.JavaDateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.common.util.URLEncoder;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSComparator.ComparisonException;

import fr.univlr.cri.conges.utils.TimeCtrl;
import fr.univlr.cri.planning.SPConstantes;
import fr.univlr.cri.planning.constant.LocalSPConstantes;
import fr.univlr.cri.planning.datacenter.SharedPlanningBus;

/**
 * Page de gestion des informations relatives au planning
 * personnel d'un individu
 * 
 * @author ctarade
 */
public class PagePlanningPersonnel extends SPComponent {
	
	/** 
	 * Le dictionnaire parametre : 
	 * - cle = nom de la categorie
	 * - value = NSArray de mots clef
	 */
	public NSMutableDictionary dicoParam;
	// la liste des categories s'obtient via dicoParam.allKeys();
	
	// une categorie
	public String catItem;
	// un mot dans la liste des mots clef
	public String motItem;
	// l'index du mot dans la liste (utilise par un setter)
	public int motIndex;
	
	// le dictionnaire "resultat"
	public NSMutableDictionary dicoResult;
	
	// periode d'interrogation
	public NSTimestamp dateDebut, dateFin;
	
	// affichage du result
	public String keyItem;
	
	// constantes pour initialisation
	private final static String DEFAULT_CAT_1 				= "ULR";
	private final static String DEFAULT_CAT_1_MOT_1 	= "DT/CRI";
	private final static String DEFAULT_CAT_2 				= "Cocktail";
	private final static String DEFAULT_CAT_2_MOT_1 	= "DT/DT-COCKTAIL";

	public PagePlanningPersonnel(WOContext context) {
		super(context);
		initComponent();
	}
	
	/**
	 * Initialiser le composant
	 */
	private void initComponent() {
		// prendre le dico de la session s'il existe 
		NSDictionary dicoSession = spSession().getDicoParam();
		if (dicoSession == null) {
			dicoParam = new NSMutableDictionary();
			// 2 categories par defaut
			dicoParam.setObjectForKey(new NSArray(new String[]{ DEFAULT_CAT_1_MOT_1}), DEFAULT_CAT_1);
			dicoParam.setObjectForKey(new NSArray(new String[]{ DEFAULT_CAT_2_MOT_1}), DEFAULT_CAT_2);
			// classement des categories
			dicoParam.setObjectForKey(new NSArray(new String[]{ DEFAULT_CAT_1, DEFAULT_CAT_2}), LocalSPConstantes.KEY_DICO_REPART_CAT_SORT_KEY);
		} else {
			dicoParam = new NSMutableDictionary(dicoSession);
		}
		// periode d'interrogation (debut annee civile / date jour +1)
		dateDebut = DateCtrl.stringToDate("01/01/" + JavaDateCtrl.nowDay().get(GregorianCalendar.YEAR));
		dateFin = DateCtrl.now().timestampByAddingGregorianUnits(0,0,1,0,0,0);
	}
	
	// getter 
	
	/**
	 * 
	 */
	public String utilisateurConnecte() {
		return spSession().connectedUserInfo().nomEtPrenom();
	}
	
	/**
	 * 
	 */
	public NSArray catList() {
		//return dicoParam.allKeys();
		return (NSArray) dicoParam.valueForKey(LocalSPConstantes.KEY_DICO_REPART_CAT_SORT_KEY);
	}
	
	/**
	 * 
	 */
	public String catTF() {
		return catItem;
	}

	/**
	 * 
	 */
	public NSArray motList() {
		return (NSArray) dicoParam.objectForKey(catItem);
	}
	
	/**
	 * 
	 */
	public String motTF() {
		return motItem;
	}
	
	/**
	 * 
	 */
	public String valueItem() {
		return Integer.toString(((Number)dicoResult.valueForKey(keyItem)).intValue());
	}
	
	/**
	 * 
	 */
	public String heuresValueItem() {
		return TimeCtrl.stringForMinutes(((Number)dicoResult.valueForKey(keyItem)).intValue());
	}

	/**
	 * 
	 */
	public String pourcentageValueItem() {
		int totalMinutes = 0;
		NSArray allKeys = dicoResult.allKeys();
		for (int i=0; i<allKeys.count(); i++) {
			totalMinutes += ((Number)dicoResult.valueForKey((String)allKeys.objectAtIndex(i))).intValue();
		}
		return Float.toString(((Number)dicoResult.valueForKey(keyItem)).floatValue()*100/(float)totalMinutes);
	}

	/**
	 * Donne l'url a mettre dans le navigateur pour pre-remplir le formulaire
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String getULRForm() throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		// sur toutes les categories
		for (int i=0; i<catList().count(); i++) {
			String cat = (String) catList().objectAtIndex(i);
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append("cat").append(i+1).append("=").append(URLEncoder.encode(cat, "ISO-8859-1"));
			NSArray motList = (NSArray) dicoParam.objectForKey(cat);
			for (int j=0; j<motList.count(); j++) {
				String mot = (String) motList.objectAtIndex(j);
				if (j==0) {
					sb.append("&");
				}
				sb.append("cat").append(i+1).append("mot").append(j+1).append("=").append(URLEncoder.encode(mot, "ISO-8859-1"));
				if (j<motList.count()-1) {
					sb.append("&");
				}
			}
		}
		sb.insert(0, spApp().getApplicationURL(context()) + "/wa/repartition?");
		return sb.toString();
	}
	
	/**
	 * La somme de toutes les minutes
	 * @return
	 */
	public int minutesTotal() {
		int total = 0;
		NSArray keys = dicoResult.allKeys();
		for (int i=0; i<keys.count(); i++) {
			String key = (String) keys.objectAtIndex(i); 
			total += ((Number)dicoResult.valueForKey(key)).intValue();
		}
		return total;
	}

	/**
	 * La somme de toutes les heures
	 * @return
	 */
	public String heuresTotal() {
		return TimeCtrl.stringForMinutes(minutesTotal());
	}
	
	// setter
	
	/**
	 * Renommer une categorie. Si vide c'est qu'on l'efface
	 */
	public void setCatTF(String value) {
		if (!StringCtrl.isEmpty(value)) {
			NSMutableArray motListMutable = new NSMutableArray((NSArray) dicoParam.valueForKey(catItem));
			dicoParam.removeObjectForKey(catItem);
			dicoParam.setObjectForKey(motListMutable.immutableClone(), value);
			NSMutableArray catListMutable = new NSMutableArray(catList());
			catListMutable.replaceObjectAtIndex(value, catListMutable.indexOfIdenticalObject(catItem));
			dicoParam.setObjectForKey(catListMutable.immutableClone(), LocalSPConstantes.KEY_DICO_REPART_CAT_SORT_KEY);
		} else {
			// on vire pas la derniere categorie
			if (dicoParam.allKeys().count() > 1) {
				dicoParam.removeObjectForKey(catItem);
				NSMutableArray catListMutable = new NSMutableArray(catList());
				catListMutable.removeIdenticalObject(catItem);
				dicoParam.setObjectForKey(catListMutable.immutableClone(), LocalSPConstantes.KEY_DICO_REPART_CAT_SORT_KEY);
			}
		}
	}
	
	/**
	 * Remplacer le <code>motIndex</code> element par celui la. Si vide c'est qu'on l'efface
	 */
	public void setMotTF(String value) {
		if (!StringCtrl.isEmpty(value)) {
			NSMutableArray motList = new NSMutableArray((NSArray) dicoParam.valueForKey(catItem));
			motList.replaceObjectAtIndex(value, motIndex);
			dicoParam.setObjectForKey(motList.immutableClone(), catItem);
		} else {
			// on vire pas le dernier mot clef
			NSMutableArray motList = new NSMutableArray((NSArray) dicoParam.valueForKey(catItem));
			if (motList.count() > 1) {
				motList.removeObjectAtIndex(motIndex);
				dicoParam.setObjectForKey(motList.immutableClone(), catItem);
			}
		}
	}
	

	// navigation
	
	private final static String DEFAULT_NEW_CAT = "<nouvelle categorie>";
	private final static String DEFAULT_NEW_MOT = "<nouveau mot clef>";
	
	/**
	 * Ajouter une categorie
	 */
	public WOComponent addCat() {
		if (dicoParam.objectForKey(DEFAULT_NEW_CAT) == null) {
			dicoParam.setObjectForKey(new NSArray(new String[]{DEFAULT_NEW_MOT}), DEFAULT_NEW_CAT);
			NSArray newCatList = catList().arrayByAddingObject(DEFAULT_NEW_CAT);
			dicoParam.setObjectForKey(newCatList, LocalSPConstantes.KEY_DICO_REPART_CAT_SORT_KEY);
		}
		return null;
	}
	
	/**
	 * Effacer la categorie en cours <code>catItem</code>
	 */
	public WOComponent removeCat() {
		dicoParam.remove(catItem);
		NSMutableArray newCatList = new NSMutableArray(catList());
		newCatList.removeIdenticalObject(catItem);
		dicoParam.setObjectForKey(newCatList, LocalSPConstantes.KEY_DICO_REPART_CAT_SORT_KEY);
		return null;
	}
	
	/**
	 * Ajouter un mot clef a la categorie en cours <code>catItem</code>,
	 * materialisee par <code>motList</code>
	 */
	public WOComponent addMot() {
		NSArray motList = (NSArray) dicoParam.valueForKey(catItem);
		motList = motList.arrayByAddingObject(DEFAULT_NEW_MOT);
		dicoParam.setObjectForKey(motList, catItem);
		return null;
	}
	
	/**
	 * Effacer <code>motItem</code> de la categorie en cours <code>catItem</code>,
	 * materialisee par <code>motList</code>. On enleve pas le dernier mot clef.
	 */
	public WOComponent removeMot() {
		NSArray motList = (NSArray) dicoParam.valueForKey(catItem);
		if (motList.count() > 1) {
			NSMutableArray motListMutable = new NSMutableArray(motList);
			motListMutable.removeIdenticalObject(motItem);
			dicoParam.setObjectForKey(motListMutable.immutableClone(), catItem);
		}
		return null;
	}
	
	/**
	 * Effectuer le calcul de repartition et remplit le dictionnaire
	 * resultat <code>dicoResult</code> qui est affiche sous forme de tableau
	 * @return
	 * @throws ComparisonException 
	 */
	public WOComponent doCalcul() throws ComparisonException {
		// transformer en date debut 00:00 et fin 23:59 
		NSTimestamp dateDebutMatin = DateCtrl.stringToDate(
				DateCtrl.dateToString((NSTimestamp) dateDebut) + " 00:00", SPConstantes.DATE_FORMAT);
		NSTimestamp dateFinSoir = DateCtrl.stringToDate(
				DateCtrl.dateToString((NSTimestamp) dateFin) + " 23:59", SPConstantes.DATE_FORMAT);
		Number noIndividu = new Integer(spSession().connectedUserInfo().noIndividu().intValue());
		NSDictionary localResult = sharedPlanningBus().calculRepartition(
				noIndividu.intValue(), dateDebutMatin, dateFinSoir, dicoParam);
		// on applique le classement
		NSArray catList = catList();
		dicoResult = new NSMutableDictionary();
		for (int i=0; i<catList.count(); i++) {
			String cat = (String) catList.objectAtIndex(i);
			dicoResult.setObjectForKey(localResult.objectForKey(cat), cat);
		}
		return null;
	}
	
	/**
	 * Remonter la categorie <code>catItem</code> dans le classement
	 */
	public WOComponent upSortCat() {	
		// on ne remonte pas si elle est deja premiere
		NSMutableArray catListMutable = new NSMutableArray(catList());
		int catItemIndex = catListMutable.indexOfIdenticalObject(catItem);
		if (catItemIndex != 0) {
			String prevCat = (String) catListMutable.objectAtIndex(catItemIndex-1);
			catListMutable.replaceObjectAtIndex(catItem, catItemIndex-1);
			catListMutable.replaceObjectAtIndex(prevCat, catItemIndex);
			dicoParam.setObjectForKey(catListMutable.immutableClone(), LocalSPConstantes.KEY_DICO_REPART_CAT_SORT_KEY);
		}
		return null;
	}
	
	/**
	 * Descendre la categorie <code>catItem</code> dans le classement
	 */
	public WOComponent downSortCat() {
		// on ne descend pas si elle est deja derniere
		NSMutableArray catListMutable = new NSMutableArray(catList());
		int catItemIndex = catListMutable.indexOfIdenticalObject(catItem);
		if (catItemIndex != catListMutable.count()-1) {
			String nextCat = (String) catListMutable.objectAtIndex(catItemIndex+1);
			catListMutable.replaceObjectAtIndex(catItem, catItemIndex+1);
			catListMutable.replaceObjectAtIndex(nextCat, catItemIndex);
			dicoParam.setObjectForKey(catListMutable.immutableClone(), LocalSPConstantes.KEY_DICO_REPART_CAT_SORT_KEY);
		}
		return null;
	}
	
	// ** raccourcis vers les bus de donnees **
	
	/**
	 * 
	 */
	private SharedPlanningBus sharedPlanningBus() {
		return spSession().dataCenter().sharedPlanningBus();
	}
	
}