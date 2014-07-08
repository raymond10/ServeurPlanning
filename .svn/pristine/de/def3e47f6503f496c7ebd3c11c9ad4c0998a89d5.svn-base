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
package fr.univlr.cri.conges.utils;


import java.util.Calendar;
import java.util.GregorianCalendar;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSNumberFormatter;
import com.webobjects.foundation.NSTimestamp;

/**
 * @author ctarade
 * 
 * To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class TimeCtrl {

  // GET MINUTES : Retourne le nombre de minutes correspondant Ž la chaine string au format %H:%M (l'inverse de stringFor: )
  public static int getMinutes(String chaine) {
    NSArray str = NSArray.componentsSeparatedByString(chaine, ":");
    int nombre = 0;

    if ((chaine == null) || ("00:00".equals(chaine)) || ("".equals(chaine)) || ("..:..".equals(chaine)))
      return 0;

    if (chaine.length() == 0)
      return 0;

    if (str.count() == 1)
      nombre = ((Number) str.objectAtIndex(0)).intValue() * 60;
    else {
      if ((((Number) new Integer((String) str.objectAtIndex(0))).intValue()) < 0)
        nombre = (-((((Number) new Integer((String) str.objectAtIndex(0))).intValue()) * 60) + ((((Number) new Integer((String) str.objectAtIndex(1)))
            .intValue())));
      else
        nombre = (((((Number) new Integer((String) str.objectAtIndex(0))).intValue()) * 60) + ((((Number) new Integer((String) str.objectAtIndex(1)))
            .intValue())));
    }

    if ((((Number) new Integer((String) str.objectAtIndex(0))).intValue()) < 0)
      nombre = -nombre; // on a passe une valeur negative

    return nombre;
  }

  // STRING FOR MINUTES
  // Formatte le nombre de minutes en une chaine au format %H:%M (l'inverse de numberOfMinutesFor: )
  public static String stringForMinutes(int numberOfMinutes) {
    String formattedString;
    int hours, minutes;
    boolean negatif = false;

    if (numberOfMinutes == 0)
      return "00:00";

    if (numberOfMinutes < 0) {
      negatif = true;
      numberOfMinutes = -numberOfMinutes;
    }

    hours = numberOfMinutes / 60;
    minutes = numberOfMinutes % 60;

    if (hours < 10)
      formattedString = "0" + hours;
    else
      formattedString = "" + hours;

    if (minutes < 10)
      formattedString = formattedString + ":0" + minutes;
    else
      formattedString = formattedString + ":" + minutes;

    if (negatif)
      formattedString = "-" + formattedString;

    return formattedString;
  }

  /**
   * retourne le nombre de minutes ecoulŽes dans la journŽe
   * 
   * @param date
   * @return
   */
  public static int getMinutesOfDay(NSTimestamp aDate) {

    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(aDate);
    return calendar.get(Calendar.MINUTE) + calendar.get(Calendar.HOUR_OF_DAY) * 60;

  }

  /**
   * mettre une date a minuit (le jour meme)
   * 
   * @param aDate
   * @return
   */
  public static NSTimestamp dateToMinuit(NSTimestamp aDate) {
    GregorianCalendar nowGC = new GregorianCalendar();
    nowGC.setTime(aDate);
    return aDate.timestampByAddingGregorianUnits(0, 0, 0, -nowGC.get(GregorianCalendar.HOUR_OF_DAY), -nowGC.get(GregorianCalendar.MINUTE), -nowGC
        .get(GregorianCalendar.SECOND));
  }

  /**
   * permet de transformer une heure en durŽe (remplace le : par un h
   * 
   * @param heure
   * @return
   */
  public static String stringHeureToDuree(String heure) {
    if (heure != null)
      return heure.replace(':', 'h');
    else
      return "00h00";
  }

  /**
   * permet de transformer une durŽe en heure (remplace le h par un :
   * 
   * @param heure
   * @return
   */
  public static String stringDureeToHeure(String heure) {
    if (heure != null)
      return heure.replace('h', ':');
    else
      return "00:00";
  }

  // TODO controler si null ou po
  public static int getHeuresFromString(String heuresMinutes) {
    return (int) ((float) TimeCtrl.getMinutes(heuresMinutes) / (float) 60);
  }

  public static int getMinutesFromString(String heuresMinutes) {
    return TimeCtrl.getMinutes(heuresMinutes) % 60;
  }

  public static String to_duree_en_jours(int minutes, String dureeJour) {
    String to_duree = "";
    double lesJours = (double) minutes / (double) (TimeCtrl.getMinutes(TimeCtrl.stringDureeToHeure(dureeJour)));
    Double unNombre = new Double(lesJours);
    NSNumberFormatter numberFormat = new NSNumberFormatter("0.00");
    to_duree += numberFormat.format(unNombre) + "j";
    return to_duree;
  }
}
