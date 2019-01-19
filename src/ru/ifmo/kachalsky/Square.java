import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Square {

    public static double getOuterDistance(Double x, Double y, FastScanner sc) throws InterruptedException {
        String xx = String.format("%.7f", x).replace(',','.');
        String yy = String.format("%.7f", y).replace(',','.');
        System.out.println("activate "+xx+ " "+yy);
        //System.out.printf("activate %f %f\n", x, y);
        String n = sc.readLine();

        String[] p = n.split("\\s+");
        if(!p[0].equals("outside")){
           //
            //while (true) Thread.sleep(10);
        }
        //assert (p[0].equals("outside"));
        return Double.parseDouble(p[1]);
    }

    public static void main(String[] args) throws Exception {

        FastScanner sc = new FastScanner();


        String[] aaa = sc.readLine().split("\\s+");

        int M = Integer.parseInt(aaa[0]);
        int N = Integer.parseInt(aaa[1]);

        Double r = 9700.0;
        ArrayList<OuterPoint> outerPoints = new ArrayList<>();
        for(int i = 0; i <= 350; i = i + 10/**/){
            //System.out.println(""+i);
            Double curX = r * Math.cos(Math.toRadians(i));
            Double curY = r * Math.sin(Math.toRadians(i));
            Double curDist = getOuterDistance((double)curX.intValue(), (double)curY.intValue(), sc);
            outerPoints.add(new OuterPoint((double)curX.intValue(), (double)curY.intValue(), curDist));
        }

        int outerPointCount = outerPoints.size();
        ArrayList<Point> allPossibleCornerPoints = new ArrayList<>();

        for(int i = 0; i < outerPointCount - 3; i++) {
            OuterPoint curPoint1 = outerPoints.get(i);
            OuterPoint curPoint2 = outerPoints.get(i+1);
            OuterPoint curPoint3 = outerPoints.get(i+2);

            Circle curCircle1 = new Circle(curPoint1.x, curPoint1.y, curPoint1.dist);
            Circle curCircle2 = new Circle(curPoint2.x, curPoint2.y, curPoint2.dist);
            Circle curCircle3 = new Circle(curPoint3.x, curPoint3.y, curPoint3.dist);

            Pair<Point, Point> inter1 = curCircle1.intersections(curCircle2);
            Point resultPoint = null;
            if(!Double.isNaN(inter1.getKey().x)) {
                if(curCircle3.checkPoint(inter1.getKey().x, inter1.getKey().y))
                    resultPoint = inter1.getKey();
            }
            if(!Double.isNaN(inter1.getValue().x)) {
                if(curCircle3.checkPoint(inter1.getValue().x, inter1.getValue().y))
                    resultPoint = inter1.getValue();
            }
            if(resultPoint != null){
                allPossibleCornerPoints.add(resultPoint);
            }
        }

        ArrayList<Point> realCornerPoints = new ArrayList<>();
        for(Point p: allPossibleCornerPoints){
            boolean alreadyExists = false;
            for(Point real: realCornerPoints){
                if((Math.abs(real.x - p.x) < 0.5d) && (Math.abs(real.y - p.y) < 0.5d)) alreadyExists = true;
            }
            if(!alreadyExists) realCornerPoints.add(p);
        }
        if (realCornerPoints.size() != 4){
           // while (true) Thread.sleep(10);
           // throw new Exception("asdas");
        };


        Point b = null;
        Point a = realCornerPoints.get(0);
        Point c = null;
        for(int i = 1; i < realCornerPoints.size(); i++){
            if(Field.myEq(Math.sqrt(M*M*100.0 + N*N*100.0), Field.pointsDist(realCornerPoints.get(0), realCornerPoints.get(i)))){
                b = realCornerPoints.get(i);
                for(int j = 0; j < realCornerPoints.size(); j++){
                    if((j!=0) && (j!=i)){
                        c = realCornerPoints.get(j);
                        break;
                    }
                }
                break;
            }
        }

        if(b == null || c==null){
           // while (true) Thread.sleep(10);

        }

        Field field = new Field(M, N, c, a, b, sc);
        field.iterate();
    }
    

    static class Point{
        double x, y;
        public Point(double px, double py) {
            x = px;
            y = py;
        }
        Point sub(Point p2) {
            return new Point(x - p2.x, y - p2.y);
        }
        Point add(Point p2) {
            return new Point(x + p2.x, y + p2.y);
        }
        public double distance(Point p2) {
            return (double) Math.sqrt((x - p2.x)*(x - p2.x) + (y - p2.y)*(y - p2.y));
        }
        Point normal() {
            double length = (double)Math.sqrt(x*x + y*y);
            return new Point(x/length, y/length);
        }
        Point scale(double s) {
            return new Point(x*s, y*s);
        }
    };

    static class Circle {
        double x, y, r, left;
        public Circle(double cx, double cy, double cr) {
            x = cx;
            y = cy;
            r = cr;
            left = x - r;
        }
        Pair<Point, Point> intersections(Circle c) {
            Point P0 =  new Point(x, y);
            Point P1 = new Point(c.x, c.y);
            double d, a, h;
            d = P0.distance(P1);
            a = (r*r - c.r*c.r + d*d)/(2*d);
            h = (double) Math.sqrt(r*r - a*a);
            Point P2 = P1.sub(P0).scale(a/d).add(P0);
            double x3, y3, x4, y4;
            x3 = P2.x + h*(P1.y - P0.y)/d;
            y3 = P2.y - h*(P1.x - P0.x)/d;
            x4 = P2.x - h*(P1.y - P0.y)/d;
            y4 = P2.y + h*(P1.x - P0.x)/d;

            return new Pair<Point, Point>(new Point(x3, y3), new Point(x4, y4));
        }

        public boolean checkPoint(double inX, double inY) {
            Double res = (inX - x)*(inX - x) + (inY - y)*(inY - y);
            Double check = r * r;
            return Math.abs(res - check) < 2.0d;
        }

    };


    public static class OuterPoint{

        Double x;
        Double y;
        Double dist;


        public OuterPoint(Double x, Double y, Double dist) {
            this.x = x;
            this.y = y;
            this.dist = dist;
        }

        @Override
        public String toString() {
            return ""+x+" "+y;
        }
    }

    public static class Field {
        Integer M;
        Integer N;
        Double side = 10.0;

        Point a;
        Point b;
        Point c;
        FastScanner sc;

        public double getInnerDistance(Double x, Double y, boolean possibleOut) throws Exception {
            String xx = String.format("%.7f", x).replace(',','.');
            String yy = String.format("%.7f", y).replace(',','.');
            System.out.println("activate "+xx+ " "+yy);
            //System.out.printf("activate %f %f\n", x, y);
            String n = sc.readLine();
            if(n.contains("iphone")) {
                while (true) Thread.sleep(10);
            }
            String[] p = n.split("\\s+");
            //assert (p[0].equals("inside"));
            if(p[0].equals("outside") && !possibleOut){
               // throw new Exception("laskjdas");
            }
            if(p[0].equals("blocked") || p[0].equals("outside")) return 1000000.0;
            return Double.parseDouble(p[1]);
        }


        public static Double pointsDist(Point a, Point b) {
            return Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
        }

        public void iterate() throws Exception {
            Point treasurePoint = null;
            Double minDist = 1000.0;
            for(int i = 0; i < M; i++){
                for(int j = 0; j < N; j++){
                    Double dX = i * side + 5.0;
                    Double dY = j * side + 5.0;

                    Point n1V = new Point((b.x - c.x)/(M*side), (b.y - c.y)/(M*side));
                    Point m1V = new Point((a.x - c.x)/(N*side), (a.y - c.y)/(N*side));
                    Point realPoint = new Point(n1V.x*dX + m1V.x*dY+ c.x, n1V.y*dX + m1V.y*dY+ c.y);

                    Double curDist = getInnerDistance(realPoint.x, realPoint.y, false);
                    if(treasurePoint == null || curDist < minDist){
                        treasurePoint = realPoint;
                        minDist = curDist;
                    }
                }
            }

            if(minDist > 10){
                // throw new Exception("sd");
                // while (true) Thread.sleep(10);
            }




            Double minX = treasurePoint.x - 10.0;

            Double minY = treasurePoint.y - 10.0;

            Double maxX = treasurePoint.x + 10.0;

            Double maxY = treasurePoint.y + 10.0;

            int maX = maxX.intValue();
            int maY = maxY.intValue();
            int miX = minX.intValue();
            int miY = minY.intValue();

            int finalX = 0;
            int finalY = 0;
            Double finalDist = 10000.0;
            for(int i = miX ; i <= maX; i++){
                if(finalDist < 0.01d) break;
                for(int j = miY; j <= maY; j++){
                    Double curDist = getInnerDistance((double) i, (double) j, true);
                    if(curDist < finalDist){
                        finalDist = curDist;
                        finalX = i;
                        finalY = j;
                        if(finalDist < 0.01d) break;
                    }
                }
            }
            System.out.println("found "+finalX + " "+finalY);
            String a = sc.next();
            if(a.equals("FAIL")) {
                //while (true) Thread.sleep(10);
            }

        }

        public static Boolean myEq(Double a, Double b) {
            return Math.abs(a - b) < 0.5d;
        }

        public Field(Integer m, Integer n, Point c, Point a, Point b, FastScanner scq) {
            sc = scq;
            this.c = c;
            this.a = a;
            this.b = b;
            if(myEq(pointsDist(c, a), m * side)) {
                M = m;
                N = n;
            } else {
                M = n;
                N = m;
            }
        }

        public Field(Integer m, Integer n, Double side) {
            M = m;
            N = n;
            this.side = side;
        }


    }

    static class FastScanner {
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

}


