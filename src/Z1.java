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

    public static final int N_X = 100;                      // Количество узлов по оси x
    public static final int N_T = 10000;                      // Количество узлов по оси t

    public static final double H_X = (X_MAX - X_MIN)/N_X;   // Шаг по оси x (-1 <= x < 0)
    public static final double H_T = (T_MAX - T_MIN)/N_T;   // Шаг по оси t (t > 0)
    public static final double EPS = Math.pow(10,-6);       //Точность метода касательных Ньютона (10^-6)

    public static double[][] u = new double[N_X][N_T];

    public static void main(String[] args) throws IOException {
            /* Задаем граничные и начальные условия */
        for (int i = 0; i < N_X; i++)
            u[i][0] = Math.sin(Math.PI * getX(i));
        for (int j = 0; j < N_T; j++)
            u[0][j] = 0;

            /* Метод бегущего счета */
        for (int j = 1; j < N_T - 1; j++){
            System.out.println("j = " +j);
            for (int i = 1; i < N_X - 1; i++){
                //System.out.println("i = " +i);
                double f = 1;
                double df = 1;
                while (Math.abs(f/df) > EPS){
                    f = getF(i, j);
                    df = getDf(i, j);
                    //System.out.print(u[i+1][j+1]+"\t");
                    u[i+1][j+1] = u[i+1][j+1] - f/df;
                    //System.out.println(Math.abs(f/df) +"\t" + f+ "\t" + u[i+1][j+1]);
                    //try {Thread.sleep(100); } catch (InterruptedException ignored) {}
                }

            }
        }
            /* Вывод в файл */
        writeToFile("data.txt");
    }

    private static void writeToFile(String fileName) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))){
            for (int i = 0;i < N_X; i++){
                for (int j = 0; j < N_T; j++){
                    bufferedWriter.write(getX(i)+"\t"+getT(j)+"\t"+u[i][j]+"\n");
                }
            }
        }
    }



    private static double getDf(int i, int j) {
        double df;
        double u1 = u[i+1][j+1];
        df = 1/(2 * H_T) - 2 * Math.exp(2*u1)/(Math.exp(2*u1)+1) /(2 * H_X);
        return df;
    }

    private static double getF(int i, int j) {
        double f;
        f = (u[i][j+1] - u[i][j] + u[i+1][j+1] - u[i+1][j])/(2 * H_T) -
            (Math.log(Math.exp(2*u[i+1][j]) + 1) - Math.log(Math.exp(2*u[i][j]) + 1) +
            Math.log(Math.exp(2*u[i+1][j+1]) + 1) - Math.log(Math.exp(2*u[i][j+1])) + 1)/(2 * H_X);


        return f;
    }

    private static double getX(int i) {
        return X_MAX - i * H_X;
    }
    private static double getT(int i) {
        double value =  T_MIN + i * H_T;
        return (double)Math.round(value * 100000) / 100000;
    }
}
