package dev.henrihenr.grooveglide.gameplay;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.henrihenr.grooveglide.config.GameConfig;
import dev.henrihenr.grooveglide.util.drawables.HitObject;
import dev.henrihenr.grooveglide.util.drawables.MoveObject;

/**
 * Eine Beatmap ist die Kombination aus einem Song und (mehreren) Charts
 */
public class BeatMap implements BeatChart, GameConfig
{
    /** Pfad zu dem BeatMap-Verzeichnis */
    private final Path path;

    /** Name der Config-Datei */
    private final static String CONFIG_NAME = "map.conf";
    
    public final String songName;
    public final String songArtist;
    public final Path songPath;
    public final int bpm;
    public final String beatMapArtist;

    public final HashMap<String,String> mapConfig;
    public final HashMap<String,String> mapCharts;
    public HashMap<String,String> chartConfig;

    //public final int offset;

    /** Das Offset (in MS), das HitObjects haben können (nicht mit Music-Offset verwechseln!) */
    //public final int hitObjectOffset; //= 6495;

    /**
     * Standart-Konstruktor
     * @param path  Pfad zu dem BeatMap-Verzeichnis
     * @param hitObjectOffset Das Offset (in MS), dass HitObjects haben können (nicht mit Music-Offset verwechseln!)
     * @throws IOException
     */
    public BeatMap(Path path) throws IOException
    {
        this.path = path;

        Path configPath = getPath(CONFIG_NAME);
        String[] configString = Files.readString(configPath).split("\n");
        
        this.mapConfig = getConfMap(configString);
        this.mapCharts = getChartsMap(configString);

        this.songName = this.mapConfig.get("SONG_NAME");
        this.songArtist = this.mapConfig.get("SONG_ARTIST");
        this.bpm = Integer.valueOf(this.mapConfig.get("SONG_BPM"));
        this.songPath = getPath(this.mapConfig.get("SONG_FILE"));
        this.beatMapArtist = this.mapConfig.get("BEATMAP_ARTIST");
    }

    @Override
    public List<List<HitObject>> buildChart(String name) throws IOException
    {
        Path chartPath = getPath(mapCharts.get(name));
        String[] chartString = Files.readString(chartPath).split("\n");

        this.chartConfig = getConfMap(chartString);

        long hitObjectOffset = 0;//Long.valueOf(chartConfig.get("MAP_OFFSET")); // ACHTUNG wird gerade von HitObjectStackk überschrieben (weil map offset technically nicht das selbe wie ho Offset ist)

        List<List<HitObject>> chart = new ArrayList<>();

        for (int i = 0; i < Integer.valueOf(chartConfig.get("MAP_LANES")); i++)
        {
            chart.add(new ArrayList<>());
        }

        for (int i = 0; i < chartString.length; i++)
        {
            if (!chartString[i].equals("[CHART]")) continue;
            for (int u = i + 1; u < chartString.length; u++)
            {
                String[] row = chartString[u].replaceAll(" ", "").split("=");

                double beat = Double.valueOf(row[0]) - 1;

                for (int j = 0; j < row[1].length(); j++) 
                {
                    if (row[1].toCharArray()[j] == '1')
                    {
                        chart.get(j).add(new HitObject(beat, j, this.bpm, hitObjectOffset));
                    }
                    if (row[1].toCharArray()[j] == '2')
                    {
                        chart.get(j).add(new MoveObject(beat, j, this.bpm, hitObjectOffset));
                    }
                }
            }
        }

        return chart;
    }

    /**
     * Kombiniert einen Dateinamen aus dem Verzeichnis mit Path
     * @param name  Dateiname
     * @return  Absoluter-Pfad
     */
    public Path getPath(String name)
    {
        return Path.of(this.path.toString() + '/' + name);
    }

    public static void main(String[] args) 
    {
        try
        {
            //BeatMap trnsttr = new BeatMap(Path.of("src/res/dev/henrihenr/beatMaps/we_wont_be_alone/"));
            //trnsttr.buildChart("EASY");
            BeatMap cloudsInBlue = new BeatMap(Path.of("src/res/dev/henrihenr/beatMaps/Camellia - Clouds in the Blue (Asherz007)(m2g)"));
            cloudsInBlue.buildChart("SKYWARD");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
