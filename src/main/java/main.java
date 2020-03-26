import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
    final private float pFruit = 0.2f; //Wahrscheinlichkeit ein großes Fruit zu erstellen
    final private int smallFruitSize = 5; //Größe von dem normalen Fruit
    final private int bigFruitSize = 10; //Größe von dem großen Fruit

    public pacman pac;
    public walls walls;
    public fruit fruits;

    private int lebenPac = 3;
    private int score = 0;
    private boolean active = true; //geht das Spiel noch?

    public void pacHit() {
        this.lebenPac -= 1;
        if (lebenPac < 0) {
            active = false;
            pac = null;
            walls = null;
            fruits = null;
        }
    }

    public void changeScore(int score) {
        this.score += score;
    }

    /**
     * stelle mapSize ein
     */
    @Override
    public void settings() {
        size(this.pacSize * 20, this.pacSize * 20);
    }

    /**
     * stelle Frame Rate ein
     * lade die Bilder und formatiere die richtig
     * speichere die in einem Map mit moveDirection als Index
     * spawn die pacman mit dem bilder als animation sprite
     */
    @Override
    public void setup() {
        frameRate(30);

        PImage[] right = new PImage[2];
        PImage[] left = new PImage[2];
        PImage[] down = new PImage[2];
        PImage[] up = new PImage[2];

        Map<moveDirection, PImage[]> store = new HashMap<>(4);

        right[0] = loadImage("closed.gif");
        right[1] = loadImage("open_right.gif");
        right[0].resize(this.pacSize, this.pacSize);
        right[1].resize(this.pacSize, this.pacSize);
        store.put(moveDirection.right, right);

        left[0] = loadImage("closed.gif");
        left[1] = loadImage("open_left.gif");
        left[0].resize(this.pacSize, this.pacSize);
        left[1].resize(this.pacSize, this.pacSize);
        store.put(moveDirection.left, left);

        down[0] = loadImage("closed.gif");
        down[1] = loadImage("open_down.gif");
        down[0].resize(this.pacSize, this.pacSize);
        down[1].resize(this.pacSize, this.pacSize);
        store.put(moveDirection.down, down);

        up[0] = loadImage("closed.gif");
        up[1] = loadImage("open_up.gif");
        up[0].resize(this.pacSize, this.pacSize);
        up[1].resize(this.pacSize, this.pacSize);
        store.put(moveDirection.up, up);
        walls = new walls(this.pacSize);
        fruits = new fruit(this, this.pacSize, pFruit, smallFruitSize, bigFruitSize);
        pac = new pacman(store, this, this.pacSize);

    }

    @Override
    public void draw() {
        background(0);
        if (active) {
            walls._draw();
            pac._draw();
            fruits._draw();
            drawText();
        } else {
            textSize(100);
            text("Game Over \n Drucken Sie einer Taste ", 0, 100, this.pacSize * 20, this.pacSize * 20);
        }

    }

    /**
     * zeichne den Information als Text High Score etc.
     */
    private void drawText() {
        textSize(30);
        String text = String.format("Leben: %d High Score: %d", lebenPac, score);
        text(text, 0, 30); //diese funktion ist das Beispiel warum Processing schlecht ist. In jedem andere Funktion die Koordinaten sind immer von dem links obere Ecke, aber hier ist es die links untere!!!!!!!!!!
    }

    /**
     * reagiere auf dem KeyPress und ändere pac richtung
     *
     * @see pacman#setDir(moveDirection)
     */
    @Override
    public void keyPressed() {

        System.out.println(keyCode);
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
        moveDirection dir = moveDirection.left; //aktuelle richtung
        int frame; // in welchem frame von dem Sprite wir uns befinden
        int count; // anzahl der frames von dem sprite
        int pacSize;
        boolean drawn = true;
        private coordinates pacCoordinates; //wo pacman ist

        walls wallstore; //alle Mauer
        fruit fruit;

        /**
         * Konstruktor
         *
         * @param ani     Map von Animationen mit Richtung als Index
         * @param main    als speicher von andere Klassen - ist eigentlich unnötig (unter class usw.) aber , es würde so ausehen, wenn es ein "unabhängiges" Class wäre
         * @param pacSize größe der Pac - ist eigentlich unnötig (unter class usw.) aber , es würde so ausehen, wenn es ein "unabhängiges" Class wäre
         */
        pacman(Map<moveDirection, PImage[]> ani, main main, int pacSize) {
            this.wallstore = main.walls;
            this.fruit = main.fruits;
            this.aniStore = ani;
            this.pacSize = pacSize;
            frame = ani.get(dir).length;
            pacCoordinates = new coordinates(0, 0);
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
         */
        private coordinates calculateMove() {
            coordinates endLocation = calculateMoveWithDir(dir);
            if (checkSanity(endLocation)) {
                endLocation = calculateMoveWithDir(fallbackDir);
                if (checkSanity(endLocation)) {
                    return null;
                }
                setDir(fallbackDir);
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
         */
        private boolean checkSanity(coordinates coordinates) {
            //kontroliere ob wir im Spielfeld sind
            if (coordinates.x < 0 || coordinates.x > this.pacSize * 20 - this.pacSize || coordinates.y < 0 || coordinates.y > this.pacSize * 20 - this.pacSize) {
                return true;
            }
            //kontroliere ob wir ein mauer haben
            return wallstore.checkWall(coordinates);
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
         * Lese die Map von map_store ab
         *
         * @param pacSize - ist eigentlich unnötig (unter class usw.) aber , es würde so ausehen, wenn es ein andere Class wäre
         * @see map_store#getMap(int)
         */
        public walls(int pacSize) {
            this.pacSize = pacSize;
            wall = createShape(RECT, 0, 0, this.pacSize, this.pacSize);
            wall.setFill(color(0, 0, 255));
            wallStore = map_store.getMap(this.pacSize);
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
         * get wallstore
         *
         * @return koordinaten von mauern
         * @see pacman#checkSanity(coordinates)
         */

        public boolean checkWall(coordinates coordinates) {
            for (coordinates cor : wallStore) {
                if (coordinates.x > cor.x - this.pacSize && coordinates.x < cor.x + this.pacSize && coordinates.y > cor.y - this.pacSize && coordinates.y < cor.y + this.pacSize) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * übergeordnete class für alle früchte
     */
    class fruit {
        int pacSize;
        float pObst;
        Map<coordinates, Boolean> fruitStore = new ConcurrentHashMap<>(); //key:location von dem Obst value: ob es groß ist
        PShape fruitSmall; //speicher für die Shapes zum spätern nutzung
        PShape fruitBig;
        private Random random = new Random();
        pacman pac;

        /**
         * @param main           als speicher von andere Klassen - ist eigentlich unnötig (unter class usw.) aber , es würde so ausehen, wenn es ein andere Class wäre
         * @param pacSize        - ist eigentlich unnötig (unter class usw.) aber , es würde so ausehen, wenn es ein andere Class wäre
         * @param pFruit
         * @param smallFruitSize
         * @param bigFruitSize
         */
        public fruit(main main, int pacSize, float pFruit, int smallFruitSize, int bigFruitSize) {
            this.pacSize = pacSize;
            this.pObst = pFruit;
            walls wallStore = main.walls;
            pac = main.pac;
            coordinates array = new coordinates(40, 40);
            System.out.println(wallStore.wallStore.contains(array));
            for (int x = 0; x < 20; x++) {
                for (int y = 0; y < 20; y++) {
                    if (!wallStore.wallStore.contains(new coordinates(this.pacSize * x, this.pacSize * y))) {
                        fruitStore.put(new coordinates(this.pacSize * x, this.pacSize * y), getRandomBoolean());
                    }
                }
            }

            fruitSmall = createShape(ELLIPSE, 0, 0, smallFruitSize, smallFruitSize);
            fruitBig = createShape(ELLIPSE, 0, 0, bigFruitSize, bigFruitSize);
        }

        public void _draw() {
            for (Map.Entry<coordinates, Boolean> entry : fruitStore.entrySet()) {
                if (entry.getValue()) {
                    shape(fruitBig, entry.getKey().x + this.pacSize * 0.5f, entry.getKey().y + this.pacSize * 0.5f);

                } else {
                    shape(fruitSmall, entry.getKey().x + this.pacSize * 0.5f, entry.getKey().y + this.pacSize * 0.5f);
                }
            }
        }

        public void notify(coordinates coordinates) {
            for (coordinates cor :
                    fruitStore.keySet()) {
                if (coordinates.x > cor.x - this.pacSize && coordinates.x < cor.x + this.pacSize && coordinates.y > cor.y - this.pacSize && coordinates.y < cor.y + this.pacSize) {
                    fruitStore.remove(cor, fruitStore.get(cor));
                    changeScore(20);
                }
            }
        }

        private boolean getRandomBoolean() {
            return random.nextFloat() < this.pObst;
        }
    }
}
