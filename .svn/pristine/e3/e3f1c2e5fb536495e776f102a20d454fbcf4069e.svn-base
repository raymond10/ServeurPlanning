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
package fr.univlr.cri.planning.thread;

import java.util.Properties;

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import fr.univlr.cri.planning.SPConstantes;
import fr.univlr.cri.planning.SPOccupation;
import fr.univlr.cri.planning._imports.StringCtrl;
import fr.univlr.cri.planning.constant.LocalSPConstantes;
import fr.univlr.cri.planning.datacenter.ParamBus;
import fr.univlr.cri.planning.factory.LecturePlanning;

/**
 * Classe assure l'acces une methode serveur. Il s'agit d'une classe gerant un
 * thead, ce qui permet de lancer plusieurs acces en meme temps.
 * 
 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
 */

public class RemoteApplicationAccessor
		implements Runnable {

	private Number keyServerMethod;
	private ParamBus paramBus;
	private String nomClient;
	private String idv;
	private String idk;
	private Object[] arguments;
	private LecturePlanning sp;
	private Properties prop;
	private NSTimestamp debut;
	private NSTimestamp fin;
	private Number keyClient;

	private String err;
	private String stat;
	private NSMutableArray<SPOccupation> arraySpOccupation;

	private Thread thread;

	public RemoteApplicationAccessor(Number aKeyServerMetho, ParamBus aParamBus, String aRemoteMethod,
			String anIdVal, String anIdKey, Object[] someArguments, LecturePlanning aSp,
			Properties aBuffer, NSTimestamp aDebut, NSTimestamp aFin, Number aKeyClient) {
		keyServerMethod = aKeyServerMetho;
		paramBus = aParamBus;
		nomClient = aRemoteMethod;
		err = "";
		idv = anIdVal;
		idk = anIdKey;
		arguments = someArguments;
		sp = aSp;
		prop = aBuffer;
		arraySpOccupation = new NSMutableArray<SPOccupation>();
		debut = aDebut;
		fin = aFin;
		keyClient = aKeyClient;
		thread = new Thread(this);
	}

	/**
	 * Lancer le thread
	 */
	public void start() {
		thread.start();
	}

	/**
   * 
   */
	public void join() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
   * 
   */
	public void run() {

		// ------------ APPEL ET RECUP RESULTATS

		long l1 = System.currentTimeMillis();

		Properties localProp = LecturePlanning.requeteMethodeServeur(
				paramBus, keyServerMethod, arguments, nomClient, hashCode());

		l1 = System.currentTimeMillis() - l1;
		log("[#" + hashCode() + "] time access : " + l1 + " ms.");

		// ------------- TRAITEMENT DES RESULTATS

		// recup a type return
		Number typeRetourS = (Number) paramBus.fetchInfoForId(
				keyServerMethod,
				LocalSPConstantes.BD_SERVEUR_KEY,
				LocalSPConstantes.BD_MET_SERV,
				LocalSPConstantes.BD_SERVEUR_VAR);

		// recup uri associée a keyS
		String uri = paramBus.fetchUriForKey(keyServerMethod);

		String statut = localProp.getProperty(SPConstantes.PROP_STATUT);

		if (!StringCtrl.isEmpty(statut) && !statut.equals("0") && !statut.equals("2")) {
			// pas d'erreur
			int typeRS = Integer.parseInt(typeRetourS.toString());

			// A REVOIR SEPARATION SELON TYPE VAR

			if (typeRS == 2 || typeRS == 3) {
				// booleen
				prop = sp.getPropertiesBoolean(localProp, idv, idk);
			} else if (typeRS >= 10 && typeRS < 20) {
				// nombre
				prop = sp.getPropertiesNumber(localProp, idv, idk);
			} else if (typeRS >= 30) {

				// Occupation - stockage avant traitement
				arraySpOccupation = LecturePlanning.appendArraySpOccupationFromProperties(
						arraySpOccupation,
						localProp,
						idv,
						idk);

				prop = LecturePlanning.getPropertiesOccupation(
						hashCode(),
						paramBus,
						keyClient,
						new NSArray(arraySpOccupation),
						idv,
						idk,
						DateCtrl.dateToString(debut, SPConstantes.DATE_FORMAT),
						DateCtrl.dateToString(fin, SPConstantes.DATE_FORMAT));

			} else {
				// type retour non connu
				String libTypeRetourS = paramBus.fetchLibTypeVarForKeyVar(typeRetourS);
				stat = "0";
				err = "Type de variable non pris en compte par ServeurPlanning : " + libTypeRetourS;
			}
		} else {
			String errMessage = localProp.getProperty(SPConstantes.PROP_ERREUR);
			// une erreur est survenue
			if (errMessage != null) {
				err = "La methode " + uri + " a retourne une erreur : [" + errMessage + "] , pour identifiant : " + idv;
				stat = "0";
			} else {
				log("La methode " + uri + " est introuvable, pour identifiant : " + idv);
				stat = "1";
			}
		}
	}

	public String getErr() {
		return err;
	}

	public String getStat() {
		return stat;
	}

	public Properties getBuffer() {
		return prop;
	}

	private void log(String message) {
		CktlLog.log(message);
	}

}
