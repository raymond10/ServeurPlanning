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
package fr.univlr.cri.planning.extern.calendartostring;

import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import fr.univlr.cri.planning.extern.calendartostring.recurrance.Recurrance;

/**
 * @deprecated
 * 
 * Classe "RepeatEvent" implémentée avec l'exemple de la classe "HolidayEvents"
 * de Johan Carlberg (site web : http://oops.se/woical/); <br>
 */
public class RepeatEvent extends SimpleEvent {

	protected String rule;

	protected NSArray dates;

	/* ------- constructeur ---------- */

	public RepeatEvent(String startTime, int endType, String endVal,
			String title, NSArray rDates, String rRules) {
		super(startTime, endType, endVal, title);
		if (rDates != null)
			dates = rDates;
		if (rRules != null)
			rule = rRules;
	}

	public NSArray repeatDates()
	{
		NSArray arr = new NSArray();
		CktlLog.log("repeatDates="+dates);
		arr = repeatDates(dates, rule);
		return arr;
	}
	
	
	// utiliser avec dates/rule ou exDates/exRule
	
	protected NSArray repeatDates(NSArray daty, String ruly) {
		//System.out.println("repeatDate Dates : "+daty.toString()+"; Rules : "+ruly);
		NSMutableArray arr = new NSMutableArray();
		if (daty.count() != 0) 
		{  													// RDATE ou EXDATE renseigné
			// ex
			// RDATE;VALUE=DATE:19970101,19970120,19970217
			// RDATE;VALUE=DATE-TIME:00010101T000000

			// apres ":" puis séparé par ","
			/*StringTokenizer st = new StringTokenizer(daty, ":");
			int nb = st.countTokens();
			String lesDates ="";
			for (int i = 0; i < nb; i++) 
				lesDates = st.nextToken();
				*/
			for (int i=0; i<daty.count();i++)
			{
				String objDate = (String) daty.objectAtIndex(i);
				
				StringTokenizer st2 = new StringTokenizer(objDate, ",");
				int nb2 = st2.countTokens();
				String uneDate ="";
				NSTimestamp laDate= null;
				for (int k = 0; k < nb2; k++) 
				{
					uneDate = st2.nextToken();
					laDate = dateFormat(uneDate);
					//System.out.println("repeatDate Dates list date : toString = "+laDate.toString()+"; dateCtrl = "+DateCtrl.dateToString(laDate, SPConstantes.DATE_FORMAT));
					arr.addObject(laDate);
				}
			}

		} else if (ruly != null){			// RRULE ou EXRULE renseigné
			// calcul des dates
//		 trouvé valeur de Until pour 3eme param Recurrance
			Date until = null;
			if (StringCtrl.containsIgnoreCase(ruly, "UNTIL="))
			{
				StringTokenizer st = new StringTokenizer(rule, "=");
				String before = st.nextToken();
				while (!before.endsWith("UNTIL"))
					 before = st.nextToken();
				String untilVal = st.nextToken();
				
				StringTokenizer sf = new StringTokenizer(untilVal, ";");
				untilVal = sf.nextToken();
				until = new Date (dateFormat(untilVal).getTime());
			}
			NSTimestamp de1 = dateDebut();
			NSTimestamp de = de1;
			//System.out.println("repeatDate Recurrance debut : toString = "+de.toString()+"; dateCtrl = "+DateCtrl.dateToString(de, SPConstantes.DATE_FORMAT));
		
			// Correction pour PB si h <= 02h00 -> Recurrance fait 1 décalage d'une journée 
			// dut au format de date qui retire 2h a la date locale.
			boolean modifH = false;
			int h = de.getHours();   // attention 0h donne 23, donc >=23 ou <=1
			if (h<=1 || h>=23)
			{ 
				de = new NSTimestamp(de.getTime()+2*60*60*1000);   // + 2h
				modifH = true;
			}
			Recurrance repetition = new Recurrance(ruly, de, until);
			List repDates = repetition.getAllMatchingDates();
			//System.out.println("repeatDate Recurrance list : toString = "+repDates.toString());
			for (int i = 0; i < repDates.size(); i++) {
				long time = ((Date) repDates.get(i)).getTime();
				if (modifH)
					time -= 2*60*60*1000;
				NSTimestamp t = new NSTimestamp(time);
				//System.out.println("repeatDate Recurrance list date : toString = "+t.toString()+"; dateCtrl = "+DateCtrl.dateToString(t, SPConstantes.DATE_FORMAT));
				arr.addObject(t);
			}
		}
		return arr;
	}

	/*
	 * protected String repeatFrequency; protected int repeatCount; protected int
	 * repeatInterval = 1; protected NSTimestamp repeatUntil; protected int
	 * repeatDebutSemaine = 0; // 0 pour dimanche, 1 lundi ... protected
	 * NSMutableArray repeatJours; protected NSMutableArray repeatMois; protected
	 * NSMutableArray repeatJoursDuMois; protected NSMutableArray
	 * repeatJoursDeLAnnee; protected NSMutableArray repeatSemaines; protected
	 * NSMutableArray repeatPositions; protected NSMutableArray repeatHeures;
	 * protected NSMutableArray repeatMinutes;
	 */

	// ------ Exemple ---------------
	// FREQ=YEARLY; ( ou MONTHLY;DAILY;HOURLY;MINUTELY;SECONDLY)
	// INTERVAL=2; -> event toutes les n frequences
	// (si Yearly, 2 -> tous les 2 ans)
	// UNTIL=20060929T215959; -> Soit jusqu'au ...
	// COUNT=2; -> Soit on compte 2 events
	// WKST=SU; -> indique début semaine, utile si BYDAY sur plusieurs jours et
	// interval diff de 1 :
	// si INTERVAL=2;BYDAY=TU,SU;WKST=MO -> on prend les mardi (1x sur 2) puis les
	// dimanches suivants ;
	// si INTERVAL=2;BYDAY=TU,SU;WKST=SU -> on prend les dimanches (1x sur 2) puis
	// les mardis suivants ;
	// BYDAY=-2MO; -> l'avant dernier Lundi
	// BYDAY=1SU,-1SU; -> le premier et le dernier dimanche
	// BYDAY=SU,MO,TU; -> les dimanche, lundi, mardi
	// BYMONTH=3,10; -> 3eme et 10eme mois de l'année
	// BYMONTHDAY=-3 -> avant avant dernier jour du mois
	// BYMONTHDAY=1,-1 -> premier et dernier jour du mois
	// BYYEARDAY=1,100,200 -> jour né1, 100 et 200 de l'annee
	// BYWEEKNO=20 -> semaine né20 de l'annee
	// BYSETPOS=3 avec BYDAY=TU,WE,TH -> on cherche les mardi, mer, jeu et on
	// prend le troisieme trouvé (c'est soit un mardi, soir un mer, soit un jeu);
	// si -2 : l'avant dernier trouvé.
	// BYHOUR=9,10
	// BYMINUTE=0,20,40
	// RDATE;TZID=US-EASTERN:19970714T083000
	// RDATE;VALUE=PERIOD:19960403T020000Z/19960403T040000Z,
	// 19960404T010000Z/PT3H

}
