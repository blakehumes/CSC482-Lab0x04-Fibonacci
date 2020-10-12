package com.company;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Array;
import java.util.Arrays;

public class Main {
    public static long[] cache;

    public static void main(String[] args) {


        RunTimeTests(1000);
    }
    public static void RunTimeTests(int nMax){
        // Formatting the table
        System.out.format("%5s %5s %12s %12s %14s %8s %8s %14s %8s %8s %14s %8s %8s %14s\n",
                "", "", "fibRecur", "", "", "fibLoop", "",
                "","fibCache", "", "","fibMatrix", "", "");
        System.out.format("%5s %5s %12s %12s %14s %8s %8s %14s %8s %8s %14s %8s %8s %14s\n",
                "X", "N", "Time", "2x Ratio", "Pred 2x Ratio", "Time", "2x Ratio",
                "Pred 2x Ratio","Time", "2x Ratio", "Pred 2x Ratio","Time", "2x Ratio", "Pred 2x Ratio");

        // Declaring the variables that track the times and ratios
        long StartSort = 0, EndSort = 0;
        long[] recurTimes = new long[nMax+1];
        long[] loopTimes = new long[nMax+1];
        long[] cacheTimes = new long[nMax+1];
        long[] matrixTimes = new long[nMax+1];
        float[] DoubleRatio = new float[4];
        double[] PredDoublRatio = new double[4];
        PredDoublRatio[0] = 0; // Predicted Doubling Ratio for fibRecur (exponential)
        PredDoublRatio[1] = 2; // Predicted Doubling Ratio for fibLoop (linear)
        PredDoublRatio[2] = 2; // Predicted Doubling Ratio for fibCache (linear)
        PredDoublRatio[3] = 4; // Predicted Doubling Ratio for fibMatrix (log2 X)

        // Fill arrays with zeroes to ensure no random values
        Arrays.fill(recurTimes, 0);
        Arrays.fill(loopTimes, 0);
        Arrays.fill(cacheTimes, 0);
        Arrays.fill(matrixTimes, 0);

        int X;
        int[] TooSlow = {0,0,0,0}; // T/F to track if each algorithm is taking too long

        // Loop through tests, doubling N each time
        for(long N = 1; N <= nMax; N++){

            // Too Slow tests if time for the algorithm is past threshold
            if(TooSlow[0] == 0) {
                // Recur test and timestamps
                StartSort = getCpuTime();
                fibRecur(N);
                EndSort = getCpuTime();
                recurTimes[(int) N] = (EndSort - StartSort) / 1000;
            }
            else
                recurTimes[(int) N] = 0;

            if(TooSlow[1] == 0) {
                // Loop test and timestamps
                StartSort = getCpuTime();
                fibLoop(N);
                EndSort = getCpuTime();
                loopTimes[(int) N] = (EndSort - StartSort) / 1000;
            }
            else
                loopTimes[(int) N] = 0;

            if(TooSlow[2] == 0) {
                // Cache test and timestamps
                StartSort = getCpuTime();
                fibCache(N);
                EndSort = getCpuTime();
                cacheTimes[(int) N] = (EndSort - StartSort) / 1000;
            }
            else
                cacheTimes[(int) N] = 0;

            if(TooSlow[3] == 0) {
                // Matrix test and timestamps
                StartSort = getCpuTime();
                fibCache(N);
                EndSort = getCpuTime();
                matrixTimes[(int) N] = (EndSort - StartSort) / 1000;
            }
            else
                matrixTimes[(int) N] = 0;

            // If N <= 40, repeat the test 19 more times to get an average of 10 tests
            if(N <= 1000){
                for(int k = 0; k < 19; k++){
                    // Too Slow tests if time for the algorithm is past threshold
                    if(TooSlow[0] == 0) {
                        // Recur test and timestamps
                        StartSort = getCpuTime();
                        fibRecur(N);
                        EndSort = getCpuTime();
                        recurTimes[(int) N] += (EndSort - StartSort) / 1000;
                    }

                    if(TooSlow[1] == 0) {
                        // Loop test and timestamps
                        StartSort = getCpuTime();
                        fibLoop(N);
                        EndSort = getCpuTime();
                        loopTimes[(int) N] += (EndSort - StartSort) / 1000;
                    }

                    if(TooSlow[2] == 0) {
                        // Cache test and timestamps
                        StartSort = getCpuTime();
                        fibCache(N);
                        EndSort = getCpuTime();
                        cacheTimes[(int) N] += (EndSort - StartSort) / 1000;
                    }

                    if(TooSlow[3] == 0) {
                        // Matrix test and timestamps
                        StartSort = getCpuTime();
                        fibCache(N);
                        EndSort = getCpuTime();
                        matrixTimes[(int) N] += (EndSort - StartSort) / 1000;
                    }

                }
                recurTimes[(int)N] = recurTimes[(int)N] / 20;
                loopTimes[(int)N] = loopTimes[(int)N] / 20;
                cacheTimes[(int)N] = cacheTimes[(int)N] / 20;
                matrixTimes[(int)N] = matrixTimes[(int)N] / 20;
            }

            // If loop is even, compute doubling ratios and the (variable) expected ratios
            if(N % 2 == 0) {
                DoubleRatio[0] = (float) recurTimes[(int)N] / recurTimes[(int)(N/2)];
                DoubleRatio[1] = (float) loopTimes[(int)N] / loopTimes[(int)(N/2)];
                DoubleRatio[2] = (float) cacheTimes[(int)N] / cacheTimes[(int)(N/2)];
                DoubleRatio[3] = (float) matrixTimes[(int)N] / matrixTimes[(int)(N/2)];
                PredDoublRatio[0] = Math.pow(2,N/2) / Math.pow(2, (double)N/2/2);
                PredDoublRatio[3] = (Math.log(N)/Math.log(2)) / (Math.log(N/2)/Math.log(2));
            }

            // Calcuate bits of N
            X = (int)(Math.log(N)/Math.log(2)+1);

            // Formatted table results
            System.out.format("%5s %5s %12s %12.1f %14.1f %8s %8.1f %14.1f %8s %8.1f %14.1f %8s %8.1f %14.1f\n",
                    X, N, recurTimes[(int)N], (N%2==0) ? DoubleRatio[0] : 0, (N%2==0 && TooSlow[0]==0) ? PredDoublRatio[0]: 0,
                    loopTimes[(int)N], (N%2==0) ? DoubleRatio[1] : 0, (N%2==0) ? PredDoublRatio[1]: 0,
                    cacheTimes[(int)N], (N%2==0) ? DoubleRatio[2] : 0, (N%2==0) ? PredDoublRatio[2]: 0,
                    matrixTimes[(int)N], (N%2==0) ? DoubleRatio[3] : 0, (N%2==0) ? PredDoublRatio[3]: 0);

            if(recurTimes[(int)N] > 500000) TooSlow[0] = 1;
            if(loopTimes[(int)N] > 30000000) TooSlow[1] = 1;
            if(cacheTimes[(int)N] > 30000000) TooSlow[2] = 1;
            if(matrixTimes[(int)N] > 30000000) TooSlow[3] = 1;


        }

    }

    public static long fibMatrix(long X){
        String binary = toBinary(X); // Get binary string of X
        // Identity matrix
        long[][] resultMatrix = {
                {1,0},
                {0,1}
        };
        // Matrix to be exponentiated, starts at X=1 or M^1
        long [][] expMatrix = {
                {1,1},
                {1,0}
        };
        int length = binary.length();

        // Loop from the end of the binary string to the beginning
        // expMatrix tracks the current exponentiated matrix (M^1, M^2, M^4, M^8, etc) for the binary digit being checked
        // If binary digit being checked is a '1', multiply resultMatrix with expMatrix
        for(int i = length - 1; i >= 0; i--){
            if(length > 1 && i != length - 1)
                expMatrix = matrixMult(expMatrix, expMatrix);

            if (binary.charAt(i) == '1')
                resultMatrix = matrixMult(resultMatrix,expMatrix);
        }
        // Return top right value of matrix
        // This is a slight variation from the notes, but the general idea is the same
        return resultMatrix[0][1];
    }
    public static String toBinary(long a){
        String s = ""; // String to store binary

        // Test a % 2 for whether 1 or 0 to prepend to binary string
        // Divide a by 2 at end of loop to move to next binary digit
        while(a > 0){
            if(a % 2 == 0){
                s = "0" + s;
            }
            else{
                s = "1" + s;
            }
            a = a / 2;
        }
        return s;
    }

    public static long[][] matrixMult(long[][] a, long[][] b){
        long[][] c = new long[2][2]; // Result

        // 2x2 Matrix Multiplication
        c[0][0] = a[0][0] * b[0][0] + a[0][1] * b[1][0];
        c[0][1] = a[0][0] * b[0][1] + a[0][1] * b[1][1];
        c[1][0] = a[1][0] * b[0][0] + a[1][1] * b[1][0];
        c[1][1] = a[1][0] * b[0][1] + a[1][1] * b[1][1];

        return c;
    }

    public static long fibRecur(long X){
        if(X == 0 || X == 1) return X;

        return fibRecur(X-1) + fibRecur(X-2);
    }
    public static long fibLoop(long X){
        long a = 0; // X - 2 (two fibonacci values behind X)
        long b = 1; // X - 1 (one fibonacci value behind X)
        long c = 1; // X

        // If X >= 2, loop through X-2 times
        for(long i = 2; i <=X; i++){
            c = a + b; // Sum previous two fibonacci values
            a = b; // X - 2 variable becomes X - 1 value for next loop
            b = c; // X - 1 variable becomes X value for next loop
        }
        return c;
    }

    public static long fibCache(long X){
        cache = new long[(int)X + 1]; // Instantiate global cache variable
        Arrays.fill(cache, -1); // Fill cache array with negative ones

        return fibCacheHelper(X);
    }
    public static long fibCacheHelper(long X){

        if(X == 1 || X == 0){
            return X;
        }
        // If X is in cache, return cached value
        else if(cache[(int)X] >= 0){
            return cache[(int)X];
        }
        // Else create X in cache recursively
        else{
            cache[(int)X] = fibCacheHelper(X - 1) + fibCacheHelper(X - 2);
            return cache[(int)X];
        }
    }
    /** Get CPU time in nanoseconds since the program(thread) started. */
    /** from: http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking#TimingasinglethreadedtaskusingCPUsystemandusertime **/
    public static long getCpuTime( ) {

        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

        return bean.isCurrentThreadCpuTimeSupported( ) ?

                bean.getCurrentThreadCpuTime( ) : 0L;

    }
}
// Test Loop
/*for(int i = 0; i < 10; i++){
            System.out.format("\nX = %d\n", i);
            long a = fibRecur(i);
            long b = fibLoop(i);
            long c = fibCache(i);
            long d = fibMatrix(i);
            System.out.format("fibRecur: %d\n", a);
            System.out.format("fibLoop: %d\n", b);
            System.out.format("fibCache: %d\n", c);
            System.out.format("fibMatrix: %d\n", d);

        }*/