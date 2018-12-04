package ru.ifmo.kachalsky.TSP;

import java.io.*;
import java.util.*;

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

public class TSPGA {
    public static void main(String args[]) throws IOException {
        FastScanner in = new FastScanner("tsp.in");
        int n = in.nextInt();
        int toBeat = in.nextInt() * 105;
        int[][] distances = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                int ij = in.nextInt() * 100;
                distances[i][j] = ij;
                distances[j][i] = ij;
            }
        }

        Path answer = new TSPGA().solve(n, toBeat, distances);


        Writer out = new PrintWriter(new FileWriter("tsp.out"));
        //System.out.println(answer.toString());
        out.write(answer.toString());
        out.close();
    }

    private Path solve(int n, int toBeat, int[][] distances) {
        ArrayList<City> cities = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            cities.add(new City(distances[i], i));
        }


        SplittableRandom random = new SplittableRandom(1228);
        int populationSize = 121;
        int crossoverSize = (populationSize - 1) / 2;
        int keep = 1;
        int tournament = 25;

        ArrayList<Path> paths = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            paths.add(new Path(cities, toBeat, i));
        }


        Population population = new Population(paths, n, random);
        Path best = population.best();
        int iter = 0;
        while (best.length() > toBeat) {
            iter++;
            Population newPopulation = new Population(populationSize, n, random);
            newPopulation.add(best); // elitism
            /*for (int i = 1; i < populationSize; i++) {
             *//*Path parent1 = population.tournament(25);
                Path parent2 = population.tournament(25);
                newPopulation.add(parent1.crossover(parent2, nextInt(n, random), nextInt(n, random)));
                newPopulation.add(parent2.crossover(parent1, nextInt(n, random), nextInt(n, random)));*//*
                newPopulation.add(population.wheelCrossover());
            }*/
            for (int i = 0; i < crossoverSize; i++) {
                Path parent1 = population.tournament(tournament);
                Path parent2 = population.tournament(tournament);
                Path[] cross = parent1.crossoverCycle(parent2);
                newPopulation.add(cross[0]);
                newPopulation.add(cross[1]);
                /*newPopulation.add(parent1.crossoverCycle(parent2*//*, nextInt(n, random), nextInt(n, random)*//*));
                newPopulation.add(parent2.crossoverCycle(parent1*//*, nextInt(n, random), nextInt(n, random)*//*));*/
            }
            population = newPopulation;
            population.mutate();
            //System.out.println(iter);
            best = population.best();
        }
        //System.out.println(iter);
        //System.out.println(best.length());
        return best;
    }

    int nextInt(int n, SplittableRandom random) {
        return (int) (random.nextDouble() * n);
    }
}

class City {
    private int[] distances;
    private int id;

    public City(int[] distances, int id) {
        this.distances = distances;
        this.id = id;
    }

    public int distanceTo(int id) {
        return distances[id];
    }

    public int getId() {
        return id;
    }
}

class Path {
    private ArrayList<City> path;
    private int size;
    private int length = 0;
    private int goal;
    private double fitness = 0.0;

    public Path(int size, int goal) {
        this.path = new ArrayList<>();
        this.goal = goal;
        this.size = size;
    }

    public Path(int size, int goal, boolean nill) {
        this.path = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            path.add(null);
        }
        this.goal = goal;
        this.size = size;
    }

    public Path(ArrayList<City> path, int goal, int seed) {
        this.path = new ArrayList<>(path);
        Collections.shuffle(this.path, new Random(seed));
        this.goal = goal;
        this.size = path.size();
    }

    public Path(ArrayList<City> path, int goal) {
        this.path = new ArrayList<>(path);
        this.goal = goal;
        this.size = path.size();
    }

    public void swap(int i, int j) {
        nullCache();
        Collections.swap(path, i, j);
    }

    private void nullCache() {
        length = 0;
        fitness = 0;
    }

    public City get(int i) {
        return path.get(i);
    }

    public void add(int i, City city) {
        path.add(i, city);
    }

    public void add(City city) {
        path.add(city);
    }

    public Path crossover(Path other, int from, int to) {
        if (from > to) {
            int swap = to;
            to = from;
            from = swap;
        }

        Path child = new Path(size, goal);
        Set<City> used = new HashSet<>();
        for (int i = from; i < to; i++) {
            City toAdd = path.get(i);
            child.add(toAdd);
            used.add(toAdd);
        }
        for (int i = 0; i < size; i++) {
            City toAdd = other.get(i);
            if (used.contains(toAdd)) {
                continue;
            }
            if (i < from) {
                child.add(0, toAdd);
            } else {
                child.add(toAdd);
            }
        }
        return child;
    }

    public City swapBetweenPaths(Path path1, Path path2, int i) {
        City buffer = path2.get(i);
        path2.path.set(i, path1.path.get(i));
        path1.path.set(i, buffer);
        return buffer;
    }

    public Path[] crossoverCycle(Path other) {
        if (this.equals(other)) {
            return new Path[]{this, other};
        }
        Path child1 = new Path(path, goal);
        Path child2 = new Path(other.path, goal);
        boolean[] swapped = new boolean[size];
        City last = swapBetweenPaths(child1, child2, 0);
        swapped[0] = true;
        int i = this.path.indexOf(last);
        while (!swapped[i]) {
            last = swapBetweenPaths(child1, child2, i);
            swapped[i] = true;
            i = this.path.indexOf(last);
        }
        return new Path[]{child1, child2};
    }

    public int length() {
        if (length > 0)
            return length;
        else {
            int length = 0;
            length += distance(0, size - 1);
            for (int i = 0; i < size - 1; i++) {
                length += distance(i, i + 1);
            }
            this.length = length;
            return length;
        }
    }

    public double fitness() {
        if (fitness == 0.0) {
            fitness = (double) goal / (double) length();
        }
        return fitness;
    }

    private int distance(int i, int j) {
        return path.get(i).distanceTo(path.get(j).getId());
    }

    @Override
    public String toString() {
        StringBuilder answer = new StringBuilder();
        for (City city : path) {
            answer.append(city.getId() + 1).append(" ");
        }
        return answer.toString();
    }
}

class Population {
    private ArrayList<Path> paths;
    private int size;
    private SplittableRandom random;
    private int pathSize;
    private double sum = 0.0;
    private double best = 0.0;

    public Population(ArrayList<Path> paths, int pathSize, SplittableRandom random) {
        this.paths = paths;
        this.size = this.paths.size();
        this.pathSize = pathSize;
        this.random = random;
    }

    public Population(int size, int pathSize, SplittableRandom random) {
        this.paths = new ArrayList<>();
        this.size = size;
        this.pathSize = pathSize;
        this.random = random;
    }

    public void add(Path path) {
        paths.add(path);
    }

    public void mutate() {
        sum = 0.0;
        for (Path path : paths) {
            path.swap(nextPathIndex(), nextPathIndex());
        }
    }

    public int nextPathIndex() {
        return (int) (random.nextDouble() * pathSize);
    }

    public Path tournament(int n) {
        Path best = paths.get((int) (random.nextDouble() * size));
        for (int i = 1; i < n; i++) {
            int randomId = (int) (random.nextDouble() * size);
            Path candidate = paths.get(randomId);
            if (candidate.length() < best.length())
                best = candidate;
        }
        // Get the fittest tour
        return best;
    }

    public Path wheelCrossover() {
        if (sum == 0.0) {
            double sum = 0.0;
            paths.sort((o1, o2) -> Double.compare(o2.fitness(), o1.fitness()));
            for (Path path : paths) {
                sum += path.fitness();
            }
            this.sum = sum;
        }

        Path parent1 = paths.get(0);
        double number = random.nextDouble() * sum;
        for (Path path : paths) {
            number -= path.fitness();
            if (number <= 0) {
                parent1 = path;
                break;
            }
        }

        Path parent2 = paths.get(0);
        double number2 = random.nextDouble() * (sum - parent1.fitness());
        for (Path path : paths) {
            if (path != parent1)
                number2 -= path.fitness();
            if (number2 <= 0) {
                parent2 = path;
                break;
            }
        }
        return parent1.crossover(parent2, nextPathIndex(), nextPathIndex());
    }

    public Path best() {
        Path best = paths.get(0);
        for (int i = 1; i < paths.size(); i++) {
            if (best.length() > paths.get(i).length())
                best = paths.get(i);
        }
        return best;
    }
}