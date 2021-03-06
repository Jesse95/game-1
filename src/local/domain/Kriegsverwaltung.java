package local.domain;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import local.domain.exceptions.KannLandNichtBenutzenException;
import local.domain.exceptions.KeinNachbarlandException;
import local.domain.exceptions.NichtGenugEinheitenException;
import local.valueobjects.*;

public class Kriegsverwaltung {

private Spielerverwaltung spielerVw;
private Weltverwaltung weltVw;
public phasen Phase;
	
	/**
	 * Konstruktor Kriegsverwaltung
	 * @param spielerVw
	 * @param weltVw
	 */
	public Kriegsverwaltung(Spielerverwaltung spielerVw, Weltverwaltung weltVw) {
	this.spielerVw = spielerVw;
	this.weltVw = weltVw;
	Phase = phasen.ANGRIFF;
	}

	/**
	 * gibt Nachbarlaender die angegriffen werden können zurück
	 * @param land
	 * @param spieler
	 * @return Vector<Land>
	 */
	public String moeglicheAngriffsziele(String landString, Spieler spieler) {
		Land land = weltVw.stringToLand(landString);
		List<Land> nachbarLaender = this.weltVw.getNachbarLaender(land);
		String nachbarLaenderString = "\nDu kannst mit " + landString + " folgende Länder angreifen: ";
		
		for(Land l : nachbarLaender) {
			if(!l.getBesitzer().equals(spieler)) {
				nachbarLaenderString += l.getName() + " | ";
			}
		}	
		
		return nachbarLaenderString;	
	}
	
	public String eigeneNachbarn(String landString, Spieler spieler) {
		Land land = weltVw.stringToLand(landString);
		List<Land> nachbarLaender = this.weltVw.getNachbarLaender(land);
		String nachbarLaenderString = "\nDu kannst mit " + landString + " auf folgende Laender verschieben: ";
		
		for(Land l : nachbarLaender) {
			if(l.getBesitzer().equals(spieler)) {
				nachbarLaenderString += l.getName() + " | ";
			}
		}	
		
		return nachbarLaenderString;	
	}
	public boolean istNachbar(Land wahlLand, Land landZiel, Spieler spieler) throws KeinNachbarlandException{
		List<Land> nachbarLaender = this.weltVw.getNachbarLaender(wahlLand);
		for(Land l : nachbarLaender){
			if(l.getName().equals(landZiel.getName())){
				return true;
			}
		}
		throw new KeinNachbarlandException(wahlLand.getName());
	}
	
	/**
	 * würfelt mehrfach und gibt Liste mit Ergebnissen zurück
	 * @param anzahl
	 * @return Vector<Integer>
	 */
	public List<Integer> wuerfeln(int anzahl) {
		List<Integer> ergebnisse = new Vector<Integer>();
				
		for(int i = 0;i < anzahl;i++) {
			ergebnisse.add((int)(Math.random() * 6) + 1);
		}
		Collections.sort(ergebnisse);
		
		return ergebnisse;
	}
	
	/**
	 * greift mit einem Land ein anderes an und gibt Verluste zurück
	 * @param angreifendesLand
	 * @param verteidigendesLand
	 * @return Vector<Integer> Verluste
	 */

//	public AttackResult befreiungsAktion(Attack attack) {
//		Attack -> Angreifer, Verteidiger, vielleicht noch wie viele Würfel
//		AttackResult -> AngreiferLand, VerteidigerLand, Gewinner / Verluste 
	public List befreiungsAktion(String angreifendesLandString, String verteidigendesLandString) {
		Land angreifendesLand = weltVw.stringToLand(angreifendesLandString);
		Land verteidigendesLand = weltVw.stringToLand(verteidigendesLandString);
		int angreiferEinheiten = angreifendesLand.getEinheiten();
		int verteidigerEinheiten = verteidigendesLand.getEinheiten();
		int angreifendeEinheiten;
		int verteidigendeEinheiten;
		List<Integer> wuerfeAngreifer;
		List<Integer> wuerfeVerteidiger;
		List<Integer> verluste = new Vector<Integer>();
		List<String> rueckgabe = new Vector<String>();
		String ausgabeString = "";
		
		
		if(angreiferEinheiten < 4) {
			angreifendeEinheiten = angreiferEinheiten - 1;
		} else {
			angreifendeEinheiten = 3;
		}
		
		if(verteidigerEinheiten < 2) {
			verteidigendeEinheiten = verteidigerEinheiten;
		} else {
			verteidigendeEinheiten = 2;
		}
		
		wuerfeAngreifer = wuerfeln(angreifendeEinheiten);
		wuerfeVerteidiger = wuerfeln(verteidigendeEinheiten);
		
		if((wuerfeVerteidiger.size() == 1) && (wuerfeVerteidiger.get(0) < wuerfeAngreifer.get(1))) {
			 verluste.add(0);
			 verluste.add(1);
		} else if((wuerfeVerteidiger.size() == 2) && (wuerfeVerteidiger.get(0) < wuerfeAngreifer.get(1)) && (wuerfeVerteidiger.get(1) < wuerfeAngreifer.get(2))) {
			 verluste.add(0);
			 verluste.add(2);
		} else if(((wuerfeVerteidiger.get(0) < wuerfeAngreifer.get(1)) && (wuerfeVerteidiger.get(1) > wuerfeAngreifer.get(2))) && ((wuerfeVerteidiger.get(0) > wuerfeAngreifer.get(1)) && (wuerfeVerteidiger.get(1) < wuerfeAngreifer.get(2)))) {
			 verluste.add(1);
			 verluste.add(1);	
		} else if((wuerfeVerteidiger.size() == 1) && (wuerfeVerteidiger.get(0) >= wuerfeAngreifer.get(1))) {
			 verluste.add(1);
			 verluste.add(0);
		} else if((wuerfeVerteidiger.size() == 2) && (wuerfeVerteidiger.get(0) >= wuerfeAngreifer.get(1)) && (wuerfeVerteidiger.get(1) >= wuerfeAngreifer.get(2))) {
			 verluste.add(2);
			 verluste.add(0);
		}
		//verluste ist ein Vector mit den Angaben: AngreiferVerlust / VerteidigerVerlust
		angreifendesLand.setEinheiten(angreifendesLand.getEinheiten() - verluste.get(0));
		verteidigendesLand.setEinheiten(verteidigendesLand.getEinheiten() - verluste.get(1));

		if(verteidigendesLand.getEinheiten() == 0) {
			ausgabeString += "Land erobert! " + verteidigendesLandString + " gehört jetzt " + angreifendesLand.getBesitzer().getName();
			verteidigendesLand.setBesitzer(angreifendesLand.getBesitzer());
			angreifendesLand.setEinheiten(angreifendesLand.getEinheiten() - 1);
			verteidigendesLand.setEinheiten(0);
			rueckgabe.add("Erobert");
		} else if(verluste.get(0) < verluste.get(1)) {
			ausgabeString += angreifendesLand.getBesitzer().getName() + " hat gewonnen ";
			rueckgabe.add(null);
		} else if(verluste.get(0) == verluste.get(1)) {
			ausgabeString += "Unentschieden! Beide verlieren eine Einheit. ";
			rueckgabe.add(null);
		} else if(verluste.get(0) > verluste.get(1)) {
			ausgabeString += verteidigendesLand.getBesitzer().getName() + " hat gewonnen ";
			rueckgabe.add(null);
		}
		
		ausgabeString += "\n" + angreifendesLandString + " hat nun noch " + angreifendesLand.getEinheiten() + " und " + verteidigendesLandString + " hat nun noch " + verteidigendesLand.getEinheiten();
		rueckgabe.add(ausgabeString);
		return rueckgabe;
	}
	
	/**
	 * setzt eine gewisse Anzahl an Einheiten auf ein Land
	 * @param anzahl
	 * @param land
	 */
	public void einheitenPositionieren(int anzahl, Land land) {
		land.setEinheiten(land.getEinheiten() + anzahl);
	}
	
	/**
	 * @param spieler
	 * @return int
	 */
	public int bekommtEinheiten(Spieler spieler) {
		int einheiten = 0;
		Spieler speicher;
		int anzahl=0;
		
		einheiten = weltVw.besitztLaender(spieler).size()/3;
		if (einheiten < 3) {
			einheiten = 3;
			for (Kontinent k : weltVw.getKontinentenListe()){
				for (int i=1;i<k.getLaender().size();i++){
					if(k.getLaender().get(i).getBesitzer() == k.getLaender().get(i-1).getBesitzer())
					{
						speicher= k.getLaender().get(i).getBesitzer();
						anzahl++;
						
					}
					if (anzahl==k.getLaender().size()){
						if (k.getName() == "Europa"){
							einheiten+=5;	
						}else if(k.getName() =="Asien"){
							einheiten+=7;
						}else if(k.getName() == "Afrika"){
							einheiten+=3;
						}else if(k.getName() == "Suedamerika"){
							einheiten+=2;
						}else if(k.getName() == "Nordamerika"){
							einheiten+=5;
						}
					}
				}
			}
			}
			
			return einheiten;
		}
	
	public void nextTurn(){
		switch(Phase){
			case VERSCHIEBEN:
				Phase = phasen.VERTEILEN;
				break;
			case ANGRIFF:
				Phase = phasen.VERSCHIEBEN;
				break;
			case VERTEILEN:
				Phase = phasen.ANGRIFF;
				break;
				
		}
	}
	/**
	 * Phasen Enum
	 */
	public enum phasen{
		VERTEILEN,ANGRIFF,VERSCHIEBEN
	}
	

	public phasen getTurn(){
		return Phase;
	}
	public Spieler nextSpieler(Spieler spieler){
		return spieler;
	}
	
	public void eroberungBesetzen(Land aLand, Land vLand, int einheiten){
		this.einheitenPositionieren(einheiten, vLand);
		this.einheitenPositionieren(-einheiten, aLand);
	}
	public boolean landWaehlen(String land, Spieler spieler) throws KannLandNichtBenutzenException{
		if(weltVw.stringToLand(land) == null){
			throw new KannLandNichtBenutzenException(land," existiert nicht");
		}else if(!weltVw.stringToLand(land).getBesitzer().equals(spieler)){
			throw new KannLandNichtBenutzenException(land," geh�rt dir nicht");	
		}else{
			return true;
		}
	}
	
	public boolean checkEinheiten(String land, int einheiten) throws NichtGenugEinheitenException{
		if(weltVw.stringToLand(land).getEinheiten() < 2){
			throw new NichtGenugEinheitenException(land, " hat zu wenig Einheiten");
		}else if(weltVw.stringToLand(land).getEinheiten() <= einheiten){
			throw new NichtGenugEinheitenException(land, " hat nicht so viele Einheiten");
		}else if(einheiten < 1){
			throw new NichtGenugEinheitenException(land, " kann nicht so wenig Einheiten verschicken");
		}else{
			return true;
		}
	}
	
}
