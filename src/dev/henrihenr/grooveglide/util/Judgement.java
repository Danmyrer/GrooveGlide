package dev.henrihenr.grooveglide.util;

import java.util.List;

import dev.henrihenr.grooveglide.config.Playfield;
import dev.henrihenr.grooveglide.util.drawables.HitObject;
import dev.henrihenr.grooveglide.util.drawables.Hitline;

/**
 * Jeweilige Bewertungsklassen, die vergeben werden können
 */
public enum Judgement implements Playfield
{
    NONE, MISS, E_OKAY, E_GREAT, E_PERFECT, L_PERFECT, L_GREAT, L_OKAY;

    // Judgements für frühe Zeiten
    public static final double JUDGEMENT_TIME_E_OKAY = 0.1;//0.065;
    public static final double JUDGEMENT_TIME_E_GREAT = 0.06;//0.045;
    public static final double JUDGEMENT_TIME_E_PERFECT = 0.04;//0.03;

    // Judgements für späte Zeiten
    public static final double JUDGEMENT_TIME_L_PERFECT = -0.035;//-0.02;
    public static final double JUDGEMENT_TIME_L_GREAT = -0.05;//-0.035;
    public static final double JUDGEMENT_TIME_L_OKAY = -0.08;//-0.05;

    /**
     * Offset, dass die Position der Hitline beschreibt
     * @implNote Idealerweise nicht statisch, aber was soll man machen 🤷
     */
    public static int laneOffset;

    public static void setLaneOffset(int offset)
    {
        if (offset > LANES - 4 || offset < 0) throw new IndexOutOfBoundsException("Lane existiert nicht!");

        laneOffset = offset;
    }

    public static void moveLaneOffset(Hitline.Direction dir)
    {
        try
        {
            if (dir == Hitline.Direction.LEFT)
            {
                setLaneOffset(laneOffset - 1);
            }
            else if (dir == Hitline.Direction.RIGHT)
            {
                setLaneOffset(laneOffset + 1);
            }
            else
            {
                setLaneOffset(LANES / 2 - 2);
            }
        }
        catch (IndexOutOfBoundsException e) { return; }
    }

    /**
     * Berechnet die Absolute distanz eines {@link HitObject} zur {@link Hitline}
     * @param lane  Lane, auf der sich das HO befindet
     * @param index Index / Position des HO
     * @return  Ergebnis der Rechnung
     */
    private static double checkDistanceToHitObject(int lane, int index, List<List<HitObject>> hitObject)
    {
        return hitObject.get(lane).get(index).pos().y - HITLINE_Y;
    }

    /**
     * Berechnet die relative / playfieldunabhängige Distanz eines {@link HitObject}
     * 
     * @see Demo#checkDistanceToHitObject(int, int)
     * @param lane  Lane, auf der sich das HO befindet
     * @return  Ergebnis der Rechnung
     */
    private static double checkRelativeDistanceToHitObject(int lane, List<List<HitObject>> hitObject)
    {
        return checkDistanceToHitObject(lane, 0, hitObject) / PLAYFIELD_PADDED.y;
    }

    /**
     * Berechnet das Judgement-Ergebnis für das nächste HO
     * @param lane  Lane, auf der sich das HO befindet
     * @return  Ergebnis der Rechnung
     */
    public static Judgement judgeTime(int lane, List<List<HitObject>> hitObject)
    {
        if (hitObject.get(lane).size() == 0) return Judgement.NONE;

        double time = checkRelativeDistanceToHitObject(lane, hitObject);

        if (time <= JUDGEMENT_TIME_E_PERFECT && time >= JUDGEMENT_TIME_L_PERFECT)
        {
            return time < 0 ? Judgement.E_PERFECT : Judgement.L_PERFECT;
        }
        if (time <= JUDGEMENT_TIME_E_GREAT && time >= JUDGEMENT_TIME_L_GREAT)
        {
            return time < 0 ? Judgement.E_GREAT : Judgement.L_GREAT;
        }
        if (time <= JUDGEMENT_TIME_E_OKAY && time >= JUDGEMENT_TIME_L_OKAY)
        {
            return time < 0 ? Judgement.E_OKAY : Judgement.L_OKAY;
        }
        return Judgement.NONE;
    }
}
