package gameengine;

/**
 * Created by leo on 11/30/14.
 */
public class HexPoint {
    private int q;
    private int r;

    public HexPoint(int q, int r){
        this.q = q;
        this.r = r;
    }

    public HexPoint(int[] qr){
        this.q = qr[0];
        this.r = qr[1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HexPoint hexPoint = (HexPoint) o;

        if (q != hexPoint.q) return false;
        if (r != hexPoint.r) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = q;
        result = 31 * result + r;
        return result;
    }

    public HexPoint add(HexPoint other){
        return new HexPoint(this.q + other.q, this.r + other.r);
    }
    public HexPoint subtract(HexPoint other){
        return new HexPoint(this.q - other.q, this.r - other.r);
    }
    public HexPoint scale(int scalar) { return new HexPoint(this.q * scalar, this.r * scalar); }

    public boolean isZero(){ return q == 0 && r == 0; }
    public boolean isNeighbor(HexPoint other) {
        return HexDirection.lookup(other.subtract(this)) != null;
    }

    @Override
    public String toString() {
        return "HexPoint(" + q + ")(" + r + ")";
    }
}
