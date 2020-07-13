# INF_HAUSAUFGABE (Pacman)

## Rennen
1. Clonen Sie den Repository mit oder laden Sie es als tar runter
    ~~~
    git clone https://github.com/guney1911/INF_HAUSAUFGABEN
    ~~~
2. öffnen Sie die processing-1.0-beta.tar oder.zip, die unter `INF_HAUSAUFGABEN/build/distributions/processing-1.0-SNAPSHOT` steht, entweder mit einem Archive Manager oder mit dem folgenden Befehl:
    ~~~ shell script
    cd INF_HAUSAUFGABEN/build/distributions && tar -xvf processing-*.tar  
    ~~~
3. Rennen sie das Programm mit 
    ~~~
    ./processing*/bin/processing 
    ~~~
4. Sourcecode befindet sich in [src/main/java](https://github.com/guney1911/INF_HAUSAUFGABEN/tree/master/src/main/java)
   
## Referenz 
- Unter `build/docs/javadocs/main.html` kann man den JavaDocs finden. Klicken sie einfach auf main.html, um anzufangen

## Erklärung von dem Code
### Klassen
#### coordinates
- speichert koordinaten als x und y 
#### moveDirection
- Enum mit 4 Werte für jede Richtung
~~~
    up, down, left, right
~~~
#### Main
- Spiel Logik, wie Score und Leben.
- Anfangs Klasse für Processing → draw(), setup() usw, ist hier

#### pacman
- Ort- uns Steuerungslogik für PacMan als Object. 
- Speichert Animationen als ein Map von einer Liste der Frames (Sprite) zu den möglichen Richtungen.
- Bewegungs Kalkulation:
    - fängt mit calulateMove() an:
        - ruft zuerst mit der eingestellten Richtung calculateMoveWithDir(dir)
        - schaut ob die gerechnete richtung Sinn ergibt → wiederholt den gleichen Prozess mit dem Fallback Richtung, falls es nicht klappt
    - calculateMoveWithDir() rechnet aus dem jetizigen Koordinaten dem neuen, indem es einfach den Konstante speed zu dem richtigen Variable (x oder y) zufügt oder abnimmt.
    - checkSanity() schaut, ob der gerechnete Wert sinnvoll ist (z. B: ob es in dem Spielfeld ist):
        1. kontrolliere ob pacman im spielfeld ist:
         ~~~
            //kontroliere ob wir im Spielfeld sind
            if (coordinates.x < 0 || coordinates.x > pacSize * 19 - pacSize || coordinates.y < 0 || coordinates.y > pacSize * 19 - pacSize) {
                return true;
            }
        ~~~
        - ` coordinates.y < 0` und ` coordinates.x < 0` sind selbverständlich → processing hat keinen negativen Werte
        - `coordinates.x (y) > pacSize * 19 - pacSize` hat den - pacSize. weil processing definiert Koordinaten als die links obere Punkt eines Objekts und pacman muss aufhören, wenn seine rechte Seite auf die rechte Wand berührt. D. h. wenn we genau ein pacSize links bzw. oben ist 
        2. kontrolliere ob da ein Mauer ist mit `walls.checkWall(coordinates)` (später erklärt)
        3. kontrolliere ob pacman in dem Geist Respawn Area ist 
    - wenn alles `false` erginbt, dann ist die gerechnete Koordinate gültig → ändere dir pacCoordinates zu dem gerechnete und zeichne das pacman da
#### walls
- liest die Karte von map.txt ab.
- speichert als eine List von Koordinaten ab
- zeichnet die Shape wall in jeder koordinate in dieser liste
- checkWall-Funktion schaut ob pacman mit einer Mauer kollidiert
    ~~~
    for (coordinates cor : wallStore) {
                   if (coordinates.x > cor.x - this.pacSize && coordinates.x < cor.x + this.pacSize && coordinates.y > cor.y -      this.pacSize && coordinates.y < cor.y + this.pacSize) { //magie
                     return true;
                    }
                }
    ~~~
  
#### fruits
- liest map.txt, um die Orte zu finden, wo ein Obst stehen soll
- schafft zwei shapes, um später zu spawnen
- entscheidet ob ein Obst groß oder klein sein soll
- speichert alles in einem Map mit koordinate und boolean, wobei bool. die Größe representiert

#### ghosts
- animation ist wie pacman
- wichtig ist die Bewegungsfunktion
    1. es probiert zuerst mit der Richtung vom letzen Mal zu kalkulieren, wo nächste schritt sein soll
    2. falls es nicht klappt, dann fügt es zuerst die probierte Richtung in einer Liste, dann wählt er ein neues richtung , wobei die Liste dafür sorgt, dass die gleiche Richtung nicht zweimal gewählt wird
    3. er ruft sich selbst mit der neuen Richtung als dir der Ghost → Schritt 1
    4. wenn eine korrekte Koordinate gerechnet wird, dann gibt er die zurück
- checkPacMan-Funktion schaut, ob pacman und ghost kollidiert haben, falls dann:
    ~~~
    if (superPac) {  //falls pacman super ist
        changeScore(80);
        resetPosition(); //schickt ghost zurück zum Ursprung
    } else {
        pacHit();

    }
    ~~~
    
