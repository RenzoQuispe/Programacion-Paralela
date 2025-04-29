package com.pruebas_semana6;

import java.util.Random;

import edu.rice.pcdp.PCDP;

/*
    villena@pcVillena:~/Escritorio/Renzo/Programacion Paralela/PCDP_tests$ mvn exec:java -Dexec.mainClass="com.pruebas_semana6.MatrixMultiplication"
    villena@pcVillena:~/Escritorio/Renzo/Programacion Paralela/PCDP_tests$ mvn compile
*/


public class MatrixMultiplication {

  private static void initializeMatrix(double[][] matrix) {
        Random rand = new Random();
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = rand.nextDouble() * 10;
            }
        }
    }
    
public class SuspendableException extends Exception {

    public SuspendableException() {
        super();
    }

    public SuspendableException(String message) {
        super(message);
    }

    public SuspendableException(String message, Throwable cause) {
        super(message, cause);
    }

    public SuspendableException(Throwable cause) {
        super(cause);
    }
}
    
    
 public static void seqMatrixMultiply(double[][] A, double[][] B, double[][] C, int n) throws SuspendableException {
	 long startTime = System.nanoTime(); 
	 PCDP.forseq2d(0, n - 1, 0, n - 1, (i, j) -> 
		{ 
			C[i][j] = 0; 
			for (int k = 0; k < n; k++) { 
				C[i][j] += A[i][k] * B[k][j];
			} 
		}	 
	); 
    long timeInNanos = System.nanoTime() - startTime;
	printResults("seqMatrixMultiply", timeInNanos, C[n-1][n-1]); 
}
 public static void parMatrixMultiply(double[][] A, double[][] B, double[][] C, int n) throws SuspendableException {
	 long startTime = System.nanoTime(); 
	 PCDP.forall2d(0, n - 1, 0, n - 1, (i, j) -> 
		{ 
			C[i][j] = 0; 
			for (int k = 0; k < n; k++) { 
				C[i][j] += A[i][k] * B[k][j];
			} 
		}	 
	); 
    long timeInNanos = System.nanoTime() - startTime;
	printResults("parMatrixMultiply", timeInNanos, C[n-1][n-1]); 
}
 

 public static void par2MatrixMultiply(double[][] A, double[][] B, double[][] C, int n) throws SuspendableException {
	 long startTime = System.nanoTime(); 
	 PCDP.forall2dChunked(0, n - 1, 0, n - 1, (i, j) ->  //number or chunks number of blocks
		{ 
			C[i][j] = 0; 
			for (int k = 0; k < n; k++) { 
				C[i][j] += A[i][k] * B[k][j];
			} 
		}	 
	); 
    long timeInNanos = System.nanoTime() - startTime;
	printResults("par2MatrixMultiply", timeInNanos, C[n-1][n-1]); 
}
 
 private static void printResults(String name,long timeInNanos,double sum){
    System.out.printf(" %s completed in %8.3f milliseconds, with C[n-1][n-1] = %8.5f \n", name, timeInNanos/1e6,sum);
}



 public static void main(String[] args) throws SuspendableException {
        int n = 1200; // Size of matrix (e.g. 500x500) 1000
        double[][] A = new double[n][n];
        double[][] B = new double[n][n];
        double[][] C = new double[n][n];

        // Fill A and B with random values
        initializeMatrix(A);
        initializeMatrix(B);

        // Perform sequential matrix multiplication
        seqMatrixMultiply(A, B, C, n);
		parMatrixMultiply(A, B, C, n);
		par2MatrixMultiply(A, B, C, n);
    }
 }