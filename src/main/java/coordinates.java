import java.util.Objects;

public class coordinates {
    public int x;
    public int y;
    public coordinates(int x, int y){
        this.x = x; this.y =y;
    }
    public coordinates (){}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        coordinates that = (coordinates) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);

    }

    @Override
    public String toString() {
        return "coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
