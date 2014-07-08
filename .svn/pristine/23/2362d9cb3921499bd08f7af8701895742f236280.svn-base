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
import java.util.Collections;
import java.util.Date;
import java.util.List;

/***
Code simplifié de Jason Henriksen trouvé sur 'http://sourceforge.net/projects/recurrance/'
. <br>

"This class is the main developer interface to Recurrance.  It actually contains a 
list of RecurranceRule objects and uses them to fill the same set of methods as
RecurranceRule.  The difference is that you may union together more that one rule
using this object.

This class will eventually be able to handle the union and intersection of rules.
For now, it just holds the one rule."

***/

public class Recurrance 
{
  private ArrayList ruleList = null;


  /***
  Creates a recurrance object based on the rule.  The parsers will figure out
  whether the rule is xml or RRULE formated and behave appropriately
  @param rule the rule description
  @param start the inclusive start of the range over which to recurr
  @param end the inclusive end of the range over which to recurr
  ***/
  public Recurrance(String rule, Date start, Date end)
  {
    ruleList = new ArrayList();

//    if(-1 != rule.indexOf("<")){
//      // make a xml rule
//      ruleList.add( new RecurranceRuleXml(rule.trim(),start,end) );
//    }
//    else{
      // make an rfc rule
      ruleList.add( new RecurranceRuleRfc(rule.trim(),start,end) );
    //}

  }

  /***
  Creates a recurrance object based on the rule.  The parsers will figure out
  whether the rule is xml or RRULE formated and behave appropriately.  Uses the given
  time for the start and the start time plus FREQ*2*INTERVAL for the end.
  For example:
    FREQ=YEARLY;INTERVAL=2 will end four years from the start date.
    FREQ=MINUTELY;INTERVAL=5 will end ten minutes from the start date.
  Use this for finding the next when you don't have a set end date, and you don't want to
  get an occurrance for every minute between now and 2045.  (Which would take a long time
  to build up)
  @param rule the rule description
  @param start the inclusive start of the range over which to recurr
  ***/
  public Recurrance(String rule,Date start)
  {
    ruleList = new ArrayList();

//    if(-1 != rule.indexOf("<")){
//      // make a xml rule
//      ruleList.add( new RecurranceRuleXml(rule.trim(),start,null) );
//    }
//    else{
      // make an rfc rule
     ruleList.add( new RecurranceRuleRfc(rule.trim(),start,null) );
    //}

  }



  /***
  Creates a recurrance object based on the rule.  The parsers will figure out
  whether the rule is xml or RRULE formated and behave appropriately.  Uses the current
  time for the start and the current time plus FREQ*2*INTERVAL for the end.
  For example:
    FREQ=YEARLY;INTERVAL=2 will end four years from now.
    FREQ=MINUTELY;INTERVAL=5 will end ten minutes from now.
  Use this for finding the next when you don't have a set end date, and you don't want to
  get an occurrance for every minute between now and 2045.  (Which would take a long time
  to build up)
  @param rule the rule description
  ***/
  public Recurrance(String rule)
  {
    ruleList = new ArrayList();

//    if(-1 != rule.indexOf("<")){
//      // make a xml rule
//      ruleList.add( new RecurranceRuleXml(rule.trim(),null,null) );
//    }
//    else{
      // make an rfc rule
      ruleList.add( new RecurranceRuleRfc(rule.trim(),null,null) );
    //}

  }

  /***
  Returns a List of Date objects which match the getn rule.  You can then create 
  a whatever for each of those dates if you want to.  Note that the start and end
  may be a subset of the rules start and end range.
  @param start the inclusive start of the range over which to return dates
  @param end the inclusive end of the range over which to dates
  @return a list of dates
  ***/
  public List getAllMatchingDatesOverRange(Date start, Date end)
  {
    List results = new ArrayList();
    List all = getAllMatchingDates();
    Collections.sort(all);

    for(int ctr=0;ctr<all.size();ctr++){
      Date cur = (Date)all.get(ctr);
      if( (cur.before(end) && cur.after(start) ) ||
           cur.equals(start) || 
           cur.equals(end) ){
        results.add(cur);
      }
    }

    return results;
  }

  /***
  @return a list of the dates that match the recurrance rule
  ***/
  public List getAllMatchingDates()
  {
    return ((RecurranceRuleRfc)ruleList.get(0)).getAllMatchingDates();
  }

  /***
  @return the rule used to define this recurrance.
  ***/
  public String getRule()
  {
    return ((RecurranceRuleRfc)ruleList.get(0)).getRule();
  }

  /***
  @return true iff the getn date appears as a recurrance in the rule set.
  ***/
  public boolean matches(Date value)
  {
    List all = getAllMatchingDates();
    return all.contains(value);
  }

  /***
  @return the next date in the recurrance after the getn one, or null if there is no
  such date.  Note that even if value is a match, this will return the one after it.
  ***/
  public Date next(Date value)
  {
    List all = getAllMatchingDates();
    Collections.sort(all);

    for(int ctr=0;ctr<all.size();ctr++){
      Date cur = (Date)all.get(ctr);
      if( cur.after( value ) ){
        return cur;
      }
    }
    return null;
  }

  /***
  @return the previous date in the recurrance before the getn one, or null if there 
  is no such date.  Note that even if value is a match, this will return the one 
  before it.
  ***/
  public Date prev(Date value)
  {
    List all = getAllMatchingDates();
    Collections.sort(all);

    for(int ctr=all.size()-1;ctr>-1;ctr--){
      Date cur = (Date)all.get(ctr);
      if( cur.before( value ) ){
        return cur;
      }
    }
    return null;
  }


  /* **
  Unit testing method
  ***/
//  public static void main(String[] args)
//  {
//    System.err.println("\n\nRecurrance:\n ");
//    System.err.println("Using input: "+args[0]);
//
//    long t = System.currentTimeMillis();
//
//    // now until about three years from now
//
//    long end = 31536000000l;
//    end = end * 5;
//    Date e = new Date( t + end );
//
//    System.err.println("start " + new Date(t));
//    System.err.println("end   " + e  );
//
//    Recurrance r = new Recurrance(args[0], new Date(t), e );
//
//  }


  /***
  This is a handy utility method for setting up a calendar from a set of strings  
  ***/
  public static void fastSet(Calendar c,
                             String year,
                             String month,
                             String day,
                             String hour,
                             String minute,
                             String second) throws Exception
  {
    c.set(Integer.parseInt(year),
          Integer.parseInt(month)-1,
          Integer.parseInt(day),
          Integer.parseInt(hour),
          Integer.parseInt(minute),
          Integer.parseInt(second));
  }


}
