package dev.henrihenr.grooveglide.gameplay;

import dev.henrihenr.grooveglide.util.Judgement;

public class Score
{
    private static final double scoreOnMiss = 0;
    private static final double scoreOnOKHit = 50;
    private static final double scoreOnGreatHit = 100;
    private static final double scoreOnPerfectHit = 300;

    private int combo;
    private int score;

    public Score(int initialPoints, int initialCombo)
    {
        this.combo = initialCombo;
        this.score = initialPoints;
    }

    public Score()
    {
        this(0, 0);
    }

    public int getScore()
    {
        return this.score;
    }

    public int getCombo()
    {
        return this.combo;
    }

    public void processHit(Judgement judgement)
    {
        switch (judgement)
        {
            case E_PERFECT:
            case L_PERFECT: 
                combo++;
                processScore(scoreOnPerfectHit);
                break;
            case E_GREAT:
            case L_GREAT:
                combo++;
                processScore(scoreOnGreatHit);
                break;
            case E_OKAY:
            case L_OKAY:
                combo++;
                processScore(scoreOnOKHit);
                break;
            case MISS:
                combo = 0;
                processScore(scoreOnMiss);
                break;
            default:
                break;
        }
    }

    private void processScore(double onHit)
    {
        this.score += onHit * (this.combo < 100 ? 1 : (0.005 * this.combo));
    }
}
