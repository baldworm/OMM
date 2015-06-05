import java.io.*;

/**
 * Используя метод переменных направлений решите краевую задачу
 *
 * Created by bw on 25.05.2015.
 */
public class Z2 {
    private static final double X = Math.PI;
    private static final double Y = 3;
    private static final double T = 20;

    private static final int NX = 80;
    private static final int NY = 80;
    private static final int NT = 80;

    private static final double DX = X / NX;
    private static final double DY = Y / NY;
    private static final double DT = T / NT;

    private static double[] A, B, C, F, U, alpha, beta;
    private static double[][] Umiddle = new double[NX][NY];
    private static double[][][] u = new double[NX][NY][NT];

    public static void main(String[] args) throws IOException {
            /* Начальные условия*/
        initial();
            /* Решение */
        solver();
            /* Запись в файл */
        writeToFile("dataZ2");
    }

    private static void solver() throws IOException {
        for (int i = 1; i < NT; i++){
            bound_conditions(i);
            half_layer_solver(i);
            layer_solver(i);
        }
        System.out.println("Solved");

    }

    private static void writeToFile(String fileName) throws IOException {
        for (int s = 0; s < NT; s++) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("dataZ2T\\"+fileName+""+s+".txt"))) {
                for (int j = 0; j < NY; j++){
                    for (int i = 0; i < NX; i++){
                        bufferedWriter.write(getX(i)+"\t"+getY(j)+"\t"+u[i][j][s]+"\n");
                    }
                }

            }
        }
        for (int s = 0; s < NX; s++) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("dataZ2X\\"+fileName+""+s+".txt"))) {
                for (int j = 0; j < NY; j++){
                    for (int i = 0; i < NT; i++){
                        bufferedWriter.write(getT(i)+"\t"+getY(j)+"\t"+u[s][j][i]+"\n");
                    }
                }

            }
        }
        for (int s = 0; s < NY; s++) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("dataZ2Y\\"+fileName+""+s+".txt"))) {
                for (int j = 0; j < NT; j++){
                    for (int i = 0; i < NX; i++){
                        bufferedWriter.write(getX(i)+"\t"+getT(j)+"\t"+u[i][s][j]+"\n");
                    }
                }

            }
        }
    }

    private static double getX(int i) {
        return i * DX;
    }
    private static double getY(int i) {
        return i * DY;
    }
    private static double getT(int i) {
        return i * DT;
    }

    private static void layer_solver(int s) {
        double kapa1 = 1.0;
        double kapa2 = 1.0;
        double mu1 = 0;
        double mu2 = 0;

        create_factors_field(NY);

        for (int i = 1; i < NX - 1; i++){
            for (int j = 1; j < NY - 1; j++){
                A[j] = 0.5*DT/(DY*DY);
                B[j] = 0.5*DT/(DY*DY);
                C[j] = 1.0 + DT/(DY*DY);
                F[j] = (0.5*DT/(DX*DX))*Umiddle[i+1][j] + (1.0 - DT/(DX*DX))*Umiddle[i][j] +
                       (0.5*DT/(DX*DX))*Umiddle[i-1][j] + 0.5*DT*Math.sin(DX*i)*Math.sin(DT*(s - 0.5));
            }
            sweep(kapa1, kapa2, mu1, mu2, NY);
            for (int j = 0; j < NY; j++){
                u[i][j][s] = U[j];
            }
        }
    }

    private static void half_layer_solver(int s) {

        double kapa1 = 0;
        double kapa2 = 0;
        double mu1 = 0;
        double mu2 = 0;

        create_factors_field(NX);
        for(int j = 1; j < NY - 1; j++){
            for(int i = 1; i < NX - 1; i++){
                A[i] = 0.5*DT/(DX*DX);
                B[i] = 0.5*DT/(DX*DX);
                C[i] = 1.0 + DT/(DX*DX);
                F[i] = (0.5*DT/(DY*DY))*u[i][j+1][s-1] + (1.0 - DT/(DY*DY))*u[i][j][s-1]
                     + (0.5*DT/(DY*DY))*u[i][j-1][s-1] + 0.5*DT*Math.sin(DX*i)*Math.sin(DT*(s - 0.5));
            }
            sweep(kapa1, kapa2, mu1, mu2, NX);

            for(int i = 0; i < NX; i++)
                Umiddle[i][j] = U[i];
        }
        //delete_factors_field_all(Nx);

    }

    private static void sweep(double kapa1, double kapa2, double mu1, double mu2, int N){
        alpha[1] = kapa1;
        beta[1] = mu1;

        for(int i = 1; i < N-1; i++){
            alpha[i+1] = B[i]/(C[i] - alpha[i]*A[i]);
            beta[i+1] = (A[i]*beta[i] + F[i]) / (C[i] - alpha[i]*A[i]);
        }

        U[N-1] = (mu2 + beta[N-1]*kapa2)/(1 - alpha[N-1]*kapa2);

        for(int n = N-2; n >= 0; n--)
            U[n] = alpha[n+1]*U[n+1] + beta[n+1];
    }


    private static void bound_conditions(int s) {
        for(int j = 0; j < NY; j++){
            u[0][j][s] = 0;
            u[NX-1][j][s] = 0;
        }
        for(int i = 0; i < NX; i++){
            u[i][0][s] = 0;
            u[i][NY-1][s] = 0;
        }
    }

    private static void initial() {
        for (int i = 0; i < NX; i++)
            for (int j = 0; j < NY; j++)
                u[i][j][0] = 0;
    }

    private static void create_factors_field(int n){
        A = new double[n];
        B = new double[n];
        C= new double[n];
        F = new double[n];
        U = new double[n];
        alpha = new double[n];
        beta = new double[n];
    }



}
