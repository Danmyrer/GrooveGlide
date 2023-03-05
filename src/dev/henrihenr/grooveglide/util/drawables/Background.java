package dev.henrihenr.grooveglide.util.drawables;

import java.awt.Graphics;

import dev.henrihenr.game2d.AbstractGameObj;
import dev.henrihenr.game2d.Vertex;
import dev.henrihenr.grooveglide.config.Playfield;
import dev.henrihenr.grooveglide.util.Color;

/**
 * GameObj, das den Hintergrund beschreibt
 */
public class Background extends AbstractGameObj implements Playfield
{
    private java.awt.Color color;

    /**
     * Konstruktor der Klasse.
     * @param pos   Position des Objektes
     * @param width Breite des Objektes
     * @param height    Höhe des Objektes
     */
    public Background(Vertex pos, double width, double height, java.awt.Color color)
    {
        super(pos, new Vertex(), width, height);
        this.color = color;
    }
    
    /**
     * Konstruktor der Klasse. Setzt die Maße des Objects so, dass der gesamte Hintergrund gefüllt ist.
     * @see Background#Background(Vertex, double, double)
     */
    public Background()
    {
        this(new Vertex(), PLAYFIELD.x, PLAYFIELD.y, Color.GUNMETAL_DARK);
    }

    @Override
    public void paintTo(Graphics g)
    {
        g.setColor(color);
        g.fillRect((int) pos().x, (int) pos().y, (int) width(), (int) height());
    }
}
