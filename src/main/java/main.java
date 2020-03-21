import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main extends PApplet {

    static private int speed = 10; //Geschwindigkeit mitdem alles sich bewegt pixel pro frame
    static private int pacSize = 40;
    pacman pac;
    walls walls;

    /**
     * stelle mapSize ein
     */
    @Override
    public void settings() {
        size(pacSize*20, pacSize*20);
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
        right[0].resize(pacSize, pacSize);
        right[1].resize(pacSize, pacSize);
        store.put(moveDirection.right, right);

        left[0] = loadImage("closed.gif");
        left[1] = loadImage("open_left.gif");
        left[0].resize(pacSize, pacSize);
        left[1].resize(pacSize, pacSize);
        store.put(moveDirection.left, left);

        down[0] = loadImage("closed.gif");
        down[1] = loadImage("open_down.gif");
        down[0].resize(pacSize, pacSize);
        down[1].resize(pacSize, pacSize);
        store.put(moveDirection.down, down);

        up[0] = loadImage("closed.gif");
        up[1] = loadImage("open_up.gif");
        up[0].resize(pacSize, pacSize);
        up[1].resize(pacSize, pacSize);
        store.put(moveDirection.up, up);
        walls = new walls(pacSize);

        pac = new pacman(store,walls,pacSize);

    }

    @Override
    public void draw() {
        background(0);
        walls._draw();
        pac._draw();
    }

    /**
     * reagiere auf dem KeyPress und andere pac richtung
     * @see pacman#setDir(moveDirection)
     */
    @Override
    public void keyPressed() {

        System.out.println(keyCode);
        if (key == CODED) {
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
        }

    }

    /**
     * move direction
     */
    public enum moveDirection {
        up, down, left, right
    }

    /**
     *  class pacman muss unter dem class main stehen, sodass processsing funktioniert
     */
    public class pacman {
        public Map<moveDirection, PImage[]> aniStore;
        public PImage[] aniCurrent; // ani sprite von dem aktuellen richtung
        moveDirection fallbackDir; //richtung zu folgen, wenn man einem Wand erreicht
        moveDirection dir = moveDirection.left; //aktuelle richtung
        int frame; // in welchem frame von dem Sprite wir uns befinden
        int count; // anzahl der frames von dem sprite
        walls wallstore; //alle Mauer
        int pacSize;
        private float x; //wo pacman ist
        private float y;

        /**
         * KOnstruktor
         * @param ani Map von Animationen mit Richtung als Index
         * @param wallstore speicher von mauern ist eigentlich unnötig (unter class usw.) aber , es würde so ausehen, wenn es ein andere Class wäre
         * @param pacSize größe der Pac  ist eigentlich unnötig (unter class usw.) aber , es würde so ausehen, wenn es ein andere Class wäre
         */
        pacman(Map<moveDirection, PImage[]> ani,walls wallstore,int pacSize) {
            this.wallstore = wallstore;
            this.aniStore = ani;
            this.pacSize = pacSize;
            frame = ani.get(dir).length;
            x = 0;
            y = 0;
            fallbackDir = moveDirection.up;
            aniCurrent = ani.get(dir);
        }
        boolean drawn = true;

        /**
         * zeichne das Pacman, im neuen Lokation
         * @see pacman#calculateMove()
         */
        void _draw() {
            frame = aniStore.get(dir).length;
            if (count >= frame) {
                count = 0; //falls wir das ende von dem sprite erreichen, fänge von anfang an
            }
            float[] newCoordinates = calculateMove();
            if (newCoordinates != null) {
                x = newCoordinates[0];
                y = newCoordinates[1];

            }
            image(aniCurrent[count], x, y);

            //wechsele den Bild jede zweite frame
            if(drawn){
            count++;}
            drawn = !drawn;


        }

        /**
         * set direction
         * stellt die richtung und speichert die alte als fallback
         * ändert der animation zu dem neuen richtung
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
         * anfangs funktion für die bewegungskalkulation
         * probiert yuerst den normale richtung, wenn nicht , dann die alte "fallback"
         * @return nächste koordinaten
         * @see pacman#calculateMoveWithDir(moveDirection)
         * @see pacman#checkSanity(float[])
         */
        private float[] calculateMove() {
            float[] endLocation = calculateMoveWithDir(dir);
            if (checkSanity(endLocation)) {
                endLocation = calculateMoveWithDir(fallbackDir);
                if (checkSanity(endLocation)) {
                    return null;
                }
                setDir(fallbackDir);
            }
            return endLocation;
        }

        /**
         * RECHNE DIR KOORDINATEN IM ABHÄNGIGKEIT VON RICHTUNG
         * @param Dir richtung zum rechnen
         * @return gerechnete koordinaten
         */
        private float[] calculateMoveWithDir(moveDirection Dir) {
            float[] endLocation = new float[2];
            switch (Dir) {
                case up:
                    endLocation[0] = x;
                    endLocation[1] = y - speed;
                    break;
                case down:
                    endLocation[0] = x;
                    endLocation[1] = y + (speed);
                    break;
                case left:
                    endLocation[0] = x - speed;
                    endLocation[1] = y;
                    break;
                case right:
                    endLocation[0] = x + (speed);
                    endLocation[1] = y;
                    break;
            }
            return endLocation;
        }

        /**
         * kontroliere ob die gerechnete koordinaten sinn ergeben e.g. keine Wände sind da
         * @param coordinates gerechnete koordinaten
         * @return true, wenn es kein sinn ergibt
         */
        private boolean checkSanity(float[] coordinates) {
            if( coordinates[0] < 0 || coordinates[0] > pacSize*20 - pacSize || coordinates[1] < 0 || coordinates[1] > pacSize*20 - pacSize){
                return true;
            }
            for (int [] cor:wallstore.getWallstore()
                 ) {
                if(coordinates[0]>cor[0]-pacSize && coordinates[0]<cor[0]+pacSize &&  coordinates[1]>cor[1]-pacSize && coordinates[1]<cor[1]+pacSize){
                    return true;
                }
            }
            return false;
        }


    }

    /**
     * speicher und zeichne methoden für die mauer (muss auch wegen der gleichen Probleme unter Main stehen)
     */
    public class walls{
        List<int[]> wallstore;
        PShape wall;
        int pacSize;

        /**
         * Schaffe ein Shape, um wiederholt zu benutzen
         * Lese die Map von map_store ab
         * @see map_store#getMap(int)
         * @param pacSize ist eigentlich unnötig (unter class usw.) aber , es würde so ausehen, wenn es ein andere Class wäre
         */
        public walls(int pacSize){
            this.pacSize = pacSize;
            wall = createShape(RECT,0,0,pacSize,pacSize);
            wall.setFill(color(0,0,255));
            wallstore = map_store.getMap(pacSize);
        }

        /**
         * zeichnet den Shape wall in jedem Koordinate
         */
        public void _draw(){
            for (int[] cor: wallstore
                 ) {
                shape(wall,cor[0],cor[1]);
            }
        }

        /**
         * get wallstore
         * @see pacman#checkSanity(float[])
         * @return koordinaten von mauer
         */
        public List<int[]> getWallstore() {
            return wallstore;
        }
    }
}
