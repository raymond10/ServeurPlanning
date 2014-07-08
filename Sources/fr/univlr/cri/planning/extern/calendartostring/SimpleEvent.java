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

import java.util.StringTokenizer;

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.foundation.NSTimeZone;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;

import fr.univlr.cri.planning.SPConstantes;

/**
 * @deprecated
 * 
 * Classe inspirŽe de la classe SimpleEvent de Johan Carlberg, version 1.0, 
 * 2002-09-30
 */

public class SimpleEvent extends Object {
  //TODO a deplacer dans SPConstantes les 3 types de statutEvent
  private final static String STATUT_PROVISOIRE = "TENTATIVE";
  private final static String STATUT_CONFIRME = "CONFIRMED";
  private final static String STATUT_ANNULE = "CANCELLED";
  
	private String dateDebut; // format idem dtend
  private String dateFin; 
  private String duree;
  private String titre;
  private String statutEvent;  // "TENTATIVE" / "CONFIRMED" / "CANCELLED" 
  private String classe;  // "PUBLIC" / "PRIVATE" / "CONFIDENTIAL"
  private String description; // details
  private String categorie;  
  private String location;  //  mettre dans description
  private String organisateur;  // mettre dans description
  private String invites; // attendee
  private String contact; 
  private String resources; // materiels nŽcessaires, dans description
  private String commentaire; // dans description;
  
  private NSTimestampFormatter dateTimeFormatter;
  private NSTimestampFormatter dateFormatter;
  private NSTimestampFormatter utcDateTimeFormatter;
  
  // CONSTRUCTEUR 
    
  public SimpleEvent(String startTime, int endType, String endVal, String title) {
  	
  	dateTimeFormatter = new NSTimestampFormatter ("%Y%m%dT%H%M%S");
  	dateTimeFormatter.setDefaultFormatTimeZone (NSTimeZone.timeZoneWithName("Europe/Paris", true));
  	dateFormatter = new NSTimestampFormatter ("%Y%m%d");
  	dateFormatter.setDefaultFormatTimeZone (NSTimeZone.timeZoneWithName("Europe/Paris", true));
  	utcDateTimeFormatter = new NSTimestampFormatter ("%Y%m%dT%H%M%SZ");
  	utcDateTimeFormatter.setDefaultFormatTimeZone (NSTimeZone.timeZoneWithName ("UTC", false));
  	
  	if (startTime != null)
  		dateDebut = startTime; 
  	
  	if (endVal != null)
  	if (endType == 1)
  		dateFin = endVal;
  	else if (endType == 2)
  		duree = endVal;
  	else 
  		CktlLog.trace("2eme Parametre (endType) non dŽterminŽ, dois etre = Ž 1 ou 2.");
  	if (title != null)
  		titre = title;
  	
  	//CktlLog.log("SimpleEvent() dateDebut=" + dateDebut + " dateFin=" + dateFin+ " duree=" + duree + " titre=" + titre);
  }


  // GETTERS retournant des infos utiles a EdtScol

  public NSTimestamp dateDebut() 
  {
  	NSTimestamp debut = null;
  	if (dateDebut != null)
  	{
  		debut = dateFormat(dateDebut);
  	}
  	return debut;
  }

  public NSTimestamp dateFin() 
  {
  	NSTimestamp fin = null;
  	if (dateFin != null)
  	{
  		fin = dateFormat(dateFin);
  	}
  	else if ( duree != null)
  	{
  		fin = dureeFormat(duree);
  	}
  	return fin;
  }
  
  // ------ Affichage public ou privee
  
  public String classe()  {
  	/*if (classe == null || classe.equals("PUBLIC"))
  		classe = "PUBLIC";
  	else classe = "PRIVATE";*/
  	return classe;
  }

  // -------- Motif de l'occupation
  
  public String type() {
  	// ajout Enseignement et Examen ???
  	
  	String type = "";
  	String titreMAJ = titre.toUpperCase();
  	if (titreMAJ.startsWith("REUNION") || 
  			titreMAJ.startsWith("RŽUNION") || 
  			titreMAJ.startsWith("RŽUNION")) 
  		type = "REUNION";
  	else if (titreMAJ.startsWith("FORMATION")) 
  		type = "FORMATION";
  	else if (categorie != null)
  	{
  		if (StringCtrl.containsIgnoreCase(categorie, "REUNION") || 
  				StringCtrl.containsIgnoreCase(categorie, "RŽUNION") || 
  				StringCtrl.containsIgnoreCase(categorie, "RŽUNION"))
  			type = "REUNION";
  		else if (StringCtrl.containsIgnoreCase(categorie, "FORMATION"))
  			type = "FORMATION";
  		else type = "BLOCAGE";
  	}
  	else type = "BLOCAGE";

  	return type;
  }
  
  /**
   * 
   * @return
   */
  public String details() {
  	String details = "";
  	if (!StringCtrl.isEmpty(titre))
  		details += titre;
  	if (!StringCtrl.isEmpty(description) && details.length()+description.length()+18 < 245)
  		details += "\\n description : "+ description;
  	if (!StringCtrl.isEmpty(organisateur) && details.length()+organisateur.length()+19 < 245)
  		details += "\\n organisateur : "+ organisateur;
  	if (!StringCtrl.isEmpty(location) && details.length()+location.length()+11 < 245)
  		details += "\\n lieu : "+ location;
  	if (!StringCtrl.isEmpty(resources) && details.length()+resources.length()+16 < 245)
  		details += "\\n resources : "+ resources;
  	if (!StringCtrl.isEmpty(invites) && details.length()+invites.length()+14 < 245)
  		details += "\\n invites : "+ invites;
  	if (!StringCtrl.isEmpty(contact) && details.length()+contact.length()+14 < 245)
  		details += "\\n contact : "+ contact;
  	// DT 786 : suppression du libelle de l'etat dans le motif
  	/*
  	if (!StringCtrl.isEmpty(statutEvent) && details.length()+statutEvent.length()+18 < 245) {	
  		details += "\\nstatut : " + (
  				statutEvent.equals(STATUT_PROVISOIRE) ? "provisoire" :
  					statutEvent.equals(STATUT_CONFIRME) ? "confirme" :
  						statutEvent.equals(STATUT_ANNULE) ? "annule" : "inconnu");
  	}*/
  	if (!StringCtrl.isEmpty(commentaire) && details.length()+commentaire.length()+18 < 245)
  		details += "\\n commentaire : "+ commentaire;
  	
  	return details;
  }
  
  // etat 
  
  /**
   * Un event est visible si le statut est :
   * - sans statut
   * - statut confirme
   */
  private boolean isEtatVisible() {
  	return StringCtrl.isEmpty(statutEvent) || statutEvent.equals(STATUT_CONFIRME);
  }
  
  // classe
  
  /**
   * Un event est visible si la classe est :
   * - sans classe
   * - classe publique ou privee
   */
  private boolean isClasseVisible() {
  	return StringCtrl.isEmpty(classe()) || classe().equals(SPConstantes.AFF_PUBLIC) || classe().equals(SPConstantes.AFF_PRIVEE);
  }
  
  // 
  
  /**
   * Visibilite event 
   */
  public boolean isVisible() {
  	return isEtatVisible() && isClasseVisible();
  }
  
  
  // SETTERS pour ajout d' infos optionnelles
  
	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setInvites(String invites) {
		this.invites = invites;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setOrganisateur(String organisateur) {
		this.organisateur = organisateur;
	}

	public void setResources(String resources) {
		this.resources = resources;
	}

	public void setStatutEvent(String statutEvent) {
		this.statutEvent = statutEvent;
	}


	protected NSTimestamp dateFormat(String date)
	{
//			Exemple :
//				DTEND;VALUE=DATE;TZID=/mozilla.org/20050126_1/Africa/Ceuta:20060712
//				DTSTART:19701025T030000
//				DTSTAMP:20060711T074550Z
		// ou des dates simples extraits de rDate ou exdate
		
		//    valeur aprŽs les derniers ":"
		// on trouve format par nb de caracteres
		
		//System.out.println("dateFormat String :"+date);
		
		StringTokenizer st = new StringTokenizer(date, ":");
		int num = st.countTokens();
		String d = null;
		for (int i = 0; i< num; i++)
		{
			d = st.nextToken();
		}
		if (d.endsWith(" "))
			d = d.substring(0, d.length()-1);
		NSTimestamp rep = null;
		try{
		if (d.length() == 8)
			rep=(NSTimestamp)dateFormatter.parseObject(d);
		else if (d.length() == 15)
			rep=(NSTimestamp)dateTimeFormatter.parseObject(d);
		else if (d.length() == 16)
			rep=(NSTimestamp)utcDateTimeFormatter.parseObject(d);
		}
		catch (Exception e)
		{
			CktlLog.trace("Format de date non trouvŽ : "+e);
		}
		
	//	System.out.println("dateFormat NSTimestamp : toString = "+rep.toString()+"; dateCtrl = "+DateCtrl.dateToString(rep, SPConstantes.DATE_FORMAT));
		
		return rep;
	}
	
	private NSTimestamp dureeFormat(String duree)
	{
// 			 dur-value  = (["+"] / "-") "P" (dur-date / dur-time / dur-week)
//	     dur-date   = dur-day [dur-time]
//	     dur-time   = "T" (dur-hour / dur-minute / dur-second)
//	     dur-week   = 1*DIGIT "W"
//	     dur-hour   = 1*DIGIT "H" [dur-minute]
//	     dur-minute = 1*DIGIT "M" [dur-second]
//	     dur-second = 1*DIGIT "S"
//	     dur-day    = 1*DIGIT "D"
//	Exemple Pour 15 jours, 5 h et 20 seconds : P15DT5H0M20S
//		   Pour 7 weeks : P7W
		long l = 0;  // en milliSecondes
		boolean plus = true;
		int caract = 0;
		if (duree.startsWith("-P"))
		{
			plus = false;
			caract += 2;
		}
		else if (duree.startsWith("+P"))
			caract += 2;
		else caract += 1;
		String cutDuree = duree.substring(caract);
		
		if (cutDuree.endsWith("W"))
		{
			cutDuree = duree.substring(0, cutDuree.length()-1);
			l = Long.parseLong(cutDuree)*7*24*60*60*1000;
		}
		else if (StringCtrl.containsIgnoreCase(cutDuree, "D"))
		{
			// sŽparŽ avec T
			// si 1 seul element -> D seul
			// sinon D puis T (HMS)
			StringTokenizer st = new StringTokenizer(cutDuree, "T");
			String d = st.nextToken();
			// virer le "D" a la fin
			d = d.substring(0, d.length()-1);
			l += Long.parseLong(d)*24*60*60*1000;
			if (st.hasMoreTokens())
				l += timeHMS(st.nextToken());
		}
		else if (cutDuree.startsWith("T"))
			l += timeHMS(cutDuree);
		
		NSTimestamp rep = null;
		if (plus)
			rep = new NSTimestamp(dateDebut().getTime()+l);
		else 
			rep = new NSTimestamp(dateDebut().getTime()-l);
		
		return rep;
	}
	
	// Traduit string de duree avec heure/min/sec
	
	private long timeHMS(String HMS)
	{
		long rep = 0;
		HMS = HMS.substring(1); // vire le "T"
		if (StringCtrl.containsIgnoreCase(HMS, "H"))
		{
		StringTokenizer st = new StringTokenizer(HMS, "H");
		String h = st.nextToken();
		rep = Long.parseLong(h)*60*60*1000;
		if (st.hasMoreTokens())
			HMS = st.nextToken();
		else
			HMS = null;
		}
		if (HMS != null && StringCtrl.containsIgnoreCase(HMS, "M"))
		{
			StringTokenizer st = new StringTokenizer(HMS, "M");
			String m = st.nextToken();
			rep += Long.parseLong(m)*60*1000;
			if (st.hasMoreTokens())
				HMS = st.nextToken();
			else
				HMS = null;
		}
		if (HMS != null && HMS.endsWith("S"))
		{
			String s = HMS.substring(0, HMS.length()-1);
			rep += Long.parseLong(s)*1000;
		}
			
	return rep;
	}

	
		/* PropriŽtŽ d'un event
		 * 1 seule fois :
		 *          class / created (inutile)/ description / dtstart / geo (inutile)/
		            last-mod (inutile)/ location / organizer / priority (inutile)/
		            dtstamp (inutile)/ seq (inutile)/ status / summary = titre/ transp (inutile)/
		            uid (inutile)/ url (inutile)/ recurid (inutile)/
		  *  l'un ou l'autre :        
		            dtend / duration /
		  * peut etre plusieurs fois :
		              attach (inutile)/ attendee / categories / comment /
		              contact / exdate / exrule / rstatus (inutile)/ related (inutile)/
		              resources / rdate / rrule / x-prop (inutile)
			 */
		
	   // ----------- Exemple
    
//	 DTSTART;TZID=Europe/Paris:20060508T140000
//	DTEND:19960401T235959Z ou DTEND;VALUE=DATE:19980704 avec "DATE-TIME"(defaut) ou "DATE"
//	 DURATION:PT1H0M0S -> 1h ; PT15M -> 15min
//	  CATEGORIES:APPOINTMENT,EDUCATION
//	  ORGANIZER:MAILTO:jsmith@host1.com
//	  ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=TENTATIVE;CN=Henry Cabot
//	   :MAILTO:hcabot@host2.com
//	  ATTENDEE;ROLE=REQ-PARTICIPANT;DELEGATED-FROM="MAILTO:bob@host.com"
//	   ;PARTSTAT=ACCEPTED;CN=Jane Doe:MAILTO:jdoe@host1.com
//	 CONTACT:Jim Dolittle\, ABC Industries\, +1-919-555-1234
	    
}
