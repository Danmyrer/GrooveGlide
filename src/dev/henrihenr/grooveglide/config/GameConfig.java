package dev.henrihenr.grooveglide.config;

/**
 * Globale Konfigurationen zum Spielverhalten
 */
public interface GameConfig 
{
    /** Geschwindigkeit, in der das {@link HitObject} fällt <i>(aka Y-Velocity * (-1))</i>. */
    public static final int SCROLL_SPEED = 12;

    /**
     * Bestimmt das Intervall in dem das Spiel arbeitet (und somit auch die FPS und die Geschwindigkeit)<p>
     * <b>Am besten nicht ändern!</b>
     */
    public static final int TIMER_LOOP_DELAY = 15; // sicherer Modus = 15 (ca 60 FPS)ljkd

    public static final String SOUND_CLICK = "skin/sounds/Click.wav";

    double FPS = 1000.0 / TIMER_LOOP_DELAY;
    double SCROLL_PER_SEK = SCROLL_SPEED * FPS;

    boolean MOD_NO_FAIL = false; // <<-- Hier Boolean auf "true" setzen für "No Fail - Mod"
}
