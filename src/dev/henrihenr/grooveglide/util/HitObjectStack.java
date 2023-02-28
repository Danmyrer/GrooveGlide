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

    public HitObjectStack(List<List<HitObject>> hitObjects, int anchorFreq, int bufferBeats)
    {
        this.anchorFreq = anchorFreq; // in beats
        this.bufferBeats = bufferBeats;

        this.hitObjects = buildHitObjStack(hitObjects);
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
                if (ho.beat - lastAnchorBeat > anchorFreq || u == 0)
                {
                    double tempOffset = ho.beat - lastAnchorBeat;
                    lastAnchorBeat = ho.beat;
                    hos.get(i).add(new HitObject(
                        ho.beat,
                        ho.lane,
                        ho.bpm,
                        tempOffset,
                        true
                    ));
                }
                else
                {
                    hos.get(i).add(new HitObject(
                        ho.beat,
                        ho.lane, 
                        ho.bpm,
                        0
                        ));
                }
            }
        }
        
        return hos;
    }

    public List<List<HitObject>> getNewHitObjecs(int beat)
    {
        List<List<HitObject>> newHitObj = new ArrayList<>();
        
        for (int i = 0; i < hitObjects.size(); i++) // jede lane
        {
            newHitObj.add(new ArrayList<HitObject>());
            if (this.hitObjects.get(i).get(0).beat + 1 <= beat)
            {
                int counter = 0;
                for (int j = 0; j < this.hitObjects.get(i).size(); j++)
                {
                    if (j != 0 && hitObjects.get(i).get(j).anchor) break;
                    newHitObj.get(i).add(hitObjects.get(i).get(j));
                    counter++;
                }
                while(counter > 0) // genutzte elemente von dem stack entfernen
                {
                    this.hitObjects.get(i).remove(0);
                    counter--;
                }
            }

            fixHitObjectX(newHitObj.get(i));
        }
        newHitObj.forEach(x -> {
            System.out.print("[");
            x.forEach(y -> {
                System.out.print("X");
            });
            System.out.println("]");
        });
        return newHitObj;
    }

    private void fixHitObjectX(List<HitObject> hitObjects)
    {
        for (int i = 1; i < hitObjects.size(); i++)
        {
            double beatdelta = hitObjects.get(i).beat - hitObjects.get(0).beat;
            Vertex currentPos = hitObjects.get(i).pos();
            hitObjects.get(i).setPos(new Vertex(currentPos.x, currentPos.y - 100 * beatdelta));
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
