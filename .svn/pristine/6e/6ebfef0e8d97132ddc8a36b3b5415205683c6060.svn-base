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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.ValidationException;

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.database.CktlUserInfoDB;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WORequest;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;

import fr.univlr.cri.planning.DemandePlanning;
import fr.univlr.cri.planning.PartagePlanning;
import fr.univlr.cri.planning.SPConstantes;
import fr.univlr.cri.planning.SPOccupation;
import fr.univlr.cri.planning.constant.LocalSPConstantes;
import fr.univlr.cri.planning.extern.calendartostring.CalendarNotFoundException;
import fr.univlr.cri.planning.extern.calendartostring.CalendarObject;
import fr.univlr.cri.planning.factory.HugICalendarFactory;
import fr.univlr.cri.planning.factory.LecturePlanning;
import fr.univlr.cri.planning.icalendar.SPCalendar;

/**
 * Bus rassemblant tout les traitements specifiques aux fichiers ICalendar
 * (.ics)
 * 
 * @author ctarade
 */

public class ICalendarBus extends A_SPDataBusCaching {

	public ICalendarBus(EOEditingContext arg0) {
		super(arg0);
	}

	/**
	 * @deprecated recuperation d'infos contenues dans un .ics
	 */
	public StringBuffer getBufferOccupationFromICalendar(WORequest requete) {

		HugICalendarFactory icalFactory = new HugICalendarFactory(paramBus());
		StringBuffer buffer = new StringBuffer();
		String err = "";
		Boolean statut = Boolean.TRUE;

		// utiliser PartagePlanning
		NSDictionary params = PartagePlanning.dicoParams(requete);

		Number noIndividu = (Number) params.valueForKey("noIndividu");
		if (noIndividu == null) {
			err = "Parametre noIndividu absent.";
			statut = Boolean.FALSE;
			CktlLog.trace("parameter noIndividu missing.");
		} else {
			NSTimestamp deb = null;
			if (params.valueForKey("debut") != null) {
				deb = (NSTimestamp) params.valueForKey("debut");
			}
			NSTimestamp fin = null;
			if (params.valueForKey("fin") != null) {
				fin = (NSTimestamp) params.valueForKey("fin");
			}

			NSArray<CktlRecord> recsIcal = paramBus().fetchICalendar(noIndividu);
			NSMutableArray<CktlRecord> recsInfosMutable = new NSMutableArray<CktlRecord>(recsIcal);

			String calName = (String) params.valueForKey("calendarName");
			if (!StringCtrl.isEmpty(calName)) {
				// requete pour un calendrier donné
				if (!calName.endsWith(LocalSPConstantes.ICS_FILE_NAME_EXTENSION))
					calName += LocalSPConstantes.ICS_FILE_NAME_EXTENSION;
				CktlRecord selectedRecIcal = null;
				for (int i = 0; i < recsInfosMutable.count(); i++) {
					CktlRecord recIcal = (CktlRecord) recsInfosMutable.objectAtIndex(i);
					String lien = recIcal.stringForKey(LocalSPConstantes.BD_ICAL_LIEN);
					String nomICal = recIcal.stringForKey(LocalSPConstantes.BD_ICAL_NAME);
					if (!lien.endsWith("/"))
						lien += "/";
					if (nomICal.equals(calName))
						selectedRecIcal = recIcal;
				}
				recsInfosMutable.removeAllObjects();
				if (selectedRecIcal != null)
					recsInfosMutable.addObject(selectedRecIcal);
				else
					CktlLog.trace("calendar '" + selectedRecIcal + "' not found.");
			}

			int nbEvents = 0;
			for (int c = 0; c < recsInfosMutable.count(); c++) {
				// String nomCal = (String)infosICal.objectAtIndex(c);
				CktlRecord record = (CktlRecord) recsInfosMutable.objectAtIndex(c);
				String lien = record.stringForKey(LocalSPConstantes.BD_ICAL_LIEN);
				String nomICal = record.stringForKey(LocalSPConstantes.BD_ICAL_NAME);
				if (!lien.endsWith("/"))
					lien += "/";
				String nomComplet = lien + nomICal;
				String typeCal = record.stringForKey(LocalSPConstantes.BD_ICAL_TYPE);

				// on essaye d'utiliser le cache
				CalendarObject cal = icalFactory.getCalendarObjectFromCache(nomComplet);
				if (cal != null) {
					buffer = icalFactory.appendCalendarObjectToStringBuffer(buffer, cal, deb, fin, nbEvents, nomICal);
				} else {
					try {
						buffer = icalFactory.appendBufferFromICalendarFile(buffer, noIndividu, nomComplet, typeCal, deb, fin, nomICal);
					} catch (CalendarNotFoundException cnf) {
						CktlLog.trace("calendar not found : CalendarNotFoundException");
						err = "Calendrier " + nomComplet + " non trouve.";
						statut = Boolean.FALSE;
					} catch (Exception exc) {
						CktlLog.trace("problem translating .ics file");
						err = "Probleme de traduction du iCalendar.";
						statut = Boolean.FALSE;
					}
				}

				nbEvents = icalFactory.getNbEvents();
			}
		}

		if (statut != Boolean.FALSE)
			buffer.append("statut = 1\n");
		else
			buffer.append("statut = 0\n");
		if (err != "")
			buffer.append("erreur = " + err + "\n");

		return buffer;
	}

	/**
	 * Creation d'un nouveau fichier .ics.
	 */
	public boolean doCreateFileICalendarFromRequest(WORequest requete) {
		/*
		 * String all = requete.contentString();
		 * 
		 * // "cheminEtNom = "+cheminEtNomCal+"*\n"); StringTokenizer st = new
		 * StringTokenizer(all, "*"); String cheminEtNom = st.nextToken();
		 * cheminEtNom = cheminEtNom.substring(12);
		 * 
		 * Properties prop = SPMethodes.stringToProperties(all);
		 * 
		 * HugICalendarFactory pp = new HugICalendarFactory(paramBus());
		 * StringBuffer buffer = pp.iCalMeth(prop);
		 * 
		 * return pp.createFileICalendar(buffer.toString(), cheminEtNom);
		 */
		return false;
	}

	/**
	 * Effectuer l'extraction des cles d'une {@link WORequest}, et faire
	 * l'eventuel nettoyage (virer l'extension ics par exemple)
	 * 
	 * @param request
	 * @return
	 */
	public NSDictionary<String, String> getRequestKeys(WORequest request) {

		NSMutableDictionary<String, String> dico = new NSMutableDictionary<String, String>();

		String login = (String) request.formValueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_LOGIN_KEY);
		if (!StringCtrl.isEmpty(login)) {
			dico.setObjectForKey(login, LocalSPConstantes.DIRECT_ACTION_PARAM_LOGIN_KEY);
		}
		String msremKey = (String) request.formValueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_DIPLOME_SPECIALISATION_PARCOURS_SEMESTRE_KEY);
		if (!StringCtrl.isEmpty(msremKey)) {
			dico.setObjectForKey(msremKey, LocalSPConstantes.DIRECT_ACTION_PARAM_DIPLOME_SPECIALISATION_PARCOURS_SEMESTRE_KEY);
		}
		String ggrpKey = (String) request.formValueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_GROUPE_ETUDIANT_KEY);
		if (!StringCtrl.isEmpty(ggrpKey)) {
			dico.setObjectForKey(ggrpKey, LocalSPConstantes.DIRECT_ACTION_PARAM_GROUPE_ETUDIANT_KEY);
		}
		String salNumero = (String) request.formValueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_SALLE_KEY);
		if (!StringCtrl.isEmpty(salNumero)) {
			dico.setObjectForKey(salNumero, LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_SALLE_KEY);
		}
		String roKey = (String) request.formValueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_OBJET_KEY);
		if (!StringCtrl.isEmpty(roKey)) {
			dico.setObjectForKey(roKey, LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_OBJET_KEY);
		}
		String cStructure = (String) request.formValueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_CODE_STRUCTURE_KEY);
		if (!StringCtrl.isEmpty(cStructure)) {
			dico.setObjectForKey(cStructure, LocalSPConstantes.DIRECT_ACTION_PARAM_CODE_STRUCTURE_KEY);
		}
		String strDateDebut = (String) request.formValueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_DATE_DEBUT);
		if (!StringCtrl.isEmpty(strDateDebut)) {
			dico.setObjectForKey(strDateDebut, LocalSPConstantes.DIRECT_ACTION_PARAM_DATE_DEBUT);
		}
		String strDateFin = (String) request.formValueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_DATE_FIN);
		if (!StringCtrl.isEmpty(strDateFin)) {
			dico.setObjectForKey(strDateFin, LocalSPConstantes.DIRECT_ACTION_PARAM_DATE_FIN);
		}

		// suite a un ajout par une appli externe (ex: phpICalendar)
		// on peut avoir l'extension ".ics" => on la virer

		for (int i = 0; i < dico.allKeys().count(); i++) {
			String key = (String) dico.allKeys().objectAtIndex(i);
			String value = (String) dico.objectForKey(key);
			if (value.endsWith(LocalSPConstantes.ICS_FILE_NAME_EXTENSION)) {
				value = StringCtrl.replace(value, LocalSPConstantes.ICS_FILE_NAME_EXTENSION, "");
			}
			dico.setObjectForKey(value, key);
		}

		return dico.immutableClone();
	}

	/**
	 * Determiner l'uid passée dans une {@link WORequest} C'est soit le login,
	 * soit le mrsemKey ... Il ne doit y en avoir 1 et 1 seul
	 * 
	 * @param request
	 * @return
	 */
	public String getUidForRequestDictionary(NSDictionary dico) {
		String uid = null;

		if (dico.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_LOGIN_KEY) != null) {
			// individu ? => login et donc noIndividu
			/*
			 * String login = (String)
			 * dico.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_LOGIN_KEY);
			 * CktlUserInfoDB ui = new CktlUserInfoDB(this); ui.compteForLogin(login,
			 * null, true); uid = Integer.toString(ui.noIndividu().intValue());
			 */
			uid = (String) dico.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_LOGIN_KEY);
		} else if (dico.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_DIPLOME_SPECIALISATION_PARCOURS_SEMESTRE_KEY) != null) {
			// diplome ? => mrsemKey
			uid = (String) dico.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_DIPLOME_SPECIALISATION_PARCOURS_SEMESTRE_KEY);
		} else if (dico.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_GROUPE_ETUDIANT_KEY) != null) {
			// groupe etudiant ? => ggrpKey
			uid = (String) dico.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_GROUPE_ETUDIANT_KEY);
		} else if (dico.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_SALLE_KEY) != null) {
			// salle ? => salNumero
			uid = (String) dico.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_SALLE_KEY);
		} else if (dico.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_OBJET_KEY) != null) {
			// objet ? => roKey
			uid = (String) dico.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_OBJET_KEY);
		} else if (dico.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_CODE_STRUCTURE_KEY) != null) {
			// groupe ? => cStructure
			uid = (String) dico.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_CODE_STRUCTURE_KEY);
		}

		return uid;
	}

	/**
	 * Determiner le nom du fichier sur le filesystem du serveur d'apres le
	 * contenu d'une requete au serveur de planning
	 * 
	 * @param dicoRequest
	 * @return
	 */
	private String getCalendarFileNameForRequestDictionnay(NSDictionary dicoRequest) {
		String fileName = "inconnu";

		if (dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_LOGIN_KEY) != null) {
			// pour un login, login.ics
			fileName = (String) dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_LOGIN_KEY);

		} else if (dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_DIPLOME_SPECIALISATION_PARCOURS_SEMESTRE_KEY) != null) {
			// pour un diplome mrsemKey_<mrsemKey>.ics
			fileName = LocalSPConstantes.DIRECT_ACTION_PARAM_DIPLOME_SPECIALISATION_PARCOURS_SEMESTRE_KEY + "_"
						+ (String) dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_DIPLOME_SPECIALISATION_PARCOURS_SEMESTRE_KEY);

		} else if (dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_GROUPE_ETUDIANT_KEY) != null) {
			// pour un groupe ggrpKey_<ggrpKey>.ics
			fileName = LocalSPConstantes.DIRECT_ACTION_PARAM_GROUPE_ETUDIANT_KEY + "_"
							+ (String) dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_GROUPE_ETUDIANT_KEY);

		} else if (dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_SALLE_KEY) != null) {
			// pour une salle salNumero_<salNumero>.ics
			fileName = LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_SALLE_KEY + "_"
								+ (String) dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_SALLE_KEY);

		} else if (dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_OBJET_KEY) != null) {
			// pour un objet roKey_<roKey>.ics
			fileName = LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_OBJET_KEY + "_"
									+ (String) dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_OBJET_KEY);

		} else if (dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_CODE_STRUCTURE_KEY) != null) {
			// pour un objet cStructure_<cStructure_>.ics
			fileName = LocalSPConstantes.DIRECT_ACTION_PARAM_CODE_STRUCTURE_KEY + "_"
										+ (String) dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_CODE_STRUCTURE_KEY);
		}

		fileName += LocalSPConstantes.ICS_FILE_NAME_EXTENSION;

		return fileName;
	}

	/**
	 * Retourner le fichier .ics associe a toutes les occupations du systeme
	 * d'informations (cree avec ical4j).
	 * 
	 * @param requete
	 * 
	 * @param metclient
	 *          : la methode client declaree dans la base de donnees pour
	 *          retrouver les methodes serveurs sur lesquelles se connecter
	 */

	public NSData getICalendarStream(WORequest requete, String metclient) {

		NSDictionary<String, String> dicoRequest = getRequestKeys(requete);

		// l'identifiant passé
		String uid = getUidForRequestDictionary(dicoRequest);
		// nom du calendrier (par exemple le libellé visible dans ical.app)
		String calendarName = "";
		// preparation des parametres pour l'appel au framework
		String idKey = null;

		if (dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_LOGIN_KEY) != null) {
			// individu ? => login et donc noIndividu
			// on va remplacer l'uid par le numéro d'individu ...
			CktlUserInfoDB ui = new CktlUserInfoDB(this);
			ui.compteForLogin(uid, null, true);
			uid = Integer.toString(ui.noIndividu().intValue());
			//
			calendarName = ui.nomEtPrenom();
			//
			idKey = Integer.toString(SPConstantes.IDKEY_INDIVIDU.intValue());

		} else if (dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_DIPLOME_SPECIALISATION_PARCOURS_SEMESTRE_KEY) != null) {
			// diplome ? => mrsemKey
			calendarName = "Diplome";
			//
			idKey = Integer.toString(SPConstantes.IDKEY_DIPLOME.intValue());

		} else if (dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_GROUPE_ETUDIANT_KEY) != null) {
			// groupe ? => mrsemKey
			calendarName = "GroupeEtudiant";
			//
			idKey = Integer.toString(SPConstantes.IDKEY_GROUPE_ETUDIANT.intValue());

		} else if (dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_SALLE_KEY) != null) {
			// salle ? => salNumero
			calendarName = "Salle";
			//
			idKey = Integer.toString(SPConstantes.IDKEY_SALLE.intValue());

		} else if (dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_NUMERO_OBJET_KEY) != null) {
			// objet ? => roKey
			calendarName = "Objet";
			//
			idKey = Integer.toString(SPConstantes.IDKEY_OBJET.intValue());

		} else if (dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_CODE_STRUCTURE_KEY) != null) {
			// groupe ? => cStructure
			calendarName = "Groupe";
			//
			idKey = Integer.toString(SPConstantes.IDKEY_GROUPE.intValue());
		}

		// s'assurer que l'agent autorise la publication de son agenda
		/*
		 * CktlRecord recIndividu = null; // temoin si le flux ics doit etre remplis
		 * ou pas boolean shouldFeedICalendarStream = true;
		 * 
		 * NSArray individuList = fetchArray("sp_Individu", newCondition(
		 * "noIndividu=%@", new NSArray(ui.noIndividu())), null); if
		 * (individuList.count() > 0) { recIndividu = (CktlRecord)
		 * individuList.lastObject(); }
		 * 
		 * // pas de flux si pas de publication autorisee // par defaut, if
		 * (recIndividu != null) { //String indAgenda =
		 * recIndividu.stringForKey("indAgenda"); // TODO par defaut, tout le monde
		 * est 'N' ................. //if (!StringCtrl.isEmpty(indAgenda) &&
		 * indAgenda.equals("O")) { shouldFeedICalendarStream = true; //} }
		 */

		// le flux a retourner
		NSData calendarStream = null;

		final SPCalendar calendar = new SPCalendar(uid, paramBus(), calendarName);

		String idVal = uid;

		// la fenetre temporelle d'interrogation (si aucune date debut - date fin ne
		// sont precisées
		// on prend celle definit dans les parametres de l'application)
		NSTimestamp dateDebut = null;
		NSTimestamp dateFin = null;
		if (dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_DATE_DEBUT) != null
				&& dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_DATE_FIN) != null) {
			dateDebut = DateCtrl.stringToDate((String) dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_DATE_DEBUT),
					SPConstantes.DATE_FORMAT);
			dateFin = DateCtrl.stringToDate((String) dicoRequest.valueForKey(LocalSPConstantes.DIRECT_ACTION_PARAM_DATE_FIN),
					SPConstantes.DATE_FORMAT);
		}

		// si l'une des 2 dates a foiré lors de la conversion, on prend les dates
		// definies par l'application
		if (dateDebut == null || dateFin == null) {
			dateDebut = app().icsDateDebut();
			dateFin = app().icsDateFin();
		}

		// creer la cle
		String dicoCalendarKey = buildKey(metclient, idKey, idVal, dateDebut, dateFin);

		// tenter une recuperation depuis le cache
		calendarStream = (NSData) getObjectFromCache(dicoCalendarKey);

		// pas trouve, on recree le flux
		if (calendarStream == null) {

			String strDebut = DateCtrl.dateToString(dateDebut, SPConstantes.DATE_FORMAT);
			String strFin = DateCtrl.dateToString(dateFin, SPConstantes.DATE_FORMAT);

			Properties param = new Properties();
			param.put(SPConstantes.PAR1_METHCLIENT, metclient);
			param.put(SPConstantes.PROP_OC_IDKEY, idKey);
			param.put(SPConstantes.PROP_OC_IDVAL, idVal);
			param.put(SPConstantes.PROP_OC_DEB, strDebut);
			param.put(SPConstantes.PROP_OC_FIN, strFin);

			// recuperation des occupations du SI
			Properties p = sharedPlanningBus().getPropertiesOccupationFromParams(new LecturePlanning(paramBus(), param));

			// CktlLog.trace("p="+p, true);

			NSArray<NSArray<SPOccupation>> arraysOcc = DemandePlanning.getPlanning(p);

			long l1 = System.currentTimeMillis();
			// chaque groupe d'evenement est ajoute
			for (int i = 0; i < arraysOcc.count(); i++) {
				calendar.addSPOccupations(arraysOcc.objectAtIndex(i));
			}
			// enregistrement
			calendar.commitEvents();
			l1 = System.currentTimeMillis() - l1;
			CktlLog.log("creating calendar : " + l1 + " ms.");

			// doPreStoreDiskStuff();

			// construction du nom du fichier
			// String filePathICalendar =
			// doPreStoreDiskStuff() + fsSeparator() +
			// getCalendarFileNameForRequestDictionnay(dicoRequest);

			// on construit le calendrier que s'il y a des choses dedans ...

			// modif pdm20131113
			// if (arraysOcc.count() > 0) {

			// ecriture du fichier sur le disque
			/*
			 * try { l1 = System.currentTimeMillis();
			 * 
			 * FileOutputStream fout = new FileOutputStream(filePathICalendar);
			 * CalendarOutputter outputter = new CalendarOutputter();
			 * outputter.output(calendar, fout); l1 = System.currentTimeMillis() - l1;
			 * CktlLog.log("write calendar file : "+l1+" ms ("+filePathICalendar+
			 * ")"); } catch (Exception e) { e.printStackTrace(); } try {
			 * calendarStream = new NSData(new File(filePathICalendar)); } catch
			 * (IOException e) { e.printStackTrace(); }
			 */

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// modif pdm20131113
			// CalendarOutputter outputter = new CalendarOutputter();
			CalendarOutputter outputter = new CalendarOutputter(arraysOcc.count() > 0);

			try {
				outputter.output(calendar, baos);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			calendarStream = new NSData(baos.toByteArray());

			// modif pdm20131113
			// } else {
			// // pas d'occupations : fichier vide
			// calendarStream = new NSData();
			// CktlLog.trace("ics file is empty : no occupations ...");
			// }

			// sauvegarde du flux en cache
			putObjectInCache(dicoCalendarKey, calendarStream);
		}/*
			 * } else { // pas de publication : fichier vide calendarStream = new
			 * NSData(); CktlLog.trace("ics file is empty : IND_AGENDA = 'N' ..."); }
			 */

		// lire le contenu du fichier
		return calendarStream;

	}

	// ** raccourcis vers les bus de donnees **

	private ParamBus paramBus() {
		return sPDataCenter().paramBus();
	}

	private SharedPlanningBus sharedPlanningBus() {
		return sPDataCenter().sharedPlanningBus();
	}

	// ** les differents cache **

	private Integer ttlObjectCalendarStream;

	public int ttlObject() {
		if (ttlObjectCalendarStream == null) {
			ttlObjectCalendarStream = new Integer(app().ttlICalendarWrite());
		}
		return ttlObjectCalendarStream.intValue();
	}

}
