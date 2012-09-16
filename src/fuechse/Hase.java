package fuechse;

import java.util.List;
import java.util.Random;

/**
 * Ein einfaches Modell eines Hasen.
 * Ein Hase altert, bewegt sich, geb�rt Nachwuchs und stirbt.
 * 
 * @author David J. Barnes und Michael K�lling
 * @version 2006.03.30
 */
public class Hase
{
    // Eigenschaften aller Hasen (statische Datenfelder).

    // Das Alter, in dem ein Hase geb�rf�hig wird.
    private static final int GEBAER_ALTER = 5;
    // Das H�chstalter eines Hasen.
    private static final int MAX_ALTER = 50;
    // Die Wahrscheinlichkeit, mit der ein Hase Nachwuchs geb�rt.
    private static final double GEBAER_WAHRSCHEINLICHKEIT = 0.15;
    // Die maximale Gr��e eines Wurfes (Anzahl der Jungen)
    private static final int MAX_WURFGROESSE = 5;
    // Ein Zufallsgenerator, der die Geburten beeinflusst.
    private static final Random rand = new Random();
    
    // Individuelle Eigenschaften eines Hasen (Instanzfelder).
    
    // Das Alter dieses Hasen.
    private int alter;
    // Ist dieser Hase noch lebendig?
    private boolean lebendig;
    // Die Position dieses Hasen
    private Position position;

    /**
     * Erzeuge einen neuen Hasen. Ein neuer Hase kann das Alter 0 
     *(neu geboren) oder ein zuf�lliges Alter haben.
     * @param zufaelligesAlter soll der Hase ein zuf�lliges Alter haben?
     */
    public Hase(boolean zufaelligesAlter)
    {
        alter = 0;
        lebendig = true;
        if(zufaelligesAlter) {
            alter = rand.nextInt(MAX_ALTER);
        }
    }
    
    /**
     * Das was ein Hase die meiste Zeit tut - er l�uft herum.
     * Manchmal geb�rt er Nachwuchs und irgendwann stirbt er
     * an Altersschw�che.
     * @param naechstesFeld Das Feld, in das hinein kopiert wird.
     * @param neueHasen Eine Liste, in die neue Hasen eingef�gt werden.
     */
    public void laufe(Feld naechstesFeld, List<Hase> neueHasen)
    {
        alterErhoehen();
        if(lebendig) {
            int geburten = gebaereNachwuchs();
            for(int b = 0; b < geburten; b++) {
                Hase neuerHase = new Hase(false);
                neueHasen.add(neuerHase);
                Position pos = naechstesFeld.zufaelligeNachbarposition(position);
                neuerHase.setzePosition(pos);
                naechstesFeld.platziere(neuerHase, pos);
            }
            Position neuePosition = naechstesFeld.freieNachbarposition(position);
            // nur in das n�chste Feld setzen, wenn eine Position frei ist
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
     * Erh�he das Alter dieses Hasen.
     * Dies kann zu seinem Tod f�hren.
     */
    private void alterErhoehen()
    {
        alter++;
        if(alter > MAX_ALTER) {
            lebendig = false;
        }
    }
    
    /**
     * Geb�re Nachwuchs, wenn dieser Hase geb�rf�hig ist.
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
     * Ein Hase kann geb�ren, wenn er das geb�rf�hige Alter
     * erreicht hat.
     * @return true falls dieser Hase geb�ren kann, false sonst.
     */
    private boolean kannGebaeren()
    {
        return alter >= GEBAER_ALTER;
    }
    
    /**
     * Pr�fe, ob dieser Hase noch lebendig ist.
     * @return true wenn dieser Hase noch lebt.
     */
    public boolean istLebendig()
    {
        return lebendig;
    }

    /**
     * Signalisiere, dass dieser Hase gefressen wurde   :-(
     */
    public void setzeGefressen()
    {
        lebendig = false;
    }
    
    /**
     * Setze die Position dieses Hasen.
     * @param zeile die vertikale Koordinate der Position.
     * @param spalte die horizontale Koordinate der Position.
     */
    public void setzePosition(int zeile, int spalte)
    {
        this.position = new Position(zeile, spalte);
    }

    /**
     * Setze die Position dieses Hasen.
     * @param position die Position dieses Hasen.
     */
    public void setzePosition(Position position)
    {
        this.position = position;
    }
}
