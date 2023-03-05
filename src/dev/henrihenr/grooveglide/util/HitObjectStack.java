package dev.henrihenr.grooveglide.util;

import java.util.ArrayList;
import java.util.List;

import dev.henrihenr.game2d.Vertex;
import dev.henrihenr.grooveglide.config.GameConfig;
import dev.henrihenr.grooveglide.config.Playfield;
import dev.henrihenr.grooveglide.util.drawables.HitObject;

public class HitObjectStack implements GameConfig, Playfield
{
    private final List<List<HitObject>> hitObjects;
    
    private final int bufferBeats;
    private final int anchorFreq;

    private int laneOffset;

    private double bpm;

    /**
     * Konstruktor
     * 
     * @param hitObjects Liste aller HOs die in den Stack geladen werden sollen
     * @param anchorFreq Intervall, in dem neue Beats geladen werden sollen. Standart sollte = 1 sein
     * @param bufferBeats Puffer für besonders langsame PCs (am besten bei 0 stehen lassen), kann probleme verursachen
     */
    public HitObjectStack(List<List<HitObject>> hitObjects, int anchorFreq, int bufferBeats)
    {
        this.anchorFreq = anchorFreq; // in beats
        this.bufferBeats = bufferBeats;

        this.hitObjects = buildHitObjStack(hitObjects);
        this.bpm = hitObjects.get(0).get(0).bpm;
    }

    /**
     * @see HitObjectStack#HitObjectStack(List, int, int)
     */
    public HitObjectStack(List<List<HitObject>> hitObjects, int anchorFreq)
    {
        this(hitObjects, anchorFreq, 1);
    }

    /**
     * @see HitObjectStack#HitObjectStack(List, int, int)
     */
    public HitObjectStack(List<List<HitObject>> hitObjects)
    {
        this(hitObjects, 24);
    }

    /**
     * Generiert aus den eingegeben HOs einen Stack
     * 
     * @param hitObjects Liste von unbearbeiteten HOs
     * @return Stack
     */
    private List<List<HitObject>> buildHitObjStack(List<List<HitObject>> hitObjects)
    {
        List<List<HitObject>> hos = new ArrayList<>();
        
        for (int i = 0; i < hitObjects.size(); i++) 
        {
            hos.add(new ArrayList<>());
            double lastAnchorBeat = 0;
            for (int u = 0; u < hitObjects.get(i).size(); u++) 
            {
                List<HitObject> lane = hitObjects.get(i);
                HitObject ho = lane.get(u);
                double tempOffset = ho.beat - lastAnchorBeat;
                if (ho.beat - lastAnchorBeat > anchorFreq || u == 0)
                {
                    lastAnchorBeat = ho.beat;
                    hos.get(i).add(new HitObject(
                        ho.beat,
                        ho.lane,
                        ho.bpm,
                        0,
                        true
                    ));
                }
                else
                {
                    hos.get(i).add(new HitObject(
                        ho.beat,
                        ho.lane, 
                        ho.bpm,
                        tempOffset,
                        false
                        ));
                }
            }
        }
        
        return hos;
    }

    /**
     * Stellt dem Spiel einen gewisse Anzahl an HOs bereit. Hierdurch muss das spiel nicht ständig tausende Noten bewegen und sichert, dass die Noten immer Synchron bleiben
     * 
     * @param beat Aktueller Beat des Spiels
     * @return Liste an HOs mit Korrigierter X-Position
     */
    public List<List<HitObject>> getNewHitObjecs(int beat)
    {
        List<List<HitObject>> newHitObj = new ArrayList<>();
        
        if (beat % 16 == 0) changeMovementOpOffset(); // soll die lane gewechselt werden?
        for (int i = 0; i < this.laneOffset; i++) newHitObj.add(new ArrayList<>()); // leere lanes als ausgleich hinzufügen

        for (int iLane = 0; iLane < hitObjects.size(); iLane++) // jede lane
        {
            newHitObj.add(new ArrayList<HitObject>());
            if (this.hitObjects.get(iLane).size() == 0) continue;
            if (this.hitObjects.get(iLane).get(0).beat <= beat) // NOTIZ AN MICH SELBST ICH MACH SCHON NE BEAT-KORREKTUR AMK HIER NIX ÄNDERN
            {
                int counter = 0;
                for (int iObject = 0; iObject < this.hitObjects.get(iLane).size(); iObject++)
                {
                    if (iObject != 0 && hitObjects.get(iLane).get(iObject).anchor) break;
                    newHitObj.get(iLane + this.laneOffset).add(hitObjects.get(iLane).get(iObject));
                    counter++;
                }
                while(counter > 0) // genutzte elemente aus dem stack entfernen
                {
                    this.hitObjects.get(iLane).remove(0);
                    counter--;
                }
            }

            fixHitObjectY(newHitObj.get(iLane), beat);
            moveHitObjectXOneLaneYUp(newHitObj.get(iLane));
        }
        

        movementOp(newHitObj, this.laneOffset);
        return newHitObj;
    }

    /**
     * Korrigiert die X-Position der zu ladenen HitObjects, um auch gebrochene Beats (Beat 3,45556) zu ermöglichen
     * @param hitObjects
     * @param beat
     */
    private void fixHitObjectY(List<HitObject> hitObjects, int beat) // HIER NICHTS MEHR BERÜHREN GERADE KLAPPTS UND WENN DA WAS KAPUTT GEHT GEHE ICH KAPUTT
    {
        if (hitObjects.size() == 0) return;

        /*
         * Seconds per Beat = 60 / bpm
         * Scroll per Beat = SCROLL_PER_SEC * 60 / bpm
         */
        double scrollPerBeat = (SCROLL_PER_SEK * 60) / this.bpm;

        HitObject firstHO = hitObjects.get(0);
        double beatDelta = beat - firstHO.beat;
        double offset = scrollPerBeat * beatDelta; // hier kommt KEIN Minus hin, weil die noten immer NACH ihrem Beat geladen Werden
        firstHO.setPos(new Vertex(firstHO.pos().x, offset));

        for (int i = 1; i < hitObjects.size(); i++)
        {
            double beatdelta = hitObjects.get(i).beat - firstHO.beat;//hitObjects.get(i).offset;
            Vertex currentPos = hitObjects.get(i).pos();
            hitObjects.get(i).setPos(new Vertex(currentPos.x, firstHO.pos().y-(scrollPerBeat * beatdelta))); // hier kommt ein Minus hin weil die noten NACH ihrem VORGÄNGER geladen werden
        }
    }

    /**
     * Verschiebt alle neuen noten ein mal umd die Höhe des Spielfelds. So werden sie außerhalb des Sichtbaren bereichs geladen.
     * @param hitObjects zu verschiebende HitObjects
     */
    private void moveHitObjectXOneLaneYUp(List<HitObject> hitObjects)
    {
        for (HitObject hitObject : hitObjects) 
        {
            Vertex currentPos = hitObject.pos();
            double offset = PLAYFIELD.y;
            hitObject.setPos(new Vertex(currentPos.x, currentPos.y - offset));
        }
    }

    /**
     * Verschiebt noten um ein offset innerhalb des Stacks
     * @param list Stack
     * @param offset Offset der Note in Lanes
     */
    private void movementOp(List<List<HitObject>> list, int offset)
    {
        for (List<HitObject> hitObjects : list) 
        {
            for (HitObject hitObject : hitObjects) 
            {
                hitObject.lane += offset;
                
                double tempPosY = hitObject.pos().y;
                hitObject.setPos(new Vertex(Playfield.getLanePaddedX(hitObject.lane), tempPosY));
            }
        }
    }

    /**
     * Bestimmt zufällig ob - und wenn ja - in welche Richtung die folgenden Noten verschoben werden sollen.
     */
    private void changeMovementOpOffset()
    {
        boolean movementOp = (Math.random() <= 0.5);
        if (!movementOp) return;

        int movementDir;
        if (this.laneOffset <= 0) movementDir = 1;
        else if (this.laneOffset >= LANES - 4) movementDir = -1;
        else movementDir = (Math.random() <= 0.5) ? 1 : -1;

        this.laneOffset += movementDir;
    }

    /**
     * Zählt die verbleibenden HitObjects im Stack
     * @return int des Wertes
     */
    public int getRemainingHitObjects()
    {
        int counter = 0;
        for (List<HitObject> list : hitObjects) {
            counter += list.size();
        }
        return counter;
    }

    @Override
    public String toString()
    {
        return List.of(anchorFreq, bufferBeats, hitObjects.toString()).toString();
    }
}
