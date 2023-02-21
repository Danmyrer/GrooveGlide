package dev.henrihenr.grooveglide.util.sound;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

import dev.henrihenr.grooveglide.config.GameConfig;
import dev.henrihenr.grooveglide.config.Playfield;

/**
 * Klasse, die die Hintergrund-Musik beschreibt
 */
public class Music extends Thread implements GameConfig, Playfield
{
    private File soundFile;
    private AudioInputStream audioIn;
    private Clip clip;
    private FloatControl volumeControl;

    /** Offset des Audio <b>in MS</b> */
    private double offset;

    /**
     * Standart-Konstruktor
     * @param file  Dateipfad
     * @param offset    Die Differenz zwischen dem Start der Map und dem Start des Songs
     */
    public Music(String file, int offset)
    {
        try
        {
            this.soundFile = new File(file);
            this.audioIn = AudioSystem.getAudioInputStream(this.soundFile);
            this.clip = AudioSystem.getClip();
            this.offset = offset(offset);
            clip.open(audioIn);

            initVolumeControl();
        }
        catch (UnsupportedAudioFileException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
        catch (LineUnavailableException e) { e.printStackTrace(); }
    }

    /**
     * Standart-Konstruktor
     * @param file  Dateipfad
     * @param offset    Die Differenz zwischen dem Start der Map und dem Start des Songs
     */
    public Music(Path file, int offset)
    {
        this(file.toString(), offset);
    }

    private void initVolumeControl() throws LineUnavailableException
    {
        Mixer mixer = AudioSystem.getMixer(null);
        Line.Info[] lInfos = mixer.getSourceLineInfo();

        if (lInfos.length >= 1 && lInfos[0] instanceof Line.Info) 
        {
            Line line = mixer.getLine(lInfos[0]);
            line.open();

            if (!line.isControlSupported(FloatControl.Type.VOLUME)) 
            {
                this.volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }
        }
    }

    /**
     * Berechnet das genaue Offset, abhängig von Fenstergröße und Puffern
     * @param initialOffset Songbezogenes-Offset
     * @return  genaues Offset
     */
    public static double offset(double initialOffset)
    {
        double a = PLAYFIELD_PADDED_OFFSET.y / 2; // = 0
        double b = HITLINE_Y;
        double offset = (a + b) / SCROLL_PER_SEK * 1000 + initialOffset;
        return offset;
    }

    @Override
    public void run()
    {
        try
        {
            Thread.sleep(Math.round(offset));
        } 
        catch (InterruptedException e) { e.printStackTrace(); }

        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void pause()
    {
        clip.stop();
    }

    /** Momentan nicht im Hintergrund, pausiert den rest */
    public void fadeOut(int duration)
    {
        float diffVolume = Math.abs(volumeControl.getValue() - volumeControl.getMinimum());

        while (volumeControl.getValue() > volumeControl.getMinimum())
        {
            float newVolume = volumeControl.getValue() - diffVolume / 35 / (duration / 1000);
            
            try
            {
                volumeControl.setValue(newVolume);
                Thread.sleep(35);
            }
            catch (IllegalArgumentException e)
            {
                break;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public long getMillisecondLength()
    {
        return clip.getMicrosecondLength()/1000;
    }

    public static void main(String[] args) throws InterruptedException
    {
        String fileName = "src/res/dev/henrihenr/audio/Toby Fox - Snowdin Town.wav";

        Music music = new Music(fileName, 0);

        music.start();
        System.out.println("Hallo");

        music.fadeOut(5000);

        Thread.sleep(music.getMillisecondLength() - 1000);
    }
}
