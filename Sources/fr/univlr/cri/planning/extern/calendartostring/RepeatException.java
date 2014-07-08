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

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;




/**
 * Classe RepeatEvent implémentée avec l'exemple de la classe HolidayEvents de
 * Johan Carlberg
 */

public class RepeatException extends RepeatEvent {

	protected String exRule;

	protected NSArray exDates;

	/* constructeur */

	public RepeatException(String startTime, int endType, String endVal,
			String title, NSArray rDates, String rRules, NSArray exdate, String exrule) {
		super(startTime, endType, endVal, title, rDates, rRules);
		if (exdate != null)
			exDates = exdate;
		if (exrule != null)
			exRule = exrule;
	}

	// Dates qui font exception a la répétition

	public NSArray repeatExDates() {
		NSArray arr = new NSArray();
		arr = repeatDates(exDates, exRule);
		return arr;
	}

	// dates qui font effectivement parti de la repetition
	// (dates répétées - dates exception)

	// a tester
	public NSArray repeatRealDates() {
		NSArray repeat = repeatDates();
		NSArray except = repeatExDates();
		
		System.out.println("repeatRealDates tab repeat : "+repeat.toString());
		System.out.println("repeatRealDates tab except : "+except.toString());
		
		NSMutableArray reponse = new NSMutableArray(repeat);
		// normalement les 2 tab non vides mais au cas ou...
		if (repeat.count() != 0 && except.count() != 0) {

			/*
			 * DEPEND DU FORMAT DES DATES (DATE / DATE-TIME) 
			 * Si exception DATE -> sur une journée 
			 *  On cherche les repeat sur cette journée et les vire 
			 *  Si repeat DATE, 1 possibilité, sinon plusieurs 
			 * Si exception DATE-TIME -> une heure précise
			 *  On cherche ce DATE-TIME précis 1 possibilité sauf si
			 *  TIME diff zero et repeat DATE : 0 possibilités.
			 */

			// hypothése que si date avec heure 00:00 -> DATE
			// sinon DATE-TIME (peu de chance qu'un evenement commence réellement é
			// minuit)
			
			for (int k = 0; k < except.count(); k++) 
			{
			Object o = except.objectAtIndex(k);
			Date d = (Date) o;
			
			// System.out.println("heure : "+d.getHours()+" ; min : "+d.getMinutes());
			// donne 23h et 00m pour date a minuit (format modifie h)
			if (d.getHours() == 23 && d.getMinutes() == 0) // exception DATE
			{
				long debJ = d.getTime();
				long finJ = d.getTime() + 1000 * 60 * 60 * 24; // + 1 jour
				for (int i = 0; i < repeat.count(); i++) {
					Object o2 = repeat.objectAtIndex(i);
					long t = ((Date) o2).getTime();
					if (t >= debJ && t < finJ)
						reponse.removeObject(o2);
				}
			} else // exception DATE-TIME
			{
				long time = d.getTime();
				for (int i = 0; i < repeat.count(); i++) {
					Object o2 = repeat.objectAtIndex(i);
					long t = ((Date) o2).getTime();
					if (t == time)
						reponse.removeObject(o2);
				}
			}
			}
		}
		System.out.println("repeatRealDates tab reponse : "+reponse.toString());
		
		return reponse;
	}

}
