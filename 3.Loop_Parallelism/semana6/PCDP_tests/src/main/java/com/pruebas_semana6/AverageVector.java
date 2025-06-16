package com.pruebas_semana6;
import edu.rice.pcdp.PCDP;
public class AverageVector {
	 int n;
	 double[] myVal;
	 double[] myNew;
	
	 
	 public AverageVector(int n) {
	        this.n = n;
	        this.myVal = new double[n + 2]; // +2 for boundaries
	        this.myNew = new double[n + 2];
	        
	        // Initialize with some values, e.g., all 0s except boundary
	        for (int i = 0; i <= n + 1; i++) {
	            myVal[i] = 0;
	            myNew[i] = 0;
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
	 private static void printResults(String name,long timeInNanos,double sum){
		    System.out.printf(" %s completed in %8.3f milliseconds, with  = %8.5f \n", name, timeInNanos/1e6,sum);
		}

	 
	public void runSequential(int iterations) { 
		long startTime = System.nanoTime(); 
		for (int iter = 0; iter < iterations; iter++) { 
			for (int j = 1; j <= n; j++) { 
				myNew[j] = (myVal[j - 1] + myVal[j + 1]) / 2.0; 
				} 
			double[] temp = myNew; 
			myNew = myVal; 
			myVal = temp; 
			}
		long timeInNanos = System.nanoTime() - startTime;
		printResults("runSequential", timeInNanos, myVal[0]); 
		}
		
	public void runForall(final int iterations)  { 
		long startTime = System.nanoTime(); 
		for (int iter = 0; iter < iterations; iter++) { 
			PCDP.forall(1, n, (j)-> { 
				myNew[j] = (myVal[j - 1] + myVal[j + 1]) / 2.0; 
			}); 
			double[] temp = myNew; 
			myNew = myVal; 
			myVal = temp; 
		} 
		long timeInNanos = System.nanoTime() - startTime;
		printResults("runForall", timeInNanos, myVal[0]); 
	} 
		
	
	public void runForallGrouped(final int iterations, final int tasks)  {  
		long startTime = System.nanoTime(); 
		for (int iter = 0; iter < iterations; iter++) { 
			PCDP.forall(0, tasks - 1, (i) -> { 
				for (int j = i * (n / tasks) + 1; j <= (i + 1) * (n / tasks); j++) 
					myNew[j] = (myVal[j - 1] + myVal[j + 1]) / 2.0; 
			}); 
			double[] temp = myNew; 
			myNew = myVal;
			myVal = temp; 
		}
		 long timeInNanos = System.nanoTime() - startTime;
		 printResults("runForallGrouped", timeInNanos, myVal[0]); 
	}
	
	public void runForallBarrier(final int iterations, final int tasks)  {  
		long startTime = System.nanoTime(); 
	/*	PCDP.forallPhased(0, tasks - 1, (i) -> { 
				double[] myVal = this.myVal; 
				double[] myNew = this.myNew; 
				for (int iter = 0; iter < iterations; iter++) { 
					for (int j = i (n / tasks) + 1; j <= (i + 1)* (n / tasks); j++) 
						myNew[j] = (myVal[j - 1] + myVal[j + 1]) / 2.0; 
					next();//barrier	
					double[] temp = myNew; 
					myNew = myVal; 
					myVal = temp; 
				}
			});	*/
		 long timeInNanos = System.nanoTime() - startTime;
		 printResults("runForallBarrier", timeInNanos, myVal[0]); 
		}			
 
	
	
		
	   public static void main(String[] args) {
		        int n = 1000;            // Number of interior points
		        int iterations = 100000;  // Number of  iterations
		        int task  = 50;
		        AverageVector test = new AverageVector(n);
		        test.runSequential(iterations);
		        test.runForall(iterations);
		        test.runForallGrouped(iterations,task);
		       // test.runForallBarrier()
		       
		    }
}