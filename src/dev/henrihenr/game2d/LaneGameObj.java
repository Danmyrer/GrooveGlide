package dev.henrihenr.game2d;

import dev.henrihenr.grooveglide.config.Playfield;

/**
 * Abstraktes Game-Objekt, das sich auf den Lanes des Spiels bewegen kann
 */
public abstract class LaneGameObj extends AbstractGameObj implements Playfield
{
    public enum Pos
    {
        MAXLEFT, MAXRIGHT, CENTER
    }

    private int currentLine;

    public LaneGameObj(Vertex p, Vertex v, double w, double h, Pos initialLine)
    {
        super(p, v, w, h);
        setLine(initialLine);
    }

    public LaneGameObj(Vertex p, Vertex v, double w, double h, int lane)
    {
        super(p, v, w, h);

        try
        {
            setLine(lane);
        } catch (IndexOutOfBoundsException e)
        {
            /*if (this instanceof KeyPressedGlow)
                return; // das ist so unheilig
            else
                throw e;*/ // vllt mal Prüfen, ob das hier noch notwendig ist
        }
    }

    /**
     * Gibt die Länge des Objektes in Lanes an
     * 
     * @return Länge des LaneGameObj
     */
    abstract public int getGameObjLaneLength();

    public int getMaxLeft()
    {
        return 0;
    }

    public int getMaxRight()
    {
        return LANES - getGameObjLaneLength();
    }

    public int getCenter()
    {
        return LANES / 2 - (getGameObjLaneLength() / 2);
    }

    /**
     * Erhalten der aktuellen Lane / Des aktuellen Lane-Offsets
     * 
     * @return Lane-Offset
     */
    public int getLine()
    {
        return currentLine;
    }

    /**
     * Setzt die X-Position der Hitline auf einen bestimmte Lane
     * 
     * @param lane
     * @throws IndexOutOfBoundsException wenn die Lane nicht existiert
     */
    public void setLine(int lane)
    {
        if (lane < getMaxLeft() || lane > getMaxRight())
            throw new IndexOutOfBoundsException(
                    lane + " -> Lanes sind nur zwischen " + getMaxLeft() + " und " + getMaxRight() + " erlaubt!");

        this.pos.x = PLAYFIELD_PADDED_OFFSET.x / 2 + (PLAYFIELD_PADDED.x / LANES * lane);
        this.currentLine = lane;
    }

    public void setLine(Pos lane)
    {
        switch (lane)
        {
        case MAXLEFT:
            setLine(getMaxLeft());
            break;
        case MAXRIGHT:
            setLine(getMaxRight());
            break;
        case CENTER:
            setLine(getCenter());
            break;
        }
    }

    /**
     * Bewegt die Hitline auf eine Lane um ein bestimmtes Offset
     * 
     * @param offset
     * @throws IndexOutOfBoundsException wenn die Lane nicht existiert
     */
    public void moveLine(int offset)
    {
        this.setLine(currentLine + offset);
    }

    /**
     * Bewegt das Objekt eine Lane nach Links
     */
    public void moveLineLeft()
    {
        try
        {
            moveLine(-1);
        } catch (IndexOutOfBoundsException e)
        {
            return;
        }
    }

    public void moveLineRight()
    {
        try
        {
            moveLine(1);
        } catch (IndexOutOfBoundsException e)
        {
            return;
        }
    }

    public void moveCenter()
    {
        setLine(getCenter());
    }
}
