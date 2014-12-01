package common;

/**
 * Created by leo on 12/1/14.
 */
public class MultiKey<T1, T2> {
    private final T1 a;
    private final T2 b;

    public MultiKey(T1 a, T2 b){
        this.a = a;
        this.b = b;
    }

    public static <T1, T2> MultiKey<T1, T2> newKey(T1 a, T2 b){
        return new MultiKey<T1, T2>(a, b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultiKey multiKey = (MultiKey) o;

        if (!a.equals(multiKey.a)) return false;
        if (!b.equals(multiKey.b)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = a.hashCode();
        result = 31 * result + b.hashCode();
        return result;
    }
}
