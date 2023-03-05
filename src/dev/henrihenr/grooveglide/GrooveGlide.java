package dev.henrihenr.grooveglide;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

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
import dev.henrihenr.grooveglide.util.BeatTimer;
import dev.henrihenr.grooveglide.util.Color;
import dev.henrihenr.grooveglide.util.HitObjectStack;
import dev.henrihenr.grooveglide.util.Judgement;
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
    private BeatTimer beatTime;

    private final Health health;
    private final Score score;

    private static enum WinState { WON, LOST, PENDING }
    private WinState winState = WinState.PENDING;
    
    /**
     * Standart-Konstruktor. Das spiel kann mit {@code new Main(path, diff).play()} gestartet werden
     * @param beatMapPath   Pfad zu dem Verzeichnis der Beatmap (wo sich die Config Befindet)
     * @param difficulty    Auswahl der Chart / Schwierigkeit einer Beatmap. Eine Beatmap kann verschiedene Charts haben
     * 
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
            this.hitObjectStack = new HitObjectStack(beatMap.buildChart(difficulty), 1);
            this.music = new Music(beatMap.songPath, (int) Music.offset(Music.offset(0))); // Music.offset(0) ist immer die zeit die ne note von x = 0 bis zur Hitline brauch das abuse ich hier jetzt mal einfach 💀 Yen wenn du das siehst dont judge me pls
            this.beatTime = new BeatTimer(this).setStartOffset(Integer.valueOf(beatMap.chartConfig.get("MAP_OFFSET"))).setBeatMS(Math.round(60000 / (double)beatMap.bpm)).initTimer();
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
        for (int i = 0; i < 6; i++)
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

    @Override
    public void onStart()
    {
        try
        {
            for (int i = 5; i > 0; i--)
            {
                System.out.println("MACH DICH BEREIT: " + i);
                Thread.sleep(1000);
                clearConsole();
            }
        }
        catch(InterruptedException e) { e.printStackTrace(); }
        
        music.start();
        beatTime.start();
        screen.setState(State.ACTIVE);
    }

    @Override
    public void doChecks()
    {
        hitChecks();
        winStateChecks();
    }

    @Override
    public void onBeat(int beat) // note an mich selbst beachte das der beat wegen dem music offset falsch "klingt", aber da die noten ja bis zur weißen linie noch müssen passt alles 🙂🔫
    {
        List<List<HitObject>> hos = hitObjectStack.getNewHitObjecs(beat);
        if (hos == null) return; // wenn die Liste Leer ist. Wenn das Auftritt dauert es nicht mehr lange bis die Map vorbei ist
        for (int i = 0; i < hos.size(); i++) 
        {
            final int fi = i;
            SwingUtilities.invokeLater(() -> { // der timer für onbeat läuft nicht auf dem EDT (swing main thread)
                hitObjects.get(fi).addAll(hos.get(fi));
            });
        }
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

    @SuppressWarnings("all") // Wegen dem "No-Fail-Mod"
    private WinState checkWinLoseCondition()
    {
        // Lost
        if (!MOD_NO_FAIL && !health.isAlive()) return WinState.LOST;
        
        // Pending
        if (hitObjectStack.getRemainingHitObjects() > 0) return WinState.PENDING;
        for (List<HitObject> hoList : hitObjects) if (hoList.size() != 0) return WinState.PENDING;

        // Won
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

        Judgement judgement = Judgement.judgeTime(hitLane + Judgement.laneOffset, hitObjects);
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
     * Versteckt den Indikator für den Keypress
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
        System.out.println("Remaining: " + hitObjectStack.getRemainingHitObjects());

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
        return this.winState == WinState.LOST;
    }

    public static void main(String[] args) 
    {
        //new GrooveGlide(Path.of("maps/Zutomayo - Darken (Henri Henr)(m2g)"), "AFTERNOON").play(); // Maps: "AFTERNOON", "DARKNESS"
        new GrooveGlide(Path.of("maps/boy pablo - wtf (Henri Henr)(m2g)"), "NORMAL").play(); // Maps: "EASY", "NORMAL"
        
        //new GrooveGlide(Path.of("maps/Aiyru - Station (FAMoss)(m2g)"), "INSANE").play(); // Maps: "BEGINNER", "EASY", "NORMAL", "HARD", "INSANE"
        //new GrooveGlide(Path.of("maps/Camellia - Clouds in the Blue (Asherz007)(m2g)"), "SKYWARD").play(); // Maps: "NORMAL", "HARD", "INSANE", "SKYWARD"
        //new GrooveGlide(Path.of("maps/Camellia - Embracing intelligences (Leniane)(m2g)"), "HARD").play(); // Maps: "HARD", "ACCEPTANCE"
        //new GrooveGlide(Path.of("maps/Various Artists - International Wrestling Festival 2015 -WORLD OF ANIKI- (Surono)(m2g)"), "DECADES MANIANIKI").play(); // Maps: "DECADES MANIKANI"
        //new GrooveGlide(Path.of("maps/Martin Garrix - Animals (DrawdeX)(m2g)"), "MX").play(); // Maps: "NM", "HD", "MX"
        //new GrooveGlide(Path.of("maps/Android52 - Super Anime Groove 3d World (Mastermile)(m2g)"), "HARD").play(); // Maps: "HARD"
        //new GrooveGlide(Path.of("maps/Feint - We Won_t Be Alone (feat. Laura Brehm) (-NoName-)(m2g)"), "TOGETHER").play(); // Maps: "EASY", "NORMAL", "HARD", "TOGETHER"
        //new GrooveGlide(Path.of("maps/Panda Eyes _ Teminite - Highscore (Leniane)(m2g)"), "GAME OVER").play();
    }
}