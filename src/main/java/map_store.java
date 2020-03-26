import java.util.ArrayList;
import java.util.List;

/**
 * nur Speicherplatz für den Map
 */
public  class map_store {
    /**
     * Map als Liste von koordinaten
     *
     * @param pacSize Größe des Pacs, um die Raster zu rechnen
     * @return Liste von Koordinaten
     */
    static List<coordinates> getMap(int pacSize) {
        List<coordinates> Map = new ArrayList<>();

        //Addiere a (Ziehe Bild)
        for (int x = 1; x < 9; x++) {
            Map.add(new coordinates(pacSize * x, pacSize));
        }
        //Addiere a.1
        for (int x = 11; x < 19; x++) {
            Map.add(new coordinates(pacSize * x, pacSize));
        }
        //b
        for (int y = 2; y < 6; y++) {
            Map.add(new coordinates(pacSize * 8, pacSize * y));
        }
        //b.1
        for (int y = 2; y < 6; y++) {
            Map.add(new coordinates(pacSize * 11, pacSize * y));
        }
        //c
        for (int y = 5; y < 15; y++) {
            Map.add(new coordinates(pacSize * 2, pacSize * y));
        }
        //c.1
        for (int y = 5; y < 15; y++) {
            Map.add(new coordinates(pacSize * 17, pacSize * y));
        }
        //d
        for (int x = 5; x < 7; x++) {
            for (int y = 6; y < 8; y++) {
                Map.add(new coordinates(pacSize * x, pacSize * y));
            }
        }
        //d1
        for (int x = 13; x < 15; x++) {
            for (int y = 6; y < 8; y++) {
                Map.add(new coordinates(pacSize * x, pacSize * y));
            }
        }
        //d2
        for (int x = 5; x < 7; x++) {
            for (int y = 12; y < 14; y++) {
                Map.add(new coordinates(pacSize * x, pacSize * y));
            }
        }
        //d3
        for (int x = 13; x < 15; x++) {
            for (int y = 12; y < 14; y++) {
                Map.add(new coordinates(pacSize * x, pacSize * y));
            }
        }
        //e
        for (int x = 8; x < 12; x++) {
            for (int y = 8; y < 12; y++) {
                Map.add(new coordinates(pacSize * x, pacSize * y));
            }
        }
        //f
        for (int y = 14; y < 18; y++) {
            Map.add(new coordinates(pacSize * 8, pacSize * y));
        }
        //f1
        for (int y = 14; y < 18; y++) {
            Map.add(new coordinates(pacSize * 11, pacSize * y));
        }
        //g
        for (int x = 1; x < 9; x++) {
            Map.add(new coordinates(pacSize * x, pacSize * 18));
        }
        //g1
        for (int x = 11; x < 19; x++) {
            Map.add(new coordinates(pacSize * x, pacSize * 18));
        }

        return Map;
    }

}
