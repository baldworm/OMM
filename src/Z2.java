import java.io.*;

/**
 * Используя метод переменных направлений решите краевую задачу
 *
 * Created by bw on 25.05.2015.
 */
public class Z2 {
        /* Количество шагов по сетке */
    public static final int N_X = 50;
    public static final int N_Y = 50;
    public static final int N_T = 50;
        /* Области значений */
    public static final double X_MIN = 0;
    public static final double X_MAX = Math.PI;
    public static final double Y_MIN = 0;
    public static final double Y_MAX = Math.PI * 2;
    public static final double T_MIN = 0;
    public static final double T_MAX = 1;
        /* Шаг по сетке */
    public static final double H_X = (X_MAX - X_MIN)/N_X;
    public static final double H_Y = (Y_MAX - Y_MIN)/N_Y;
    public static final double H_T = (T_MAX - T_MIN)/N_T;

    public static double[][][] w = new double[N_X+1][N_Y+1][N_T+1];

    public static void main(String[] args) throws IOException {
        System.out.println("Fuck OMM");
        solute();
    }
    public static void solute() throws IOException { //TODO переименовать
            /* Граничные условия*/
        for (int i = 1; i <= N_X; i++)
            for (int j = 1; j <= N_Y; j++)
                w[i][j][1] = tBorderFunction(getX(i),getY(j));
            /* Метод прогонки*/
        int kap11 = 1;
        int kap12 = 0;
        int kap21 = 0;
        int kap22 = 0;
        int m11 = 0;
        int m12 = 0;
        int m21 = 0;
        int m22 = 0;
        double an1 = .5*H_T/(Math.pow(H_X,2));
        double bn1 = .5*H_T/(Math.pow(H_X,2));
        double cn1 = 1 + .5*H_T/(Math.pow(H_X,2));
        double an2 = .5*H_T/(Math.pow(H_Y,2));
        double bn2 = .5*H_T/(Math.pow(H_Y,2));
        double cn2 = 1 + .5*H_T/(Math.pow(H_Y,2));

        double[][] alpha1 = new double[N_X+1][N_X+1];
        double[][] alpha2 = new double[N_Y+1][N_Y+1];
        double[][] betta1 = new double[N_X+1][N_X+1];
        double[][] betta2 = new double[N_Y+1][N_Y+1];

        alpha1[1][1] = kap11;
        alpha2[1][1] = kap21;
        betta1[1][1] = m11;
        betta2[1][1] = m21;

            /* Промежуточный */
        for (int i = 1; i < N_T/2; i++){
            for (int j = 2; j <= N_Y - 1; j++){
                for (int k = 1; k <= N_X - 1; k++){
                    alpha1[k+1][1] = bn1/(cn1 - alpha1[k][1]*an1);
                    betta1[k+1][1] = (an1 * betta1[k][1] + fn1(w, k, j, i))/(cn1 - alpha1[k][1]*an1);
                }
                w[N_X][j][2*i] = (m12 + betta1[N_X][1]*kap12)/(1 - alpha1[N_X][1]*kap12);
                for (int k = N_X; k >= 2; k--){
                    w[k - 1][j][2*i] = alpha1[k][1]*w[k][j][2*i] + betta1[k][1];
                }
            }
            /* С промежуточного */
            for (int k = 2; k <= N_X - 1; k++){
                for (int j = 1; j <= N_Y - 1; j++) {
                    alpha2[j+1][1] = bn2/(cn2 - alpha2[j][1]*an2);
                    betta2[j+1][1] = (an2 * betta2[j][1] + fn2(w,k,j,i))/(cn2 - alpha2[j][1]*an2);
                }
                System.out.println(i+"\t"+k);
                w[k][N_Y][2*i + 1] = (m22 + betta2[N_Y][1]*kap22)/(1 - alpha2[N_Y][1]*kap22);
                for (int j = N_Y; j >= 2; j--){
                    w[k][j-1][2*i + 1] = alpha2[j][1] * w[k][j][2*i + 1] + betta2[j][1];
                }
            }


        }
        /* График при t = 2*/

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("datat2.txt"));
        for (int i = 0;i < N_X; i++){
            for (int j = 0; j < N_Y; j++){
                bufferedWriter.write(getX(i)+"\t"+getY(i)+"\t"+w[i][j][0]+"\n");
            }
        }
        bufferedWriter.close();



    }

    private static double tBorderFunction(double x, double y) {
        return Math.sin(3 * x)*Math.cos(y);
    }
        /* Метод аппроксимации на шаге 1*/
    private static double fn1(double[][][] w, int i, int j, int k){
        double f = .5 * (4*H_T/Math.pow(H_Y,2))*(w[i][j-1][2*k - 1] + w[i][j+1][2*k - 1])
                + (1 - (4*H_T/Math.pow(H_Y,2)))*w[i][j][2*k -1]
                + .5 * 4 * H_T * nelin(getX(i),getY(j),getT(2*k));
        return f;
    }
    /* Метод аппроксимации на шаге 2*/
    private static double fn2(double[][][] w, int i, int j, int k){
        double f = .5 * (4*H_T/Math.pow(H_X,2))*(w[i-1][j][2*k]+ w[i+1][j][2*k])
                + (1 - (4*H_T/Math.pow(H_X,2)))*w[i][j][2*k]
                + .5 * 4 * H_T * nelin(getX(i),getY(j),getT(2*k));
        return f;
    }
    private static double nelin (double x, double y, double t){
        return 0*x + 0*y + 0*t;
    }
    private static double getX(int i){
        return X_MIN + (i - 1)*H_X;
    }
    private static double getY(int i){
        return Y_MIN + (i - 1)*H_Y;
    }
    private static double getT(int i){
        return T_MIN + (i - 1)*H_T;
    }



}
