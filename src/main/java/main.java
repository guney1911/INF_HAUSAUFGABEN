import jdk.internal.jline.internal.Nullable;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Haupt Class oder Applet dient als HauptLogik
 * muss außerdem über alle andere Klassen stehen, processsing macht das automatisch
 * Sonst darf man die Processing commands nicht im andee Klassen benutzen
 */
public class main extends PApplet {
    //Endgültige Werte für Konfiguration
    final private int speed = 10; //Geschwindigkeit mitdem alles sich bewegt pixel pro frame
    final private int pacSize = 40;//Größe des Pacs => größe des Spielfelds ist von diesem Wert abhängig => Spielfeld ist ein Raster 20x20
    final private int fps = 30;
    final private int superPacTime = 5; //wie lang soll pacman als superpac bleiben. ( in sekunden)
    final private float pFruit = 0.015f; //Wahrscheinlichkeit ein großes Fruit zu erstellen
    final private int smallFruitSize = 5; //Größe von dem normalen Fruit
    final private int bigFruitSize = 10; //Größe von dem großen Fruit

    pacman pac;
    walls walls;
    fruit fruits;
    List<ghost> ghosts;
    private int lebenPac = 3;
    private int score = 0;
    private boolean superPac = false; // ob pacman geister essen kann. true => ja
    private boolean active = true; //geht das Spiel noch?
    private boolean gameOver; //wie hat das Spiel gendet? => true = gestorben

    /**
     * gebraucht sodass die .jar rennbar ist
     *
     * @param args
     */
    public static void main(String[] args) {

        PApplet.main(main.class);
    }

    /**
     * pacman ist getroffen von dem Geister
     */
    public void pacHit() {
        this.lebenPac -= 1;
        changeScore(-80);
        pac.pacCoordinates = new coordinates(pacSize, 0); //pacman zu anfangs position
        for (ghost ghost : ghosts) {
            ghost.resetPosition(); //schicke alle geiser zurück
        }
        if (lebenPac < 0) {
            active = false;
            gameOver = true;
        }
    }

    /**
     * füge ine zahl zu dem Score
     *
     * @param score kann negativ sein
     */
    public void changeScore(int score) {
        this.score += score;
    }

    /**
     * stelle mapSize ein
     */
    @Override
    public void settings() {
        size(this.pacSize * 19, this.pacSize * 19); //19 x19 raster
    }

    /**
     * stelle Frame Rate ein
     * lade die Bilder und formatiere die richtig
     * speichere die in einem Map mit moveDirection als Index
     * spawn die pacman mit dem bilder als animation sprite
     */
    @Override
    public void setup() {
        frameRate(fps);

        PImage[] right = new PImage[2];
        PImage[] left = new PImage[2];
        PImage[] down = new PImage[2];
        PImage[] up = new PImage[2];

        Map<moveDirection, PImage[]> aniStore = new HashMap<>(4); //speichere bilder pro richtung
        //lade alle Bilder in eigene Arrays pro Richtung
        right[0] = loadImage("closed.gif");
        right[1] = loadImage("open_right.gif");
        right[0].resize(this.pacSize, this.pacSize);
        right[1].resize(this.pacSize, this.pacSize);
        aniStore.put(moveDirection.right, right);

        left[0] = loadImage("closed.gif");
        left[1] = loadImage("open_left.gif");
        left[0].resize(this.pacSize, this.pacSize);
        left[1].resize(this.pacSize, this.pacSize);
        aniStore.put(moveDirection.left, left);

        down[0] = loadImage("closed.gif");
        down[1] = loadImage("open_down.gif");
        down[0].resize(this.pacSize, this.pacSize);
        down[1].resize(this.pacSize, this.pacSize);
        aniStore.put(moveDirection.down, down);

        up[0] = loadImage("closed.gif");
        up[1] = loadImage("open_up.gif");
        up[0].resize(this.pacSize, this.pacSize);
        up[1].resize(this.pacSize, this.pacSize);
        aniStore.put(moveDirection.up, up);
        pac = new pacman(aniStore);

        ghosts = new ArrayList<>(); //liste von alle  Ghosts

        PImage scaredGhost = loadImage("scared-ghost.png"); //lade alle Bilder
        scaredGhost.resize(pacSize, pacSize);

        Map<moveDirection, PImage> clyde = new HashMap<>();
        PImage clydeLeft = loadImage("leftlook-clyde.png");
        clydeLeft.resize(pacSize, pacSize);
        clyde.put(moveDirection.left, clydeLeft);
        clyde.put(moveDirection.up, clydeLeft);
        PImage clydeRight = loadImage("rightlook-clyde.png");
        clydeRight.resize(pacSize, pacSize);
        clyde.put(moveDirection.right, clydeRight);
        clyde.put(moveDirection.down, clydeRight);
        ghosts.add(new ghost(scaredGhost, clyde, new coordinates(8 * pacSize, 9 * pacSize)));

        Map<moveDirection, PImage> inky = new HashMap<>();
        PImage inkyLeft = loadImage("leftlook-inky.png");
        inkyLeft.resize(pacSize, pacSize);
        inky.put(moveDirection.left, inkyLeft);
        inky.put(moveDirection.up, inkyLeft);
        PImage inkyRight = loadImage("rightlook-inky.png");
        inkyRight.resize(pacSize, pacSize);
        inky.put(moveDirection.right, inkyRight);
        inky.put(moveDirection.down, inkyRight);
        ghosts.add(new ghost(scaredGhost, inky, new coordinates(9 * pacSize, 9 * pacSize)));

        Map<moveDirection, PImage> pinky = new HashMap<>();
        PImage pinkyLeft = loadImage("leftlook-pinky.png");
        pinkyLeft.resize(pacSize, pacSize);
        pinky.put(moveDirection.left, pinkyLeft);
        pinky.put(moveDirection.up, pinkyLeft);
        PImage pinkyRight = loadImage("rightlook-pinky.png");
        pinkyRight.resize(pacSize, pacSize);
        pinky.put(moveDirection.right, pinkyRight);
        pinky.put(moveDirection.down, pinkyRight);
        ghosts.add(new ghost(scaredGhost, pinky, new coordinates(10 * pacSize, 9 * pacSize)));

        Map<moveDirection, PImage> blinky = new HashMap<>();
        PImage blinkyLeft = loadImage("leftlook-blinky.png");
        blinkyLeft.resize(pacSize, pacSize);
        blinky.put(moveDirection.left, blinkyLeft);
        blinky.put(moveDirection.up, blinkyLeft);
        PImage blinkyRight = loadImage("rightlook-blinky.png");
        blinkyRight.resize(pacSize, pacSize);
        blinky.put(moveDirection.right, blinkyRight);
        blinky.put(moveDirection.down, blinkyRight);
        ghosts.add(new ghost(scaredGhost, blinky, new coordinates(9 * pacSize, 8 * pacSize)));

        walls = new walls(this.pacSize);
        fruits = new fruit();

    }

    @Override
    public void draw() {
        background(0);
        if (active) {
            //zeichne jedes komponent
            walls._draw();
            pac._draw();
            fruits._draw();
            drawText();
            for (ghost ghost : ghosts) {
                ghost._draw();
            }
        } else {
            //falls spiel fertig ist
            textSize(100);
            String s;

            if (gameOver)
                s = "Game Over \n Drucken Sie eine Taste ";
            else
                s = "Fertig \n Drucken Sie eine Taste ";

            text(s, 0, 100, this.pacSize * 20, this.pacSize * 20);
        }

    }

    /**
     * zeichne den Information als Text: High Score usw.
     */
    private void drawText() {
        textSize(pacSize);
        String lives = String.format("Leben: %d  ", lebenPac);
        text(lives, 2 * pacSize, 2 * pacSize);
        String sScore = String.format("Score %d", score);
        text(sScore, 11 * pacSize, 5 * pacSize);
    }

    /**
     * reagiere auf dem KeyPress und ändere pac richtung
     *
     * @see pacman#setDir(moveDirection)
     */
    @Override
    public void keyPressed() {
        if (key == CODED && active) {
            switch (keyCode) {
                case UP:
                    pac.setDir(moveDirection.up);
                    break;
                case DOWN:
                    pac.setDir(moveDirection.down);
                    break;
                case LEFT:
                    pac.setDir(moveDirection.left);
                    break;
                case RIGHT:
                    pac.setDir(moveDirection.right);
                    break;
            }
        } else if (!active) {
            exit();
        }

    }

    /**
     * Richtung
     */
    public enum moveDirection {
        up, down, left, right
    }

    /**
     * class pacman muss unter dem class main stehen, sodass processsing funktioniert
     */
    class pacman {
        public Map<moveDirection, PImage[]> aniStore;
        public PImage[] aniCurrent; // ani sprite von dem aktuellen richtung
        moveDirection fallbackDir; //richtung zu folgen, wenn man einem Wand erreicht
        moveDirection dir = moveDirection.right; //aktuelle richtung
        int frame; // in welchem frame von dem Sprite wir uns befinden
        int count; // anzahl der frames von dem sprite
        boolean drawn = true;
        coordinates pacCoordinates; //wo pacman ist
        private int superPacFrameCount = 0;

        /**
         * Konstruktor
         *
         * @param ani Map von Animationen mit Richtung als Index
         */
        pacman(Map<moveDirection, PImage[]> ani) {

            this.aniStore = ani;
            frame = ani.get(dir).length;
            pacCoordinates = new coordinates(pacSize, 0);
            fallbackDir = moveDirection.up;
            aniCurrent = ani.get(dir);
        }

        /**
         * zeichne das Pacman, im neuen Lokation
         *
         * @see pacman#calculateMove()
         */
        void _draw() {
            frame = aniStore.get(dir).length;
            if (count >= frame) {
                count = 0; //falls wir das ende von dem sprite erreichen, fänge von anfang an
            }
            coordinates newCoordinates = calculateMove();
            if (newCoordinates != null) {
                pacCoordinates = newCoordinates;


            }
            image(aniCurrent[count], pacCoordinates.x, pacCoordinates.y);

            //wechsele den Bild jede zweite frame
            if (drawn) {
                count++;
            }
            drawn = !drawn;
            superPacCounter();

        }

        /**
         * wird in jedem frame gerufen
         * zählt die Frames mit superPacFrameCounter bis 5 Sekunden und macht pacman wieder normal
         */
        private void superPacCounter() {
            if (superPac) {
                if (superPacFrameCount == superPacTime * fps) {
                    superPacFrameCount = 0;
                    superPac = false;
                }
                superPacFrameCount++;
            }
        }

        /**
         * set direction
         * stellt die richtung und speichert die alte als fallback
         * ändert der animation zu dem neuen richtung
         *
         * @param dir neue richtung
         */
        public void setDir(moveDirection dir) {
            if (this.dir != dir) {
                aniCurrent = aniStore.get(dir);
                fallbackDir = this.dir;
                this.dir = dir;
            }
        }

        /**
         * anfangs funktion für die bewegungs kalkulation
         * probiert yuerst den normale richtung, wenn nicht , dann die alte "fallback"
         *
         * @return nächste koordinaten
         * @see pacman#calculateMoveWithDir(moveDirection)
         * @see pacman#checkSanity(coordinates)
         * @see fruit#notify(coordinates)
         */
        private coordinates calculateMove() {
            coordinates endLocation = calculateMoveWithDir(dir);
            if (checkSanity(endLocation)) {
                endLocation = calculateMoveWithDir(fallbackDir);
                if (checkSanity(endLocation)) {
                    return null;
                }
                aniCurrent = aniStore.get(fallbackDir); //ändere Animation Richtung nch fallBackDir, weil ursprungliche Dir ungütig ist-
            } else {
                aniCurrent = aniStore.get(dir); //wenn der Rechnung mit der eigentlische Richtung möglich ist, dann ändere die ANimation zurück
            }
            fruits.notify(endLocation);
            return endLocation;
        }

        /**
         * RECHNE DIR KOORDINATEN IM ABHÄNGIGKEIT VON RICHTUNG
         *
         * @param Dir richtung zum rechnen
         * @return gerechnete koordinaten
         */
        private coordinates calculateMoveWithDir(moveDirection Dir) {
            coordinates endLocation = new coordinates();
            switch (Dir) {
                case up:
                    endLocation.x = pacCoordinates.x;
                    endLocation.y = pacCoordinates.y - speed;
                    break;
                case down:
                    endLocation.x = pacCoordinates.x;
                    endLocation.y = pacCoordinates.y + (speed);
                    break;
                case left:
                    endLocation.x = pacCoordinates.x - speed;
                    endLocation.y = pacCoordinates.y;
                    break;
                case right:
                    endLocation.x = pacCoordinates.x + (speed);
                    endLocation.y = pacCoordinates.y;
                    break;
            }
            return endLocation;
        }

        /**
         * kontroliere ob die gerechnete koordinaten sinn ergeben e.g. keine Wände sind da
         *
         * @param coordinates gerechnete koordinaten
         * @return true, wenn es kein sinn ergibt
         * @see walls#checkWall(coordinates)
         * @see pacman#checkGhostArea(coordinates)
         */
        private boolean checkSanity(coordinates coordinates) {
            //kontroliere ob wir im Spielfeld sind
            if (coordinates.x < 0 || coordinates.x > pacSize * 19 - pacSize || coordinates.y < 0 || coordinates.y > pacSize * 19 - pacSize) {
                return true;
            }
            //kontroliere ob wir ein mauer haben
            if (walls.checkWall(coordinates))
                return true;
            return checkGhostArea(coordinates); //pacman darf nicht im ghost spawn area rein
        }

        /**
         * schaue ob pacMan sich in der ghost spawn ort befindet
         *
         * @param coordinates gerechnete koordinaten
         * @return ture fllas es nicht erlaubt ist
         */
        private boolean checkGhostArea(coordinates coordinates) {
            return (coordinates.x < 12 * pacSize && coordinates.x > 6 * pacSize && coordinates.y > 7 * pacSize && coordinates.y < 11 * pacSize);
        }

    }

    /**
     * speicher und zeichne methoden für die mauer (muss auch wegen der gleichen Probleme unter Main stehen)
     */
    class walls {
        List<coordinates> wallStore;
        PShape wall;
        int pacSize;

        /**
         * Schaffe ein Shape, um wiederholt zu benutzen
         * Lese die Map von map.txt ab
         *
         * @param pacSize - ist eigentlich unnötig (unter class usw.) aber , es würde so ausehen, wenn es ein andere Class wäre
         * @see walls#readMapFromFile()
         */
        public walls(int pacSize) {
            this.pacSize = pacSize;
            wall = createShape(RECT, 0, 0, this.pacSize, this.pacSize);
            wall.setFill(color(0, 0, 255));
            try {
                wallStore = readMapFromFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * im map.txt bedeutet 1 ein mauer und  jeder neuer zeile die nächste zeile im raster
         *
         * @return liste von orte wo ein mauer sich befindet
         * @throws IOException falls etwas schief geht
         */
        List<coordinates> readMapFromFile() throws IOException {
            List<coordinates> coordinatesList = new ArrayList<>();
            BufferedReader br = createReader("map.txt");
            String s;
            int y = 0;
            while ((s = br.readLine()) != null) { //gehe durch linien pro linie y++
                for (int x = 0; x < s.length(); x++) { //gehe durch alle Buchstaben in einem Linie
                    if (s.charAt(x) == '1') {
                        coordinatesList.add(new coordinates(pacSize * x, pacSize * y)); //falls es mit 1 markiert ist mache eine neue Wall
                    }
                }
                y++;
            }
            return coordinatesList;
        }

        /**
         * zeichnet den Shape wall in jedem Koordinate
         */
        public void _draw() {
            for (coordinates cor : wallStore
            ) {
                shape(wall, cor.x, cor.y);
            }
        }


        /**
         * schaue ob ein Mauer vorhanden sind
         *
         * @param coordinates Koordinaten
         * @return true => mauer ist da
         * @see pacman#checkSanity(coordinates)
         */
        public boolean checkWall(coordinates coordinates) {
            for (coordinates cor : wallStore) {
                if (coordinates.x > cor.x - this.pacSize && coordinates.x < cor.x + this.pacSize && coordinates.y > cor.y - this.pacSize && coordinates.y < cor.y + this.pacSize) { //magie
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * class für  früchte
     */
    class fruit {
        private final Random random = new Random(); // wird benutzt, um zu entscheiden, ob es ein großes Obst sein soll oder nicht
        Map<coordinates, Boolean> fruitStore; //key:location von dem Obst value: ob es groß ist
        PShape fruitSmall; //speicher für die Shapes zum spätern nutzung
        PShape fruitBig;

        /**
         * füge zu dem Map ein Obst zu, wenn da kein Wall ist. Entscheide ob es groß sein soll oder nicht.
         */
        public fruit() {
            try {
                fruitStore = readFruitMapFromFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fruitSmall = createShape(ELLIPSE, 0, 0, smallFruitSize, smallFruitSize);
            fruitBig = createShape(ELLIPSE, 0, 0, bigFruitSize, bigFruitSize);
        }

        /**
         * im map.txt bedeutet 0 ein Obst und  jeder neuer zeile die nächste zeile im raster
         *
         * @return liste von orte wo ein Obst sich befinden soll mit dem sorte von dem obst
         * @throws IOException falls etwas schief geht
         */
        Map<coordinates, Boolean> readFruitMapFromFile() throws IOException {
            Map<coordinates, Boolean> coordinatesList = new ConcurrentHashMap<>();
            BufferedReader br = createReader("map.txt");
            String s;
            int y = 0;
            while ((s = br.readLine()) != null) { //gehe durch linien pro linie y++
                for (int x = 0; x < s.length(); x++) { //gehe durch alle Buchstaben in einem Linie
                    if (s.charAt(x) == '0') {
                        coordinatesList.put(new coordinates(pacSize * x, pacSize * y), getRandomBoolean()); //falls es mit 0 markiert ist mache eine neue Obst und entscheide ob es groß ist
                    }
                }
                y++;
            }
            return coordinatesList;
        }

        /**
         * Zeichne in jedem Frame die Obste
         *
         * @see main#draw()
         */
        public void _draw() {
            for (Map.Entry<coordinates, Boolean> entry : fruitStore.entrySet()) {
                if (entry.getValue()) {
                    shape(fruitBig, entry.getKey().x + pacSize * 0.5f, entry.getKey().y + pacSize * 0.5f);

                } else {
                    shape(fruitSmall, entry.getKey().x + pacSize * 0.5f, entry.getKey().y + pacSize * 0.5f);
                }
            }
        }

        /**
         * schaue ob in dem neue Koordinaten Obste vorhanden sind.
         * Wenn ja, füge Pubkte zu
         *
         * @param coordinates neue Koordinaten
         * @see
         */
        public void notify(coordinates coordinates) {
            for (coordinates cor :
                    fruitStore.keySet()) {
                if (coordinates.x > cor.x - pacSize && coordinates.x < cor.x + pacSize && coordinates.y > cor.y - pacSize && coordinates.y < cor.y + pacSize) {
                    if (fruitStore.get(cor)) { //kontrolliere ob es ein großes Obst war
                        changeScore(40);
                        superPac = true; //mac den pacman zu superPac
                    } else {
                        changeScore(20);
                    }
                    fruitStore.remove(cor, fruitStore.get(cor));
                }
            }
            if (fruitStore.size() == 0) { //falls keine obst mehr vorhanden ist, dann beende das Spiel
                active = false;
                gameOver = false;

            }
        }

        private boolean getRandomBoolean() {
            return random.nextFloat() < pFruit;
        }
    }

    class ghost {
        PImage scaredGhost; //das Image für ängstige Geist
        Map<moveDirection, PImage> aniStore; //bilder für normale Geist ordnet nach richtung
        coordinates currentCor;
        coordinates startCor; //wo der geist respawnen soll
        moveDirection dir = moveDirection.down;
        Random random = new Random();

        private ghost(PImage scaredGhost, Map<moveDirection, PImage> normal, coordinates startCor) {
            this.scaredGhost = scaredGhost;
            this.aniStore = normal;
            this.currentCor = startCor;  //ich weiß dass hier kein "this" sein muss, aber gewohnheit
            this.startCor = startCor;
        }

        void _draw() {
            coordinates newCoordinates = calculateMove(null);
            if (newCoordinates != null) {
                currentCor = newCoordinates;

            }
            checkPacMan(currentCor); //schaue ob pacman an dem neuen ort ist

            if (superPac) {
                image(scaredGhost, currentCor.x, currentCor.y); //zeiche den Ghost, fall pacman ist super, dann zeichne ängstige Ghost

            } else {
                image(aniStore.get(dir), currentCor.x, currentCor.y); //zeiche den Ghost, fall pacman ist super, dann zeichne ängstige Ghost
            }


        }

        //


        /**
         * anfangs  und rekursive funktion für die Bewegungskalkulation
         * probiert zuerst die "dir", wenn nicht wählt eine neue "dir" und rennt sich wieder
         * Wenn eine gefunden ist gibt das Lösung zurück durch die ebenen
         * Nach 4 Ebenen gibt null zurück (theoretisch unmöglich)
         *
         * @return nächste koordinaten
         * @see ghost#calculateMoveWithDir(moveDirection)
         * @see ghost#checkSanity(coordinates)
         * @see ghost#getRandomDir(List)
         */
        private coordinates calculateMove(@Nullable List<moveDirection> triedDirs) {
            coordinates endLocation = calculateMoveWithDir(dir);
            if (checkSanity(endLocation) || endLocation == null) { //schaue ob die nächste koordinate richtig ist
                //falls nicht 
                if (triedDirs == null)
                    triedDirs = new ArrayList<moveDirection>(); //dann schaffe die Liste von ausprobierte Richtungen
                triedDirs.add(dir); //füge die momentane Richtung dazu
                dir = getRandomDir(triedDirs); // wähle eine neue Richtung
                endLocation = calculateMove(triedDirs); //renne diese Funktion wieder //recursive

            }
            return endLocation;
        }

        /**
         * schaue ob der Geist den pacman berührt
         *
         * @param coordinates gerechnete neue koordinaten
         */
        private void checkPacMan(coordinates coordinates) {
            coordinates pacCoordinates = pac.pacCoordinates;
            if (coordinates.x > pacCoordinates.x - pacSize && coordinates.x < pacCoordinates.x + pacSize && coordinates.y > pacCoordinates.y - pacSize && coordinates.y < pacCoordinates.y + pacSize) {
                if (superPac) {
                    //falls pacman super ist
                    changeScore(80);
                    resetPosition();
                } else {
                    pacHit();

                }
            }
        }

        /**
         * schicke den Geist zurück zum Anfangsort
         */
        void resetPosition() {
            this.currentCor = this.startCor;
        }

        /**
         * finde eine zufäligge richtung, die nicht im triedDir ist
         *
         * @param triedDirs ausgeprobierte Richtungen
         * @return neue Richtung
         */
        private moveDirection getRandomDir(List<moveDirection> triedDirs) {


            List<moveDirection> allDirections = new ArrayList<>(); //liste von alle möglichen Richtungen
            allDirections.add(moveDirection.up);
            allDirections.add(moveDirection.down);
            allDirections.add(moveDirection.left);
            allDirections.add(moveDirection.right);

            allDirections.removeAll(triedDirs); //nehme alle ausprobierte richtungen aus dem allDirections
            if (allDirections.size() == 0) {
                System.out.println(currentCor + "error");

                return null;
            }
            return allDirections.get(random.nextInt(allDirections.size())); //wähle eine zufällige zahl im bereich von der Liste zB. wenn 1 Ding im Liste ist wähle ein zahl bis (exclusiv)  3

        }

        /**
         * RECHNE DIe KOORDINATEN IM ABHÄNGIGKEIT VON RICHTUNG
         *
         * @param Dir richtung zum rechnen
         * @return gerechnete koordinaten
         */
        private coordinates calculateMoveWithDir(moveDirection Dir) {
            if (dir == null) {
                System.out.println(currentCor);
                return null;
            }
            coordinates endLocation = new coordinates();
            switch (Dir) {
                case up:
                    endLocation.x = currentCor.x;
                    endLocation.y = currentCor.y - speed;
                    break;
                case down:
                    endLocation.x = currentCor.x;
                    endLocation.y = currentCor.y + (speed);
                    break;
                case left:
                    endLocation.x = currentCor.x - speed;
                    endLocation.y = currentCor.y;
                    break;
                case right:
                    endLocation.x = currentCor.x + (speed);
                    endLocation.y = currentCor.y;
                    break;

            }
            return endLocation;
        }

        /**
         * kontroliere ob die gerechnete koordinaten sinn ergeben e.g. keine Wände sind da
         *
         * @param coordinates gerechnete koordinaten
         * @return true, wenn es kein sinn ergibt
         */
        private boolean checkSanity(coordinates coordinates) {
            //kontroliere ob wir im Spielfeld sind
            if (coordinates.x < 0 || coordinates.x > pacSize * 19 - pacSize || coordinates.y < 0 || coordinates.y > pacSize * 19 - pacSize) {
                return true;
            }
            //kontroliere ob wir ein mauer haben
            return walls.checkWall(coordinates);
        }
    }
}
