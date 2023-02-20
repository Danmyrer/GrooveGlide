package dev.henrihenr.grooveglide.gameplay;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import dev.henrihenr.grooveglide.util.drawables.HitObject;

/**
 * Beschreibung der Chart einer BeatMap
 */
public interface BeatChart
{
    /**
     * Erstellt eine Chart für eine Bestimmte Beat-Map
     * @param name  Name der Chart (bzw. Schwierigkeit)
     * @return  Chart (Liste mit Liste für GameObjects)
     * @throws IOException
     */
    abstract List<List<HitObject>> buildChart(String name) throws IOException;

    /**
     * Erstellt eine Map mit allen Configurationen aus der Config
     * @param confString    String, der die Konfiguration enthält
     * @return  HashMap
     */
    default HashMap<String, String> getConfMap(String[] confString)
    {
        HashMap<String, String> tempConfig = new HashMap<>();

        for (int i = 0; i < confString.length; i++)
        {
            if (confString[i].equals("[CONFIG]")) continue;
            if (confString[i].equals("[CHART]") || confString[i].equals("")) break;

            String[] conf = confString[i].split("=");
            tempConfig.put(conf[0], conf[1]);
        }

        return tempConfig;
    }

    /**
     * Erstellt eine Map mit allen Charts / Schwierigkeiten und deren Pfaden
     * @param confString   String, der die Konfiguration enthält
     * @return  HashMap
     */
    default HashMap<String, String> getChartsMap(String[] confString)
    {
        HashMap<String, String> tempCharts = new HashMap<>();

        for (int i = 0; i < confString.length; i++)
        {
            if (confString[i].equals("[CHARTS]"))
            {
                for (int u = i + 1; u < confString.length; u++)
                {
                    if (confString[u].equals("")) break;
                    String[] map = confString[u].split("=");
                    tempCharts.put(map[0], map[1]);
                }
            }
        }

        return tempCharts;
    }
}
