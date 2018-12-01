package ru.ifmo.kachalsky.TSP;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

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

public class TravelingSalespersonProblem {
    public static void main(String args[]) throws IOException {
        FastScanner in = new FastScanner("tsp.in");
        int n = in.nextInt();
        int toBeat = in.nextInt() * 105;
        int[][] distances = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                int ij = in.nextInt();
                distances[i][j] = ij;
                distances[j][i] = ij;
            }
        }

        Path currentPath = new Path(distances);
        int bestLenght = currentPath.length();
        Path bestPath = currentPath;
        double t = 1000.0;
        int numberOfIterations = 10000000;
        double coolingRate = 0.9999999;

        for (int i = 0; i < numberOfIterations; i++) {
            if (t > 0.01 && !(bestLenght <= toBeat)) {
                currentPath.randomSwap();
                int currentDist = currentPath.length();
                if (currentDist < bestLenght)
                    bestLenght = currentDist;
                else if (Math.exp((bestLenght - currentDist) / t) < Math.random()) {
                    currentPath = currentPath.revert();
                }
                t*=coolingRate;
            } else {
                break;
            }
        }


        System.out.println(currentPath.length() / 100);
        Writer out = new PrintWriter(new FileWriter("tsp.out"));
        out.write(currentPath.print());
        out.close();
    }
}

class Path {
    private Path prevPath;
    private List<Integer> cities;
    private int[][] distances;
    private int size;
    private Random rnd = new Random(1L);

    public Path(int[][] distances) {
        this.distances = distances;
        this.size = this.distances.length;
        this.cities = IntStream.rangeClosed(0, size - 1).boxed().collect(toList());


        Collections.shuffle(cities, rnd);
    }

    private Path(Path path) {
        this.distances = path.distances;
        this.size = path.size;
        this.cities = new ArrayList<>(path.cities);
        this.rnd = path.rnd;
    }

    public int length() {
        int length = 0;
        for (int i = 0; i < size - 1; i++) {
            if (i == 0) {
                length += distanceTo(cities.get(i), cities.get(size - 1));

            }
            length += distanceTo(cities.get(i), cities.get(i + 1));
        }
        return length*100;
    }

    private int distanceTo(int i, int j) {
        return distances[i][j];
    }

    private void swap(int i, int j) {
        this.prevPath = new Path(this);
        Collections.swap(cities, i, j);
    }

    public Path revert() {
        return prevPath;
    }

    public void randomSwap() {
        int i = rnd.nextInt(size);
        int j = rnd.nextInt(size);
        swap(i, j);
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(cities.get(i) + 1);
            if (i < size - 1)
                sb.append(" ");
        }
        return sb.toString();
    }
}