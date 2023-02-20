package dev.henrihenr.grooveglide.util.drawables;

import java.awt.Graphics;

import dev.henrihenr.game2d.AbstractGameObj;
import dev.henrihenr.game2d.Vertex;
import dev.henrihenr.grooveglide.config.GameConfig;
import dev.henrihenr.grooveglide.config.Playfield;
import dev.henrihenr.grooveglide.util.Color;

/**
 * GameObj, das ein HitObject beschreibt
 */
public class HitObject extends AbstractGameObj implements Playfield, GameConfig
{
    // Relative Objektdaten und Binds zu anderen Objekten
    public static final double HITOBJ_HEIGHT_QUO = 40;

    public int lane;
    public double beat;
    public double bpm;
    public double offset;
    public boolean anchor = false;

    /**
     * Standart-Konstruktor
     * @param beat  Beat, in dem das Objekt genutzt wird
     * @param lane  Lane, auf dem das Objekt genutzt wird
     * @param bpm   Bpm. Relevant für die y-Positionsbestimmung
     * @param offset    Das Offset (int MS), das HitObjects haben können (nicht mit Music-Offset verwechseln!)
     */
    public HitObject(double beat, int lane, double bpm, double offset, boolean anchor)
    {
        super(
            new Vertex(
                Playfield.getLanePaddedX(lane), 
                - (
                    (PLAYFIELD_PADDED.y * (1 - HITLINE_REL_POS)) +
                    (PLAYFIELD_PADDED.y / HITOBJ_HEIGHT_QUO) + 
                    (SCROLL_PER_SEK / 1000.0 * offset) +
                    (SCROLL_PER_SEK * ((60.0 * beat) / bpm))
                )
            ), 
        new Vertex(0, SCROLL_SPEED), 
        LANE_LENGTH_PADDED,
        PLAYFIELD_PADDED.y / HITOBJ_HEIGHT_QUO);
        this.lane = lane;
        this.beat = beat;
        this.bpm = bpm;
        this.offset = offset;
        this.anchor = anchor;
    }

    public HitObject(double beat, int lane, double bpm, double offset)
    {
        this(beat, lane, bpm, offset, false);
    }

    /**
     * Konstruktor
     * @see HitObject#HitObject(double, int, int, int)
     * @param beat  Beat, in dem das Objekt genutzt wird
     * @param lane  Lane, auf dem das Objekt genutzt wird
     * @param bpm   Bpm. Relevant für die y-Positionsbestimmung
     */
    public HitObject(double beat, int lane, int bpm)
    {
        this(beat, lane, bpm, 0, false);
    }

    /**
     * Konstruktor
     * @see HitObject#HitObject(double, int, int)
     * @param beat  Beat, in dem das Objekt genutzt wird
     * @param lane  Lane, auf dem das Objekt genutzt wird
     */
    public HitObject(int lane, int bpm)
    {
        this(0, lane, bpm);
    }

    public void setPos(Vertex pos)
    {
        this.pos = pos;
    }
    
    @Override
    public void paintTo(Graphics g)
    {
        g.setColor(Color.SIZZLING_RED);
        g.fillRect((int) pos().x, (int) pos().y, (int) width(), (int) height());
    }
}
