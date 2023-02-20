package dev.henrihenr.game2d;

import java.util.List;
import java.awt.event.*;
import java.awt.*;

public interface Game
{

  int width();

  int height();

  GameObj player();

  List<List<? extends GameObj>> goss();

  final javax.swing.JFrame frame = new javax.swing.JFrame();;

  /** Initialisierung */
  void init();

  /** Wird nach Abgeschlossener Initialisierung ausgeführt */
  void onStart();

  /** Wird während des Spiels ausgeführt */
  void doChecks();

  void keyPressedReaction(KeyEvent keyEvent);

  /**
   * Wird beim Loslassen eines Key aufgerufen
   * 
   * @implNote Brauch ich, dass man die Tasten gedrückt halten kann.
   * @param keyEvent
   */
  void keyReleasedReaction(KeyEvent keyEvent);

  default void move()
  {
    if (ended())
      return;
    for (var gos : goss())
      gos.forEach(go -> go.move());
    player().move();
  }

  boolean won();

  boolean lost();

  default boolean ended()
  {
    return won() || lost();
  }

  default void paintTo(Graphics g)
  {
    for (var gos : goss())
      gos.forEach(go -> go.paintTo(g));
    player().paintTo(g);
  }

  /**
   * Initialisierung und Starten des gesamten Spiels
   * 
   * @implNote Erweiterung durch {@link SwingScreen#state}
   * @param screen {@link SwingScreen} instanz
   * @param state Status, den das Spiel zu Beginn haben soll.
   * @return SwingScreen-Instanz
   */
  default void play(SwingScreen screen, SwingScreen.State state)
  {
    init();

    screen.setState(state);

    frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    frame.add(screen);
    frame.pack();
    frame.setVisible(true);

    onStart();
  }

  /**
   * Konstruktor für rückwärts Kompatibilität
   * 
   * @see Game#play(name.panitz.game2d.SwingScreen.State)
   */
  default void play()
  {
    this.play(new SwingScreen(this), SwingScreen.State.ACTIVE);
  }

}
