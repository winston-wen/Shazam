package shazam.hash;

/**
 * Created by Wen Ke on 2016/11/04.
 * Complex number
 */
public class Complex {
    private double a, b;
    public Complex(double _a, double _b) {
        a = _a;
        b = _b;
    }
    public Complex() {
        a = 0;
        b = 0;
    }

    public Complex add(final Complex e) {
        return new Complex(a + e.a, b + e.b);
    }

    public Complex sub(final Complex e) {
        return new Complex(a - e.a, b - e.b);
    }

    public Complex mul(final Complex e) {
        return new Complex (a * e.a - b * e.b, a * e.b + b * e.a);
    }

    public double abs() {
        // use hypot instead of sqrt(pow(a, 2), pow(b, 2))
        // to improve performance
        return Math.hypot(a, b);
    }
}
