package dev.henrihenr.grooveglide.gameplay;

import dev.henrihenr.grooveglide.config.GameConfig;
import dev.henrihenr.grooveglide.util.Judgement;

public class Health implements GameConfig
{
    // Kann ich irgendwann wenn ich mehr Zeit habe spannender machen
    private static final double healthOnMiss = -0.09;//-0.16;
    private static final double healthOnOKHit = 0;
    private static final double healthOnGreatHit = 0.01;
    private static final double healthOnPerfectHit  = 0.04;
    
    private static final double maxHP = 1;
    private static final double minHP = 0;

    /**
     * HP des Spielers. Werden prozentual angegeben (also 1 = 100%) bei 0 ist der Spieler logischerweise Tod
     */
    private double healthPoints;

    public Health(int initialHP)
    {
        this.healthPoints = initialHP;
    }

    public Health()
    {
        this(1);
    }

    public double getHealth()
    {
        return this.healthPoints;
    }

    public void processHit(Judgement judgement)
    {
        switch (judgement)
        {
            case E_PERFECT:
            case L_PERFECT: 
                healthPoints += healthOnPerfectHit;
                break;
            case E_GREAT:
            case L_GREAT:
                healthPoints += healthOnGreatHit;
                break;
            case E_OKAY:
            case L_OKAY:
                healthPoints += healthOnOKHit;
                break;
            case MISS:
                healthPoints += healthOnMiss;
                break;
            default:
                break;
        }

        if (this.healthPoints > maxHP) this.healthPoints = maxHP;
        if (this.healthPoints < minHP) this.healthPoints = minHP;
    }

    public boolean isAlive()
    {
        return this.healthPoints > 0;
    }

    @Override
    public String toString()
    {
        return String.valueOf(this.healthPoints);
    }

    public static void main(String[] args) 
    {
        Health h = new Health();
        System.out.println(h.toString());
        h.processHit(Judgement.E_GREAT);
        System.out.println(h.toString());
        h.processHit(Judgement.MISS);
        System.out.println(h.toString());
    }
}
