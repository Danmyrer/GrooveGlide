package dev.henrihenr.grooveglide.util;

import java.util.List;

import javax.swing.Timer;

import dev.henrihenr.grooveglide.util.sound.Music;

public class BeatTimer extends Thread
{
    private Timer timer;

    private Beat beatTimer;

    private long startOffset = 0;

    private long beatMS = 1000;
    private long beatNS = -1;
    private int beat = 0;

    private long lastNanoTime = 0;

    public BeatTimer(Beat beatTimer)
    {
        this.beatTimer = beatTimer;
    }

    public BeatTimer setStartOffset(double initialOffset)
    {
        this.startOffset = Math.round(Music.offset(initialOffset));
        return this;
    }

    public BeatTimer setBeatMS(long ms)
    {
        this.beatMS = ms;
        return this;
    }

    public BeatTimer setInitialBeat(int beat)
    {
        this.beat = beat;
        return this;
    }

    public BeatTimer initTimer()
    {
        this.beatNS = this.beatMS * 1000000;
        this.timer = new Timer(1, check -> 
        {
            if (System.nanoTime() - this.lastNanoTime >= this.beatNS)
            {
                this.lastNanoTime += this.beatNS;
                beat++;
                beatTimer.onBeat(beat);
            }
        });
        return this;
    }

    @Override
    public void run()
    {
        if(this.timer == null) return;
        if(this.beatNS == -1) return;

        if(this.timer.isRunning()) return;

        if (this.startOffset > 0) try
        {
            Thread.sleep(startOffset);
        } 
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        this.lastNanoTime = System.nanoTime() - this.beatNS;
        this.timer.start();
    }

    public int getBeat()
    {
        return this.beat;
    }

    @Override
    public String toString()
    {
        return List.of(startOffset, beatMS, beat).toString();
    }
}
