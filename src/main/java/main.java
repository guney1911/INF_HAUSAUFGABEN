import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main extends PApplet {

    static private int speed = 20; //Geschwindigkeit mitdem alles sich bewegt pixel pro sekunde
    static private int mapSize = 1000;
    static private int pacSize = 40;
    pacman pac;
    walls walls;

    /**
     * stelle mapSize ein
     */
    @Override
    public void settings() {
        size(mapSize, mapSize);
    }

    /**
     * stelle Frame Rate ein
     * lade die Bilder und formatiere die richtig
     * speichere die in einem Map mit moveDirection als Index
     * spawn die pacman mit dem bilder als animation sprite
     */
    @Override
    public void setup() {
        frameRate(15);

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
        walls = new walls();

        pac = new pacman(store,walls);

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
     *  class pacman muss unter dem  main stehen sodass processsing funktioniert
     */
    public class pacman {
        public Map<moveDirection, PImage[]> aniStore;
        public PImage[] aniCurrent; // ani sprite von dem aktuellen richtung
        moveDirection fallbackDir; //richtung zu folgen, wenn man einem Wand erreicht
        moveDirection dir = moveDirection.left; //aktuelle richtung
        int frame; // in welchem frame von dem Sprite wir uns befinden
        int count; // anzahl der frames von dem sprite
        walls wallstore;
        private float x; //wo pacman ist
        private float y;

        /**
         *  konstruktor
         * @param ani map von dem sprites mit richtung als index
         */
        pacman(Map<moveDirection, PImage[]> ani,walls wallstore) {
            this.wallstore = wallstore;
            this.aniStore = ani;
            frame = ani.get(dir).length;
            x = 500;
            y = 500;
            fallbackDir = moveDirection.up;
            aniCurrent = ani.get(dir);
        }

        /**
         * zeichne das Pacman
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
            count++;


        }

        /**
         * set direction
         * stellt die richtung und speichert die alte als fallback
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
         * kontroliere ob die gerechnete koordinaten sinn ergeben e.g. keine Wände mind da
         * @param coordinates gerechnete koordinaten
         * @return true, wenn es kein sinn ergibt
         */
        private boolean checkSanity(float[] coordinates) {
            if( coordinates[0] < 0 || coordinates[0] > mapSize - pacSize || coordinates[1] < 0 || coordinates[1] > mapSize - pacSize){
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

    public class walls{
        List<int[]> wallstore;
        PShape wall;
        public walls(){
            wall = createShape(RECT,0,0,pacSize,pacSize);
            wall.setFill(color(0,0,255));
            wallstore =new ArrayList<>();
            wallstore.add(new int[]{pacSize*0, pacSize*0});
            wallstore.add(new int[]{pacSize*1, pacSize*0});
            wallstore.add(new int[]{pacSize*2, pacSize*0});
            wallstore.add(new int[]{pacSize*3, pacSize*0});
            wallstore.add(new int[]{pacSize*4, pacSize*0});
            wallstore.add(new int[]{pacSize*5, pacSize*0});
            wallstore.add(new int[]{pacSize*5, pacSize});
        }
        public void _draw(){
            for (int[] cor: wallstore
                 ) {
                shape(wall,cor[0],cor[1]);
            }
        }

        public List<int[]> getWallstore() {
            return wallstore;
        }
    }
}
