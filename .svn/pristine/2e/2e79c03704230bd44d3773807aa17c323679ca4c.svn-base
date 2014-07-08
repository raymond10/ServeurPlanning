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

/**
Code simplifié de Jason Henriksen trouvé sur 'http://sourceforge.net/projects/recurrance/'
.<br>

"By night one way, BYDAY another...
Represents a day of the week and OPTIONALLY which count of that day of the week.
For values of count:
	-N means the Nth last given week day in the unit of measure
	 0 means all given week days in the unit of measure
	+N means the Nth first given week day in the unit of measure
If you think this should be a bean, I encourage you to suck on one."

***/

public class ByDay
{
  public ByDay(int count, String weekday)
  {
    this.count = count;
    this.weekday = weekday;
  }


  public ByDay(String descriptor)
  {
    //--- get the week
    weekday = descriptor.substring(descriptor.length()-2);
    if(!Constants.weekDayValues.contains(weekday)){
      throw new IllegalArgumentException("Error: "+weekday+" is not a valid weekday! Use capitolized two letter codes only");
    }

    //--- get the count
    if(descriptor.length() > 2){
      count = Integer.parseInt( descriptor.substring(0,descriptor.length()-2));
      if ( count>53 || count<-53 ){
        throw new IllegalArgumentException("Error: You have asked for an illegal number of weekdays");
      }
    }
  }

  public int     count = 0;
  public String  weekday = null;

  public String toString()
  {
    return "["+count+":"+weekday+"]";
  }
}




