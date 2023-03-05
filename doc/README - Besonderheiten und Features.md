![Cover Image](https://repository-images.githubusercontent.com/604114394/e46212af-96d7-4ffe-a10b-da10ab3c5c12)

# GrooveGlide

### ➜ Besonderheiten und Features

*"Groove Glide" ist ein Rhythmus-Spiel, bei dem du eine Plattform bewegst, um fallende Noten im Takt der Musik zu treffen.*

Das Spiel ist zwar nicht mein erstes größeres Projekt, dennoch bin ich bei der Umsetzung auf einige Hindernisse getroffen, die ich durch besondere Features überwinden konnte. Durch das Genre Rythmusspiel selbst haben sich unter Anderem drei Aufgaben herauskristallisiert.

## Timer in Java und Synchronität zur Musik

Da man sich in einem Rythmusspiel vor allem auf sein Gehör und Rythmusgefühl verlassen muss, ist es sehr wichtig, dass das Spiel immer Synchron bleibt. Leider sind die Timerklassen hierfür nicht die beste Lösung.

Auch wenn man eine Widerholung nach 15 ms einstellt, kommt es oft zu Variationen in dem Ausführungsintervall zwischen 0.5 und 2 ms. Diese "Fehler" Addieren sich auf, bis an einem Punkt auch dem Spieler auffält, das die Note gerade nicht zu der Musik "passt". Daher habe ich einen Timerklasse geschrieben, die sich selbst korrigiert.  

```java
public class BeatTimer extends Thread
{
    // ...
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

        if (this.startOffset > 0) try { Thread.sleep(startOffset); } 
        catch (InterruptedException e) { e.printStackTrace(); }

        this.lastNanoTime = System.nanoTime() - this.beatNS;
        this.timer.start();
    }
    // ...
}
```

Vereinfacht kann man hier sagen, dass der "normale" Timer, der auf einem Interval von 1 ms läuft prüft, ob die Dauer eines Beats bereits abgewartet wurde. Dadurch, dass der Timer die Dauer immer in Abhängigkeit des Startzeitpunktes prüft, können keine Folgefehler mehr auftreten und sich aufsummieren.

## Fallende Noten und Optimierung

Da eine einzelne Beatmap hunderte oder auch tausende Noten beinhalten kann, wäre es unsinnig, alle Noten auf einmal als GameObject zu laden und mit der `move()`-Methode zu verschieben. Das würde zu lange Dauern und hat auch wieder das Risiko, das sich kleine Folgefehler aufsummieren (da bspw. die letzte Note später verschoben wird, als die erste). Deswegen werden alle aus der Chart geladenen Noten in einen Stack hinzugefügt, der in kleinen Päckchen die Noten dem `goss` hinzufügt.

> Die Klasse kann unter /src/..../util/HitObjectStack.java gefunden werden.

## Eigene Beatmaps erstellen

Ein Rythmusspiel kann nicht ohne Beatmaps existieren. Daher ist es wichtig, den Erstellprozess von Beatmaps möglichst einfach zu gestalten. Da das Schreiben von purem Java-Code zu kompliziert wäre, habe ich für GrooveGlide das Erstellen durch eigene Dateiformate vereinfacht.

<img src="file:///home/henri/Dokumente/OOSE/Projekte/GrooveGlide/doc/res/ordner.png" title="" alt="" data-align="center">

> Die Formate sind eigentlich selbsterklärend, einfach mal draufklicken und anschauen :)

Ein visueller Editor wäre natürlich die Beste lösung, die Entwicklung würde aber den Rahmen dieses Projektes sprengen. Daher "leiht" sich GrooveGlide den Editor des Spiels **Osu!**

[Osu!](https://osu.ppy.sh/home) ist ein Open-Source Rythmusspiel, das unter der Leitung von Dean „peppy“ Herbert entwickelt wird. Der Spielmodus Osu!Mania hat viele Parallelen zu GrooveGlide, weswegen durch die Nutzung meines Entwickelten ManiaConverters, eigene Maps einfacher de­signt werden können. Wie der ConverterGenutzt wird und was zu beachten ist finden sie [hier](https://github.com/Danmyrer/GrooveGlide/blob/aff1114d0e2af7ef8a1c4c75eb7b5ba15c691d33/doc/README%20-%20ManiaConverter.md).

Chart-Dateien sind für die Entwicklung von 4-Lane Charts gedacht, aber das Spielfeld selbst hat 6 Lanes. Daher wird alle 16-Beats zufällig bestimmt, ob - und wenn ja - in welche Richtung alle Noten verschoben werden sollen.

> Die Umsetzung kann in HitObjectStack.java am Beginn der Methode `public List<List<HitObject>> getNewHitObjecs(int beat)` gefunden werden.

Hierdurch muss der Spieler die Plattform zum Mithalten verschieben, um noch alle Noten fangen zu können.