import java.io.*;

public class Ver10 {
    public static void main (String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("n = ");
        int n = Integer.parseInt(reader.readLine());

        float[][] A = new float[n][n];

        for (int i = 0; i < n; i++) {
            A[i] = new float[n];
            String[] s = reader.readLine().split(" ");
            for (int j = 0; j < n; j++)
                A[i][j] = Float.parseFloat(s[j]);
        }

        reader.close();

        int[][] B = getB(A);
        print(B);
    }
    public static int[][] getB(float[][] a){
        int n = a.length;
        int[][] b = new int[n][n];

        for (int i = 0; i < n; i++){
            b[i] = new int[n];
            for (int j = 0; j < n; j++){
                b[i][j] = getValue(a,i,j);
            }
        }
        return b;
    }
   private static int getValue(float[][] a, int posI, int posJ){
       int n = a.length;
       for (int i = posI -1; i <= posI + 1; i++){
           if (i < 0 || i >= n) continue;
           for (int j = posJ -1; j <= posJ + 1; j++){
               if (j < 0 || j >= n) continue;
               if (i == posI && j == posJ) continue;
               if (a[i][j] >= a[posI][posJ]) return 0;
           }
       }
       return 1;
   }

    private static void print(int[][] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(a[i][j]+" ");
            }
            System.out.println();
        }
    }
}
