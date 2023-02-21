package dev.henrihenr.grooveglide;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import dev.henrihenr.game2d.Game;
import dev.henrihenr.game2d.GameObj;
import dev.henrihenr.game2d.SwingScreen;
import dev.henrihenr.game2d.SwingScreen.State;
import dev.henrihenr.game2d.Vertex;
import dev.henrihenr.grooveglide.config.GameConfig;
import dev.henrihenr.grooveglide.config.Playfield;
import dev.henrihenr.grooveglide.gameplay.BeatMap;
import dev.henrihenr.grooveglide.gameplay.Health;
import dev.henrihenr.grooveglide.gameplay.Score;
import dev.henrihenr.grooveglide.util.Beat;
import dev.henrihenr.grooveglide.util.Color;
import dev.henrihenr.grooveglide.util.HitObjectStack;
import dev.henrihenr.grooveglide.util.Judgement;
import dev.henrihenr.grooveglide.util.Time;
import dev.henrihenr.grooveglide.util.drawables.Background;
import dev.henrihenr.grooveglide.util.drawables.HitObject;
import dev.henrihenr.grooveglide.util.drawables.Hitline;
import dev.henrihenr.grooveglide.util.drawables.KeyPressedGlow;
import dev.henrihenr.grooveglide.util.sound.Music;
import dev.henrihenr.grooveglide.util.sound.SoundEffect;

/**
 * Main-Methode des Spiels
 * @implNote stand 28.12 ca 1400 Zeilen lol
 * @author Daniel Mayer, OOSE Gruppe B
 */
public class GrooveGlide implements Game, Beat, Playfield, GameConfig
{
    private final Hitline player;
    private final List<List<? extends GameObj>> goss;
    private final List<KeyPressedGlow> keyPressedGlow;
    private final List<GameObj> background;
    private final int width;
    private final int height;
    
    private SwingScreen screen;
    private List<List<HitObject>> hitObjects;
    private HitObjectStack hitObjectStack;
    private final List<SoundEffect> hitSounds;
    private Music music;
    private Time time;

    private final Health health;
    private final Score score;

    private static enum WinState { WON, LOST, PENDING }
    private WinState winState = WinState.PENDING;
    
    /**
     * Standart-Konstruktor. Das spiel kann mit {@code new Main(path, diff).play()} gestartet werden
     * @param beatMapPath   Pfad zu dem Verzeichnis der Beatmap (wo sich die Config Befindet)
     * @param difficulty    Auswahl der Chart / Schwierigkeit einer Beatmap. Eine Beatmap kann verschiedene Charts haben
     */
    GrooveGlide(Path beatMapPath, String difficulty)
    {
        this.player = new Hitline();
        this.goss = new ArrayList<>();
        this.hitObjects = new ArrayList<>();
        this.keyPressedGlow = new ArrayList<>();
        this.background = new ArrayList<>();
        this.width = (int) PLAYFIELD.x;
        this.height = (int) PLAYFIELD.y;
        this.hitSounds = new ArrayList<>();

        this.health = new Health();
        this.score = new Score();

        try
        {
            BeatMap beatMap = new BeatMap(beatMapPath);
            //this.hitObjects = beatMap.buildChart(difficulty);
            this.hitObjectStack = new HitObjectStack(beatMap.buildChart(difficulty));
            this.music = new Music(beatMap.songPath, 0);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // region getter
    @Override
    public void play()
    {
        screen = new SwingScreen(this); // Jetzt hab ich kontrolle
        this.play(screen, SwingScreen.State.INACTIVE); // Logische Prüfungen sollen ab kontrolliertem Zeitpunkt stattfinden.
    }

    @Override
    public List<List<? extends GameObj>> goss()
    {
        return this.goss;
    }

    @Override
    public GameObj player()
    {
        return this.player;
    }

    @Override
    public int height()
    {
        return this.height;
    }

    @Override
    public int width()
    {
        return this.width;
    }
    // endregion

    /**
     * Initialisieren des Spiels.
     */
    @Override
    public void init()
    {
        goss().clear();
        initBackground();
        initHitObject();
        initKeyPressedGlow();

        handleMove(Hitline.Direction.CENTER); // bewegt die Hitline für den Anfang in die Mitte

        initHitSound();
        initTime();
    }

    /**
     * Initialisieren des Background
     */
    private void initBackground()
    {
        goss.add(background);
        background.add(new Background());
        background.add(new Background(
                new Vertex(
                    PLAYFIELD_PADDED_OFFSET.x / 2,
                    PLAYFIELD_PADDED_OFFSET.y / 2
                ), 
                PLAYFIELD_PADDED.x, PLAYFIELD_PADDED.y, 
                Color.GUNMETAL
            )
        );
    }

    /**
     * Initialisieren des {@link HitObject}
     */
    private void initHitObject()
    {
        for (int i = 0; i < LANES; i++)
        {
            hitObjects.add(new ArrayList<HitObject>());
            goss.add(hitObjects.get(i));
        }
    }

    /**
     * Initialisieren der {@link KeyPressedGlow}
     */
    private void initKeyPressedGlow()
    {
        goss.add(keyPressedGlow);
        for (int i = 0; i < 4; i++)
        {
            System.out.println(i);
            keyPressedGlow.add(new KeyPressedGlow(0, i));
        }
    }

    /**
     * Initialisieren der HitSounds
     */
    private void initHitSound()
    {
        for (int i = 0; i < LANES; i++)
        {
            hitSounds.add(new SoundEffect(SOUND_CLICK, 5));
        }
    }

    private void initTime()
    {
        this.time = new Time(this).initTimer();
        time.startTimer();
    }

    @Override
    public void onStart()
    {
        try
        {
            for (int i = 3; i > 0; i--)
            {
                System.out.println("START IN: " + i);
                Thread.sleep(1000);
            }
            System.out.println("GO");
        }
        catch(InterruptedException e) { e.printStackTrace(); }
        
        music.start();
        screen.setState(State.ACTIVE);
    }

    @Override
    public void doChecks()
    {
        hitChecks();
        winStateChecks();
    }

    @Override
    public void onBeat(int beat)
    {
        System.out.println(beat);
        hitObjects.addAll(hitObjectStack.getNewHitObjecs(beat));
    }

    private void hitChecks()
    {
        for (int i = 0; i < hitObjects.size(); i++)
        {
            if (hitObjects.get(i).size() == 0) continue;
            if (hitObjects.get(i).get(0).pos().y >= PLAYFIELD.y - PLAYFIELD_PADDED_OFFSET.y / 2 - PLAYFIELD_PADDED.y / HitObject.HITOBJ_HEIGHT_QUO)
            {
                hitObjects.get(i).remove(0);

                health.processHit(Judgement.MISS);
                score.processHit(Judgement.MISS);

                printGameInfo(Judgement.MISS);
            }
        }
    }

    private void winStateChecks()
    {
        this.winState = checkWinLoseCondition();
        if (won() || lost())
        {
            printGameInfo(Judgement.NONE);
            System.out.println(this.winState.toString());
            
            screen.setState(State.INACTIVE);
            frame.setVisible(false);

            music.fadeOut(5000);
            music.pause();
            System.exit(0);
        }
    }

    private WinState checkWinLoseCondition()
    {
        if (!health.isAlive()) return WinState.LOST;
        
        for (List<HitObject> hoList : hitObjects) 
        {
            if (hoList.size() != 0) return WinState.PENDING;
        }

        return WinState.WON;
    }


    @Override
    public void keyPressedReaction(KeyEvent keyEvent)
    {
        switch (keyEvent.getKeyCode())
        {
            case KeyEvent.VK_D : handleKeyPress(0); break;
            case KeyEvent.VK_F : handleKeyPress(1); break;
            case KeyEvent.VK_J : handleKeyPress(2); break;
            case KeyEvent.VK_K : handleKeyPress(3); break;
            
            case KeyEvent.VK_S : handleMove(Hitline.Direction.LEFT);; break;
            case KeyEvent.VK_L : handleMove(Hitline.Direction.RIGHT);; break;
            case KeyEvent.VK_SPACE : handleMoveTrigger(); break;

            case KeyEvent.VK_C : handleMove(Hitline.Direction.CENTER); break;
        }
    }

    @Override
    public void keyReleasedReaction(KeyEvent keyEvent)
    {
        switch (keyEvent.getKeyCode())
        {
            case KeyEvent.VK_D : handleKeyRelease(0); break;
            case KeyEvent.VK_F : handleKeyRelease(1); break;
            case KeyEvent.VK_J : handleKeyRelease(2); break;
            case KeyEvent.VK_K : handleKeyRelease(3); break;
        }
    }

    private void handleMoveTrigger()
    {
        if (player.getState() == Hitline.State.TRIGGERED)
        {
            player.setState(Hitline.State.REGULAR);
        }
        else
        {
            player.setState(Hitline.State.TRIGGERED);
        }
    }

    /**
     * Zeigt den Indikator für den Keypress
     * @param hitLane
     */
    private void handleKeyPress(int hitLane)
    {
        keyPressedGlow.get(hitLane).showMe();
        hitSounds.get(hitLane).play();

        Judgement judgement = Judgement.judgeTime(hitLane, hitObjects);
        if (judgement != Judgement.MISS && judgement != Judgement.NONE)
        {
            hitObjects.get(hitLane + Judgement.laneOffset).remove(0);
        }

        health.processHit(judgement);
        score.processHit(judgement);

        if (player.getState() == Hitline.State.TRIGGERED)
        {
            handleMove(
                hitLane < 2 ? Hitline.Direction.LEFT : Hitline.Direction.RIGHT
            );

            player.setState(Hitline.State.REGULAR);
        }

        printGameInfo(judgement);
    }

    /**
     * Versteckt den Indikator für den Keypres                System.out.print("\033[H\033[2J");  
                System.out.flush();s
     * @param position
     */
    private void handleKeyRelease(int hitLane)
    {
        keyPressedGlow.get(hitLane).hideMe();;
    }

    private void handleMove(Hitline.Direction dir)
    {
        if (dir == Hitline.Direction.LEFT)
        {
            player.moveLineLeft();

            this.getClass().getSimpleName();
            keyPressedGlow.forEach(k -> { k.moveLineLeft(); });
        }
        else if (dir == Hitline.Direction.RIGHT)
        {
            player.moveLineRight();
            keyPressedGlow.forEach(k -> { k.moveLineRight(); });
        }
        else
        {
            player.moveCenter();
            keyPressedGlow.forEach(k -> { k.moveCenter(); });
        }

        Judgement.moveLaneOffset(dir);
    }

    private void printGameInfo(Judgement judgement)
    {
        clearConsole();

        System.out.println((int)(health.getHealth() * 100) + "% HP");
        System.out.println(score.getScore() + " PTS");
        System.out.println(score.getCombo() + "x Combo");
        
        System.out.println("\n----------------------\n");

        if (judgement != Judgement.NONE) System.out.println(judgement);
    }

    /** GitHub (siehe Anhang) + Panitz GameOfLife */
    private final static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");
            
            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                System.out.print("\033[H\033[2J");  
                System.out.flush();
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean won()
    {
        return this.winState == WinState.WON;
    }

    @Override
    public boolean lost()
    {
        //return false;
        return this.winState == WinState.LOST;
    }

    public static void main(String[] args) 
    {
        new GrooveGlide(Path.of("maps/Zutomayo - Darken (Henri Henr)(m2g)"), "AFTERNOON").play();
    }
    
}