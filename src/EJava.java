import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static java.lang.Math.*;

public class EJava {
    private Console in;
    private Pair<Integer, Integer> mn;

    public static void main(String[] args) {
        new EJava().solve();
    }

    private void solve() {
        in = new Console();
        mn = in.getMN();
        Labyrinth lab = findLabyrinth();
        Circle best = lab.findBest(in);
        Pair<Integer, Integer> tr = findTreasure(best);
        in.submit(tr.getKey(), tr.getValue());
    }

    private Pair<Integer, Integer> findTreasure(Circle start) {
        Point center = start.p;
        Circle left = getCircle(center.move(-1, 0));
        Circle right = getCircle(center.move(0, 1));
        Point[] inter = left.inter(start);
        Point[] r = right.inter(inter);
        return new Pair<>((int) round(r[0].x), (int) round(r[0].y));
    }

    private Circle getCircle(Point point) {
        return new Circle(point, in.activate(point, true));
    }

    private Labyrinth findLabyrinth() {
        ArrayList<Circle> circles = new ArrayList<>();
        for (int i = 0; i < 360; i += 10) {
            double rad = Math.toRadians(i);
            Point search = new Point(Math.round(9000.0d * Math.cos(rad)), Math.round(9000.0d * Math.sin(rad)));
            circles.add(new Circle(search, in.activate(search, false)));
        }
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 0; i < circles.size() - 3; i++) {
            Circle c1 = circles.get(i);
            Circle c2 = circles.get(i + 1);
            Point[] inter = c1.inter(c2);
            Circle c3 = circles.get(i + 2);
            Point[] r = c3.inter(inter);
            for (Point res : r) {
                boolean in = false;
                for (Point uni : points)
                    if (uni.aeq(res))
                        in = true;
                if (!in) {
                    points.add(res);
                }
            }
        }
        return new Labyrinth(points, mn);
    }
}

class Labyrinth {
    private Point a;
    private Point b;
    Point c;
    int m;
    int n;
    Point cbi;
    Point abi;

    public Labyrinth(ArrayList<Point> points, Pair<Integer, Integer> mn) {
        m = mn.getKey();
        n = mn.getValue();
        a = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            Point curr = points.get(i);
            if (curr.minus(a).aeq(m * 10.0)) {
                b = curr;
            }
        }
        for (int i = 1; i < points.size(); i++) {
            Point curr = points.get(i);
            if (curr.minus(b).aeq(n * 10.0)) {
                c = curr;
            }
        }
        cbi = c.minus(b).normalize();
        abi = a.minus(b).normalize();
    }

    public Point getPoint(int i, int j) {
        return b.plus(abi.mul(i + 0.5)).plus(cbi.mul(j + 0.5));
    }

    public Circle findBest(Console console) {
        Point minP = null;
        double minD = 1000000.0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                Point test = getPoint(i, j);
                Double resp = console.activate(test, true);
                if (minP == null || resp < minD) {
                    minD = resp;
                    minP = test;
                }
            }
        }
        return new Circle(minP, minD);
    }
}

class Console {
    FastScanner in = new FastScanner();

    Pair<Integer, Integer> getMN() {
        return new Pair<>(in.nextInt(), in.nextInt());
    }

    double activate(Point p, boolean inside) {
        System.out.println(p.toString());
        String header = in.next();
        if (header.equals("inside")) {
            double r = in.nextDouble();
            in.next();
            return r;
        }
        if (header.equals("outside")) {
            if (inside) {
                in.next();
                in.next();
                return 1000000.0;
            } else {
                double r = in.nextDouble();
                in.next();
                return r;
            }
        }

        in.next();
        return 1000000.0;
    }

    boolean submit(int x, int y) {
        System.out.println(String.format("found %d %d", x, y));
        return in.next().equals("OK");
    }

}

class Circle {
    Point p;
    double r;

    public Circle(Point p, double r) {
        this.p = p;
        this.r = r;
    }

    public double x() {
        return p.x;
    }

    public double y() {
        return p.y;
    }

    public Point[] inter(Circle other) {
        double d = p.minus(other.p).length();
        if (d > r + other.r)
            return new Point[0];
        if (d < Math.abs(r - other.r))
            return new Point[0];
        if (d == 0.0 && r == other.r)
            return new Point[0];
        double a = (r * r - other.r * other.r + d * d) / (2 * d);
        double h = sqrt(r * r - a * a);
        Point p2 = (other.p.minus(p)).mul(a / d).plus(p);
        double x1 = p2.x + h * (other.y() - y()) / d;
        double y1 = p2.y - h * (other.x() - x()) / d;
        if (h == 0)
            return new Point[]{new Point(x1, y1)};
        double x2 = p2.x - h * (other.y() - y()) / d;
        double y2 = p2.y + h * (other.x() - x()) / d;
        return new Point[]{new Point(x1, y1), new Point(x2, y2)};
    }

    public Point[] inter(Point[] in) {
        for (Point p : in) {
            if (inter(p))
                return new Point[]{p};
        }
        return new Point[0];
    }

    public boolean inter(Point other) {
        return abs((other.x - x()) * (other.x - x()) + (other.y - y()) * (other.y - y()) - r * r) < 0.5d;
    }
}

class Point {
    double x;
    double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point minus(Point other) {
        return new Point(x - other.x, y - other.y);
    }

    public Point move(int x, int y) {
        return new Point(this.x + x, this.y + y);
    }

    public Point plus(Point other) {
        return new Point(x + other.x, y + other.y);
    }

    public Point mul(double m) {
        return new Point(x * m, y * m);
    }

    public Point div(double m) {
        return new Point(x / m, y / m);
    }

    public double length() {
        return sqrt(x * x + y * y);
    }

    public boolean aeq(Point other) {
        return abs(x - other.x) < 0.5d && abs(y - other.y) < 0.5d;
    }

    public boolean aeq(double other) {
        return abs(length() - other) < 0.5d;
    }

    public Point normalize() {
        return this.mul(10.0 / length());
    }

    @Override
    public String toString() {
        return String.format("activate %.9f %.9f", x, y);
    }
}

class FastScanner {
    BufferedReader br;
    StringTokenizer st;

    public FastScanner(String s) {
        try {
            br = new BufferedReader(new FileReader(s));
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public FastScanner() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    String next() {
        while (st == null || !st.hasMoreElements()) {
            try {
                st = new StringTokenizer(br.readLine());
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return st.nextToken();
    }

    String readLine() {
        String s = "";
        try {
            InputStreamReader converter = new InputStreamReader(System.in);
            BufferedReader in = new BufferedReader(converter);
            s = in.readLine();
        } catch (Exception e) {
            System.out.println("Error !Exception: " + e);
        }
        return s;
    }

    int nextInt() {
        return Integer.parseInt(next());
    }

    long nextLong() {
        return Long.parseLong(next());
    }

    double nextDouble() {
        return Double.parseDouble(next());
    }
}
