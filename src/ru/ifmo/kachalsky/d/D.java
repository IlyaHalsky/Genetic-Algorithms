package ru.ifmo.kachalsky.d;


import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class D {
    public static void main(String args[]) throws IOException {
        Pattern p = Pattern.compile("([-]?\\d?)([x]?\\^?(\\d?))([y]?\\^?(\\d?))");
        FastScanner in = new FastScanner("unimulti.in");
        String input = in.next();
        String[] strs = input.replace("-","+-").split("\\+");

        Monomial[] monomials = new Monomial[strs.length];
        for (int i = 0; i < strs.length; i++) {
            if (!strs[i].isEmpty()) {
                Matcher m = p.matcher(strs[i]);
                m.find();
                System.out.println(strs[i]);
                System.out.println(m.group(1));
            }
        }

        System.out.println(Arrays.toString(monomials));
        Writer out = new PrintWriter(new FileWriter("unimulti.out"));
        out.write(input);
        out.close();
    }
}

class Polynomial {
    Monomial[] list;

    public Polynomial(Monomial[] list){
        this.list = list;
    }

    long get(int x, int y) {
        long result = 0;
        for (Monomial mon : list) {
            result += mon.get(x,y);
        }
        return result;
    }
}

class Monomial{
    int num;
    int xPow;
    int yPow;

    public Monomial(int a, int b, int c) {
        this.num = a;
        this.xPow = b;
        this.yPow = c;
    }

    long get(int x, int y) {
        return num * intPow(x, xPow) * intPow(y, yPow);
    }

    private long intPow(int a, int b) {
        if (b == 0)
            return 1L;
        if (b == 1)
            return a;
        return a * intPow(a, b- 1);
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