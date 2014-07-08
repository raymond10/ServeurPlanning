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
package fr.univlr.cri.planning.factory;
import java.util.Properties;

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;

import com.webobjects.appserver.WORequest;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import fr.univlr.cri.planning.SPConstantes;
import fr.univlr.cri.planning.SPMethodes;
import fr.univlr.cri.planning.SPOccupation;
import fr.univlr.cri.planning.datacenter.ParamBus;

public class ModifPlanning {

	private ParamBus paramBus;
	private NSArray params;
	
	public ModifPlanning(ParamBus aParamBus) {
		super();
		paramBus = aParamBus;
	}

	// ------ traitemnt param dans cas modifplanning

	public ModifPlanning(ParamBus aParamBus, WORequest requete) {
		super();
		paramBus = aParamBus;

		NSMutableArray arr = new NSMutableArray();

		String all = requete.contentString();
		Properties prop = SPMethodes.stringToProperties(all);
		String appUrl = prop.getProperty(SPConstantes.PROP_CALLER);
		arr.addObject(SPConstantes.PROP_CALLER);
		arr.addObject(appUrl);
		String idkey = prop.getProperty(SPConstantes.PROP_OC_IDKEY);
		String idval = prop.getProperty(SPConstantes.PROP_OC_IDVAL);
		arr.addObject(idkey);
		arr.addObject(idval);
		int i = 0;
		while (prop.getProperty(SPConstantes.PROP_OC_DEB + i) != null) {
			String debut = prop.getProperty(SPConstantes.PROP_OC_DEB + i);
			String fin = prop.getProperty(SPConstantes.PROP_OC_FIN + i);
			String motif = prop.getProperty(SPConstantes.PROP_OC_TYPE + i);
			arr.addObject(debut);
			arr.addObject(fin);
			arr.addObject(motif);
			i++;
		}

		params = arr.immutableClone();
		traceParams();

	}

	/**
	 * Methode d'affichage des parametres de la classe.
	 */
	private void traceParams() {
		// on plante pas sur un log
		try {
			StringBuffer logBuf = new StringBuffer("parameters : ");
			logBuf.append("nomVariable").append("=[").append(params.objectAtIndex(0)).append("], ");
			logBuf.append(SPConstantes.PROP_CALLER).append("=[").append(params.objectAtIndex(1)).append("], ");
			logBuf.append(SPConstantes.PROP_OC_IDKEY).append("=[").append(params.objectAtIndex(2)).append("], ");
			logBuf.append(SPConstantes.PROP_OC_IDVAL).append("=[").append(params.objectAtIndex(3)).append("]");
			int max = params.count() - 2;
			for (int i = 4; i < max; i += 3) {
				if (i == 4)
					logBuf.append(" - ");
				logBuf.append("[");
				logBuf.append(SPConstantes.PROP_OC_DEB).append("=").append(params.objectAtIndex(i)).append(", ");
				logBuf.append(SPConstantes.PROP_OC_FIN).append("=").append(params.objectAtIndex(i+1)).append(", ");
				logBuf.append(SPConstantes.PROP_OC_TYPE).append("=").append(params.objectAtIndex(i+2));
				logBuf.append("]");
				if (i < max -3) 
					logBuf.append(", ");
			}
			CktlLog.trace(logBuf.toString());
		} catch (Exception e) {}
	}
	
	// ------------ trouver Conflit ------------
	// retourne les numéros des codageNew en conflit

	public NSMutableArray zoneConflit(NSArray codageNew, NSArray codageOld) {
		// 0000-0001 0000-0010 0000-0010 0000-0101 0000-1100
		// 0000-0000 0000-0000 0000-1000 0000-1000 0000-1000
		// ET si deux 1 -> 1 OU si un ou deux 1 -> 1
		// la je veux 0+0 =0 ; 1+0 =1 ; si 1+1 occ = 1 et 1 : 0010-0010
		// ET bit a bit + cas double

		NSMutableArray resConflit = new NSMutableArray();

		for (int i = 0; i < codageNew.count(); i++) {
			Byte nb1 = (Byte) codageNew.objectAtIndex(i);
			short nb1a = Short.parseShort(nb1.toString());

			Byte nb2 = (Byte) codageOld.objectAtIndex(i);
			short nb2a = Short.parseShort(nb2.toString());

			byte neww = (byte) (nb1a & nb2a);

			// zone de conflit : occupé/absent

			if (nb1a > 2 && nb2a > 2) // 2 occupé/absent
			{
				if (((nb1a & 4) == 4) && ((nb2a & 4) == 4))
				// 2 occupations (== 4 5 6 7 ou >12)
				{
					neww += 64;
					resConflit.addObject(new Integer(i)); // i : ligne tabs en conflit
				} else if ((nb1a & 8) == 8 && (nb2a & 8) == 8) { // 2 absences
					neww += 128;
					resConflit.addObject(new Integer(i));
				} else
					// donc 1 occupé + 1 absence
					resConflit.addObject(new Integer(i));
			}
		}
		return resConflit;
	}

	// -------------------------------------

	// decoder les spocc en conflit, -> dans un NSArray d'NSArrays d'SPOcc.

	public NSArray decodeConflit(NSArray zonesConflit, NSArray occupsNew,
			NSArray occupsOld, NSTimestamp deb, NSTimestamp fin) {
		NSMutableArray result = new NSMutableArray();

		NSMutableArray occupsNew2 = new NSMutableArray(occupsNew);
		// déterminer les intervalles de conflits
		NSArray interval = intervalles(zonesConflit);

		long depart = deb.getTime(); // en milliSecond
		// long longueur = fin.getTime() - depart; // en milliSecond

		for (int i = 0; i + 1 < interval.count(); i += 2) {
			// pour chaque intervalle trouvé
			long numDeb = Integer.parseInt(interval.objectAtIndex(i).toString()) * 60 * 1000;																																											// milliSec
			long numFin = Integer.parseInt(interval.objectAtIndex(i + 1).toString()) * 60 * 1000;
			NSTimestamp dateD = new NSTimestamp(depart + numDeb); 
			NSTimestamp dateF = new NSTimestamp(depart + numFin); // date fin conflit

			// trouver les newOcc consernées -> ajout meth dans SPOcc
			NSArray newOccZone = SPOccupation.findObjectCutingZone(occupsNew2, dateD, dateF);
			// ne garder que les Congés et Occupations (seul a creer des conflits)
			newOccZone = justeCongesEtOccup( newOccZone) ;
			for (int k = 0; k < newOccZone.count(); k++) {
				// pour chaque newOcc trouver date debut et fin
				SPOccupation occN = (SPOccupation) newOccZone.objectAtIndex(k);
				occupsNew2.removeIdenticalObject(occN);  
				// si une newOcc sur plusieurs intervalles, pas besoin de refaire (sera meme result)
				
				//Number codeT = session.dataCenter().paramBus().fetchCodeTempForTypeTemp(occN.getTypeTemps());
				
				NSTimestamp debNew = occN.getDateDebut();
				NSTimestamp finNew = occN.getDateFin();
				// trouver OldOcc consernées
				NSArray oldOccZone = SPOccupation.findObjectCutingZone(occupsOld, debNew,
						finNew);
				// ne garder que les Congés et Occupations (seul a creer des conflits)
				oldOccZone = justeCongesEtOccup( oldOccZone) ;
				// insérer dans tab la newOcc et ses OldOcc
				if (oldOccZone.count() != 0) 
				  {
					NSMutableArray unConflit = new NSMutableArray();
					unConflit.addObject(occN);
					for (int d = 0; d < oldOccZone.count(); d++)
						unConflit.addObject((SPOccupation) oldOccZone.objectAtIndex(d));
					// insérer dans tableau global
					result.addObject(unConflit);
				  }
			} // fin for each newOcc
		} // fin for each intervalle

		return new NSArray(result);
	}

	// ------------------- Creation Message d'alerte ----------

	public String messageAlert(NSArray arrOccs) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < arrOccs.count(); i++) {
			NSArray unConflit = (NSArray) arrOccs.objectAtIndex(i);
			for (int k = 0; k < unConflit.count(); k++) {
				SPOccupation occ = (SPOccupation) unConflit.objectAtIndex(k);
				String deb = DateCtrl.dateToString(occ.getDateDebut(),
						SPConstantes.DATE_FORMAT);
				String fin = DateCtrl.dateToString(occ.getDateFin(),
						SPConstantes.DATE_FORMAT);
				if (k == 0)
					buffer.append(" La periode de '" + deb + "' é '" + fin + "' contient déjé :");
				else
					buffer.append("   " + k + ") '" + occ.getTypeTemps() + "' de '" + deb
							+ "' é '" + fin + "'; "); // trouver nomAppli ???
			}
		}
		return buffer.toString();
	}

	// ----------------------
	// détermine les intervalles de conflits

	private NSArray intervalles(NSArray zonesConflit) {
		NSMutableArray interval = new NSMutableArray();
		int prec = Integer.parseInt(zonesConflit.objectAtIndex(0).toString());
		int now = Integer.parseInt(zonesConflit.objectAtIndex(1).toString());
		int indiceSuiv = 2;
		interval.addObject(zonesConflit.objectAtIndex(0)); // debut 1

		while (indiceSuiv < zonesConflit.count()) // TQ non fin tab
		{
			while (indiceSuiv < zonesConflit.count() && prec + 1 == now) {
				prec = now;
				now = Integer.parseInt(zonesConflit.objectAtIndex(indiceSuiv)
						.toString());
				indiceSuiv++;
			}
			// arrivé a 1 limite

			if (indiceSuiv < zonesConflit.count()) // pas limite fin tab, changement
																							// d'interval
			{
				interval.addObject(zonesConflit.objectAtIndex(indiceSuiv - 2)); // fin n
				interval.addObject(zonesConflit.objectAtIndex(indiceSuiv - 1)); // debut
																																				// n+1
				prec = now;
				now = Integer.parseInt(zonesConflit.objectAtIndex(indiceSuiv)
						.toString());
				indiceSuiv++;
			} else
				interval.addObject(zonesConflit.objectAtIndex(indiceSuiv - 1)); // fin
																																				// tableau
		}
		// interval.addObject(zonesConflit.objectAtIndex(zonesConflit.count()-1));
		// // fin tab
		return new NSArray(interval);
	}

	// -----------------------------
	private NSArray justeCongesEtOccup(NSArray oldOccZone) {
		NSMutableArray oo = new NSMutableArray();
		
		for (int w = 0; w < oldOccZone.count(); w++) {
			SPOccupation occ = (SPOccupation) oldOccZone.objectAtIndex(w);
			String type = occ.getTypeTemps();
			Number numT = paramBus.fetchCodeTempForTypeTemp(type);
			if (Integer.parseInt(numT.toString()) > 2) {
				oo.addObject(occ);
			}
		}
		return new NSArray(oo);
	}

	public NSArray getParams() {
		return params;
	}

	// --------------------
	// methode pour test performance
	// repérer conflit par comp dateDebut, dateFin, sans codage.
	// quand coupe, tester si types conflictuels

	/*
	 * public NSArray findConflits() {
	 *  }
	 */
}
