package dev.henrihenr.game2d;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

import dev.henrihenr.grooveglide.config.GameConfig;

public class SwingScreen extends JPanel implements GameConfig
{
  private static final long serialVersionUID = 1403492898373497054L;
  Game logic;
  Timer t;

  public enum State
  {
    ACTIVE, INACTIVE
  }

  /**
   * Status, der entscheidet, ob die Logik regelmäßig Abgefragt werden soll.
   * 
   * @implNote Notwendig, da beim Laden der HO die Velicity global deaktiviert
   * sein soll
   */
  public State state = State.INACTIVE;

  public SwingScreen(Game gl)
  {
    this.logic = gl;

    t = new Timer(TIMER_LOOP_DELAY, (ev) ->
    {
      if (state == State.ACTIVE)
      {
        logic.move();
        logic.doChecks();
      }
      repaint();
      getToolkit().sync();
      requestFocus();
    });
    t.start();

    addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent e)
      {
        logic.keyPressedReaction(e);
      }

      @Override
      public void keyReleased(KeyEvent e) // Siehe Game.java
      {
        logic.keyReleasedReaction(e);
      }
    });
    setFocusable(true);
    requestFocus();
  }

  /**
   * Konstruktor mit der Möglichkeit, einen Standart-Status zu setzen
   * 
   * @param gl Game
   * @param state Status
   * @implNote Siehe: {@link SwingScreen#State}
   */
  public SwingScreen(Game gl, State state)
  {
    this(gl);
    this.state = state;
  }

  /**
   * Erhalten des aktuellen Status
   * 
   * @return Status
   * @implNote Siehe: {@link SwingScreen#State}
   */
  public State getState()
  {
    return this.state;
  }

  /**
   * Setzt den aktuellen Status, <b>hierbei werden die logischen Prüfungen
   * <i>deaktiviert / aktiviert</i></b>
   * 
   * @implNote Siehe: {@link SwingScreen#State}
   */
  public void setState(State state)
  {
    this.state = state;
  }

  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension((int) logic.width(), (int) logic.height());
  }

  @Override
  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    logic.paintTo(g);
  }
}
