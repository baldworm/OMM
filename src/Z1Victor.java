import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by bw on 25.05.2015.
 */
public class Z1Victor {
    private static final int N_X = 80;
    private static final int N_T = 80;
    private static final double EPS = 0.00001;

    private static double[][] u = new double[N_T][N_X];

    private static final double DX = 1./N_X;
    private static final double DT = 1./N_T;

    public static void main(String[] args) throws IOException {
        boundConditions();
        solver();
        writeToFile("dataZ1Victor.txt");
    }



    private static void boundConditions() {
        for (int i = 0; i < N_X; i++)
            u[0][i] = Math.pow(getX(i),2) + 1;

        for (int i = 0; i < N_T; i++)
            u[i][0] = Math.exp(-getT(i));
    }
    private static void solver() {
        for (int i = 0; i < N_T - 1; i++){
            for (int j = 0; j < N_X - 1; j++){
                u[i+1][j+1] = solve(i,j);
            }
        }
        System.out.println("Solved");
    }
    private static double solve(int i, int j) {
        double u_old = .5, u_new = 0.1;
        while (Math.abs(u_new - u_old)>EPS){
            u_old = u_new;
            u_new = u_old - equation(u_old, i, j)/d_equation(u_old);
        }
        return u_new;
    }
    private static double equation(double x, int i, int j) {
        return (x - u[i][j+1])/DT + (f(x) - f (u[i+1][j]))/DX;
    }
    private static double d_equation(double x) {
        return 1/DT + df(x)/DX;
    }
    private static double f(double x){
        return Math.pow(Math.tan(Math.exp(x*x)),-1);
    }
    private static double df(double x){
        return 2*Math.exp(x*x)*x/(1 + Math.exp(2*x*x));
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

    private static double getX(int i){
        return i * DX;
    }
    private static double getT(int i){
        return i * DT;
    }

}
