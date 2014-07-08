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
package fr.univlr.cri.planning.datacenter;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Comment;
import net.fortuna.ical4j.model.property.Contact;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Resources;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.foundation.NSTimestamp;

/**
 * Surcharge de la classe de ical4j VEvent pour avoir acces à des methodes
 * particulieres
 * 
 * @author ctarade
 */
public class SPVEvent {

	private VEvent vEvent;

	public SPVEvent(VEvent aVEvent) {
		super();
		vEvent = aVEvent;
	}

	public VEvent getVEVent() {
		return vEvent;
	}

	public boolean isRepetition() {
		boolean isRepetition =
				vEvent.getProperties(Property.RRULE) != null ||
						vEvent.getProperties(Property.RDATE) != null ||
						vEvent.getProperties(Property.EXRULE) != null ||
						vEvent.getProperties(Property.EXDATE) != null;
		return isRepetition;
	}

	public boolean isExDate() {
		boolean isExDate =
				vEvent.getProperties(Property.EXDATE) != null;
		return isExDate;
	}

	public NSTimestamp dateDebut() {
		NSTimestamp dateDebut = null;
		dateDebut = new NSTimestamp(vEvent.getStartDate().getDate().getTime());
		CktlLog.log("dateDebut=" + dateDebut);
		return dateDebut;
	}

	public NSTimestamp dateFin() {
		return new NSTimestamp(vEvent.getEndDate().getDate().getTime());
	}

	// -------- Motif de l'occupation

	public String type() {
		// ajout Enseignement et Examen ???
		String type = "BLOCAGE";
		String titreMAJ = (summary() != null ? summary().getValue().toUpperCase() : "");
		if (titreMAJ.startsWith("REUNION") ||
				titreMAJ.startsWith("RÉUNION") ||
				titreMAJ.startsWith("RéUNION")) {
			type = "REUNION";
		} else if (titreMAJ.startsWith("FORMATION")) {
			type = "FORMATION";
		} else if (categories() != null) {
			if (StringCtrl.containsIgnoreCase(categories().getValue(), "REUNION") ||
					StringCtrl.containsIgnoreCase(categories().getValue(), "RÉUNION") ||
					StringCtrl.containsIgnoreCase(categories().getValue(), "RéUNION")) {
				type = "REUNION";
			} else if (StringCtrl.containsIgnoreCase(categories().getValue(), "FORMATION")) {
				type = "FORMATION";
			}
		}

		return type;
	}

	private final static int DETAILS_MAX_SIZE = 245;

	/**
	 * 
	 * @return
	 */
	public String details() {
		String details = "";
		if (summary() != null) {
			details += summary().getValue();
		}
		checkSizeAndAppend(details, "\\n description : ", description(), DETAILS_MAX_SIZE);
		checkSizeAndAppend(details, "\\n organisateur : ", organizer(), DETAILS_MAX_SIZE);
		checkSizeAndAppend(details, "\\n lieu : ", location(), DETAILS_MAX_SIZE);
		checkSizeAndAppend(details, "\\n resources : ", resources(), DETAILS_MAX_SIZE);
		checkSizeAndAppend(details, "\\n invites : ", attendee(), DETAILS_MAX_SIZE);
		checkSizeAndAppend(details, "\\n contact : ", contact(), DETAILS_MAX_SIZE);
		checkSizeAndAppend(details, "\\n commentaire : ", comment(), DETAILS_MAX_SIZE);

		return details;
	}

	/**
	 * Methode interne de controle de la taille des champs.
	 * 
	 * @param stringOrginal
	 * @param prefix
	 * @param stringToAppend
	 * @param maxSize
	 * @return <em>false</em> si la taille est depasse ou bien si
	 *         <code>stringToAppend</code> est vide
	 */
	private String checkSizeAndAppend(String stringOrginal, String prefix, Property propertyToAppend, int maxSize) {
		return (propertyToAppend != null && !StringCtrl.isEmpty(propertyToAppend.getValue()) && stringOrginal.length() + prefix.length() + propertyToAppend.getValue().length() < maxSize) ?
						stringOrginal + prefix + propertyToAppend.getValue() : "";
	}

	// etat

	/**
	 * Un event est visible si le statut est : - sans statut - statut confirme
	 */
	private boolean isStatusVisible() {
		return status() == null ||
				status().equals(Status.VEVENT_CONFIRMED);
	}

	// classe

	/**
	 * Un event est visible si la classe est : - sans classe - classe publique ou
	 * privee
	 */
	private boolean isClazzVisible() {
		return clazz() == null ||
				clazz().equals(Clazz.PUBLIC) ||
				clazz().equals(Clazz.PRIVATE);
	}

	/**
	 * Un event est visible si la classe est : - sans classe - classe publique ou
	 * privee
	 */
	public boolean isClassePublic() {
		return clazz() == null ||
				clazz().equals(Clazz.PUBLIC);
	}

	//

	/**
	 * Visibilite event
	 */
	public boolean isVisible() {
		return isStatusVisible() && isClazzVisible();
	}

	//

	public String classDisplay() {
		return (clazz() == null ? Clazz.PUBLIC.toString() : clazz().getValue());
	}

	// raccourcis vers les proprietes

	private Summary summary() {
		return (Summary) vEvent.getProperty(Property.SUMMARY);
	}

	private Categories categories() {
		return (Categories) vEvent.getProperty(Property.CATEGORIES);
	}

	private Description description() {
		return (Description) vEvent.getProperty(Property.DESCRIPTION);
	}

	private Location location() {
		return (Location) vEvent.getProperty(Property.LOCATION);
	}

	private Organizer organizer() {
		return (Organizer) vEvent.getProperty(Property.ORGANIZER);
	}

	private Resources resources() {
		return (Resources) vEvent.getProperty(Property.RESOURCES);
	}

	private Attendee attendee() {
		return (Attendee) vEvent.getProperty(Property.ATTENDEE);
	}

	private Contact contact() {
		return (Contact) vEvent.getProperty(Property.CONTACT);
	}

	private Comment comment() {
		return (Comment) vEvent.getProperty(Property.COMMENT);
	}

	private Clazz clazz() {
		return (Clazz) vEvent.getProperty(Property.CLASS);
	}

	private Status status() {
		return (Status) vEvent.getProperty(Property.STATUS);
	}
}
