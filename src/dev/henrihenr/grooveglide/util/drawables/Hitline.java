package dev.henrihenr.grooveglide.util.drawables;

import java.awt.Graphics;

import dev.henrihenr.game2d.LaneGameObj;
import dev.henrihenr.game2d.Vertex;
import dev.henrihenr.grooveglide.util.Color;

/**
 * GameObj, das die Hitline beschreibt
 */
public class Hitline extends LaneGameObj
{
    // Relative Objektdaten und Binds zu anderen Objekten
    public static final double HITLINE_HEIGHT_QUO = 120;

    private State state;
    public enum State {
        REGULAR, TRIGGERED
    }
    public enum Direction {
        LEFT, RIGHT, CENTER
    }

    /**
     * Konstruktor der Klasse 😎. Berechnet die Maße und Position anhand der Spielfeldgröße
     */
    public Hitline()
    {
        super(
            new Vertex(
                PLAYFIELD_PADDED_OFFSET.x / 2, 
                HITLINE_Y), 
            new Vertex(), 
            PLAYFIELD_PADDED.x - (PLAYFIELD_PADDED.x / (LANES - 3)), 
            PLAYFIELD_PADDED.y / HITLINE_HEIGHT_QUO,
            LaneGameObj.Pos.CENTER
        );
    }

    @Override
    public int getGameObjLaneLength()
    {
        return 4;
    }

    public State getState()
    {
        return this.state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    @Override
    public void paintTo(Graphics g)
    {
        g.setColor(this.state == State.TRIGGERED ? Color.RED : Color.MINT_CREAM);
        g.fillRect((int) pos().x, (int) pos().y, (int) width(), (int) height());
    }
}
