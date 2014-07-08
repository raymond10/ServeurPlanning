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

package fr.univlr.cri.planning.extern.calendartostring.recurrance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;


/**
 * 
 * Code simplifié de Jason Henriksen trouvé sur 'http://sourceforge.net/projects/recurrance/'.
 *
 */

public class Constants
{
  public static HashSet freqValues;
  public static HashSet weekDayValues;

  public static ArrayList allMonths = new ArrayList();
  public static ArrayList allYearDays = new ArrayList();
  public static ArrayList allMonthDays = new ArrayList();
  public static ArrayList allWeekDays = new ArrayList();
  public static ArrayList allYearWeeks = new ArrayList();
  public static ArrayList allHours = new ArrayList();
  public static ArrayList allMinutes = new ArrayList();
  public static ArrayList allSeconds = new ArrayList();


  static{
    //--- valid frequencies
    freqValues = new HashSet();
    freqValues.add("SECONDLY");
    freqValues.add("MINUTELY");
    freqValues.add("HOURLY");
    freqValues.add("DAILY");
    freqValues.add("WEEKLY");
    freqValues.add("MONTHLY");
    freqValues.add("YEARLY");

    //--- valid days of the week
    weekDayValues = new HashSet();
    weekDayValues.add("SU");
    weekDayValues.add("MO");
    weekDayValues.add("TU");
    weekDayValues.add("WE");
    weekDayValues.add("TH");
    weekDayValues.add("FR");
    weekDayValues.add("SA");

    int ctr;

    for(ctr=0;ctr<12;ctr++) allMonths.add( new Integer(ctr) );  // months are irritating
    for(ctr=1;ctr<367;ctr++) allYearDays.add( new Integer(ctr) ); // 366 days possible!
    for(ctr=1;ctr<32;ctr++) allMonthDays.add( new Integer(ctr) );
    for(ctr=1;ctr<8;ctr++) allWeekDays.add( new Integer(ctr) );
    for(ctr=1;ctr<54;ctr++) allYearWeeks.add( new Integer(ctr) ); // week 53 possible!
    for(ctr=0;ctr<24;ctr++) allHours.add( new Integer(ctr) );
    for(ctr=0;ctr<60;ctr++) allMinutes.add( new Integer(ctr) );
    for(ctr=0;ctr<60;ctr++) allSeconds.add( new Integer(ctr) );

  }

  public  static final int      FREQ_SECONDLY = 1;
  public  static final int      FREQ_MINUTELY = 2;
  public  static final int      FREQ_HOURLY   = 3;
  public  static final int      FREQ_DAILY    = 4;
  public  static final int      FREQ_WEEKLY   = 5;
  public  static final int      FREQ_MONTHLY  = 6;
  public  static final int      FREQ_YEARLY   = 7;

  public static int convertFreqNameToFreqNumber(String freq)
  {
    if("SECONDLY".equals(freq)){return FREQ_SECONDLY;}
    if("MINUTELY".equals(freq)){return FREQ_MINUTELY;}
    if("HOURLY".equals(freq)){return FREQ_HOURLY;}
    if("DAILY".equals(freq)){return FREQ_DAILY;}
    if("WEEKLY".equals(freq)){return FREQ_WEEKLY;}
    if("MONTHLY".equals(freq)){return FREQ_MONTHLY;}
    if("YEARLY".equals(freq)){return FREQ_YEARLY;}
    throw new IllegalArgumentException("  ERROR: "+freq+" is not a valid FREQ type");
  }

  public static int convertFreqNameToCalendarField(String freq)
  {
    if("SECONDLY".equals(freq)){return Calendar.SECOND;}
    if("MINUTELY".equals(freq)){return Calendar.MINUTE;}
    if("HOURLY".equals(freq)){return Calendar.HOUR;}
    if("DAILY".equals(freq)){return Calendar.DAY_OF_MONTH;}
    if("WEEKLY".equals(freq)){return Calendar.WEEK_OF_YEAR;}
    if("MONTHLY".equals(freq)){return Calendar.MONTH;}
    if("YEARLY".equals(freq)){return Calendar.YEAR;}
    throw new IllegalArgumentException("  ERROR: "+freq+" is not a valid FREQ type");
  }

  public static int convertDayNameToDayNumber(String day)
  {
    if("SU".equals(day)){return Calendar.SUNDAY;}
    if("MO".equals(day)){return Calendar.MONDAY;}
    if("TU".equals(day)){return Calendar.TUESDAY;}
    if("WE".equals(day)){return Calendar.WEDNESDAY;}
    if("TH".equals(day)){return Calendar.THURSDAY;}
    if("FR".equals(day)){return Calendar.FRIDAY;}
    if("SA".equals(day)){return Calendar.SATURDAY;}
    throw new IllegalArgumentException("  ERROR: "+day+" is not a valid week day");
  }

  public static String convertDayNumberToDayName(int day)
  {
    if(Calendar.SUNDAY==day){return "SU";}
    if(Calendar.MONDAY==day){return "MO";}
    if(Calendar.TUESDAY==day){return "TU";}
    if(Calendar.WEDNESDAY==day){return "WE";}
    if(Calendar.THURSDAY==day){return "TH";}
    if(Calendar.FRIDAY==day){return "FR";}
    if(Calendar.SATURDAY==day){return "SA";}
    throw new IllegalArgumentException("  ERROR: "+day+" is not a valid week day number");
  }


}


