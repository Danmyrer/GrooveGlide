package dev.henrihenr.grooveglide.config;

import dev.henrihenr.game2d.Vertex;

/**
 * Die ganze Mathematik für die Berechnung diverser Positionen auf dem Playfield
 */
public interface Playfield 
{
    // region Manuell setzbare Werte
    // public static final Vertex PLAYFIELD = new Vertex(600, 1000);
    public static final Vertex PLAYFIELD = new Vertex(500, 750);

    public static final Vertex PLAYFIELD_PADDING = new Vertex(0.1, 0); // Y bloß nicht ändern das macht alles Kaputt

    public static final int LANES = 6;
    public static final double LANE_PADDING = 0.1;

    /** Höhe der Hitline relativ zu {@link Playfield#PLAYFIELD_PADDED} */
    public static final double HITLINE_REL_POS = 0.9;
    // endregion 🎃

    // region Automatisch berechnete Werte
    /** Padding, das für den Ausgleich des {@link Playfield#PLAYFIELD_PADDED} notwendig ist */
    public static final Vertex PLAYFIELD_PADDED_OFFSET = new Vertex(
        PLAYFIELD.x * PLAYFIELD_PADDING.x,
        PLAYFIELD.y * PLAYFIELD_PADDING.y
    );
    /** Größe des Playfields nach Abzug des Paddings */
    public static final Vertex PLAYFIELD_PADDED = new Vertex(
        PLAYFIELD.x - PLAYFIELD_PADDED_OFFSET.x,
        PLAYFIELD.y - PLAYFIELD_PADDED_OFFSET.y
    );

    /** Länge der einzelnen Lanes <b>ohne Padding</b> */
    public static final double LANE_LENGTH = PLAYFIELD_PADDED.x / LANES;
    /** Padding, das für den Ausgleich des {@link Playfield#LANE_LENGTH_PADDED} notwendig ist */
    public static final double LANE_LENGTH_PADDED_OFFSET = LANE_LENGTH * LANE_PADDING;
    /** Länge der Lane nach Abzug des Paddings */
    public static final double LANE_LENGTH_PADDED = LANE_LENGTH - LANE_LENGTH_PADDED_OFFSET;

    public static final double HITLINE_Y = PLAYFIELD.y * HITLINE_REL_POS;

    /**
     * Berechnung der X-Position einer Lane <b>ohne Lane-Padding</b>
     * @param lane
     * @return  X-Position
     */
    public static double getLaneX(int lane)
    {
        return PLAYFIELD_PADDED_OFFSET.x / 2 + lane * LANE_LENGTH;
    }

    /**
     * Berechnung der X-Position einer Lane mit Lane-Padding
     * @param lane
     * @return  X-Position
     */
    public static double getLanePaddedX(int lane)
    {
        return getLaneX(lane) + LANE_LENGTH_PADDED_OFFSET / 2;
    }
    // endregion
}