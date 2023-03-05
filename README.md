![Cover Image](https://repository-images.githubusercontent.com/604114394/e46212af-96d7-4ffe-a10b-da10ab3c5c12)

# GrooveGlide

### ➜ Installation und Gameplay

*"Groove Glide" ist ein Rhythmus-Spiel, bei dem du eine Plattform bewegst, um fallende Noten im Takt der Musik zu treffen.*

**Bist du bereit, dein Rhythmusgefühl auf die Probe zu stellen?** Dann lass dich von **Groove Glide** mitreißen! Bewege deine Plattform geschickt von links nach rechts und treffe die fallenden Noten im Takt der Musik. Je besser dein Timing, desto höher deine Punktzahl und desto größer dein Erfolgserlebnis. Mit coolen Beats und einem spannenden Gameplay ist "Groove Glide" das perfekte Spiel für alle, die Rhythmus im Blut haben. Also, worauf wartest du noch? Gleite in den Groove und werde zum Rhythmus-Champion!

## Besonderheiten und Features

Siehe: [README - Besonderheiten und Features](https://github.com/Danmyrer/GrooveGlide/blob/3d2c886abeeffcd6423636782062e005352aff17/doc/README%20-%20Besonderheiten%20und%20Features.md)

## Installation

```shell
git clone https://github.com/Danmyrer/GrooveGlide.git
```

Das Spiel enthält 2 vorinstallierte Beatmaps (*Zutomayo - Darken / Boy Pablo - wtf*), weitere Maps können aus folgendem GitHub-Repository geklont werden:

```shell
# im Ordner GrooveGlide
cd maps
git clone GrooveGlideExtra DANIEL TODO
mv GrooveGlideExtra/* .
rmdir GrooveGlideExtra
```

> Maps können auch aus `.osz`-Dateien des Spiels Osu generiert werden, mehr dazu siehe ####

## Songauswahl

Songs können in der `main`-Methode der Java-Datei `GrooveGlide.java` ausgewählt werden. Die Struktur sieht wie folgt aus:

```java
new GrooveGlide(Path.of("PFAD"), "CHART").play();
```

`PFAD` ist hierbei der Dateipfad, des Ordners, der die `map.conf`-Datei der Beatmap enthält. Relative Pfade sind auch möglich, diese beginnen ab dem Root-Verzeichnis des Projekts (`maps/...`)

`CHART` ist der Name der Chart (Schwierigkeit), die man spielen möchte. Der Name kann von dem Dateinamen der Chart abgelesen werden (`hard.chart` ➜ `"HARD"`).

Für die Chart `Afternoon` der Beatmap `Zutomayo - Darken` ergäbe sich folgende Initialisierung:

```java
new GrooveGlide(Path.of("maps/Zutomayo - Darken (Henri Henr)(m2g)"), "AFTERNOON").play();
```

Durch das Starten der Main-Methode sollte das Spiel wie gewohnt starten.

> Es sollte sich immer nur eine Map in der Main-Methode befinden. Der Rest kann einfach auskommentiert werden.

## Gameplay

Ziel des Spiels ist es, die fallenden **Noten** mit dem Player - die weiße **Hitline** - zu fangen, und einen Highscore zu erreichen.

Die Noten fallen entsprechend der ausgewählten Beatmap an einer bestimmten Stelle herunter. Diese Stelle nennt sich **Lane**.

> Das Spielfeld besitzt insgesamt 6 Lanes

Die Hitline kann mit den Tasten **D**, **F**, **J**, **K** an unterschiedlichen Lanes angeschlagen werden. Die Position kann an dem Aufleuchten der Lane nach Tastendruck erkannt werden. Um eine Note zu "fangen", muss sie beim Berühren der Hitline an entsprechender Position gefangen werden.

Da das Spielfeld mehr Lanes als der Spieler Tasten hat, kann die Hitlane mit den Tasten **S** und **L** nach rechts und links geschoben werden.

> Alternativ kann die Hitlane auch wie folgt verschoben werden:
> 
> 1. Leertaste drücken
>    
>    - D oder F drücken ➜ Links
>    
>    - J oder K drücken ➜ Rechts

Statistiken wie Genauigkeit, Punktzahl, Combo, Health-Points, Verbleibende Noten können während und nach dem Gameplay im Terminal gelesen werden.

Wenn der Spieler 0% Health erreicht gilt das Spiel als Verloren und wird abgebrochen.

Wenn der Spieler alle Noten "überlebt" hat, gilt das Spiel als Gewonnen und wird beendet.

### TL:DR

|                  |                       |
| ---------------- | --------------------- |
| Grundsteuerung   | D F J K               |
| Hitlane ➜ Links  | S                     |
| Hitlane ➜ Rechts | L                     |
| Verloren wenn    | Health = 0%           |
| Gewonnen wenn    | Ende der Map erreicht |
| Statistiken wo?  | Im Terminal           |

## Eigene Beatmaps und Charts erstllen

Siehe [README - ManiaConverter](https://github.com/Danmyrer/GrooveGlide/blob/302617a722bc073975bcd72823869cf1b10a329f/doc/README%20-%20ManiaConverter.md)

## Quellen

Siehe: [README - Quellen](https://github.com/Danmyrer/GrooveGlide/blob/9609f5354c9ab4832372e3dc2c3e0a8646f0a550/doc/README%20-%20Quellen.md)
