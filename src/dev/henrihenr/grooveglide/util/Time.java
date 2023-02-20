package dev.henrihenr.grooveglide.util;

import java.util.List;

import javax.swing.Timer;

public class Time
{
    private Timer timer;

    private Beat beatTimer;

    private double startOffset = 0;

    private int beatMS = 1000;
    private int beat = 0;

    public Time(Beat beatTimer)
    {
        this.beatTimer = beatTimer;
    }

    public Time setStartOffset(double playOffset, double songOffset, double mapYOffset)
    {
        this.startOffset = playOffset + songOffset + mapYOffset;
        return this;
    }

    public Time setBeatMS(int ms)
    {
        this.beatMS = ms;
        return this;
    }

    public Time setInitialBeat(int beat)
    {
        this.beat = beat;
        return this;
    }

    public Time initTimer()
    {
        this.timer = new Timer(beatMS, onBeat -> 
        {
            this.beat++;
            beatTimer.onBeat(this.beat);
        });
        return this;
    }

    public void startTimer()
    {
        if(this.timer == null) return;
        if(this.timer.isRunning()) return;
        this.timer.start();
    }

    @Override
    public String toString()
    {
        return List.of(startOffset, beatMS, beat).toString();
    }
}
