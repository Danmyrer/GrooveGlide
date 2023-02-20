package dev.henrihenr.grooveglide.util.drawables;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import dev.henrihenr.game2d.LaneGameObj;
import dev.henrihenr.game2d.Vertex;
import dev.henrihenr.grooveglide.config.GameConfig;
import dev.henrihenr.grooveglide.config.Playfield;
import dev.henrihenr.grooveglide.util.Color;

/**
 * GameObj, das den Effekt beschreibt, der die HitLine leuchten lässt wenn eine Taste gedrückt ist
 */
public class KeyPressedGlow extends LaneGameObj implements GameConfig
{
    // Relative Objektdaten und Binds zu anderen Objekten
    public static final double KEYPRG_HEIGHT_QUO = 20;

    private Graphics2D g2d;

    /** Visueller Zustand des Objekts */
    public Visibility visibility;
    static enum Visibility
    {
        VISIBLE, HIDDEN
    }

    private final int relPos;

    /**
     * Standart-Konstruktor
     * @param zeroPos   Lane, auf der sich das erste KPGlow-Element befindet
     * @param relPos    Lane, auf der sich dieses KPGlow-Element <b>(relativ zu zeroPos)</b>
     */
    public KeyPressedGlow(int zeroPos, int relPos)
    {
        super(
            new Vertex(
                Playfield.getLanePaddedX(zeroPos + relPos), 
                HITLINE_Y - PLAYFIELD_PADDED.y / (KEYPRG_HEIGHT_QUO + 2)
            ), 
            new Vertex(), 
            LANE_LENGTH_PADDED, 
            PLAYFIELD_PADDED.y / KEYPRG_HEIGHT_QUO,
            relPos
        );

        this.relPos = relPos;
    }

    @Override
    public int getGameObjLaneLength() {
        return 1;
    }

    @Override
    public int getMaxLeft() 
    {
        return relPos;
    }

    @Override
    public int getMaxRight() 
    {
        return LANES - 4 + this.relPos;
    }

    @Override
    public int getCenter() 
    {
        return LANES / 2 - 2 + this.relPos;
    }

    /** Versteckt das Objekt */
    public void hideMe()
    {
        visibility = Visibility.HIDDEN;
        paintTo(g2d);
    }

    /** Zeigt das Objekt */
    public void showMe()
    {
        visibility = Visibility.VISIBLE;
        paintTo(g2d);
    }

    @Override
    public void paintTo(Graphics g)
    {
        g2d = (Graphics2D) g;

        java.awt.Color transpColor = new java.awt.Color(0, 0, 0, 0); // weil das scheinbar nicht mit meiner eigenen Color-Implementierung funktioniert...

        java.awt.Color color1 = visibility == Visibility.VISIBLE ? Color.MINT_CREAM : transpColor;

        GradientPaint gradient = new GradientPaint(
            new Point((int)pos.x, (int)(pos.y + height())), 
            color1, 
            new Point((int)pos.x, (int) pos.y), 
            transpColor
        );

        g2d.setPaint(gradient);
        g2d.fillRect((int) pos().x, (int) pos().y, (int) width(), (int) height());
    }
}
