# INF_HAUSAUFGABE (Pacman)

## Rennen
1. Laden sie den processing-VERSION-.tar runter
1. öffnen sie es entweder mit einem Archive Manager oder mit dem folgende Befehl:
    ~~~ shell script
    $ tar -xvf processing-*.tar
    ~~~
3. Rennen sie den Programm mit 
    ~~~
   $./processing*/bin/processing
   ~~~
   
## Referenz 
- Unter [ort] kann man den JavaDocs finden. Klicken sie einfach auf main.html, um anzufangen

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
- Anfangs Klasse für Processing => draw(), setup() usw, ist hier

#### pacman
- Ort- uns Steuerungslogik für PacMan als Object. 
- Speichert Animationen als ein Map von einer Liste der Frames (Sprite) zur mögliche Richtungen.
- Bewegungs Kalkulation:
    - fängt mit calulateMove() an:
        - ruft zuerst mit dem eingestellte Richtung calculateMoveWithDir(dir)
        - schaut ob die gerechnete richtung Sinn ergibt => wiederholt den gleichen Prozess mit dem Fallback Richtung, falls es nicht klappt
    - calculateMoveWithDir() rechnet aus dem jetizigen Koordinaten dem neuen, indem es einfach den Konstante speed zu dem richtigen Variable (x oder y) zufügt oder abnimmt.
    - checkSanity() schaut ob die gerechnete Wert sinvoll ist (z. B: ob es in dem Spielfeld ist):
        1. kontrolliere ob pacman im spielfeld ist:
         ~~~
            //kontroliere ob wir im Spielfeld sind
            if (coordinates.x < 0 || coordinates.x > pacSize * 19 - pacSize || coordinates.y < 0 || coordinates.y > pacSize * 19 - pacSize) {
                return true;
            }
        ~~~
        - ` coordinates.y < 0` und ` coordinates.x < 0` sind selbverständlich => processing hat keinen negativen Werte
        - `coordinates.x (y) > pacSize * 19 - pacSize` hat den - pacSize. weil processing definiert Koordinaten als die links obere Punkt eines Objekts und pacman muss aufhören, wenn seine rechte Seite auf die rechte Wand berührt. D. h. wenn we genau ein pacSize links bzw. oben ist 
        2. kontrolliere ob da ein Mauer ist mit `walls.checkWall(coordinates)` (später erklärt)
        3. kontrolliere ob pacman in dem Geist Respawn Area ist 
    - wenn alles `false` erginbt, dann ist die gerechnete Koordinate gültig => ändere dir pacCoordinates zu dem gerechnete und zeichne das pacman da
#### walls
