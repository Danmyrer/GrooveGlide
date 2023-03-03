package dev.henrihenr.grooveglide.util;

import java.util.ArrayList;
import java.util.List;

import dev.henrihenr.game2d.Vertex;
import dev.henrihenr.grooveglide.config.GameConfig;
import dev.henrihenr.grooveglide.util.drawables.HitObject;

public class HitObjectStack implements GameConfig
{
    private final List<List<HitObject>> hitObjects;
    
    private final int bufferBeats;
    private final int anchorFreq;

    private double bpm;

    public HitObjectStack(List<List<HitObject>> hitObjects, int anchorFreq, int bufferBeats)
    {
        this.anchorFreq = anchorFreq; // in beats
        this.bufferBeats = bufferBeats;

        this.hitObjects = buildHitObjStack(hitObjects);
        this.bpm = hitObjects.get(0).get(0).bpm;
    }

    public HitObjectStack(List<List<HitObject>> hitObjects, int anchorFreq)
    {
        this(hitObjects, anchorFreq, 1);
    }

    public HitObjectStack(List<List<HitObject>> hitObjects)
    {
        this(hitObjects, 24);
    }

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

    public List<List<HitObject>> getNewHitObjecs(int beat)
    {
        List<List<HitObject>> newHitObj = new ArrayList<>();
        
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
                    newHitObj.get(iLane).add(hitObjects.get(iLane).get(iObject));
                    counter++;
                }
                while(counter > 0) // genutzte elemente aus dem stack entfernen
                {
                    this.hitObjects.get(iLane).remove(0);
                    counter--;
                }
            }

            fixHitObjectX(newHitObj.get(iLane), beat);
        }
        newHitObj.forEach(x -> {
            System.out.print("[");
            x.forEach(y -> {
                System.out.print("X");
            });
            System.out.println("]");
        });

        System.out.println("Remaining: " + this.getRemainingHitObjects());
        return newHitObj;
    }

    private void fixHitObjectX(List<HitObject> hitObjects, int beat) // HIER NICHTS MEHR BERÜHREN GERADE KLAPPTS UND WENN DA WAS KAPUTT GEHT GEHE ICH KAPUTT
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
