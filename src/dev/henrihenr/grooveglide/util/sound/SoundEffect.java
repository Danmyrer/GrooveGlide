package dev.henrihenr.grooveglide.util.sound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import dev.henrihenr.grooveglide.config.GameConfig;

public class SoundEffect implements GameConfig
{
    private File soundFile;
    private AudioInputStream audioIn;

    private List<Clip> clip;
    
    /**
     * Standart-Konstruktor
     * @param file  Dateipfad
     * @param clips Anzahl der Clips, die Verfügbar sein sollen. 
     * Bei zu wenigen Clips, funktionieren die Hitsounds bei zu hohem input nicht richtig.
     */
    public SoundEffect(String file, int clips)
    {
        this.clip = new ArrayList<>(clips);
        
        for (int i = 0; i < clips; i++)
        {
            try
            {
                this.soundFile = new File(file);
                this.audioIn = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);

                clip.addLineListener(new LineListener() {
                    @Override
                    public void update(LineEvent e)
                    {
                        if (e.getType() == LineEvent.Type.STOP)
                        {
                            clip.stop();
                            clip.setFramePosition(0);
                        }
                    }
                });

                this.clip.add(clip);
            }
            catch (UnsupportedAudioFileException e) { e.printStackTrace(); }
            catch (IOException e) { e.printStackTrace(); }
            catch (LineUnavailableException e) { e.printStackTrace(); }
        }
    }
    
    /**
     * Konstruktor, der die Clips auf 0 setzt.
     * @see SoundEffect#SoundEffect(String, int)
     * @param file  Dateipfad
     */
    public SoundEffect(String file)
    {
        this(file, 1);
    }

    /**
     * Spiel den SoundEffekt
     */
    public void play()
    {
        for (Clip c : clip)
        {
            if (c.isRunning()) continue;
            c.start();
            break;
        }
    }

    /**
     * @return Länge des SoundEffects
     */
    public int getClipSize()
    {
        return clip.size();
    }

    public static void main(String[] args) 
    {
        SoundEffect se = new SoundEffect(SOUND_CLICK, 5);
        System.out.println(se.getClipSize());
    }
}
