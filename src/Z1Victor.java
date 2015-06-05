import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by bw on 25.05.2015.
 */
public class Z1Victor {
    /* Область значений */
    public static final double X_MIN = 0;
    public static final double X_MAX = 1;
    public static final double T_MIN = 0;
    public static final double T_MAX = 2;

    public static final int N_X = 40;                      // Количество узлов по оси x
    public static final int N_T = 40;                      // Количество узлов по оси t

    public static final double H_X = (X_MAX - X_MIN)/N_X;   // Шаг по оси x (-1 <= x < 0)
    public static final double H_T = (T_MAX - T_MIN)/N_T;   // Шаг по оси t (t > 0)
    public static final double EPS = Math.pow(10,-4);       //Точность метода касательных Ньютона (10^-6)

    public static double[][] u = new double[N_X][N_T];

    public static void main(String[] args) throws IOException {
            /* Задаем граничные и начальные условия */
        for (int i = 0; i < N_X; i++)
            u[i][0] = Math.pow(getX(i),2)+1;
        for (int j = 0; j < N_T; j++)
            u[0][j] = Math.exp(-getT(j));

            /* Метод бегущего счета */
        solver();
            /* Вывод в файл */
        writeToFile("dataz1victor.txt");
    }
    private static void solver(){

        for (int j = 1; j < N_T; j++){
            System.out.println("j = " +j);
            for (int i = 1; i < N_X; i++){
                System.out.println("i = " +i);
                solve(i,j);
            }
        }
    }
    private static void solve(int i, int j){
        double f = 1;//getF(i,j);
        double df = 1;//getDf(i,j);
        //u[i+1][j+1] = u[i+1][j+1] - f/df;
        //System.out.println("f = "+f+"\tdf = "+df);


        while (Math.abs(f/df) > EPS){
            f = getF(i, j);
            df = getDf(i, j);
            //System.out.print(u[i+1][j+1]+"\t");
            u[i][j] -= f/df;

            //System.out.println(Math.abs(f / df) + "\t" + f + "\t" + df + "\t" + u[i][j] + "\t" + u[i - 1][j - 1] + "\t" + u[i][j - 1] + "\t" + u[i - 1][j]);
            //try {Thread.sleep(100); } catch (InterruptedException ignored) {}

            //if (Math.abs(f/df)/u[i][j]<EPS) break;
        }
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

    /* старая функция
    private static double getDf(int i, int j) {
        double max = 100;
        double df;
        double u1 = u[i][j];
        double e = (u1<max)?Math.exp(2*u1)/(Math.exp(2*u1)+1):1; //при больших u1 появляется неопределенность бесконечность/бесконечность

        df = 1.0/(2 * H_T) - 2 * e /(2 * H_X);

        //System.out.println("u1 = "+u1+"\t"+(1/(2 * H_T))+"\t"+(2 * Math.exp(2*u1)/(Math.exp(2*u1)+1) /(1 * H_X))+"\t"+df);
        return df;
    }
    */
    private static double getDf(int i, int j){
        return 1/(2 * H_T) + df(u[i][j])/(2 * H_X);
    }
    private static double df(double u){
        if (u>25) System.out.println("pizda");
        return 2*u*Math.exp(u*u)/(1 + Math.exp(2*u*u));
    }
    /* старая функция,
    private static double getF(int i, int j) {
        double max = 100; //так как при расчетах экспонента взлетает очень высоко, программа не может посчитать
        double f;
        double u01 = (u[i][j-1]<max)    ? Math.log(Math.exp(2*u[i][j-1]) + 1)     : 2*u[i][j-1];
        double u11 = (u[i-1][j-1]<max)  ? Math.log(Math.exp(2*u[i-1][j-1]) + 1)   : 2*u[i-1][j-1];
        double u00 = (u[i][j]<max)      ? Math.log(Math.exp(2*u[i][j]) + 1)       : 2*u[i][j];
        double u10 = (u[i-1][j]<max)    ? Math.log(Math.exp(2*u[i-1][j]) + 1)     : 2*u[i-1][j];

        f = (u[i-1][j] - u[i-1][j-1] + u[i][j] - u[i][j-1])/(2 * H_T) - (u01-u11+u00-u10)/(2*H_X);

        /*
            старая версия
            f = (u[i-1][j] - u[i-1][j-1] + u[i][j] - u[i][j-1])/(2 * H_T) -
                (Math.log(Math.exp(2*u[i][j-1]) + 1) - Math.log(Math.exp(2*u[i-1][j-1]) + 1) +
                Math.log(Math.exp(2*u[i][j]) + 1) - Math.log(Math.exp(2*u[i-1][j]) + 1))/(2 * H_X);

        * /

        return f;
    }
    */
    private static double getF(int i, int j){
        double f;
        f = (u[i][j]-u[i][j-1])/(2*H_T) + (f(u[i][j])-f(u[i-1][j]))/(2 * H_X);
        return f;

    }
    private static double f(double u){
        return 1/Math.tan(Math.exp(u*u));
    }

    private static double getX(int i) {
        return X_MAX - i * H_X;
    }
    private static double getT(int i) {
        return  T_MIN + i * H_T;
    }
}
