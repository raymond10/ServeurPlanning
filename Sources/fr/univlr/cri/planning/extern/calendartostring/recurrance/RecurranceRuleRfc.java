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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.cocktail.fwkcktlwebapp.common.CktlLog;

/****
Code simplifié de Jason Henriksen trouvé sur 'http://sourceforge.net/projects/recurrance/'. 
<br>
"Describes a single recurrance rule.  This object is actually a facade for a number
of smaller objects that a user should never need to worry about."

****/


public class RecurranceRuleRfc 
{

  protected String      freq = null;
  protected int         count = 1;
  protected int         interval=1;
  protected String      weekStart = null;
  protected String      rule = null;

  protected HashSet     secSet = new HashSet();
  protected HashSet     minSet = new HashSet();
  protected HashSet     hourSet = new HashSet();
  protected HashSet     weekDaySet = new HashSet();
  protected HashSet     monthDaySet = new HashSet();
  protected HashSet     yearDaySet = new HashSet();
  protected HashSet     yearSet = new HashSet();
  protected HashSet     weekNumberSet = new HashSet();
  protected HashSet     monthSet = new HashSet();
  protected HashSet     bspSet = null;

  protected ArrayList   resultList = null;

  protected ArrayList   protoDateList = new ArrayList();

  protected Date        start;
  protected Date        end;

  protected boolean foundFreq = false;
  protected boolean foundCount = false;
  protected boolean foundInterval = false;
  protected boolean foundBySecond = false;
  protected boolean foundByMinute = false;
  protected boolean foundByHour = false;
  protected boolean foundByDay = false;
  protected boolean foundByMonthDay = false;
  protected boolean foundByYearDay = false;
  protected boolean foundByWeekNo = false;
  protected boolean foundByMonth = false;

  protected int     offsetUnit = 0;
  protected int     offsetAmount = 0;


  /***
  Creates a recurrance object based on the rule.  The parses will figure out
  whether the rule is xml or RRULE formated and behave appropriately
  @param rule the rule description
  @param start the inclusive start of the range over which to recurr
  @param end the inclusive end of the range over which to recurr
  ***/
  RecurranceRuleRfc(String ruleI, Date startI, Date endI)
  {
    try{

      this.start = startI;
      this.end = endI;
      this.rule = ruleI;

      ArrayList ruleList = new ArrayList();
      StringTokenizer stk = new StringTokenizer(rule,";");

      //===== READ the rules that have been provided and make the meta-sets and instructions 
      //===== that will be needed later.
      while ( stk.hasMoreTokens() ){
        String curRule = stk.nextToken();
        ruleList.add(curRule);
        String key = curRule.substring(0,curRule.indexOf("="));
        String value = curRule.substring(curRule.indexOf("=")+1);
        //println("  "+key+"  =  "+value);

        if ( "FREQ".equals(key) ) {
          //--- no dups
          if ( foundFreq ) throw new IllegalArgumentException("Error: duplicate key");
          else foundFreq = true;
          //--- only valid options
          if ( !Constants.freqValues.contains(value) ) {
            println(">>"+Constants.freqValues);
            throw new IllegalArgumentException("Error: invalid FREQ value");
          }
          //--- accept the rule
          freq = value;
        }
        else if ( "COUNT".equals(key) ) {
          //--- no dups
          if ( foundCount ) throw new IllegalArgumentException("Error: duplicate key");
          else foundCount = true;
          if ( !foundFreq ) throw new IllegalArgumentException("Error: freq must be specified before "+key);
          count = Integer.parseInt(value);
          if ( count<1 ) throw new IllegalArgumentException("Error: COUNT < 1");
        }
        else if ( "INTERVAL".equals(key) ) {
          //--- no dups
          if ( foundInterval ) throw new IllegalArgumentException("Error: duplicate key");
          else foundInterval = true;
          if ( !foundFreq ) throw new IllegalArgumentException("Error: freq must be specified before "+key);
          interval = Integer.parseInt(value);
          if ( interval<1 ) throw new IllegalArgumentException("Error: INTERVAL < 1");
        }
        else if ( "BYSECOND".equals(key) ) {
          //--- no dups
          if ( foundBySecond ) throw new IllegalArgumentException("Error: duplicate key");
          else foundBySecond = true;
          if ( !foundFreq ) throw new IllegalArgumentException("Error: freq must be specified before "+key);
          if ( "SECONDLY".equals(freq) ) throw new IllegalArgumentException("Error: freq=SECONDLY and BYSECOND are mutually exclusive");
          secSet = getNumberSet(value,0,59,key);
        }
        else if ( "BYMINUTE".equals(key) ) {
          //--- no dups
          if ( foundByMinute ) throw new IllegalArgumentException("Error: duplicate key");
          else foundByMinute = true;
          if ( !foundFreq ) throw new IllegalArgumentException("Error: freq must be specified before "+key);
          if ( "MINUTELY".equals(freq) ) throw new IllegalArgumentException("Error: freq=MINUTELY and BYMINUTE are mutually exclusive");
          minSet = getNumberSet(value,0,59,key);
        }
        else if ( "BYHOUR".equals(key) ) {
          //--- no dups
          if ( foundByHour ) throw new IllegalArgumentException("Error: duplicate key");
          else foundByHour = true;
          if ( !foundFreq ) throw new IllegalArgumentException("Error: freq must be specified before "+key);
          if ( "HOURLY".equals(freq) ) throw new IllegalArgumentException("Error: freq=HOURLY and BYHOUR are mutually exclusive");
          hourSet = getNumberSet(value,0,23,key);
        }
        else if ( "BYDAY".equals(key) ) {
          //--- no dups
          if ( foundByDay ) throw new IllegalArgumentException("Error: duplicate key");
          else foundByDay = true;
          if ( !foundFreq ) throw new IllegalArgumentException("Error: freq must be specified before "+key);
          //if ( "DAILY".equals(freq) ) throw new IllegalArgumentException("Error: freq=DAILY and BYDAY are mutually exclusive (try freq=WEEKLY)");
          weekDaySet = getWeekDaySet(value);
        }
        else if ( "BYMONTHDAY".equals(key) ) {
          //--- no dups
          if ( foundByMonthDay ) throw new IllegalArgumentException("Error: duplicate key");
          else foundByMonthDay = true;
          if ( !foundFreq ) throw new IllegalArgumentException("Error: freq must be specified before "+key);
          //if ( "DAILY".equals(freq) ) throw new IllegalArgumentException("Error: freq=DAILY and BYMONTHDAY are mutually exclusive");
          monthDaySet = getNumberSet(value,-31,31,key);
        }
        else if ( "BYYEARDAY".equals(key) ) {
          //--- no dups
          if ( foundByYearDay ) throw new IllegalArgumentException("Error: duplicate key");
          else foundByYearDay = true;
          if ( !foundFreq ) throw new IllegalArgumentException("Error: freq must be specified before "+key);
          //if ( "DAILY".equals(freq) ) throw new IllegalArgumentException("Error: freq=DAILY and BYYEARDAY are mutually exclusive");
          yearDaySet = getNumberSet(value,-366,366,key);
        }
        else if ( "BYWEEKNO".equals(key) ) {
          //--- no dups
          if ( foundByWeekNo ) throw new IllegalArgumentException("Error: duplicate key");
          else foundByWeekNo = true;
          if ( !foundFreq ) throw new IllegalArgumentException("Error: freq must be specified before "+key);
          if ( !"YEARLY".equals(freq) ) throw new IllegalArgumentException("Error: BYWEEKNO is only valid in a FREQ=YEARLY command");
          weekNumberSet = getNumberSet(value,-53,53,key);
        }
        else if ( "BYMONTH".equals(key) ) {
          //--- no dups
          if ( foundByMonth ) throw new IllegalArgumentException("Error: duplicate key");
          else foundByMonth = true;
          if ( !foundFreq ) throw new IllegalArgumentException("Error: freq must be specified before "+key);
          if ( "MONTHLY".equals(freq) ) throw new IllegalArgumentException("Error: freq=MONTHLY and BYMONTH are mutually exclusive");
          monthSet = getNumberSetHoldTheEvil(value,1,12,key);
        }
        else if ( "WKST".equals(key) ) {
          CktlLog.trace("ERROR: WKST not implemented in this release, ignoring ");
        }
        else if ( "UNTIL".equals(key) ) {
        	//CktlLog.trace("'UNTIL' not implemented in this release, value must be insert in 'end' argument.");
        }
        else if ( "BYSETPOS".equals(key) ) {
          //--- remember which items out of the individual frequency sets we want to keep
          bspSet = getNumberSet(value,-366,366,key);
        }
        else if ( "OFFSET".equals(key) ) {
          if(value.endsWith("DATE")){
            offsetUnit = Calendar.DATE;
            offsetAmount = Integer.parseInt( value.substring(0,value.length()-4) );
          }
          else if(value.endsWith("HOUR")){
            offsetUnit = Calendar.HOUR;
            offsetAmount = Integer.parseInt( value.substring(0,value.length()-4) );
          }
          else if(value.endsWith("MIN")){
            offsetUnit = Calendar.MINUTE;
            offsetAmount = Integer.parseInt( value.substring(0,value.length()-3) );
          }
          else {
            throw new IllegalArgumentException("Error: unable to parse OFFSET");
          }
        }
        else{
          throw new IllegalArgumentException("Error: illegal key");
        }

      }

      if ( !foundFreq ) throw new IllegalArgumentException("Error: FREQ is required");

      if ( start == null ) {
        start=new Date();
      }
      
      boolean endNull = false;
      if ( end == null ) {
      	endNull = true;
        Calendar temp = Calendar.getInstance(); 
        temp.setTime(new Date());
        if (count != 1)
        	temp.add(Constants.convertFreqNameToCalendarField(freq),interval*count);
        else // ni count ni until -> infini -> 10x
        	temp.add(Constants.convertFreqNameToCalendarField(freq),interval*10);
        end = temp.getTime();
      }
      
//      System.out.println("recurrance start : "+start.toString()+
//      		"; dateCtrl = "+DateCtrl.dateToString(new NSTimestamp(start.getTime()), SPConstantes.DATE_FORMAT));
//      System.out.println("recurrance end : "+end.toString() +
//      		"; dateCtrl = "+DateCtrl.dateToString(new NSTimestamp(end.getTime()), SPConstantes.DATE_FORMAT));
      
      //===== WALK the dates that are described in the freq/interval.  Also fill in any
      //===== sets that are required at each level
      Calendar cur = Calendar.getInstance();
      cur.clear();
      cur.setLenient(false);
      Calendar limit = Calendar.getInstance();
      limit.clear();
      limit.setLenient(false);
      limit.setTime(end);

      //----------------- Build the year list 
      cur.setTime( start );
     // int val;

      if ( "YEARLY".equals(freq) ){
        //--- build by freq
        protoDateList.addAll( getFullDateValueList(Calendar.YEAR,interval,cur,limit) );
      }

      //----------------- Build the month list 
      cur.setTime( start );
      if ( "MONTHLY".equals(freq) ){
        //--- build by freq
        protoDateList.addAll( getFullDateValueList(Calendar.MONTH,interval,cur,limit) );
      }
      else if ( !foundByMonth ){
        monthSet.add( new Integer( cur.get(Calendar.MONTH) ) ); // adding user fudge
      }

      //----------------- Build the day list  STILL VERY EVIL
      cur.setTime( start );
      if ( "DAILY".equals(freq) ){
        //--- build by freq
        protoDateList.addAll( getFullDateValueList(Calendar.DAY_OF_MONTH,interval,cur,limit) );
      }
      else if ( !foundByDay && !foundByWeekNo && !foundByMonthDay && !foundByYearDay ){
        monthDaySet.add( new Integer( cur.get(Calendar.DAY_OF_MONTH) ) );  
      }

      //----------------- Build the week list     
      cur.setTime( start );
      if ( "WEEKLY".equals(freq) ){
        //--- build by freq
      	CktlLog.log("interval="+interval+" cur="+cur+" limit="+limit);
        protoDateList.addAll( getFullDateValueList(Calendar.WEEK_OF_YEAR,interval,cur,limit) );
        if ( !foundByDay ){
          weekDaySet.add( new ByDay(0,Constants.convertDayNumberToDayName(cur.get(Calendar.DAY_OF_WEEK)) ) );  
        }
      }
      //--- the week from original date is not taken as a default


      //----------------- Build the hour list 
      cur.setTime( start );
      if ( "HOURLY".equals(freq) ){
        //--- build by freq
        protoDateList.addAll( getFullDateValueList(Calendar.HOUR_OF_DAY,interval,cur,limit) );
      }
      else if ( !foundByHour ){
        hourSet.add( new Integer( cur.get(Calendar.HOUR_OF_DAY) ) );  
      }

      //----------------- Build the minute list 
      cur.setTime( start );
      if ( "MINUTELY".equals(freq) ){
        //--- build by freq
        protoDateList.addAll( getFullDateValueList(Calendar.MINUTE,interval,cur,limit) );
      }
      else if ( !foundByMinute ){
        minSet.add( new Integer( cur.get(Calendar.MINUTE) ) );  
      }

      //----------------- Build the second list 
      cur.setTime( start );
      if ( "SECONDLY".equals(freq) ){
        //--- build by freq
        protoDateList.addAll( getFullDateValueList(Calendar.SECOND,interval,cur,limit) );
      }
      else if ( !foundBySecond ){
        secSet.add( new Integer( cur.get(Calendar.SECOND) ) );  
      }


      resultList = new ArrayList();
      Calendar result = Calendar.getInstance();
      result.setLenient(false);
      result.clear();


//      if ( TestRecurrance.debug ){
//        println("pS   "+protoDateList.size());
//        println("yS   "+yearSet.size()+"  "+yearSet);
//        println("mS   "+monthSet.size()+"  "+monthSet);
//        println("mDS  "+monthDaySet.size()+"  "+monthDaySet);
//        println("wDS  "+weekDaySet.size()+"  "+weekDaySet);
//        println("hS   "+hourSet.size()+"  "+hourSet);
//        println("minS "+minSet.size()+"  "+minSet);
//        println("sS   "+secSet.size()+"  "+secSet);
//      }


      //===== CULL the dates that are not acceptable to BYxyz commands which are of a higher
      //===== magnitude than the frequency
      int freqNumber = Constants.convertFreqNameToFreqNumber(freq);

      for ( int pdCtr = protoDateList.size()-1 ; pdCtr>=0 ; pdCtr-- ){
        Calendar curProtoDate = (Calendar)protoDateList.get(pdCtr);

        // BYMONTH
        if ( foundByMonth && freqNumber <= Constants.FREQ_MONTHLY ){
          if ( ! isDateInMetaSet(curProtoDate,Calendar.MONTH,monthSet) ){
            println("remove by month");
            protoDateList.remove(pdCtr);
            continue;
          }
        }
        // BYWEEKNO
        if ( foundByWeekNo && freqNumber <= Constants.FREQ_WEEKLY ){
          if ( ! isDateInMetaSet(curProtoDate,Calendar.WEEK_OF_YEAR,weekNumberSet) ){
            println("remove by week no");
            protoDateList.remove(pdCtr);
            continue;
          }
        }
        // BYYEARDAY
        if ( foundByYearDay && freqNumber <= Constants.FREQ_DAILY ){
          if ( ! isDateInMetaSet(curProtoDate,Calendar.DAY_OF_YEAR,yearDaySet) ){
            println("remove by year day");
            protoDateList.remove(pdCtr);
            continue;
          }
        }
        // BYMONTHDAY
        if ( foundByMonthDay && freqNumber <= Constants.FREQ_DAILY ){
          if ( ! isDateInMetaSet(curProtoDate,Calendar.DAY_OF_MONTH,monthDaySet) ){
            println("remove by month day");
            protoDateList.remove(pdCtr);
            continue;
          }
        }
        // BYDAY - Deeply Evil
        if ( foundByDay && freqNumber <= Constants.FREQ_DAILY ){
          if ( ! isDateInWeekDayMetaSet(curProtoDate,Calendar.DAY_OF_MONTH,weekDaySet) ){
            println("remove by month day");
            protoDateList.remove(pdCtr);
            continue;
          }
        }
        // BYHOUR
        if ( foundByHour && freqNumber <= Constants.FREQ_HOURLY ){
          if ( ! isDateInMetaSet(curProtoDate,Calendar.HOUR_OF_DAY,hourSet) ){
            println("remove by hour");
            protoDateList.remove(pdCtr);
            continue;
          }
        }
        // BYMINUTE
        if ( foundByMinute && freqNumber <= Constants.FREQ_MINUTELY ){
          if ( ! isDateInMetaSet(curProtoDate,Calendar.MINUTE,minSet) ){
            println("remove by minute");
            protoDateList.remove(pdCtr);
            continue;
          }
        }
      }

      //===== SPEW in the dates that are added by using a BYxyz of lower 
      //===== magnitude than the frequency

      HashSet resultSet = new HashSet();

      for ( int pdCtr = protoDateList.size()-1 ; pdCtr>=0 ; pdCtr-- ){
        Calendar curProtoDate = (Calendar)protoDateList.get(pdCtr);

        HashSet resultPile = new HashSet();
        resultPile.add(curProtoDate);

        // BYMONTH
        if ( freqNumber > Constants.FREQ_MONTHLY ){
          resultPile = breedMetaSet(resultPile,Calendar.MONTH,monthSet);
        }
        // BYWEEKNO
        if ( freqNumber > Constants.FREQ_WEEKLY ){
          resultPile = breedMetaSet(resultPile,Calendar.WEEK_OF_YEAR,weekNumberSet);
        }
        // BYYEARDAY
        if ( freqNumber > Constants.FREQ_DAILY && freqNumber != Constants.FREQ_WEEKLY ){
          resultPile = breedMetaSet(resultPile,Calendar.DAY_OF_YEAR,yearDaySet);
        }
        // BYMONTHDAY
        if ( freqNumber > Constants.FREQ_DAILY && freqNumber != Constants.FREQ_WEEKLY ){
          resultPile = breedMetaSet(resultPile,Calendar.DAY_OF_MONTH,monthDaySet);
        }
        // BYDAY - Deeply Evil
        if ( freqNumber > Constants.FREQ_DAILY                  /*&& freqNumber != Constants.FREQ_WEEKLY*/ ){
          resultPile = breedWeekDayMetaSet(resultPile,weekDaySet);
        }
        // BYHOUR
        if ( freqNumber > Constants.FREQ_HOURLY ){
          resultPile = breedMetaSet(resultPile,Calendar.HOUR_OF_DAY,hourSet);
        }
        // BYMINUTE
        if ( freqNumber > Constants.FREQ_MINUTELY ){
          resultPile = breedMetaSet(resultPile,Calendar.MINUTE,minSet);
        }
        // BYSECOND
        if ( freqNumber > Constants.FREQ_SECONDLY ){
          resultPile = breedMetaSet(resultPile,Calendar.SECOND,secSet);
        }
        resultSet.addAll(resultPile);
      }

      println("pS pb "+resultSet.size());

      //--- convert the calendar objects into date objects
      for ( Iterator rsIt = resultSet.iterator(); rsIt.hasNext() ; ){
        Calendar c = (Calendar)rsIt.next();
        resultList.add(c.getTime());
      }

      Collections.sort(resultList);

      //===== ENSURE the limitation on the primary dates
      //--- ensure the bounds
      for ( int rctr=resultList.size()-1;rctr>=0;rctr-- ){
        Date curDate = (Date)resultList.get(rctr);
        if ( ! ( (curDate.before(end) || curDate.equals(end)) && 
                 (curDate.after(start) || curDate.equals(start)) ) ){
          resultList.remove(rctr);
        }
      }
      
//      System.out.println("recurrance resultList 1 : "+resultList.toString());
      

//      if ( TestRecurrance.debug ){
//        println("rS "+resultList.size());
//      }

      //===== FILTER the dates returned
      //--- apply the BYSETPOS filter by examining the dates for each recurrance and culling appropriately
      //--- note that finalList MUST be sorted for this to work.
      resultList = (ArrayList)filterUsingBySetPos(resultList, bspSet, Constants.convertFreqNameToCalendarField(freq) );

      //--- apply the OFFSET filter by altering each date as requested.  NOTE:  This may take dates outside
      //      the start and end range.  (I.E. you'll want a meeting reminder before the start of the first 
      //      meeting)

      //--- ensure the count
      List finalList;
      if (resultList.contains(new Date(startI.getTime())))
      	resultList.remove(new Date(startI.getTime()));
      if ( endNull && count != 1 && resultList.size() > count ){
        finalList = (List)resultList.subList(0,count);
      }
      else{
        finalList = resultList;
      }

      //==== MODIFY the dates returned using OFFSET
      if( offsetAmount != 0 ){
        Calendar modificationTool = Calendar.getInstance();
        for(int ctr=0;ctr<finalList.size();ctr++){
          Date curMod = (Date)finalList.get(ctr);
          modificationTool.setTime(curMod);
          modificationTool.add(offsetUnit, offsetAmount);
          curMod = modificationTool.getTime();
          finalList.set(ctr,curMod);
        }
      }
      resultList = new ArrayList(finalList);
      
//      System.out.println("recurrance resultList 2 : "+resultList.toString());
    }
    catch ( RuntimeException e ){
      System.err.println(rule);
      throw e;
    }

  }

  /* **
  Returns a List of Date objects which match the given rule.  You can then create 
  a whatever for each of those dates if you want to.  Note that the start and end
  may be a subset of the rules start and end range.
  @param start the inclusive start of the range over which to return dates
  @param end the inclusive end of the range over which to dates
  @return a list of dates
  ***/
//  public List getAllMatchingDatesOverRange(Date start, Date end)
//  {
//    return null;
//  }


  /***
  returns a list of all the dates that matched.
  ***/
  public List getAllMatchingDates()
  {
    return resultList;
  }


  /* **
  @return true iff the given date appears as a recurrance in the rule set.
  ***/
//  public boolean matches(Date value)
//  {
//    return false;
//  }


  /* **
  @return the next date in the recurrance after the getn one, or null if there is no
  such date.  Note that even if value is a match, this will return the one after it.
  ***/
//  public Date next(Date value)
//  {
//    return null;
//  }

  /* **
  @return the previous date in the recurrance before the getn one, or null if there 
  is no such date.  Note that even if value is a match, this will return the one 
  before it.
  ***/
//  public Date prev(Date value)
//  {
//    return null;
//  }

  /* **
  @return the rule used to create this object
  ***/
//  public String getRuleAsRRule()
//  {
//    return rule;
//  }







  //==================== Utilities
  /***
  split a list into validated numbers
  ***/
  private HashSet getNumberSet(String list, int low, int high, String key)
  {
    HashSet result = new HashSet();
    StringTokenizer stk = new StringTokenizer(list,",");
    while ( stk.hasMoreTokens() ){
      int cur = Integer.parseInt(stk.nextToken());
      //--- be in range
      if ( cur<low || cur>high ){
        throw new IllegalArgumentException("ERROR: invalid integer list: "+key);
      }
      //--- if low != 0 then 0 is disallowed
      if ( low!=0 && cur==0 ){
        throw new IllegalArgumentException("ERROR: zero is not allowed in "+key);
      }
      result.add( new Integer(cur) );
    }
    return result;
  }

  /***
  Use this for getting months, when the user gives it to you 1 based, but java wants it 0 based.
  ***/
  private HashSet getNumberSetHoldTheEvil(String list, int low, int high, String key)
  {
    HashSet old = getNumberSet(list, low, high, key);
    HashSet result = new HashSet();

    for ( Iterator it = old.iterator();it.hasNext(); ){
      result.add( new Integer( ((Integer)it.next()).intValue()-1 ) );
    }

    return result;

  }

  /***
  split a list into validated ByDay objects
  ***/
  private HashSet getWeekDaySet(String list)
  {
    HashSet result = new HashSet();
    StringTokenizer stk = new StringTokenizer(list,",");
    while ( stk.hasMoreTokens() ){
      ByDay bd = new ByDay(stk.nextToken());
      if ( bd.count != 0 && 
           !( "MONTHLY".equals(freq) || "YEARLY".equals(freq) ) ) {
        throw new IllegalArgumentException("  ERROR: You can only specify a number with a week day in the YEARLY or MONTHLY frequencies.");
      }
      result.add( bd );
    }
    return result;
  }



  /***
  Gets all of the valid units over a span of time with respect to the interval.
  This is only used for finding frequencies.
  @param unit a Calendar constant such as YEAR, DAY_OF_MONTH, HOUR, etc
  @param interval the interval to jump across. 1 => every, 2 => every other, etc
  @param cur the start of the range
  @param end the end of the range.
  ***/
//  private List getValueList(int unit, int interval, Calendar inputCur, Calendar inputEnd)
//  {
//    Calendar cur = roundDown(unit,(Calendar)inputCur.clone());
//    Calendar end = roundDown(unit,(Calendar)inputEnd.clone());
//
//    int val = cur.get(unit);
//
//    ArrayList resultList = new ArrayList();
//    //println("val "+val);
//    resultList.add(new Integer(val));
//
//    cur.add(unit, interval);
//
//    while ( cur.getTime().before(end.getTime()) ||
//            cur.getTime().equals(end.getTime()) ){              // while not past the end date
//      val = cur.get(unit);
//      //println("val "+val);
//      resultList.add(new Integer(val));
//      cur.add(unit, interval);
//    }
//
//    return resultList;
//  }

  /***
  Gets all of the valid units over a span of time with respect to the interval.
  This is only used for finding frequencies.
  @param unit a Calendar constant such as YEAR, DAY_OF_MONTH, HOUR, etc
  @param interval the interval to jump across. 1 => every, 2 => every other, etc
  @param cur the start of the range
  @param end the end of the range.
  ***/
  private List getFullDateValueList(int unit, int interval, Calendar inputCur, Calendar inputEnd)
  {
    Calendar cur = roundDown(unit,(Calendar)inputCur.clone());
    Calendar end = roundDown(unit,(Calendar)inputEnd.clone());

    println("Round Down  "+cur.getTime());
    println("Round Up    "+end.getTime());
    //int val = cur.get(unit);

    ArrayList resultList = new ArrayList();
    resultList.add( cur.clone() );
    println("val "+cur.getTime());

    cur.add(unit, interval);

    while ( cur.getTime().before(end.getTime()) ||
            cur.getTime().equals(end.getTime()) ){              // while not past the end date
      println("val "+cur.getTime());
      resultList.add( cur.clone() );
      cur.add(unit, interval);
    }

    return resultList;
  }


  /***
  Round down a date.  i.e. if I have 1998/03/29 at 13:30 and I round at the 
  MONTH level, I'll get 1998/03/01 at 00:00
  @param unRounded a calendar value
  @return a rounded calendar value
  ***/
  private Calendar roundDown(int unit,Calendar unRounded)
  {
    Calendar result = (Calendar)unRounded.clone();
    //println("pre "+result.getTime());

    if ( unit < Calendar.YEAR ) result.set(Calendar.YEAR,0);
    if ( unit < Calendar.MONTH ) result.set(Calendar.MONTH,0);
    if ( unit < Calendar.DAY_OF_MONTH ) result.set(Calendar.DAY_OF_MONTH,1);
    if ( unit < Calendar.HOUR_OF_DAY ) result.set(Calendar.HOUR_OF_DAY,0);
    if ( unit < Calendar.MINUTE ) result.set(Calendar.MINUTE,0);
    if ( unit < Calendar.SECOND ) result.set(Calendar.SECOND,0);

    if ( unit == Calendar.WEEK_OF_YEAR ) result.set(Calendar.WEEK_OF_YEAR,unRounded.get(Calendar.WEEK_OF_YEAR));

    //println("post "+result.getTime());
    return result;
  }



  /***
  This one is pretty specific.  Say you have a set of day_in_month values.  However some of the
  values are negative. (i.e. -3 meaning the third to the last day of the month)  This method makes
  a new set that replaces the negative values with the appropriate positive values, then checks that
  the date given is actually in the list of acceptable values.  If it is, it returns true.
  @param curProtoDate a date under consideration for inclusion
  @param unit the unit of interest DAY_OF_MONTH, WEEK_OF_YEAR, DAY_OF_WEEK, etc
  @param metaSet the set potentially containing negative values
  @return true iff the date is part of the meta set
  ***/
  private boolean isDateInMetaSet(Calendar curProtoDate , int unit , HashSet metaSet)
  {
    Integer cur = new Integer( curProtoDate.get(unit) );
    HashSet locallyValidSet = (HashSet)metaSet.clone();

    int localMax = curProtoDate.getActualMaximum(unit);
    int modVal;

    for ( Iterator it = locallyValidSet.iterator() ; it.hasNext() ; ){
      modVal = ((Integer)it.next()).intValue();
      if ( modVal<0 ){
        //-1 implies the last item in the group, so the +1 makes it all work out.
        locallyValidSet.add( new Integer( modVal+localMax+1 ) );
      }
    }

    if ( !locallyValidSet.contains(cur) ){
      return false;
    }

    return true;
  }

  /***
  Deep magic from before the beginning of time.  
  The values in the metaSet must be ByDay values.  Then depending on the presence or non presence of the
  count the field and also on the frequency value, this method will return false for dates that do not 
  match the expanded meta set.
  @param curProtoDate a date under consideration for inclusion
  @param unit the unit of interest DAY_OF_MONTH, WEEK_OF_YEAR, DAY_OF_WEEK, etc
  @param metaSet the set potentially containing negative values
  @return true iff the date is part of the meta set
  ***/
  private boolean isDateInWeekDayMetaSet(Calendar curProtoDate , int unit , HashSet metaSet)
  {
    for ( Iterator it = metaSet.iterator() ; it.hasNext() ; ){
      ByDay bd  = (ByDay)it.next();

      if ( bd.count==0 ){
        //--- any correct day of the week will do
        if ( curProtoDate.get(Calendar.DAY_OF_WEEK) == Constants.convertDayNameToDayNumber(bd.weekday) ){
          return true;
        }
      }
      else{
        Calendar hunter = huntDayOfWeek(curProtoDate, bd);
        if ( hunter.get(Calendar.DAY_OF_YEAR) == curProtoDate.get(Calendar.DAY_OF_YEAR) ){
          return true;
        }
      }
    }
    //--- we can only get here if none of the previous items matched and returned true.
    return false;

  }

  /***
  Hunts for the n first or last count of a weekday within a Calendar.MONTH or a Calendar.YEAR
  as described by the ByDay object.
  @param curProtoDate the date we are looking relative two.  (gives the month/year of interest)
  @param unit either Calendar.MONTH or Calendar.YEAR depending on the current
  ***/
  private Calendar huntDayOfWeek(Calendar curProtoDate, 
                                 ByDay bd)
  {
    Calendar hunter = (Calendar)curProtoDate.clone();
    //println("hunting");

    // figure out if we're talking months or years.
    int unit = Calendar.DAY_OF_MONTH;
    if ( "YEARLY".equals(freq) ){
      unit = Calendar.DAY_OF_YEAR;
    }
    //println("hunting: unit "+unit);

    // figure out what direction we're going
    int start = 1; 
    int limit = curProtoDate.getActualMaximum(unit);
    int direction = 1;
    if ( bd.count < 0 ){
      start = limit;
      limit = 1;
      direction = -1;
    }
    //println("hunting: start "+start);
    //println("hunting: limit "+limit);

    // figure out day we want
    int goalDow = Constants.convertDayNameToDayNumber(bd.weekday);

    // spin through the days and hunt for the date of the requested week day
    hunter.set(unit,start);
    int foundCount = 0;
    int safety = 0;
    while ( foundCount < Math.abs(bd.count) ) {
      if ( hunter.get(Calendar.DAY_OF_WEEK) == goalDow ){
        foundCount++;
      }
      hunter.add(unit,direction);
      safety++;
      if ( safety>366 ){
        // this should never happen.  :]
        throw new IllegalArgumentException("  ERROR: This loop has spun out of control. Either the week code you entered is bad, or you have asked for too large of an ofset");
      }
    }
    // undo the for-loop fudging and return the value
    hunter.add(unit,-1*direction);
    //println("hunting: found count "+foundCount);
    //println("hunting: returning "+hunter.getTime());

    return hunter;
  }



  /***
  This one is pretty specific.  Say you have a set of day_in_month values.  However some of the
  values are negative. (i.e. -3 meaning the third to the last day of the month)  This method makes
  a new set that replaces the negative values with the appropriate positive values, then multiplies all
  of the entries in the original set by the entries in the meta set.  The resulting hash of entries is 
  returned to the user
  @param origSet the set of original values
  @param unit the unit of interest DAY_OF_MONTH, WEEK_OF_YEAR, DAY_OF_WEEK, etc
  @param metaSet the set potentially containing negative values
  @return the now more populous set
  ***/
  private HashSet breedMetaSet(HashSet origSet , int unit , HashSet metaSet)
  {
    if ( metaSet==null || metaSet.size()==0 ) {
      return origSet;
    }

    HashSet result = new HashSet();

    for ( Iterator oit = origSet.iterator() ; oit.hasNext() ; ){
      Calendar curProtoDate = (Calendar)oit.next();

      int localMax = curProtoDate.getActualMaximum(unit);
      int modVal;

      for ( Iterator it = metaSet.iterator() ; it.hasNext() ; ){
        modVal = ((Integer)it.next()).intValue();
        if ( modVal<0 ){
          //-1 implies the last item in the group, so the +1 makes it all work out.
          modVal = modVal+localMax+1;
        }
        Calendar newDate = (Calendar)curProtoDate.clone();
        newDate.set(unit,modVal);
        result.add(newDate);
      }
    }

    return result;
  }


  /***
  Deep magic.  This method takes a given date and using the meta set of weekDays, it finds the proper
  week day or days that are being discussed and puts those days into the result set to be returned.
  @param origSet the set of original values
  @param metaSet the set ByDay values describing the week days of interest
  @return the now more populous set
  ***/
  private HashSet breedWeekDayMetaSet(HashSet origSet , HashSet metaSet)
  {
    println("in bwdms");
    if ( metaSet==null || metaSet.size()==0 ) {
      return origSet;
    }

    int unit = Calendar.MONTH;
    if ( "YEARLY".equals(freq) ){
      unit = Calendar.YEAR;
    }
    println("in bwdms: unit = "+unit);

    HashSet result = new HashSet();

    for ( Iterator oit = origSet.iterator() ; oit.hasNext() ; ){
      Calendar curProtoDate = (Calendar)oit.next();

      for ( Iterator it = metaSet.iterator() ; it.hasNext() ; ){
        ByDay bd  = (ByDay)it.next();

        if ( bd.count==0 ){
          println("in bwdms: finding all");
          // they want all occurrences
          ByDay localbd = new ByDay(1,bd.weekday);


          Calendar str = Calendar.getInstance();
          str.setTime(start);
          Calendar hunter = huntDayOfWeek( str , localbd);

          // some of the other BYxyz commands may limit the scope of this breeding.  
          // figure that out here
          //int localUnit = unit;
          boolean needSameMonth = false;
          boolean needSameWeek = false;
          boolean needSameDay = false;
          if ( foundByMonth || "MONTHLY".equals(freq) ) needSameMonth = true;
          //if ( foundByMonthDay || foundByWeekNo || foundByYearDay || "WEEKLY".equals(freq) ) needSameWeek = true;
          if ( foundByWeekNo || "WEEKLY".equals(freq) ) needSameWeek = true;
          if ( foundByMonthDay || foundByYearDay || "DAILY".equals(freq) ) needSameDay = true;

          println("nsm "+needSameMonth);
          println("nsw "+needSameWeek);
          println("nsd "+needSameDay);

          while ( hunter.getTime().before(end) ){               //JJHNOTE
            if ( hunter.get(Calendar.YEAR) == curProtoDate.get(Calendar.YEAR) ){
              if ( !needSameMonth || ( needSameMonth && hunter.get(Calendar.MONTH) == curProtoDate.get(Calendar.MONTH) ) ){
                if ( !needSameWeek || ( needSameWeek && hunter.get(Calendar.WEEK_OF_YEAR) == curProtoDate.get(Calendar.WEEK_OF_YEAR)) ){
                  if ( !needSameDay || ( needSameDay && hunter.get(Calendar.DAY_OF_YEAR) == curProtoDate.get(Calendar.DAY_OF_YEAR)) ){
                    result.add( hunter.clone() );
                  }
                }
              }
            }
            hunter.add( Calendar.WEEK_OF_YEAR, 1);
          }

        }
        else{
          println("in bwdms: finding specific");
          // they want a specific count
          result.add( huntDayOfWeek(curProtoDate, bd) );
        }
      }
    }

    return result;
  }


  /***
  @param finalList a SORTED ArrayList of Date objects
  ***/
  public List filterUsingBySetPos(List inputList, HashSet bspSet, int freqNumber)
  {
    println("in bsp");
    //--- sanity checking
    if(inputList == null) return null;
    if(bspSet == null) return inputList;

    //--- holding areas
    ArrayList newResultList = new ArrayList();
    ArrayList curList = new ArrayList();


    //--- set up for the loop by holding the current unit value
    Calendar curCal = Calendar.getInstance();
    curCal.setTime( (Date)inputList.get(0) );
    int curVal = curCal.get(freqNumber);
    //ArrayList curSet = new ArrayList();

    //--- for each date in the sorted list
    for(int ctr = 0; ctr<inputList.size() ; ctr++){
      curCal.setTime( (Date)inputList.get(ctr) );

      if(curCal.get(freqNumber) == curVal){
        //--- if we're still in the same set, add it to the current set
        curList.add((Date)inputList.get(ctr));
      }
      else{
        //--- if we've found a new set, process the old one  JJHNOTE
        newResultList.addAll( getSetPosItems(bspSet,curList) );
        curList.clear();
        curList.add((Date)inputList.get(ctr));
        curVal = curCal.get(freqNumber);
      }

    }
    //--- process the last set
    newResultList.addAll( getSetPosItems(bspSet,curList) );

    return newResultList;
  }

  public ArrayList getSetPosItems(HashSet controlSet, ArrayList values)
  {
    println("in getSetPosItems");
    ArrayList resultList = new ArrayList();
    Integer curInt = null;

    Iterator it = controlSet.iterator();
    while( it.hasNext() ){
      curInt = (Integer)it.next();
      int i = curInt.intValue();
      try{
        if(i>=0){
          //-- 1 is the first, not 0
          resultList.add( values.get( i-1 ) );
        }
        else{
          //-- take the nth to the last
          resultList.add( values.get( values.size() + i ) );
        }
      }
      catch(IndexOutOfBoundsException ioobe){
        //--- this may happen as a matter of normal work
      }
    }

    return resultList;
  }



  /* **
  Print to sysout if the test harness has debug turned on.
  @param text what to print
  ***/
  protected static void println(String text)
  {
//    if ( TestRecurrance.debug ){
//      System.out.println(text);
//    }
  }

  public String getRule()
  {
    return rule;
  }

}
