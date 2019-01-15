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
        String[] strs = input.replace("-", "+-").split("\\+");

        Monomial[] monomials = new Monomial[strs.length];
        for (int i = 0; i < strs.length; i++) {
            if (!strs[i].isEmpty()) {
                Matcher m = p.matcher(strs[i]);
                m.find();
                String gr0 = m.group(1);
                int num;
                if (gr0.equals("-"))
                    num = -1;
                else if (gr0.isEmpty())
                    num = 1;
                else num = Integer.parseInt(gr0);

                String gr1 = m.group(2);
                String gr2 = m.group(3);
                int numX;
                if (gr1.isEmpty())
                    numX = 0;
                else if (gr2.isEmpty())
                    numX = 1;
                else numX = Integer.parseInt(gr2);

                String gr3 = m.group(4);
                String gr4 = m.group(5);
                int numY;
                if (gr3.isEmpty())
                    numY = 0;
                else if (gr4.isEmpty())
                    numY = 1;
                else numY = Integer.parseInt(gr4);
                monomials[i] = new Monomial(num, numX, numY);
            }
        }

        Polynomial pol = new Polynomial(monomials);

        long[][] f = new long[23][23];
        for (int x = 1; x < 22; x++) {
            for (int y = 1; y < 22; y++) {
                f[x][y] = pol.get(x - 11, y - 11);
            }
        }

        int max = 0;
        int min = 0;
        int plat = 0;
        for (int x = 1; x < 22; x++) {
            for (int y = 1; y < 22; y++) {
                long curr = f[x][y];
                long left = f[x - 1][y];
                long top = f[x][y -1];
                long right = f[x + 1][y];
                long bot = f[x][y +1];

                int lt = test(curr, left);
                int rt = test(curr, right);
                int tt = test(curr, top);
                int bt = test(curr, bot);

                if ((x == 1 || lt < 0) &&
                        (y == 1 || tt < 0) &&
                        (x == 21 || rt < 0) &&
                        (y == 21 || bt < 0))
                    min++;
                if ((x == 1 || lt > 0) &&
                        (y == 1 || tt > 0) &&
                        (x == 21 || rt > 0) &&
                        (y == 21 || bt > 0))
                    max++;
                if ((x != 1 && lt == 0) ||
                        (y != 1 && tt == 0) ||
                        (x != 21 && rt == 0) ||
                        (y != 21 && bt == 0))
                    plat++;
            }
        }

        Writer out = new PrintWriter(new FileWriter("unimulti.out"));
        if (max > 1)
            out.write("Multiple local maxima: Yes\n");
        else
            out.write("Multiple local maxima: No\n");
        if (min > 1)
            out.write("Multiple local minima: Yes\n");
        else
            out.write("Multiple local minima: No\n");
        if (plat > 0)
            out.write("Plateaus: Yes");
        else
            out.write("Plateaus: No");
        out.close();
    }

    static int test(long a, long b){
        return Long.compare(a, b);
    }
}

class Polynomial {
    Monomial[] list;

    public Polynomial(Monomial[] list) {
        this.list = list;
    }

    long get(int x, int y) {
        long result = 0;
        for (Monomial mon : list) {
            if (mon != null)
                result += mon.get(x, y);
        }
        return result;
    }

    @Override
    public String toString() {
        return Arrays.toString(list);
    }
}

class Monomial {
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
        return a * intPow(a, b - 1);
    }

    @Override
    public String toString() {
        return String.format("%d*x^%dy^%d", num, xPow, yPow);
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