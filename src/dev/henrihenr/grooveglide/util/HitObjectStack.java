package dev.henrihenr.grooveglide.util;

import java.util.ArrayList;
import java.util.List;

import dev.henrihenr.grooveglide.util.drawables.HitObject;

public class HitObjectStack 
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

    public int getRemainingHitObjects()
    {
        int counter = 0;
        for (List<HitObject> list : hitObjects) {
            counter += list.size();
        }
        return counter;
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
                        0,
                        ho.lane,
                        ho.bpm,
                        tempOffset,
                        true
                    ));
                }
                else
                {
                    hos.get(i).add(new HitObject(
                        ho.beat - lastAnchorBeat,
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
            if (this.hitObjects.get(i).get(0).beat >= beat)
            {
                int counter = 0;
                for (HitObject hitObject : this.hitObjects.get(i)) 
                {
                    if (hitObject.anchor) break;
                    newHitObj.get(i).add(hitObject);
                    counter++;
                }
                while(counter > 0) // elemente von dem stack entfernen
                {
                    this.hitObjects.get(i).remove(0);
                    counter--;
                }
            }
        }
        return newHitObj;
    }

    @Override
    public String toString()
    {
        return List.of(anchorFreq, bufferBeats, hitObjects.toString()).toString();
    }
}
