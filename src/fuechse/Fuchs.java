package fuechse;

import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * Ein simples Modell eines Fuchses.
 * F�chse altern, bewegen sich, fressen Hasen und sterben.
 * 
 * @author David J. Barnes und Michael K�lling
 * @version 2006.03.30
 */
public class Fuchs
{
    // Eigenschaften aller F�chse (statische Datenfelder)
    
    // Das Alter, in dem ein Fuchs geb�rf�hig wird.
    private static final int GEBAER_ALTER = 10;
    // Das H�chstalter eines Fuchses.
    private static final int MAX_ALTER = 150;
    // Die Wahrscheinlichkeit, mit der ein Fuchs Nachwuchs geb�rt.
    private static final double GEBAER_WAHRSCHEINLICHKEIT = 0.09;
    // Die maximale Gr��e eines Wurfes (Anzahl der Jungen).
    private static final int MAX_WURFGROESSE = 3;
    // Der N�hrwert eines einzelnen Hasen. Letztendlich ist
    // dies die Anzahl der Schritte, die ein Fuchs bis zur
    //n�chsten Mahlzeit laufen kann.
    private static final int HASEN_NAEHRWERT = 4;
	// Ein Zufallsgenerator, der die Geburten beeinflusst.
    private static final Random rand = new Random();
    
    // Individuelle Eigenschaften (Instanzfelder).

    // Das Alter dieses Fuchses.
    private int alter;
    // Ist dieser Fuchs noch lebendig?
    private boolean lebendig;
    // Die Position dieses Fuchses
    private Position position;
    // Der Futter-Level, der durch das Fressen von Hasen erh�ht wird.
    private int futterLevel;

    /**
     * Erzeuge einen Fuchs. Ein Fuchs wird entweder neu geboren
     * (Alter 0 Jahre und nicht hungrig) oder mit einem zuf�lligen Alter.
     * 
     * @param zufaelligesAlter falls true, hat der neue Fuchs ein 
     *        zuf�lliges Alter und einen zuf�lligen Futter-Level.
     */
    public Fuchs(boolean zufaelligesAlter)
    {
        alter = 0;
        lebendig = true;
        if(zufaelligesAlter) {
            alter = rand.nextInt(MAX_ALTER);
            futterLevel = rand.nextInt(HASEN_NAEHRWERT);
        }
        else {
            // leave age at 0
            futterLevel = HASEN_NAEHRWERT;
        }
    }
    
    /**
     * Das was ein Fuchs die meiste Zeit tut: er jagt Hasen.
     * Dabei kann er Nachwuchs geb�ren, vor Hunger sterben oder
     * an Altersschw�che.
     * @param aktuellesFeld Das aktuell belegte Feld.
     * @param naechstesFeld Das zu belegende Feld.
     * @param neueFuechse Liste, in die neue F�chse eingef�gt werden.
     */
    public void jage(Feld aktuellesFeld, Feld naechstesFeld, List<Fuchs> neueFuechse)
    {
        alterErhoehen();
        hungerVergroessern();
        if(lebendig) {
            // neue F�chse werden in Nachbarpositionen geboren.
            int geburten = gebaereNachwuchs();
            for(int b = 0; b < geburten; b++) {
                Fuchs neuerFuchs = new Fuchs(false);
                neueFuechse.add(neuerFuchs);
                Position pos = naechstesFeld.zufaelligeNachbarposition(position);
                neuerFuchs.setzePosition(pos);
                naechstesFeld.platziere(neuerFuchs, pos);
            }
            // In die Richtung bewegen, in der Futter gefunden wurde.
            Position neuePosition = findeNahrung(aktuellesFeld, position);
            if(neuePosition == null) {  // kein Futter - zuf�llig bewegen
                neuePosition = naechstesFeld.freieNachbarposition(position);
            }
            if(neuePosition != null) {
                setzePosition(neuePosition);
                naechstesFeld.platziere(this, neuePosition);
            }
            else {
                // weder Bleiben noch Gehen m�glich - �berpopulation - kein Platz 
                lebendig = false;
            }
        }
    }
    
    /**
     * Suche nach Nahrung (Hasen) in den Nachbarpositionen.
     * Es wird nur der erste lebendige Hase gefressen.
     * @param feld das Feld, in dem gesucht werden soll.
     * @param position die Position, an der sich der Fuchs befindet.
     * @return die Position mit Nahrung, oder null, wenn keine vorhanden.
     */
    private Position findeNahrung(Feld feld, Position position)
    {
        Iterator<Position> nachbarPositionen =
                               feld.nachbarpositionen(position);
        while(nachbarPositionen.hasNext()) {
            Position pos = nachbarPositionen.next();
            Object tier = feld.gibObjektAn(pos);
            if(tier instanceof Hase) {
                Hase hase = (Hase) tier;
                if(hase.istLebendig()) { 
                    hase.setzeGefressen();
                    futterLevel = HASEN_NAEHRWERT;
                    return pos;
                }
            }
        }
        return null;
    }
        
    /**
     * Geb�re Nachwuchs, wenn dieser Fuchs geb�rf�hig ist.
     * @return die Anzahl der Neugeborenen (kann Null sein).
     */
    private int gebaereNachwuchs()
    {
        int geburten = 0;
        if(kannGebaeren() && rand.nextDouble() <= GEBAER_WAHRSCHEINLICHKEIT) {
            geburten = rand.nextInt(MAX_WURFGROESSE) + 1;
        }
        return geburten;
    }

    /**
     * Ein Fuchs kann geb�ren, wenn er das geb�rf�hige
     * Alter erreicht hat.
     */
    private boolean kannGebaeren()
    {
        return alter >= GEBAER_ALTER;
    }
    
    /**
     * Erh�he das Alter dieses Fuchses. Dies kann zu seinem
     * Tod f�hren.
     */
    private void alterErhoehen()
    {
        alter++;
        if(alter > MAX_ALTER) {
            lebendig = false;
        }
    }
    
    /**
     * Vergr��ere den Hunger dieses Fuchses. Dies kann zu seinem
     * Tode f�hren.
     */
    private void hungerVergroessern()
    {
        futterLevel--;
        if(futterLevel <= 0) {
            lebendig = false;
        }
    }
    
    /**
     * Pr�fe, ob dieser Fuchs noch lebendig ist.
     * @return true wenn dieser Fuchs noch lebt.
     */
    public boolean istLebendig()
    {
        return lebendig;
    }

    /**
     * Setze die Position dieses Fuchses.
     * @param zeile die vertikale Koordinate der Position.
     * @param spalte die horizontale Koordinate der Position.
     */
    public void setzePosition(int zeile, int spalte)
    {
        this.position = new Position(zeile, spalte);
    }

    /**
     * Setze die Position dieses Fuchses.
     * @param position die Position dieses Fuchses.
     */
    public void setzePosition(Position position)
    {
        this.position = position;
    }
}
