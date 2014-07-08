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

import fr.univlr.cri.planning.extern.calendartostring.SimpleEvent;

/**
 * CalendarEvent is an interface for events used by
 * {@link PublishCalendarPage the PublishCalendarPage component}.
 * <p>
 * {@link PublishCalendarPage PublishCalendarPage} can use objects of any class
 * that implements this interface. Existing classes (for example
 * EOCustomObject subclasses), that correspond to calendar events,
 * can easily be modified to implement this interface and thus be
 * added directly to {@link PublishCalendarPage PublishCalendarPage}. If existing
 * classes does not directly correspond to calendar events, create
 * events from business data (or some algorithm) using either the
 * included {@link SimpleEvent SimpleEvent class}, a subclass of
 * {@link SimpleEvent SimpleEvent}, or any other class implementing
 * this interface.
 *
 * @author 	Johan Carlberg
 * @version 	1.0, 2002-09-30
 * @deprecated
 */

public interface CalendarEvent {

    /**
    * @return	<code>false</code> if this event have specified
     *		start and ending times.
     *		<code>true</code> if this event is a whole-day
     *		event without a specific starting and ending hour
     *		and minute.
     * @see	SimpleEvent#wholeDay
     */
    public boolean wholeDay();

    /**
     * @return  the start time of this event. For whole-day events,
     *          the time part of the NSTimestamp is ignored.
     * @see     #endTime
     */
    public NSTimestamp startTime();

    /**
     * @return  the end time of this event. For whole-day events,
     *          this should be a time on the day following the
     *          last day of this event (the time part of the
     *          NSTimestamp is ignored, and the iCalendar standard
     *          requires that the end time of an event is later
     *          than the start time.
     * @see     #startTime
     */
    public NSTimestamp endTime();

    /**
     * @return  the change counter of this event. The sequence of a
     *          calendar event is supposed to increase every time
     *		any information in the event is modified, so that
     *		updates to events can be ordered.
     * @see     SimpleEvent#sequence
     */
    public int sequence();

    /**
     * @return  status of this event. Values defined by the iCalendar
     *		standard are: "TENTATIVE", "CONFIRMED", "CANCELLED".
     *		Return null if event status is unspecified.
     */
    public String status();

    /**
     * @return  summary or textual description of this event.
     */
    public String summary();

    /**
     * @return  a persistent, globally unique identifier for this
     *          event.
     *		The unique identifier <b>must</b> be a globally unique
     *		identifier. The generator of the identifier <b>must</b>
     *		guarantee that the identifier is unique. There are
     *		several algorithms that can be used to accomplish this.
     *		The identifier is recommended to be the identical
     *		syntax to the RFC 822 addr-spec. A good method to
     *		assure uniqueness is to put the domain name or a
     *		domain literal IP address of the host on which the
     *		identifier was created on the right hand side of the
     *		"@", and on the left hand side, put a combination of
     *		the current calendar date and time of day (that is,
     *		formatted in as a date-time value) along with some
     *		other currently unique (perhaps sequential) identifier
     *		available on the system (for example, a process id
     *		number). Using a date/time value on the left hand side
     *		and a domain name or domain literal on the right hand
     *		side makes it possible to guarantee uniqueness since no
     *		two hosts should be using the same domain name or IP
     *		address at the same time. Though other algorithms will
     *		work, it is recommended that the right hand side
     *		contain some domain identifier (either of the host
     *		itself or otherwise) such that the generator of the
     *		message identifier can guarantee the uniqueness of the left
     *		hand side within the scope of that domain.
     * @see     SimpleEvent#sequence
     */
    public String uniqueId();

    /**
     * @return  the frequency of a repeating event, or 0 for a one time
     *		event. Can be specified as one of the
     *		<code>java.util.Calendar</code> field numbers
     *		<code>YEAR</code>, <code>MONTH</code>,
     *		<code>WEEK_OF_YEAR</code>, <code>DAY_OF_MONTH</code>,
     *		<code>HOUR_OF_DAY</code>, <code>MINUTE</code> or
     *		<code>SECOND</code>.
     * @see	#repeatCount
     */
    public int repeatFrequency();

    /**
     * @return  the number of occurences of a repeating event. Ignored
     *		if {@link #repeatFrequency} returns 0.
     * @see     #repeatFrequency
     */
    public int repeatCount();

    /**
     * @return  the day of week of a repeating event, or 0 for unspecified
     *		day of week. Can be specified as one of the
     *		<code>java.util.Calendar</code> <code>DAY_OF_WEEK</code>
     *		field values <code>SUNDAY</code>, <code>MONDAY</code>,
     *		<code>TUESDAY</code>, <code>WEDNESDAY</code>,
     *		<code>THURSDAY</code>, <code>FRIDAY</code>,
     *		<code>SATURDAY</code> or as the value 0. Ignored if
     *		{@link #repeatFrequency} returns 0.
     * @see	#repeatFrequency
     */
    public int repeatDayOfWeek();

    /**
     * @return  the ordinal number of day of the week within a month
     *		at which the event repeats. Together with
     *		{@link #repeatDayOfWeek} it uniquely specifies a day
     *		within the month. Ignored if {@link #repeatFrequency}
     *		returns 0.
     * @see	#repeatDayOfWeek
     */
    public int repeatDayOfWeekInMonth();

    /**
     * @return  an array of {@link Integer} indicating the days
     *		within a month, at which the event repeats, or
     *		<code>null</code> for not restricting repeating
     *		to certain days.
     *		Together with {@link #repeatDayOfWeek} it uniquely
     *		specifies a day within the month (Keep in mind
     *		that iCal 1.0 does not properly handle this).
     *		Ignored if {@link #repeatFrequency} returns 0.
     * @see	#repeatDayOfWeek
     */
    public NSArray repeatDaysOfMonth();
}

/* $Id: CalendarEvent.java,v 1.8 2002/10/17 18:53:16 johan Exp $
/* $Log: CalendarEvent.java,v $
/* Revision 1.8  2002/10/17 18:53:16  johan
/* Follow iCalendar standard by escaping " ; : \ , and line breaks in text values
/* with backslash.
/* Follow iCalendar standard by folding lines longer than 75 characters.
/*
/* Revision 1.7  2002/10/09 19:32:15  johan
/* Renamed "CalendarPage" "PublishCalendarPage".
/*
/* Revision 1.6  2002/10/06 19:23:06  johan
/* Improved JavaDoc comments.
/*
/* Revision 1.5  2002/10/06 18:51:05  johan
/* Improved JavaDoc comments.
/*
/* Revision 1.4  2002/10/06 08:56:41  johan
/* Removed unused imports of eocontrol and eoaccess.
/*
/* Revision 1.3  2002/10/06 08:49:17  johan
/* Implemented recurring events.
/*
/* Revision 1.2  2002/09/30 20:20:11  johan
/* Added JavaDoc comments.
/*
/* Revision 1.1.1.1  2002/09/30 07:00:42  johan
/* First Import
/*
/*
 */
