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
package org.cocktail.planning;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;

import org.cocktail.fwkcktlwebapp.common.util.FileCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.common.util.SystemCtrl;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.eof.ERXQ;
import fr.univlr.cri.planning.SPOccupation;
import fr.univlr.cri.planning.constant.LocalSPConstantes;
import fr.univlr.cri.planning.datacenter.A_SPDataBusCaching;
import fr.univlr.cri.planning.factory.HugICalendarFactory;

/**
 * Classe de conversion d'un flux ICS vers des occupations "ServeurPlanning" via
 * sa classe {@link SPOccupation}
 * 
 * @author ctarade
 */
public class IcsToSPOccupationFactory {

	/**
	 * Lire le contenu d'un fichier ICS localisé à l'URL urlCalendar et le
	 * transformer en liste de {@link SPOccupation}
	 * 
	 * @param urlCalendar
	 */
	public static NSArray<SPOccupation> parse(String urlCalendar) {

		NSMutableArray<SPOccupation> sPOccupationList = new NSMutableArray<SPOccupation>();
		long l1 = System.currentTimeMillis();

		// recuperer le fichier ICS en objet Calendar
		Calendar calendar = null;
		try {
			calendar = parserURLFile(urlCalendar);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		if (calendar != null) {

			try {
				sPOccupationList = getEventObjects(urlCalendar, calendar);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		l1 = System.currentTimeMillis() - l1;

		System.out.println(".............. total parse time : " + l1 + " ms (" + urlCalendar + ")");

		return sPOccupationList;
	}

	/**
	 * 
	 * @param urlCalendar
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	private static NSMutableArray<SPOccupation> getEventObjects(String urlCalendar, Calendar calendar) throws Exception {
		NSMutableArray<SPOccupation> spOccupationList = new NSMutableArray<SPOccupation>();
		for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
			Component component = (Component) i.next();
			if (component instanceof VEvent) {
				VEvent event = (VEvent) component;
				SPOccupationVEvent sPVEvent = new SPOccupationVEvent(urlCalendar, event, calendar);
				if (sPVEvent.isRepetition()) {
					spOccupationList.addObjectsFromArray(sPVEvent.getSpOccupationArray());
				} else {
					SPOccupation spOcc = sPVEvent.getSPOccupation();
					spOccupationList.addObject(spOcc);

					if (sPVEvent.isRecurrence()) {
						// si c'est une récurrence, il faut "soustraire" une des occupations
						// de la liste

						EOQualifier qualAVirer =
								ERXQ.and(
										ERXQ.equals(SPOccupation.UID_KEY, sPVEvent.getUid()),
										ERXQ.equals(SPOccupation.DATE_DEBUT_KEY, sPVEvent.getDateDebutReccurence()));

						NSArray spOccupationListAvirer = new NSMutableArray<SPOccupation>(
								EOQualifier.filteredArrayWithQualifier(spOccupationList, qualAVirer));

						EOQualifier qual = ERXQ.not(
									ERXQ.and(
											ERXQ.equals(SPOccupation.UID_KEY, sPVEvent.getUid()),
											ERXQ.equals(SPOccupation.DATE_DEBUT_KEY, sPVEvent.getDateDebutReccurence())));

						spOccupationList = new NSMutableArray<SPOccupation>(
								EOQualifier.filteredArrayWithQualifier(spOccupationList, qual));
					}

				}
				if (event.getProperties().getProperty(Property.UID) != null) {
					// exoEvent.setId(event.getProperty(Property.UID).getValue()) ;
				}
				if (event.getProperties().getProperty(Property.CATEGORIES) != null) {
					// exoEvent.setEventCategoryId(event.getProperty(Property.CATEGORIES).getValue().trim().toLowerCase())
					// ;
				}
				if (event.getProperties().getProperty(Property.SUMMARY) != null) {
					// exoEvent.setSummary(event.getSummary().getValue()) ;
					// System.out.println("sumary:"+event.getProperties().getProperty(Property.SUMMARY).getValue());
				}
				// if(event.getDescription() != null)
				// exoEvent.setDescription(event.getDescription().getValue()) ;
				// if(event.getStatus() != null)
				// exoEvent.setStatus(event.getStatus().getValue()) ;
				// exoEvent.setEventType(CalendarEvent.TYPE_EVENT) ;
				// if(event.getStartDate() != null)
				// exoEvent.setFromDateTime(event.getStartDate().getDate()) ;
				// if(event.getEndDate() != null)
				// exoEvent.setToDateTime(event.getEndDate().getDate()) ;
				// if(event.getLocation() != null)
				// exoEvent.setLocation(event.getLocation().getValue()) ;
				// if(event.getPriority() != null)
				// exoEvent.setPriority(event.getPriority().getValue()) ;
				// exoEvent.setPrivate(true) ;
				// PropertyList attendees = event.getProperties(Property.ATTENDEE) ;
				// if(attendees.size() < 1) {
				// exoEvent.setInvitation(new String[]{}) ;
				// }else {
				// String[] invitation = new String[attendees.size()] ;
				// for(int j = 0; j < attendees.size(); j ++) {
				// invitation[j] = ((Attendee)attendees.get(j)).getValue() ;
				// }
				// exoEvent.setInvitation(invitation) ;
				// }
				// eventList.add(exoEvent) ;
			}
		}

		return spOccupationList;
	}

	private static String tempDir;

	/**
	 * 
	 * @return
	 */
	private static String getTempDir() {
		if (tempDir == null) {
			tempDir = SystemCtrl.tempDir();
			if (!StringCtrl.isEmpty(tempDir) && !tempDir.endsWith(A_SPDataBusCaching.fsSeparator())) {
				tempDir += A_SPDataBusCaching.fsSeparator();
			}
			tempDir += LocalSPConstantes.ICS_CACHE_SUB_DIRECTORY;
			tempDir += A_SPDataBusCaching.fsSeparator();
		}
		return tempDir;
	}

	/**
	 * Retourne un objet {@link Calendar} a partir d'un fichier ICS. Télécharge le
	 * fichier en local sur la machine puis traite le fichier via
	 * {@link CalendarBuilder#build(InputStream)}
	 * 
	 * @param urlCalendar
	 * @return
	 * @throws Throwable
	 */
	private static Calendar parserURLFile(String urlCalendar) throws Throwable {
		// System.out.println("testParserURLFile()");
		InputStream in = null;

		String cheminFichier = getTempDir();
		cheminFichier += HugICalendarFactory.getNomFichierPourUrlIcs(urlCalendar);

		boolean hasError = true;

		try {
			// pour eviter les erreurs java.net.MalformedURLException: unknown
			// protocol: webcal
			urlCalendar = StringCtrl.replace(urlCalendar, "webcal://", "https://");

			long l1 = System.currentTimeMillis();
			URL url = new URL(urlCalendar);
			URLConnection con = url.openConnection();
			con.connect();
			in = con.getInputStream();

			l1 = System.currentTimeMillis() - l1;
			System.out.println("downloading calendar file : " + l1 + " ms (" + urlCalendar + " to " + cheminFichier + ")");

			// ecriture sur le fichier
			l1 = System.currentTimeMillis();
			FileOutputStream fos = new FileOutputStream(cheminFichier);
			byte[] buff = new byte[1024];
			int l = in.read(buff);
			while (l > 0) {
				fos.write(buff, 0, l);
				l = in.read(buff);
			}
			fos.flush();
			fos.close();
			l1 = System.currentTimeMillis() - l1;
			// System.out.println("writing calendar file : "+l1+" ms ("+fileName+")");

			hasError = false;

		} catch (java.net.ConnectException e) {
			// connection refusee
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			// erreur de connexion
			e.printStackTrace();
		}

		if (hasError) {
			System.out.println("downloading error : " + urlCalendar);
			return null;
		}

		long l1 = System.currentTimeMillis();
		FileInputStream fin = new FileInputStream(cheminFichier);
		CalendarBuilder builder = new CalendarBuilder();

		InputStream is = cleanStream(fin);

		Calendar calendar = builder.build(is);
		l1 = System.currentTimeMillis() - l1;

		// suppression du fichier
		FileCtrl.deleteFile(cheminFichier);

		return calendar;
	}

	/**
	 * Retraitement du flux du fichier ICS téléchargé afin d'enlever toutes les
	 * lignes qui font planter ical4j 1.0.1 (exception Cannot set timezone for UTC
	 * properties)
	 * 
	 * TODO a virer quand la gestion par ical4j de ces formats sera correcte ...
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private static InputStream cleanStream(InputStream is) throws IOException {

		// oter les lignes qui font planter
		StringWriter writer = new StringWriter();
		InputStreamReader streamReader = new InputStreamReader(is);
		// le buffer permet le readline
		BufferedReader buffer = new BufferedReader(streamReader);
		String line = "";
		while (null != (line = buffer.readLine())) {
			// CktlLog.log("line=" + line);
			if (line.startsWith("LAST-MODIFIED;TZID=") == false &&
					line.startsWith("CREATED;TZID=") == false) {
				writer.write(line + "\n");
			}
		}

		InputStream in = new ByteArrayInputStream(writer.toString().getBytes());

		return in;
	}

	/**
	 * Retourne un objet {@link Calendar} a partir d'un fichier ICS. Lit le
	 * contenu du fichier distant, positionne son contenu dans une {@link String}
	 * puis traite la chaine de caractere via
	 * {@link CalendarBuilder#build(java.io.Reader)}
	 * 
	 * @param urlCalendar
	 * @return
	 * @throws Throwable
	 */
	private static Calendar parseURLString(String urlCalendar) throws Throwable {
		// System.out.println("testParserURLString()");

		String string = null;

		try {
			long l1 = System.currentTimeMillis();
			URL url = new URL(urlCalendar);
			URLConnection con = url.openConnection();
			con.connect();

			BufferedInputStream in = new BufferedInputStream(con.getInputStream());
			StringWriter out = new StringWriter();
			int b;
			while ((b = in.read()) != -1)
				out.write(b);
			out.flush();
			out.close();
			in.close();
			string = out.toString();
			l1 = System.currentTimeMillis() - l1;
			// System.out.println("converting calendar url to string : "+l1+" ms ("+urlCalendar+")");
		} catch (IOException ie) {
			ie.printStackTrace();
		}

		long l1 = System.currentTimeMillis();
		StringReader sin = new StringReader(string);
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar = builder.build(sin);

		l1 = System.currentTimeMillis() - l1;
		// System.out.println("parse calendar string : "+l1+" ms ("+urlCalendar+")");

		return calendar;
	}
}
