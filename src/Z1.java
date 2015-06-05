import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by bw on 25.05.2015.
 */
public class Z1 {
        /* Область значений */
    public static final double X_MIN = -1;
    public static final double X_MAX = 0;
    public static final double T_MIN = 0;
    public static final double T_MAX = 2;

    public static final int N_X = 80;                      // Количество узлов по оси x
    public static final int N_T = 80;                      // Количество узлов по оси t

    public static final double H_X = (X_MAX - X_MIN)/N_X;   // Шаг по оси x (-1 <= x < 0)
    public static final double H_T = (T_MAX - T_MIN)/N_T;   // Шаг по оси t (t > 0)
    public static final double EPS = Math.pow(10,-5);       //Точность метода касательных Ньютона (10^-6)

    public static double[][] u = new double[N_T][N_X];

    public static void main(String[] args) throws IOException {
            /* Задаем граничные и начальные условия */
        createBoundConditions();
            /* Метод бегущего счета */
        solver();
            /* Вывод в файл */
        writeToFile("data.txt");
    }
    private static void createBoundConditions(){
        for (int i = 0;i<N_X;i++)
            u[0][i] = Math.sin(Math.PI*getX(i));

    }
    private static void solver(){
        for (int i = 1; i < N_T; i++){
            System.out.println("i = " +i);
            for (int j = 1; j < N_X; j++){
                System.out.println("j = " +j);
                u[i][j] = solve(i,j);
            }
        }
    }
    private static double solve(int i, int j){
        double old = 1, newU = 0;
        while (Math.abs(old-newU)>EPS){
            old = newU;
            newU = old - getF(old,i,j)/getDf(old);

            System.out.println(old+"\t"+newU);
            try {Thread.sleep(60); } catch (InterruptedException ignored) {}
        }
        return newU;
    }
    private static void writeToFile(String fileName) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))){
            for (int i = 0;i < N_T; i++){
                for (int j = 0; j < N_X; j++){
                    bufferedWriter.write(getX(i)+"\t"+getT(j)+"\t"+u[i][j]+"\n");
                }
            }
        }
    }

    private static double getDf(double x){
        return 1/ H_T + df(x)/H_X;
    }
    private static double df(double u){
        return -2*Math.exp(2*u)/(1 + Math.exp(2*u));
    }

    private static double getF(double x,int i, int j){
        double f;
        f = (x-u[i-1][j])/H_T + (f(x)-f(u[i][j-1]))/H_X;
        return f;

    }
    private static double f(double u){
        return -Math.log(Math.exp(2*u)+1);
    }

    private static double getX(int i) {
        return X_MAX - i * H_X;
    }
    private static double getT(int i) {
        return  T_MIN + i * H_T;
    }
}
