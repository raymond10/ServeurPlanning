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
package fr.univlr.cri.planning.extern.stringtocalendar;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;

import fr.univlr.cri.planning.SPConstantes;

/**
 * SimpleEvent is an simple implementation of an event class,
 * implementing the {@link CalendarEvent CalendarEvent interface},
 * for use by {@link PublishCalendarPage the PublishCalendarPage component}.
 * <p>
 * SimpleEvent objects can be created corresponding to your events
 * and added to PublishCalendarPage to create a calendar.
 * <p>
 * Subclass SimpleEvent if more advanced features are needed.
 *
 * @author 	Johan Carlberg
 * @version 	1.0, 2002-09-30
 */

public class AnEvent extends Object implements CalendarEvent {
    protected NSTimestamp endTime;
    protected NSTimestamp startTime;
    protected String status;
    protected String summary;
    protected String uniqueId;
    protected boolean wholeDay;
    
    // ajout
    protected String comment = null;
    protected String classe;

    /**
     * @param	aStartTime	start time of this event
     * @param	anEndTime	end time of this event
     * @param	aSummary	summary or textual description of this
     *				event
     * @param	aUniqueId	a persistent, globally unique identifier
     *				for this event
     * @see	CalendarEvent#startTime
     * @see	CalendarEvent#endTime
     * @see	CalendarEvent#summary
     * @see	CalendarEvent#uniqueId
     */
    public AnEvent (NSTimestamp aStartTime, NSTimestamp anEndTime, String aSummary, String aUniqueId, String details, String aclasse) {
	startTime = aStartTime;
	endTime = anEndTime;
	summary = aSummary;
	uniqueId = aUniqueId;
	if (details != null)
		comment = details;
	if (aclasse != null && aclasse.equals(SPConstantes.AFF_PRIVEE))
		classe = aclasse;
    }

    /**
     * @return	always returns <code>false</code>.
     *		SimpleEvent doesn't support whole-day events.
     * @see     CalendarEvent#wholeDay
     */
    public boolean wholeDay() {
	return false;
    }

    /**
     * @return  the start time of this event as specified in the
     * {@link #SimpleEvent(NSTimestamp, NSTimestamp, String, String) constructor}
     * @see     CalendarEvent#startTime
     */
    public NSTimestamp startTime() {
	return startTime;
    }

    /**
     * @return  the end time of this event as specified in the
     * {@link #SimpleEvent(NSTimestamp, NSTimestamp, String, String) constructor}
     * @see     CalendarEvent#endTime
     */
    public NSTimestamp endTime() {
	return endTime;
    }

    /**
     * @return  the change counter of this event.
     *		Computed from the current time, and will increase every
     *		ten seconds.
     * @see     CalendarEvent#sequence
     */
    public int sequence() {
	return (int)(new NSTimestamp().getTime() / 10000);
    }

    /**
     * @return  <code>null</code> since SimpleEvent doesn't support
     *		event status.
     * @see     CalendarEvent#status
     */
    public String status() {
	return status;
    }

    /**
     * @return  the summary of this event as specified in the
     * {@link #SimpleEvent(NSTimestamp, NSTimestamp, String, String) constructor}
     * @see     CalendarEvent#summary
     */
    public String summary() {
	return escapedString(summary);
    }
    
    /**
     * @return  the unique id of this event as specified in the
     * {@link #SimpleEvent(NSTimestamp, NSTimestamp, String, String) constructor}
     * @see     CalendarEvent#uniqueId
     */
    public String uniqueId() {
	return escapedString(uniqueId);
    }
    
    // ajout 
    public String comment() {
    	return escapedString(comment);
        }

    /**
     * @return  the class of this event as specified in the
     * {@link #SimpleEvent(NSTimestamp, NSTimestamp, String, String) constructor}
     * @see     CalendarEvent#classe
     */
    public String classe() {
	return classe;
    }
    
    
    /**
     * @return  0 indicating a non-repeating event. SimpleEvent doesn't
     *		support repeating events.
     */
    public int repeatFrequency() {
	return 0;
    }

    /**
     * @return  1 indicating a one-time event (although this method is
     *		never called since {@link #repeatFrequency} always return 0).
     * @see     #repeatFrequency
     */
    public int repeatCount() {
	return 1;
    }

    /**
     * @return  0 indicating unspecified day of week (although this
     *		method is never called since {@link #repeatFrequency}
     *		always returns 0).
     * @see	#repeatFrequency
     */
    public int repeatDayOfWeek() {
	return 0;
    }

    /**
     * @return  0 indicating unspecified day of week in month
     *		(although this method is never called since
     *		{@link #repeatFrequency} always returns 0).
     * @see	#repeatFrequency
     */
    public int repeatDayOfWeekInMonth() {
	return 0;
    }

    /**
     * @return  <code>null</code> indicating unspecified days
     *		within a month (although this method is never
     *		called since {@link #repeatFrequency} always
     *		returns 0.
     * @see	#repeatFrequency
     */
    public NSArray repeatDaysOfMonth() {
	return null;
    }
    
    /**
     * @return	backslash escaped text string, with special characters
     *		replaced with its backslash escaped equivalent.
     */
    protected String escapedString (String string)
    {
	StringBuffer escapedString = new StringBuffer (string);
	int index;

	for (index = escapedString.length() - 1; index >= 0; index -= 1) {
	    switch (escapedString.charAt (index)) {
		case '"':
		case ';':
		case ':':
		case '\\':
		case ',':
		    escapedString.insert (index, '\\');
		    break;

		case '\n':
		    escapedString.setCharAt (index, 'n');
		    escapedString.insert (index, '\\');
		    break;
	    }
	}
	return escapedString.toString();
    }
    
}

/* $Id: SimpleEvent.java,v 1.9 2002/10/17 18:53:17 johan Exp $
/* $Log: SimpleEvent.java,v $
/* Revision 1.9  2002/10/17 18:53:17  johan
/* Follow iCalendar standard by escaping " ; : \ , and line breaks in text values
/* with backslash.
/* Follow iCalendar standard by folding lines longer than 75 characters.
/*
/* Revision 1.8  2002/10/09 19:32:15  johan
/* Renamed "CalendarPage" "PublishCalendarPage".
/*
/* Revision 1.7  2002/10/06 19:23:06  johan
/* Improved JavaDoc comments.
/*
/* Revision 1.6  2002/10/06 08:56:41  johan
/* Removed unused imports of eocontrol and eoaccess.
/*
/* Revision 1.5  2002/10/06 08:49:17  johan
/* Implemented recurring events.
/*
/* Revision 1.4  2002/09/30 20:44:59  johan
/* More JavaDoc comments.
/*
/* Revision 1.3  2002/09/30 20:38:21  johan
/* *** empty log message ***
/*
/* Revision 1.2  2002/09/30 20:20:11  johan
/* Added JavaDoc comments.
/*
/* Revision 1.1.1.1  2002/09/30 07:00:43  johan
/* First Import
/*
/*
 */
