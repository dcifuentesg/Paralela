package co.edu.unal.paralela;

import static edu.rice.pcdp.PCDP.forseq2d;
import static edu.rice.pcdp.PCDP.forall;

/**
 * Clase envolvente pata implementar de forma eficiente la multiplicación dde matrices en paralelo.
 */
public final class MatrixMultiply {
    /**
     * Constructor por omisión.
     */
    private MatrixMultiply() {
    }

    /**
     * Realiza una multiplicación de matrices bidimensionales (A x B = C) de forma secuencial.
     *
     * @param A Una matriz de entrada con dimensiones NxN
     * @param B Una matriz de entrada con dimensiones NxN
     * @param C Matriz de salida
     * @param N Tamaño de las matrices de entrada
     */
    public static void seqMatrixMultiply(final double[][] A, final double[][] B,
            final double[][] C, final int N) {
        forseq2d(0, N - 1, 0, N - 1, (i, j) -> {
            C[i][j] = 0.0;
            for (int k = 0; k < N; k++) {
                C[i][j] += A[i][k] * B[k][j];
            }
        });
    }

    /**
     * Transposes a given NxN matrix.
     *
     * @param matrix The matrix to transpose.
     * @param N The size of the matrix.
     * @return The transposed matrix.
     */
    private static double[][] transposeMatrix(final double[][] matrix, final int N) {
        final double[][] transposed = new double[N][N];
        forall(0, N - 1, (i) -> {
            for (int j = 0; j < N; j++) {
                transposed[i][j] = matrix[j][i];
            }
        });
        return transposed;
    }

    /**
     * Realiza una multiplicación de matrices bidimensionales (A x B = C) de forma paralela.
     *
     * @param A Una matriz de entrada con dimensiones NxN
     * @param B Una matriz de entrada con dimensiones NxN
     * @param C Matriz de salida
     * @param N amaño de las matrices de entrada
     */
    public static void parMatrixMultiply(final double[][] A, final double[][] B,
            final double[][] C, final int N) {
        /*
         * Paralelización implementada usando una matriz transpuesta para mejorar
         * la eficiencia del cache.
         */
        final double[][] B_transposed = transposeMatrix(B, N);

        forall(0, N - 1, (i) -> {
            for (int j = 0; j < N; j++) {
                double sum = 0.0;
                for (int k = 0; k < N; k++) {
                    sum += A[i][k] * B_transposed[j][k];
                }
                C[i][j] = sum;
            }
        });
    }
}
