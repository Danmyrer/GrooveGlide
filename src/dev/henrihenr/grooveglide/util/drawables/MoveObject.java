package dev.henrihenr.grooveglide.util.drawables;

import java.awt.Graphics;

import dev.henrihenr.grooveglide.util.Color;

public class MoveObject extends HitObject
{
    public MoveObject(double beat, int lane, double bpm, double offset) 
    {
        super(beat, lane, bpm, offset);
    }

    public MoveObject(double beat, int lane, int bpm)
    {
        this(beat, lane, bpm, 0);
    }

    public MoveObject(int lane, int bpm)
    {
        this(0, lane, bpm);
    }
    
    @Override
    public void paintTo(Graphics g) 
    {
        g.setColor(Color.AMARANTH_PURPLE);
        g.fillRect((int) pos().x, (int) pos().y, (int) width(), (int) height());
    }
}